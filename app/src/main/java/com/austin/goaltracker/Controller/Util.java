package com.austin.goaltracker.Controller;

import com.austin.goaltracker.Model.Account;

import com.austin.goaltracker.Model.CountdownCompleterGoal;
import com.austin.goaltracker.Model.Goal;
import com.austin.goaltracker.Model.Password;
import com.austin.goaltracker.Model.StreakSustainerGoal;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseException;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Class to help with database interaction
 */
public class Util {
    public static Firebase db;
    public static Account currentUser;
    public static HashMap<Integer, Account> registeredUsers = new HashMap<>();

    public static void setDB(Firebase d) {
        db = d;
    }

    public static Integer generateLocalID(String username) {
        return username.hashCode();
    }

    public static String authenticate(String username, String password) {
        Account account = registeredUsers.get(generateLocalID(username));
        if (account != null && password.equals(account.getPassword())) {
            currentUser = account; // Log in as current user;
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
     */
    public static String registerUser(String nameFirst, String nameLast, String username,
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
        if (registeredUsers.get(generateLocalID(username)) != null) {
            return "Account Already In Use";
        }

        Account newAccount = new Account(nameFirst, nameLast, username, password, email);
        Firebase accountsRef = db.child("accounts");
        Firebase newRow = accountsRef.push();
        newAccount.setId(newRow.getKey());
        HashMap<String,Object> newAccountAsEntry = generateAccountMapping(newAccount);
        newRow.setValue(newAccountAsEntry);
        currentUser = newAccount;
        return null;
    }

    /**
     * Accounts are represented as hashmaps to access particular data
     *
     * @param account The account to generate a HashMap for.
     * @return The HashMap.
     */
    private static HashMap<String, Object> generateAccountMapping(Account account) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", account.getId());  // Integer must be stored as String for database
        map.put("firstname", account.getNameFirst());
        map.put("lastname", account.getNameLast());
        map.put("username", account.getUsername());
        map.put("password object", generatePasswordMapping(account.getPasswordObject()));
        map.put("email", account.getEmail());
        map.put("goals", generateGoalMapping(account.getGoals()));
        map.put("registered device ids", generateDeviceIdMapping(account.getRegistedGCMDevices()));
        return map;
    }

    /**
     * Accounts are represented as hashmaps to access particular data
     *
     * @param goals The list of goals to generate a HashMap for.
     * @return The HashMap.
     */
    private static ArrayList<HashMap<String, Object>> generateGoalMapping(ArrayList<Goal> goals) {
        ArrayList<HashMap<String, Object>> listOfGoals = new ArrayList<>();
        for (Goal goal : goals) {
            HashMap<String, Object> map = new HashMap<>();
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
            listOfGoals.add(map);
        }
        return listOfGoals;
    }

    /**
     * Accounts are represented as hashmaps to access particular data
     *
     * @param regIDs The list of goals to generate a HashMap for.
     * @return The HashMap.
     */
    private static ArrayList<HashMap<String, Object>> generateDeviceIdMapping(ArrayList<String> regIDs) {
        ArrayList<HashMap<String, Object>> listOfRegIDs = new ArrayList<>();
        for (String regID : regIDs) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("device reg id", regID);
            listOfRegIDs.add(map);
        }
        return listOfRegIDs;
    }

