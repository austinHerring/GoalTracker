package com.austin.goaltracker.Controller;

import com.austin.goaltracker.Model.Account;

import com.austin.goaltracker.Model.CountdownCompleterGoal;
import com.austin.goaltracker.Model.Goal;
import com.austin.goaltracker.Model.Password;
import com.austin.goaltracker.Model.StreakSustainerGoal;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Class to help with database interaction
 */
public class Util {
    public static Firebase db;
    public static Account currentUser;
    public static HashMap<String, Account> registeredUsers = new HashMap<>();

    public static void setDB(Firebase d) {
        db = d;
    }

    /**
     * Authenticates a user before logging in by filtering through local accounts
     *
     * @param username the username of the account
     * @param password the password of the account
     */
    public static String authenticate(final String username, final String password) {
        Predicate<Account> authFilter = new Predicate<Account>() {
            public boolean apply(Account account) {
                return (account.getUsername().equals(username) && account.getPassword().equals(password));
            }
        };
        Map<String, Account> filteredAccount = Maps.filterValues(registeredUsers, authFilter);
        if (filteredAccount.size() == 1) {
            currentUser = (Account) filteredAccount.values().toArray()[0]; // Log in as current user;
            return null;
        }
        return "Invalid Username Or Password";
    }

    /**
     * Attempts to Register with given information. If conditions are met, the account is created
     * and set
     *
     * @param nameFirst the first name of the account
     * @param nameLast the last name of the account
     * @param username the username of the account
     * @param password the password of the account
     * @param email the email of the account
     *
     * @return String of an error message
     */
    public static String registerUserOnDB(String nameFirst, String nameLast, final String username,
                                          String password, String email) {
        // Checks that all fields are filled in
        if (nameFirst.equals("") || nameLast.equals("") || username.equals("") || email.equals("")
                || password.equals("")) {
            return "Fill In All Fields";
        }
        // Checks for valid Password.. 6 characters, at least 1 number, lower and upper letter
        if (password.length() < 6 || !(password.matches(".*[a-z].*") && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*"))) {
            return "Invalid Password";
        }
        // Checks for valid email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid Email Address";
        }

        // Checks if username is taken
        Predicate<Account> accountFilter = new Predicate<Account>() {
            public boolean apply(Account account) {
                return (account.getUsername().equals(username));
            }
        };
        Map<String, Account> filteredAccounts = Maps.filterValues(registeredUsers, accountFilter);
        if (filteredAccounts.size() != 0) {
            return "Account Already In Use";
        }

        Account newAccount = new Account(nameFirst, nameLast, username, new Password(password), email);
        Firebase accountsRef = db.child("accounts");
        Firebase newRow = accountsRef.push();

        // ADDS ACCOUNT TO LOCAL MAP
        newAccount.setId(newRow.getKey());
        registeredUsers.put(newAccount.getId(), newAccount);

        // ADDS ACCOUNT TO DB
        HashMap<String,Object> newAccountAsEntry = generateBasicAccountMappingForDB(newAccount);
        newRow.setValue(newAccountAsEntry);

        currentUser = newAccount;
        return null;
    }

