package com.austin.goaltracker.Controller;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.austin.goaltracker.Model.GetAccount;
import com.austin.goaltracker.Model.GetAccounts;
import com.austin.goaltracker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * List adapter for users. It adapst botha friends list and un-friended users in the community
 */
public class UserListAdapter extends ArrayAdapter<GetAccount> implements Filterable {
    Activity activity;
    private GetAccounts getAccounts;
    private LayoutInflater inflater;
    private int mLayout;
    private boolean isFriendsList;

    public UserListAdapter(Activity activity, int textViewResourceId, GetAccounts getAccounts, boolean isFriendsList) {
        super(activity, textViewResourceId, getAccounts.Accounts);
        try {
            this.activity = activity;
            this.inflater = activity.getLayoutInflater();
            this.getAccounts = getAccounts;
            this.mLayout = textViewResourceId;
            this.isFriendsList = isFriendsList;

        } catch (Exception e) {
            Log.e("UserListAdapter", "Error constructing");
        }
    }

    @Override
    public int getCount() {
        return getAccounts.Accounts.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView user_name;
        public TextView number_goals;
        public ImageView profile_picture;
        public Button add_friend;
        public Button add_friend_disabled;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View v = view;
        final ViewHolder holder;
        final GetAccount getAccount = getAccounts.Accounts.get(position);

        if (view == null) {
            v = inflater.inflate(mLayout, null);
            holder = new ViewHolder();

            holder.user_name = (TextView) v.findViewById(R.id.userName);
            holder.number_goals = (TextView) v.findViewById(R.id.numberOfGoals);
            holder.profile_picture = (ImageView) v.findViewById(R.id.profilePicture);

            if (!isFriendsList) {
                holder.add_friend = (Button) v.findViewById(R.id.addFriend);
                holder.add_friend_disabled = (Button) v.findViewById(R.id.friendAdded);
            }

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        Drawable profilePic = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.goals_icon_current, null);
        String fullName = getAccount.getNameFirst() + " " + getAccount.getNameLast();
        String goalCount = "Goals: " + getAccount.getTotalGoalsStarted();

        holder.profile_picture.setBackground(profilePic);
        holder.user_name.setText(fullName);
        holder.number_goals.setText(goalCount);

        if (!isFriendsList) {
            holder.add_friend.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Util.currentUser.addFriend(getAccount.getId());
                    holder.add_friend.setVisibility(View.GONE);
                    holder.add_friend_disabled.setVisibility(View.VISIBLE);
                }
            });
        }

        return v;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<GetAccount> FilteredArrList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    results.count = getAccounts.Accounts.size();
                    results.values = getAccounts.Accounts;

                } else {
                    constraint = constraint.toString();
                    for (int i = 0; i < getAccounts.Accounts.size(); i++) {
                        GetAccount data = getAccounts.Accounts.get(i);
                        if (!(data.getNameFirst().equals(constraint))) {
                            FilteredArrList.add(data);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }
}
