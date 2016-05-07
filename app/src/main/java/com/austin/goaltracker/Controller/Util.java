package com.austin.goaltracker.Controller;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.austin.goaltracker.Model.Account;

import com.austin.goaltracker.Model.CountdownCompleterGoal;
import com.austin.goaltracker.Model.Goal;
import com.austin.goaltracker.Model.GoalClassification;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.Model.NewMemberEmail;
import com.austin.goaltracker.Model.Password;
import com.austin.goaltracker.Model.StreakSustainerGoal;
import com.austin.goaltracker.Model.ToastType;
import com.austin.goaltracker.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Class to help with database interaction
 */
public class Util {
    public static Account currentUser;

    /**
     * Authenticates a user against Firebase before logging
     *
     * @param startActivity the context from the login screen
     * @param intent where to send the user if successful login
     * @param username the username of the account
     * @param password the password of the account
     */
    public static void authenticateUserAndLoad(final Activity startActivity, final Intent intent, final String username, final String password) {
        Firebase firebaseRef = new Firebase(GoalTrackerApplication.FIREBASE_URL).child("accounts");
        com.firebase.client.Query queryRef = firebaseRef.orderByChild("username").equalTo(username);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    startActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    ToastDisplayer.displayHint("Invalid Username", ToastType.FAILURE, startActivity);
                    return;
                }

                HashMap<String, HashMap<String, Object>> accounts =
                        (HashMap<String, HashMap<String, Object>>) snapshot.getValue();

                HashMap<String, Object> userAccount = accounts.entrySet().iterator().next().getValue();
                Account accountToLoad = new Account(
                        (String)userAccount.get("firstname"),
                        (String)userAccount.get("lastname"),
                        (String)userAccount.get("username"),
                        retrieveUserPasswordToLocal(userAccount),
                        (String)userAccount.get("email"),
                        (String)userAccount.get("id"));

                if (!password.equals(accountToLoad.getPassword())) {
                    startActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    ToastDisplayer.displayHint("Invalid Password", ToastType.FAILURE, startActivity);
                    return;
                }