    /**
     * Updates a goal for an account on the database.
     *
     * @param accountId account update a goal for
     * @param goal the goal object itself to persist
     */
    public static void updateAccountGoalOnDB(String accountId, Goal goal) throws FirebaseException {
        Firebase accountGoalsRef = db.child("accounts").child(accountId).child("goals");
        String goalId = goal.getId();
        Firebase row;
        if (goalId == null) {
            row = accountGoalsRef.push();
            goal.setId(row.getKey());
        } else {
            row = accountGoalsRef.child(goalId);
        }
        HashMap<String,Object> newGoalAsEntry = generateGoalMappingForDB(goal);
        row.setValue(newGoalAsEntry, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    System.out.println("Data could not be saved. " + firebaseError.getMessage());
                    throw new FirebaseException(firebaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                }
            }
        });
    }

    /**
     * Updates a password for an account on the database.
     *
     * @param account account reset a password for
     * @param password the new password
     */
    public static Password updatePasswordForAccountOnDB(Account account, String password) throws FirebaseException {
        if (account != null) {
            Password passwordObject = (password==null) ?
                    new Password(PasswordGenerator.generatePassword()) : new Password(password);

            Firebase passwordRef = db.child("accounts").child(account.getId()).child("password object");
            HashMap<String, Object> newPassword = generatePasswordMappingForDB(passwordObject);
            passwordRef.setValue(newPassword, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        System.out.println("Data could not be saved. " + firebaseError.getMessage());
                        throw new FirebaseException(firebaseError.getMessage());
                    } else {
                        System.out.println("Data saved successfully.");
                    }
                }
            });
            return passwordObject;
        } else {
            throw new FirebaseException("This Account Does Not Exist");
        }
    }

    /**
     * Accounts are represented as hashmaps to access particular data
     *
     * @param account The account to generate a HashMap for.
     * @return The HashMap.
     */
    private static HashMap<String, Object> generateBasicAccountMappingForDB(Account account) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", account.getId());  // Integer must be stored as String for database
        map.put("firstname", account.getNameFirst());
        map.put("lastname", account.getNameLast());
        map.put("username", account.getUsername());
        map.put("password object", generatePasswordMappingForDB(account.getPasswordObject()));
        map.put("email", account.getEmail());
        return map;
    }

    /**
     * Accounts are represented as hashmaps to access particular data
     *
     * @param goal A goal to generate a HashMap for.
     * @return The HashMap.
     */
    private static HashMap<String, Object> generateGoalMappingForDB(Goal goal) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", goal.getId());
        map.put("classification", goal.classification());
        map.put("name", goal.getGoalName());
        map.put("date start", goal.getDateOfOrigin());
        map.put("date broken", goal.getBrokenDate());
        map.put("task", goal.getTask());
        map.put("frequency", goal.getIncrementType());
        map.put("cron key", goal.getCronJobKey());
        if (goal.classification().equals(Goal.Classification.COUNTDOWN)) {
            map.put("date end", ((CountdownCompleterGoal) goal).getDateDesiredFinish());
            map.put("percent progress", ((CountdownCompleterGoal) goal).getPercentProgress());
            map.put("remaining checkpoints", ((CountdownCompleterGoal) goal).getRemainingCheckpoints());
            map.put("total checkpoints", ((CountdownCompleterGoal) goal).getTotalCheckpoints());
            map.put("units remaining string", ((CountdownCompleterGoal) goal).getUnitsRemaining());
        } else {
            map.put("streak number", ((StreakSustainerGoal) goal).getStreak());
            map.put("cheat number", ((StreakSustainerGoal) goal).getCheatNumber());
            map.put("cheats remaining", ((StreakSustainerGoal) goal).getCheatsRemaining());
        }
        return map;
    }

    /**
     * Accounts are represented as hashmaps to access particular data
     *
     * @param password The password to generate a HashMap for.
     * @return The HashMap.
     */
    private static HashMap<String, Object> generatePasswordMappingForDB(Password password) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("password", password.toPasswordString());
        map.put("date last changed", password.toDateString());
        return map;
    }

    /**
     * Generates a list storing all of the accounts that are currently registered
     * in the database.
     *
     * @param snapshot firebase snapshot of data
     */
    public static void retrieveUsersToLocal(DataSnapshot snapshot) {
        if (!snapshot.exists())
            return;
        HashMap<String, HashMap<String, String>> accounts =
                ( HashMap<String, HashMap<String, String>>) snapshot.getValue();
        if (accounts == null) {
            return;
        }
        String id = "";
        Account accountToAdd = null;
        for (HashMap accountSnapshot : accounts.values()) {
            if (!registeredUsers.keySet().contains(id)) {
                id = (String)accountSnapshot.get("id");
                accountToAdd = new Account(
                        (String)accountSnapshot.get("firstname"),
                        (String)accountSnapshot.get("lastname"),
                        (String)accountSnapshot.get("username"),
                        retrieveUserPasswordToLocal(accountSnapshot),
                        (String)accountSnapshot.get("email"),
                        (String)accountSnapshot.get("id"));
                if (accountSnapshot.get("goals") != null) {
                    accountToAdd.setGoals(retrieveUserGoalsToLocal(accountSnapshot));
                }
                registeredUsers.put(id, accountToAdd);
            }
        }
    }

    /**
     * Loops through the goals for an account and converts it from DB to Object
     *
     * @param accountSnapshot the account given in from DB used to generate a hashmap
     */
    public static HashMap<String, Goal> retrieveUserGoalsToLocal(HashMap accountSnapshot) {
        HashMap<String, Goal> aggregatedGoals = new HashMap<>();
        HashMap<String, HashMap<String,Object>> goalsSnapShots  = (HashMap < String, HashMap < String, Object >>) accountSnapshot.get("goals");
        for(HashMap<String, Object> goalSnapShot : goalsSnapShots.values()) {
            if (goalSnapShot.get("classification").equals("COUNTDOWN")) {
                CountdownCompleterGoal goalToAdd = new CountdownCompleterGoal();
                goalToAdd.setId((String) goalSnapShot.get("id"));
                goalToAdd.setName((String) goalSnapShot.get("name"));
                goalToAdd.setTask((String) goalSnapShot.get("task"));
                goalToAdd.setCronJobKey((String) goalSnapShot.get("cron key"));
                goalToAdd.setUnitsRemaining((String) goalSnapShot.get("units remaining string"));
                goalToAdd.setIncrementType(Converter.stringToFrequency((String) goalSnapShot.get("frequency")));
                goalToAdd.setDateOfOrigin(Converter.longToCalendar((Long) goalSnapShot.get("date start")));
                if (goalSnapShot.get("date broken") != null) {
                    goalToAdd.setBrokenDate(Converter.longToCalendar((Long) goalSnapShot.get("date broken")));
                }
                goalToAdd.setDateDesiredFinish(Converter.longToCalendar((Long) goalSnapShot.get("date end")));
                goalToAdd.setPercentProgress(((Long) goalSnapShot.get("percent progress")).intValue());
                goalToAdd.setRemainingCheckpoints(((Long) goalSnapShot.get("remaining checkpoints")).intValue());
                goalToAdd.setTotalCheckpoints(((Long)goalSnapShot.get("total checkpoints")).intValue());
                aggregatedGoals.put((String) goalSnapShot.get("id"), goalToAdd);
            } else {
                StreakSustainerGoal goalToAdd = new StreakSustainerGoal();
                goalToAdd.setId((String) goalSnapShot.get("id"));
                goalToAdd.setName((String) goalSnapShot.get("name"));
                goalToAdd.setTask((String) goalSnapShot.get("task"));
                goalToAdd.setCronJobKey((String) goalSnapShot.get("cron key"));
                goalToAdd.setIncrementType(Converter.stringToFrequency((String) goalSnapShot.get("frequency")));
                goalToAdd.setDateOfOrigin(Converter.longToCalendar((Long) goalSnapShot.get("date start")));
                if (goalSnapShot.get("date broken") != null) {
                    goalToAdd.setBrokenDate(Converter.longToCalendar((Long) goalSnapShot.get("date broken")));
                }
                goalToAdd.setCheatNumber(((Long) goalSnapShot.get("cheat number")).intValue());
                goalToAdd.setCheatsRemaining(((Long) goalSnapShot.get("cheats remaining")).intValue());
                goalToAdd.setStreak(((Long) goalSnapShot.get("streak number")).intValue());
                aggregatedGoals.put((String) goalSnapShot.get("id"), goalToAdd);
            }
        }
        return aggregatedGoals;
    }

    /**
     * gets password object from the hashmap returned from firebase
     *
     * @param accountSnapshot firebase snapshot of data
     */
    private static Password retrieveUserPasswordToLocal(HashMap accountSnapshot) {
        HashMap<String, String> map = (HashMap<String, String>) accountSnapshot.get("password object");
        return new Password(map.get("password"), map.get("date last changed"));
    }
}
