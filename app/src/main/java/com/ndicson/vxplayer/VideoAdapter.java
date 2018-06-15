package com.ndicson.vxplayer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {
    private Context context;
    private List<Video> videoList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, location;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.title);
            location = view.findViewById(R.id.location);
            thumbnail = view.findViewById(R.id.thumbnail);
        }
    }


    public VideoAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.onlinevideo_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Video video = videoList.get(position);
        holder.name.setText(video.getTitle());
        String l = video.location;
        holder.location.setText(video.getLocation());

        if(l.equals("local")){
            Drawable d = loadImage(video.getImage());
            if(d!=null){
                Glide.with(context)
                        .load(d)
                        .into(holder.thumbnail);
            }

        }else {
            Glide.with(context)
                    .load(video.getImage())
                    .into(holder.thumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }


    private Drawable loadImage(String s){
        Drawable d = null;
        try
        {
            // get input stream
            InputStream ims = context.getAssets().open(s);
            // load image as Drawable
            d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            ims .close();
            return d;
        }
        catch(IOException ex)
        {
            return d;
        }
    }
}