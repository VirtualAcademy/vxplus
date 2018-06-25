package com.ndicson.vxplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private HashMap hp;


    public Context mContext;


    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "Videos.db";

    private static DBHelper sInstance;
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;

    }

    // ...

    public static synchronized DBHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        // create videos table
        db.execSQL(Video.CREATE_TABLE);
//        this.populateDB(mContext,Video.LOCALDATA);
//        this.populateDB(mContext,Video.ONLINEDATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Video.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public boolean insertVideo (String name, String location, String uri, String image) {

        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        // `id` will be inserted automatically, no need to add it
        ContentValues contentValues = new ContentValues();
        contentValues.put(Video.COLUMN_TITLE, name);
        contentValues.put(Video.COLUMN_LOCATION, location);
        contentValues.put(Video.COLUMN_LINK, uri);
        contentValues.put(Video.COLUMN_IMAGE, image);

        // insert row
        db.insert(Video.TABLE_NAME, null, contentValues);

        // close db connection
        db.close();

        // return newly inserted true
        return true;
    }

    public Video getVideo(int id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Video.TABLE_NAME,
                new String[]{Video.COLUMN_ID, Video.COLUMN_TITLE,Video.COLUMN_LINK , Video.COLUMN_LOCATION,Video.COLUMN_IMAGE},
                Video.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare video object
        Video video = new Video(
                cursor.getInt(cursor.getColumnIndex(Video.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Video.COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(Video.COLUMN_LINK)),
                cursor.getString(cursor.getColumnIndex(Video.COLUMN_LOCATION)),
                cursor.getString(cursor.getColumnIndex(Video.COLUMN_IMAGE)));

        // close the db connection
        cursor.close();

        return video;
    }



    public List<Video> getGivenVideos(String location) {
        List<Video> videos = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Video.TABLE_NAME + " ORDER BY " +
                Video.COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Video video = new Video();
                // get location
                String lo = cursor.getString(cursor.getColumnIndex(Video.COLUMN_LOCATION));
                if(lo.trim().equals(location)) {// Compare location
                    video.setId(cursor.getInt(cursor.getColumnIndex(Video.COLUMN_ID)));
                    video.setTitle(cursor.getString(cursor.getColumnIndex(Video.COLUMN_TITLE)));
                    video.setLink(cursor.getString(cursor.getColumnIndex(Video.COLUMN_LINK)));
                    video.setLocation(lo);
                    video.setImage(cursor.getString(cursor.getColumnIndex(Video.COLUMN_IMAGE)));
                    videos.add(video);
                }
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return videos list
        return videos;
    }


    public List<Video> getAllVideos() {
        List<Video> videos = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Video.TABLE_NAME + " ORDER BY " +
                Video.COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Video video = new Video();
                video.setId(cursor.getInt(cursor.getColumnIndex(Video.COLUMN_ID)));
                video.setTitle(cursor.getString(cursor.getColumnIndex(Video.COLUMN_TITLE)));
                video.setLink(cursor.getString(cursor.getColumnIndex(Video.COLUMN_LINK)));
                video.setLocation(cursor.getString(cursor.getColumnIndex(Video.COLUMN_LOCATION)));
                video.setImage(cursor.getString(cursor.getColumnIndex(Video.COLUMN_IMAGE)));

                videos.add(video);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return videos list
        return videos;
    }

    public int getVideosCount() {
        String countQuery = "SELECT  * FROM " + Video.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateVideo(Video video) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Video.COLUMN_TITLE, video.getTitle());
        values.put(Video.COLUMN_LINK, video.getLink());
        values.put(Video.COLUMN_LOCATION, video.getLocation());
        values.put(Video.COLUMN_IMAGE, video.getImage());

        // updating row
        return db.update(Video.TABLE_NAME, values, Video.COLUMN_ID + " = ?",
                new String[]{String.valueOf(video.getId())});
    }

    public void deleteVideo(Video video) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Video.TABLE_NAME, Video.COLUMN_ID + " = ?",
                new String[]{String.valueOf(video.getId())});
        db.close();
    }

    private String readFilefromAsset(Context context, String fname) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fname);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    public void populateDB(Context context,String ls) {
        // Variables
        ArrayList<HashMap<String, String>> videoList = new ArrayList<HashMap<String, String>>();

        List<Video> jsonitems;

        if (ls.toLowerCase().equals("local")) {
            String jsondata = readFilefromAsset(context, Video.LOCALDATA);

            // mapping json data to video object list
            jsonitems = new Gson().fromJson(jsondata, new TypeToken<List<Video>>() {
            }.getType());

            VideoManager plm = new VideoManager();
            // get all video from sdcard
            videoList = plm.getPlayList();

            // looping through playlist
            for (int i = 0; i < jsonitems.size(); i++) {
//                Log.i("LOCATION: ", "is local");
                // creating new HashMap
                Video vItem = jsonitems.get(i);
//                HashMap<String, String> video = videoList.get(i);
                // Get info
//                if(video.get("videoTitle").equalsIgnoreCase(vItem.getImage())){
//                    Log.i("Equal","Title match");
//                }

                String fileTitle = vItem.getTitle();
                for (HashMap<String, String> s:videoList
                     ) {

                    Log.i("Title is equal: ",fileTitle+" and "+ s.get("videoTitle"));
                    if (s.get("videoTitle").toLowerCase().trim().startsWith(fileTitle.toLowerCase().trim().substring(0,2)) || fileTitle.toLowerCase().contains(s.get("videoTitle")) ) {
                        vItem.setLink(s.get("videoPath"));
                        Log.i("Exceptional : ", s.get("videoTitle"));
                        if (insertVideo(vItem.getTitle(), vItem.getLocation(), vItem.getLink(), vItem.getImage())) {
                            Log.i("inserted: ", "into db");
                        }
                    }
                }

//                    }
//
//                }
//                String jsonTitle = video.get("videoTitle");
//
//                Log.i("Title is equal: ", Title+"jsonTitle: "+jsonTitle);
//                if (Title.contains(jsonTitle)) {
//                    Log.i("Title is equal: ", Title);
//                    String pathV = video.get("videoPath");
//                    vItem.setLink(pathV);
//                    if (insertVideo(vItem.getTitle(), vItem.getLocation(), vItem.getLink(), vItem.getImage())) {
//                        Log.i("inserted: ", "into db");
//                    }
//                }
            }
        } else {
            String jsondata = readFilefromAsset(context, Video.ONLINEDATA);

            // mapping json data to video object list
            jsonitems = new Gson().fromJson(jsondata, new TypeToken<List<Video>>() {
            }.getType());

            // looping through playlist

            for (int i = 0; i < jsonitems.size(); i++) {
                // creating new HashMap

                Video v = jsonitems.get(i);
                if (insertVideo(v.getTitle(), v.getLocation(), v.getLink(), v.getImage())) {
                    Log.i("database", String.valueOf(i) + " inserted");
//                        Toast.makeText(context, String.valueOf(i)+" inserted "+v.getLocation(), Toast.LENGTH_SHORT).show();
                }
            }
            Toast.makeText(context, "database populated", Toast.LENGTH_SHORT).show();
        }
    }


}