                currentUser = accountToLoad;
                addListenerToGoals(); // Handles adding goals
                GAEDatastoreController.registerdeviceForCurrentUser();
                startActivity.startActivity(intent);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    /**
     * Attempts to Register with given information. If conditions are met, the account is created
     * and set
     *
     * @param startActivity the context from the login screen
     * @param intent where to send the user if successful login
     * @param account the account information to create
     */
    public static void registerUserAndLoad(final Activity startActivity, final Intent intent, final Account account) {
        Firebase firebaseRef = new Firebase(GoalTrackerApplication.FIREBASE_URL).child("accounts");
        com.firebase.client.Query queryRef = firebaseRef.orderByChild("username").equalTo(account.getUsername());
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Firebase accountsRef = new Firebase(GoalTrackerApplication.FIREBASE_URL).child("accounts");
                    Firebase newRow = accountsRef.push();
                    account.setId(newRow.getKey());

                    // ADDS ACCOUNT TO DB
                    HashMap<String,Object> newAccountAsEntry = generateBasicAccountMappingForDB(account);
                    newRow.setValue(newAccountAsEntry);
                    currentUser = account;
                    addListenerToGoals();

                    new EmailDispatchService(new NewMemberEmail(Util.currentUser)).send();
                    GAEDatastoreController.registerdeviceForCurrentUser();
                    startActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    startActivity.startActivity(intent);
                    ToastDisplayer.displayHint("Registration Successful", ToastType.SUCCESS, startActivity);

                } else {
                    startActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    ToastDisplayer.displayHint("Account Already In Use", ToastType.FAILURE, startActivity);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    /**
     * Updates a goal for an account on the database.
     *
     * @param accountId account update a goal for
     * @param goal the goal object itself to persist
     */
    public static void updateAccountGoalOnDB(String accountId, Goal goal) throws FirebaseException {
        Firebase accountGoalsRef = new Firebase(GoalTrackerApplication.FIREBASE_URL)
                .child("accounts").child(accountId).child("goals");
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
     * @param account account to reset a password for
     * @param password the new password
     */
    public static Password updatePasswordForAccountOnDB(Account account, String password) throws FirebaseException {
        if (account != null) {
            Password passwordObject = (password==null) ?
                    new Password(PasswordGenerator.generatePassword()) : new Password(password);

            Firebase passwordRef = new Firebase(GoalTrackerApplication.FIREBASE_URL)
                    .child("accounts").child(account.getId()).child("password object");
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
        map.put("isTerminated", goal.isTerminated());
        map.put("date start", goal.getDateOfOrigin());
        map.put("date broken", goal.getBrokenDate());
        map.put("task", goal.getTask());
        map.put("frequency", goal.getIncrementType());
        map.put("cron key", goal.getCronJobKey());
        if (goal.classification().equals(GoalClassification.COUNTDOWN)) {
            map.put("date end", ((CountdownCompleterGoal) goal).getDateDesiredFinish());
            map.put("percent progress", ((CountdownCompleterGoal) goal).getPercentProgress());
            map.put("remaining checkpoints", ((CountdownCompleterGoal) goal).getRemainingCheckpoints());
            map.put("total checkpoints", ((CountdownCompleterGoal) goal).getTotalCheckpoints());
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
     * Adds a goal to the local current user
     *
     * @param goalSnapShot the account given in from DB used to generate a hashmap
     */
    public static void loadUserGoal(HashMap goalSnapShot) {
        if (goalSnapShot.get("classification").equals("COUNTDOWN")) {
            CountdownCompleterGoal goalToAdd = new CountdownCompleterGoal();
            goalToAdd.setId((String) goalSnapShot.get("id"));
            goalToAdd.setIsTerminated((boolean) goalSnapShot.get("isTerminated"));
            goalToAdd.setName((String) goalSnapShot.get("name"));
            goalToAdd.setTask((String) goalSnapShot.get("task"));
            goalToAdd.setCronJobKey((String) goalSnapShot.get("cron key"));
            goalToAdd.setIncrementType(Converter.stringToFrequency((String) goalSnapShot.get("frequency")));
            goalToAdd.setDateOfOrigin(Converter.longToCalendar((Long) goalSnapShot.get("date start")));
            if (goalSnapShot.get("date broken") != null) {
                goalToAdd.setBrokenDate(Converter.longToCalendar((Long) goalSnapShot.get("date broken")));
            }
            goalToAdd.setDateDesiredFinish(Converter.longToCalendar((Long) goalSnapShot.get("date end")));
            goalToAdd.setPercentProgress(((Long) goalSnapShot.get("percent progress")).intValue());
            goalToAdd.setRemainingCheckpoints(((Long) goalSnapShot.get("remaining checkpoints")).intValue());
            goalToAdd.setTotalCheckpoints(((Long) goalSnapShot.get("total checkpoints")).intValue());
            currentUser.getGoals().put((String) goalSnapShot.get("id"), goalToAdd);
        } else {
            StreakSustainerGoal goalToAdd = new StreakSustainerGoal();
            goalToAdd.setId((String) goalSnapShot.get("id"));
            goalToAdd.setIsTerminated((boolean) goalSnapShot.get("isTerminated"));
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
            currentUser.getGoals().put((String) goalSnapShot.get("id"), goalToAdd);
        }
    }

    /**
     * Gets password object from the hashmap returned from firebase
     *
     * @param accountSnapshot firebase snapshot of data
     */
    public static Password retrieveUserPasswordToLocal(HashMap accountSnapshot) {
        HashMap<String, String> map = (HashMap<String, String>) accountSnapshot.get("password object");
        return new Password(map.get("password"), map.get("date last changed"));
    }


    /**
     * Remove pending goal notification from Firebase
     *
     * @param notificationId Id of the notification to remove
     */
    public static void removePendingGoalNotificationFromDB(String notificationId) {
        Firebase firebaseRef = new Firebase(GoalTrackerApplication.FIREBASE_URL)
                .child("accounts")
                .child(currentUser.getId())
                .child("pending goal notifications")
                .child(notificationId);
        firebaseRef.removeValue();
    }

    /**
     * Remove goal from Firebase and GAE. Makes sure to remove associated pending
     * notifications as well
     *
     * @param goalId Id of the goal to remove
     */
    public static void removeGoalFromDB(String goalId) {
        removeAssociatedPendingGoalNotificationsFromDB(goalId);
        Firebase firebaseRef = new Firebase(GoalTrackerApplication.FIREBASE_URL)
                .child("accounts")
                .child(currentUser.getId())
                .child("goals")
                .child(goalId);
        firebaseRef.removeValue();
    }

    /**
     * Remove all pending notification that share the same goal parent
     *
     * @param goalId Id of the goal to remove pending notification associated with it
     */
    public static void removeAssociatedPendingGoalNotificationsFromDB(final String goalId) {
        final Firebase rootFirebaseRef = new Firebase(GoalTrackerApplication.FIREBASE_URL)
                .child("accounts")
                .child(currentUser.getId())
                .child("pending goal notifications");
        com.firebase.client.Query queryRef = rootFirebaseRef.orderByChild("associatedGoalId").equalTo(goalId);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, HashMap<String, Object>> notifications =
                            (HashMap<String, HashMap<String, Object>>) snapshot.getValue();
                    for (String notificationKey : notifications.keySet()) {
                        rootFirebaseRef.child(notificationKey).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private static void addListenerToGoals() {
        Firebase goalsRef = new Firebase(GoalTrackerApplication.FIREBASE_URL).child("accounts").child(currentUser.getId()).child("goals");
        goalsRef.addChildEventListener(new FirebaseGoalListener());
    }
}
