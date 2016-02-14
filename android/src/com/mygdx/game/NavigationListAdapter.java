package com.mygdx.game;


import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by monty on 1/16/2016.
 * Static Adapter for navigation list elements
 */
public class NavigationListAdapter extends ArrayAdapter<String> {

    private FragmentActivity activity;
    private static String [] items = {
        "Start Spotting",
        "Select Image",
        "Convert to Text",
        "Settings"
    };
    private static Integer [] imageId = {
        R.drawable.process,
        R.drawable.browser,
        R.drawable.process,
        R.drawable.process
    };
    public NavigationListAdapter(FragmentActivity activity) {
        super(activity.getApplicationContext(), R.layout.navigation_list, items);
        this.activity = activity;

    }
    public View getView(int position,View view,ViewGroup parent) {

        LayoutInflater inflater=activity.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.navigation_list, null ,false);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.text_element);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon_element);

        txtTitle.setText(items[position]);
        imageView.setImageResource(imageId[position]);
        return rowView;
    };

}
