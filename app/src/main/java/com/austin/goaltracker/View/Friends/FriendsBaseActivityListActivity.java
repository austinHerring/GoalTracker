package com.austin.goaltracker.View.Friends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


import com.austin.goaltracker.R;
import com.austin.goaltracker.View.FriendsBaseActivityDetailFragment;
import com.austin.goaltracker.View.FriendsBaseActivityListFragment;

import java.util.ArrayList;

/**
 *
 * to listen for item selections.
 */
public class FriendsBaseActivityListActivity extends FragmentActivity
        implements FriendsBaseActivityListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendsbaseactivity_list);

        if (findViewById(R.id.friendsbaseactivity_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((FriendsBaseActivityListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.friendsbaseactivity_list))
                    .setActivateOnItemClick(true);
        }
//        Spinner spinner = (Spinner) findViewById(R.id.spinnerSelectBase);
//        // Spinner Drop down elements
//        ArrayList<String> categories = new ArrayList<>();
//        categories.add("Friends");
//        categories.add("Goals");
//
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,categories) {
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View v = super.getView(position, convertView, parent);
//                ((TextView) v).setTextSize(16);
//                ((TextView) v).setGravity(Gravity.CENTER);
//                return v;
//            }
//            public View getDropDownView(int position, View convertView, ViewGroup parent) {
//                View v = super.getDropDownView(position, convertView, parent);
//                ((TextView) v).setGravity(Gravity.CENTER);
//                return v;
//            }
//        };
//        // Drop down layout style - list view with radio button
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // attaching data adapter to spinner
//        spinner.setAdapter(dataAdapter);

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link FriendsBaseActivityListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(FriendsBaseActivityDetailFragment.ARG_ITEM_ID, id);
            FriendsBaseActivityDetailFragment fragment = new FriendsBaseActivityDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.friendsbaseactivity_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, FriendsBaseActivityDetailActivity.class);
            detailIntent.putExtra(FriendsBaseActivityDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_goals_base, menu);
        return true;
    }
}