    /**
     * Accounts are represented as hashmaps to access particular data
     *
     * @param password The password to generate a HashMap for.
     * @return The HashMap.
     */
    private static HashMap<String, Object> generatePasswordMapping(Password password) {
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
    public static void retrieveUsers(DataSnapshot snapshot) {
        if (!snapshot.exists())
            return;
        HashMap<String, HashMap<String, String>> accounts =
                ( HashMap<String, HashMap<String, String>>) snapshot.getValue();
        if (accounts == null) {
            return;
        }
        Integer id;
        Account accountToAdd;
        // Can't use map generics because the Value varies between string and other maps. Must
        // cast each key return
        // Loop for each account
        for (HashMap account : accounts.values()) {
            id = Util.generateLocalID((String)account.get("username"));
            if (!registeredUsers.keySet().contains(id)) {
                accountToAdd = new Account(
                        (String)account.get("firstname"),
                        (String)account.get("lastname"),
                        (String)account.get("username"),
                        hydratePassword(account),
                        (String)account.get("email"),
                        (String)account.get("id"));
                if (account.get("registered device ids") != null) {
                    accountToAdd = retrieveUserRegIds(account, accountToAdd);
                }
                registeredUsers.put(id, (account.get("goals") != null) ?
                        retrieveUserGoals(account, accountToAdd) : accountToAdd);
            }
        }
    }

    /**
     * Loops through the goals for an account and converts it from DB to Object
     *
     * @param account the account given in from DB
     * @param accountToAdd the account to hydrate goals for
     */
    public static Account retrieveUserGoals(HashMap account, Account accountToAdd) {
        for(HashMap<String, Object> goal : (ArrayList<HashMap<String, Object>>) account.get("goals")) {
            if (goal.get("classification").equals("COUNTDOWN")) {
                CountdownCompleterGoal goalToAdd = new CountdownCompleterGoal();
                goalToAdd.setName((String) goal.get("name"));
                goalToAdd.setTask((String) goal.get("task"));
                goalToAdd.setCronJobKey((String) goal.get("cron key"));
                goalToAdd.setUnitsRemaining((String) goal.get("units remaining string"));
                goalToAdd.setIncrementType(Converter.stringToFrequency((String) goal.get("frequency")));
                goalToAdd.setDateOfOrigin(Converter.longToCalendar((Long) goal.get("date start")));
                if (goal.get("date broken") != null) {
                    goalToAdd.setBrokenDate(Converter.longToCalendar((Long) goal.get("date broken")));
                }
                goalToAdd.setDateDesiredFinish(Converter.longToCalendar((Long) goal.get("date end")));
                goalToAdd.setPercentProgress(((Long) goal.get("percent progress")).intValue());
                goalToAdd.setRemainingCheckpoints(((Long) goal.get("remaining checkpoints")).intValue());
                goalToAdd.setTotalCheckpoints(((Long)goal.get("total checkpoints")).intValue());
                accountToAdd.addGoal(goalToAdd);
            } else {
                StreakSustainerGoal goalToAdd = new StreakSustainerGoal();
                goalToAdd.setName((String) goal.get("name"));
                goalToAdd.setTask((String) goal.get("task"));
                goalToAdd.setCronJobKey((String) goal.get("cron key"));
                goalToAdd.setIncrementType(Converter.stringToFrequency((String) goal.get("frequency")));
                goalToAdd.setDateOfOrigin(Converter.longToCalendar((Long) goal.get("date start")));
                if (goal.get("date broken") != null) {
                    goalToAdd.setBrokenDate(Converter.longToCalendar((Long) goal.get("date broken")));
                }
                goalToAdd.setCheatNumber(((Long) goal.get("cheat number")).intValue());
                goalToAdd.setCheatsRemaining(((Long) goal.get("cheats remaining")).intValue());
                goalToAdd.setStreak(((Long) goal.get("streak number")).intValue());
                accountToAdd.addGoal(goalToAdd);
            }
        }
        return accountToAdd;
    }

    /**
     * Loops through the registered devices for an account and converts it from DB to Object
     *
     * @param account the account given in from DB
     * @param accountToAdd the account to hydrate goals for
     */
    public static Account retrieveUserRegIds(HashMap account, Account accountToAdd) {
        for(HashMap<String, Object> device : (ArrayList<HashMap<String, Object>>) account.get("registered device ids")) {
            accountToAdd.addRegisteredDevice((String) device.get("device reg id"));
        }
        return accountToAdd;
    }

    /**
     * gets password object from the hashmap returned from firebase
     *
     * @param account firebase snapshot of data
     */
    private static Password hydratePassword(HashMap account) {
        HashMap<String, String> map = (HashMap<String, String>) account.get("password object");
        return new Password(map.get("password"), map.get("date last changed"));
    }

    /**
     * Resets a password in an account.
     *
     * @param username username of account to alter
     * @param password the new password to chage to
     */
    public static Account resetPassword(String username, String password) throws FirebaseException {
        Account account = registeredUsers.get(generateLocalID(username));
        if (account != null) {
            if (password == null) {
                account.setPassword(PasswordGenerator.generatePassword());
            }
            else {
                account.setPassword(password);
            }
            updateAccountOnDB(account);
            return account;
        } else {
            throw new FirebaseException("This Account Does Not Exist");
        }
    }

    /**
     * Updates a whole account on the database.
     *
     * @param account account to update
     */
    public static void updateAccountOnDB(Account account) {
        Firebase accountsRef = db.child("accounts");
        Firebase currentRef = accountsRef.child(account.getId());
        HashMap<String, Object> current = generateAccountMapping(account);
        currentRef.setValue(current);
    }

    /**
     * Updates a the registered device ids list on the db after a login
     *
     * @param account account to update
     */
    public static void updateAccountRegIdsOnDB(Account account) {
        Firebase accountRef = db.child("accounts").child(account.getId());
        Firebase regdeviceidsRef = accountRef.child("registered device ids");
        regdeviceidsRef.setValue(account.getRegistedGCMDevices());
    }

    public static String addCronJobToDB(Goal goal, int promptMinute, int promptHour) {
        Firebase cronJobsRef = db.child("cronJobs");
        Firebase newCronJobRef = cronJobsRef.push();

        Map<String, Object> job = new HashMap<>();
        job.put("message", goal.toNotificationMessage());
        job.put("registeredDevices", currentUser.getRegistedGCMDevices());
        job.put("frequency", goal.getIncrementType());
        job.put("promptMinute", promptMinute);
        job.put("promptHour", promptHour);
        job.put("lastRun", determineLastRunDate(goal));

        newCronJobRef.setValue(job);

        return newCronJobRef.getKey();
    }

    private static long determineLastRunDate(Goal goal) {
        if (goal.classification().equals(Goal.Classification.STREAK)) {
            return -1;
        } else {
            CountdownCompleterGoal g = (CountdownCompleterGoal) goal;
            return g.getDateDesiredFinish().getTimeInMillis();
        }
    }
}
