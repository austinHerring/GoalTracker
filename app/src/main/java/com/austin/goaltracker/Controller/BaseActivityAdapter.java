package com.austin.goaltracker.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.austin.goaltracker.R;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Adapt the spinner for the base activities.
 */
public class BaseActivityAdapter extends ArrayAdapter<String>
{
    private Context context;
    private String[] activities;

    public BaseActivityAdapter(Context context, int textViewResourceId, String[] objects)
    {
        super(context, textViewResourceId, objects);
        this.context = context;
        activities = objects;
    }


    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.layout_spinner, parent, false);
        TextView label=  (TextView)row.findViewById(R.id.baseList);
        if (label != null) {
            label.setText(activities[position]);
        }
        return row;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.layout_spinner_dropdown, parent, false);
        TextView label = (TextView)row.findViewById(R.id.baseList);
        ImageView icon = (ImageView)row.findViewById(R.id.icon);
        String current = activities[position];
        int imageID = 0;

        label.setText(current);
        if (current.equals(context.toString())) {
            label.setTextColor(context.getResources().getColor(R.color.spinner_in_use));
        }

        if (current.equals("Goals")) {
            imageID = (current.equals(context.toString()))
                    ? R.drawable.goals_icon_current : R.drawable.goals_icon;
        } else if (current.equals("Friends")) {
            imageID = (current.equals(context.toString()))
                    ? R.drawable.friends_icon_current : R.drawable.friends_icon;
        } else if (current.equals("Messages")) {
            imageID = (current.equals(context.toString()))
            ? R.drawable.messages_icon_current : R.drawable.messages_icon;
        } else if (current.equals("History")) {
            imageID = (current.equals(context.toString()))
            ? R.drawable.history_icon_current : R.drawable.history_icon;
        }
        icon.setImageResource(imageID);

        return row;
    }

}
