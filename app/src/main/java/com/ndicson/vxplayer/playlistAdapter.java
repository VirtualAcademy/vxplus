package com.ndicson.vxplayer;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class playlistAdapter extends ArrayAdapter<Video> {
    private int Srcs;

    public playlistAdapter(Context context, ArrayList<Video> videos, int srcs) {
        super(context, 0, videos);
        this.Srcs = srcs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Video video = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(Srcs, parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.videoTitle);
//        title.setWidth(200);

        // Populate the data into the template view using the data object
        title.setText(video.getTitle());

        // Return the completed view to render on screen
        return convertView;
    }
}