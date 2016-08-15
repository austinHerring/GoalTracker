package com.austin.goaltracker.Controller;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import com.austin.goaltracker.Controller.Adapters.GetAccountListAdapter;
import com.austin.goaltracker.Controller.Generators.PasswordGenerator;
import com.austin.goaltracker.Controller.Services.EmailDispatchService;
import com.austin.goaltracker.Model.Account;

import com.austin.goaltracker.Model.Goal.CountdownCompleterGoal;
import com.austin.goaltracker.Model.RealTime.GetAccount;
import com.austin.goaltracker.Model.RealTime.GetAccounts;
import com.austin.goaltracker.Model.RealTime.GetGoal;
import com.austin.goaltracker.Model.RealTime.GetGoals;
import com.austin.goaltracker.Model.Goal.Goal;
import com.austin.goaltracker.Model.Enums.GoalClassification;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.Model.Enums.IncrementType;
import com.austin.goaltracker.Model.Mail.NewMemberEmail;
import com.austin.goaltracker.Model.Password;
import com.austin.goaltracker.Model.Goal.StreakSustainerGoal;
import com.austin.goaltracker.Model.Enums.ToastType;
import com.austin.goaltracker.Model.RealTime.HistoryArtifact;
import com.austin.goaltracker.R;
import com.austin.goaltracker.View.Friends.FriendsAddActivity;
import com.austin.goaltracker.View.Friends.FriendsBaseActivity;
import com.austin.goaltracker.View.Friends.FriendsOfFriendsDetailActivity;
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
                        (String)userAccount.get("nameFirst"),
                        (String)userAccount.get("nameLast"),
                        (String)userAccount.get("username"),
                        retrieveUserPasswordToLocal(userAccount),
                        (String)userAccount.get("email"));

                if (!password.equals(accountToLoad.getPassword())) {
                    startActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    ToastDisplayer.displayHint("Invalid Password", ToastType.FAILURE, startActivity);
                    return;
                }

                accountToLoad.setId((String)userAccount.get("id"));
                accountToLoad.setTotalFriends((Long)userAccount.get("totalFriends"));
                accountToLoad.setTotalGoalsStarted((Long)userAccount.get("totalGoalsStarted"));
                accountToLoad.setTotalGoalsCompleted((Long)userAccount.get("totalGoalsCompleted"));
                accountToLoad.setLongestStreak((Long)userAccount.get("longestStreak"));

                if (userAccount.get("pictureData") != null) {
                    accountToLoad.setPictureData((String) userAccount.get("pictureData"));
                }

                HashMap<String, String> friends = (HashMap<String, String>) userAccount.get("friends");
                if (friends == null) {
                    accountToLoad.setFriends(new HashMap<String, String>());
                } else {
                    accountToLoad.setFriends(friends);
                }

                currentUser = accountToLoad;

                addListenerToGoals(); // Handles adding goals
                GAEDatastoreController.registerdeviceForCurrentUser();
                startActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
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
        Firebase accountRef = new Firebase(GoalTrackerApplication.FIREBASE_URL)
                .child("accounts").child(accountId);
        Firebase accountGoalsRef = accountRef.child("goals");
        String goalId = goal.getId();
        Firebase row;
        if (goalId == null) {
            row = accountGoalsRef.push();
            goal.setId(row.getKey());
        } else {
            row = accountGoalsRef.child(goalId);
        }
        HashMap<String,Object> newGoalAsEntry = generateGoalMappingForDB(goal);
        row.setValue(newGoalAsEntry);
        accountRef.child("totalGoalsStarted").setValue(Util.currentUser.getTotalGoalsStarted());
        accountRef.child("totalGoalsCompleted").setValue(Util.currentUser.getTotalGoalsCompleted());
        accountRef.child("longestStreak").setValue(Util.currentUser.getLongestStreak());
    }

    /**
     * Updates a goal for an account on the database.
     *
     * @param accountId account update a goal for
     * @param pictureData base 64 data for profile picture
     */
    public static void updateAccountPictureOnDB(String accountId, String pictureData) throws FirebaseException {
        Firebase accountRef = new Firebase(GoalTrackerApplication.FIREBASE_URL)
                .child("accounts").child(accountId);
        accountRef.child("pictureData").setValue(pictureData);
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
     * Updates the friends for an account
     *
     * @param accountId the account to update
     * @param friendsList the new friends list
     * @param totalFriends the number of Friends
     */
    public static void updateFriendsForAccountOnDB(String accountId, HashMap friendsList, long totalFriends) throws FirebaseException {
        Firebase accountRef = new Firebase(GoalTrackerApplication.FIREBASE_URL)
                .child("accounts").child(accountId);
        accountRef.child("friends").setValue(friendsList);
        accountRef.child("totalFriends").setValue(totalFriends);
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
        map.put("nameFirst", account.getNameFirst());
        map.put("nameLast", account.getNameLast());
        map.put("username", account.getUsername());
        map.put("password", generatePasswordMappingForDB(account.getPasswordObject()));
        map.put("email", account.getEmail());
        map.put("totalFriends", account.getTotalFriends());
        map.put("totalGoalsStarted", account.getTotalGoalsStarted());
        map.put("totalGoalsCompleted", account.getTotalGoalsCompleted());
        map.put("longestStreak", account.getLongestStreak());
        map.put("pictureData", account.getPictureData());
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
        map.put("goalName", goal.getGoalName());
        map.put("isTerminated", goal.isTerminated());
        map.put("dateOfOrigin", goal.getDateOfOrigin());
        map.put("dateBroken", goal.getBrokenDate());
        map.put("task", goal.getTask());
        map.put("incrementType", goal.getIncrementType());
        map.put("cronJobKey", goal.getCronJobKey());
        if (goal.classification().equals(GoalClassification.COUNTDOWN)) {
            map.put("dateDesiredFinish", ((CountdownCompleterGoal) goal).getDateDesiredFinish());
            map.put("percentProgress", ((CountdownCompleterGoal) goal).getPercentProgress());
            map.put("remainingCheckpoints", ((CountdownCompleterGoal) goal).getRemainingCheckpoints());
            map.put("totalCheckpoints", ((CountdownCompleterGoal) goal).getTotalCheckpoints());
        } else {
            map.put("streak", ((StreakSustainerGoal) goal).getStreak());
            map.put("cheatNumber", ((StreakSustainerGoal) goal).getCheatNumber());
            map.put("cheatsRemaining", ((StreakSustainerGoal) goal).getCheatsRemaining());
        }
        return map;
    }

    /**
     * Create a History Artifact and stores on Firebase as JSON
     *
     * @param type History artifact -> FRIEND or GOAL.
     * @param association Id of associated obect.
     * @param date time of occurrence as long.
     * @param positiveAction used to track new records with goals / friend additions with friends.
     * @param getAccount optional parameter to pass account information through.
     */
    public static void addHistoryArtifactOnDB(
            String type,
            String association,
            long date,
            boolean positiveAction,
            GetAccount getAccount) throws FirebaseException
    {
        Firebase firebaseRef = new Firebase(GoalTrackerApplication.FIREBASE_URL);
        Firebase accountHistoryRef = firebaseRef
                .child("accounts")
                .child(currentUser.getId())
                .child("history");
        Firebase row = accountHistoryRef.push();
        HistoryArtifact artifact = new HistoryArtifact(association, type, date, positiveAction);
        if (getAccount != null) {
            artifact.setUsername(getAccount.getUsername());
            artifact.setUserPic(getAccount.getPictureData());
        }
        artifact.setId(row.getKey());
        row.setValue(artifact, new Firebase.CompletionListener() {
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
     * Accounts are represented as hashmaps to access particular data
     *
     * @param password The password to generate a HashMap for.
     * @return The HashMap.
     */
    private static HashMap<String, Object> generatePasswordMappingForDB(Password password) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("password", password.toPasswordString());
        map.put("dateLastChanged", password.toDateString());
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
            goalToAdd.setName((String) goalSnapShot.get("goalName"));
            goalToAdd.setTask((String) goalSnapShot.get("task"));
            goalToAdd.setCronJobKey((String) goalSnapShot.get("cronJobKey"));
            goalToAdd.setIncrementType(Converter.stringToFrequency((String) goalSnapShot.get("incrementType")));
            goalToAdd.setDateOfOrigin(Converter.longToCalendar((Long) goalSnapShot.get("dateOfOrigin")));
            if (goalSnapShot.get("dateBroken") != null) {
                goalToAdd.setBrokenDate(Converter.longToCalendar((Long) goalSnapShot.get("dateBroken")));
            }
            goalToAdd.setDateDesiredFinish(Converter.longToCalendar((Long) goalSnapShot.get("dateDesiredFinish")));
            goalToAdd.setPercentProgress(((Long) goalSnapShot.get("percentProgress")).intValue());
            goalToAdd.setRemainingCheckpoints(((Long) goalSnapShot.get("remainingCheckpoints")).intValue());
            goalToAdd.setTotalCheckpoints(((Long) goalSnapShot.get("totalCheckpoints")).intValue());
            currentUser.getGoals().put((String) goalSnapShot.get("id"), goalToAdd);
        } else {
            StreakSustainerGoal goalToAdd = new StreakSustainerGoal();
            goalToAdd.setId((String) goalSnapShot.get("id"));
            goalToAdd.setIsTerminated((boolean) goalSnapShot.get("isTerminated"));
            goalToAdd.setName((String) goalSnapShot.get("goalName"));
            goalToAdd.setTask((String) goalSnapShot.get("task"));
            goalToAdd.setCronJobKey((String) goalSnapShot.get("cronJobKey"));
            goalToAdd.setIncrementType(Converter.stringToFrequency((String) goalSnapShot.get("incrementType")));
            goalToAdd.setDateOfOrigin(Converter.longToCalendar((Long) goalSnapShot.get("dateOfOrigin")));
            if (goalSnapShot.get("dateBroken") != null) {
                goalToAdd.setBrokenDate(Converter.longToCalendar((Long) goalSnapShot.get("dateBroken")));
            }
            goalToAdd.setCheatNumber(((Long) goalSnapShot.get("cheatNumber")).intValue());
            goalToAdd.setCheatsRemaining(((Long) goalSnapShot.get("cheatsRemaining")).intValue());
            goalToAdd.setStreak(((Long) goalSnapShot.get("streak")).intValue());
            currentUser.getGoals().put((String) goalSnapShot.get("id"), goalToAdd);
        }
    }

    /**
     * Gets password object from the hashmap returned from firebase
     *
     * @param accountSnapshot firebase snapshot of data
     */
    public static Password retrieveUserPasswordToLocal(HashMap accountSnapshot) {
        HashMap<String, String> map = (HashMap<String, String>) accountSnapshot.get("password");
        return new Password(map.get("password"), map.get("dateLastChanged"));
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

    /**
     * get accounts from database and hydrate view
     *
     * @param activity the activity to get the accounts for
     * @param queryFriends users to display in the list
     * @param isFriendsOfFriends this controls permissions to view friends of friends
     */
    public static void GetAccounts(final Activity activity, final HashMap<String, String> queryFriends, final boolean isFriendsOfFriends) {
        final HashMap<String, String> currentUserFriends = Util.currentUser.getFriends();
        Firebase firebaseAccounts = new Firebase(GoalTrackerApplication.FIREBASE_URL).child("accounts");
        firebaseAccounts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists())
                    return;
                HashMap<String, HashMap<String, String>> users =
                        ( HashMap<String, HashMap<String, String>>) snapshot.getValue();
                if (users == null) {
                    return;
                }

                GetAccounts getAccounts = new GetAccounts();
                String id;
                GetAccountListAdapter listAdapter;

                if (queryFriends == null) { // Add Friends activity
                    for (HashMap accountSnapshot : users.values()) {
                        id = (String) accountSnapshot.get("id");
                        if (!currentUser.getId().equals(id)
                                && (currentUserFriends == null || !currentUserFriends.containsKey(id))) {
                            getAccounts.addAccount(retrieveUserAccount(accountSnapshot));
                        }
                    }
                    ListView listView = (ListView) activity.findViewById(R.id.list_of_users);
                    listAdapter = new GetAccountListAdapter(activity, R.layout.layout_user_row, getAccounts, false, isFriendsOfFriends);
                    ((FriendsAddActivity) activity).ListAdapter = listAdapter;
                    listView.setAdapter(listAdapter);

                }

                if (queryFriends != null) {
                    for (HashMap accountSnapshot : users.values()) {
                        id = (String)accountSnapshot.get("id");
                        if (!currentUser.getId().equals(id) && queryFriends.containsKey(id)) {
                            getAccounts.addAccount(retrieveUserAccount(accountSnapshot));
                        }
                    }
                    ListView listView = (ListView) activity.findViewById(R.id.list_of_friend_users);
                    listAdapter = new GetAccountListAdapter(activity, R.layout.layout_user_friend_row, getAccounts, true, isFriendsOfFriends);

                    if (activity.toString().equals("Friends")) {
                        ((FriendsBaseActivity) activity).ListAdapter = listAdapter;
                    } else {
                        ((FriendsOfFriendsDetailActivity) activity).ListAdapter = listAdapter;
                    }

                    listView.setAdapter(listAdapter);
                }

                activity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private static GetAccount retrieveUserAccount(final HashMap accountSnapshot) {
        GetAccount getAccount = new GetAccount();
        getAccount.setId((String)accountSnapshot.get("id"));
        getAccount.setTotalGoalsStarted(((Long) accountSnapshot.get("totalGoalsStarted")).intValue());
        getAccount.setTotalGoalsCompleted(((Long) accountSnapshot.get("totalGoalsCompleted")).intValue());
        getAccount.setLongestStreak(((Long) accountSnapshot.get("longestStreak")).intValue());
        getAccount.setTotalFriends(((Long) accountSnapshot.get("totalFriends")).intValue());
        getAccount.setNameFirst((String)accountSnapshot.get("nameFirst"));
        getAccount.setNameLast((String)accountSnapshot.get("nameLast"));
        getAccount.setUsername((String)accountSnapshot.get("username"));

        if (accountSnapshot.get("goals") != null) {
            getAccount.setGetGoals(retrieveUserGoals(accountSnapshot));
        }

        if (accountSnapshot.get("pictureData") != null) {
            getAccount.setPictureData((String) accountSnapshot.get("pictureData"));
        }

        if (accountSnapshot.get("friends") != null) {
            getAccount.setFriends((HashMap < String, String>) accountSnapshot.get("friends"));
        }
        return getAccount;
    }

    private static GetGoals retrieveUserGoals(HashMap accountSnapshot) {
        GetGoals getGoals = new GetGoals();
        HashMap<String, HashMap<String,Object>> goalsSnapShots  = (HashMap < String, HashMap < String, Object >>) accountSnapshot.get("goals");
        for(HashMap<String, Object> goalSnapShot : goalsSnapShots.values()) {
            GetGoal getGoal = new GetGoal();
            getGoal.setId((String) goalSnapShot.get("id"));
            getGoal.setGoalName((String) goalSnapShot.get("goalName"));
            getGoal.setTask((String) goalSnapShot.get("task"));
            getGoal.setIncrementType(IncrementType.valueOf((String) goalSnapShot.get("incrementType")));
            getGoal.setIsTerminated((boolean) goalSnapShot.get("isTerminated"));
            getGoal.setDateOfOrigin(Converter.longToCalendar((Long) goalSnapShot.get("dateOfOrigin")));

            if (goalSnapShot.get("dateBroken") != null) {
                getGoal.setDateBroken(Converter.longToCalendar((Long) goalSnapShot.get("dateBroken")));
            }

            GoalClassification classification = GoalClassification.valueOf((String) goalSnapShot.get("classification"));
            getGoal.setClassification(classification);

            if (classification == GoalClassification.COUNTDOWN) {
                getGoal.setDateDesiredFinish(Converter.longToCalendar((Long) goalSnapShot.get("dateDesiredFinish")));
                getGoal.setPercentProgress(((Long) goalSnapShot.get("percentProgress")).intValue());
            } else {
                getGoal.setStreak(((Long) goalSnapShot.get("streak")).intValue());
            }
            getGoals.AddGoal(getGoal);
        }
        return getGoals;
    }
}
