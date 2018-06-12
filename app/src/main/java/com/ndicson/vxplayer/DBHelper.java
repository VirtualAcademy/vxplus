package com.ndicson.vxplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "VideosOnline.db";
    public static final String VIDEOONLINE_TABLE_NAME = "VideoOnline";
    public static final String VIDEOONLINE_COLUMN_ID = "id";
    public static final String VIDEOONLINE_COLUMN_NAME = "Title";
    public static final String VIDEOONLINE_COLUMN_URI = "uri";
    public static final String VIDEOONLINE_COLUMN_IMAGE = "image";
    public static final String VIDEOONLINE_COLUMN_LOCATION = "location";
    private HashMap hp;

    private String location = "Online";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table videoonline " +
                        "(id integer primary key, name text,location text,uri text, image text)"
        );

        db.execSQL("CREATE TABLE " + VIDEOONLINE_TABLE_NAME + " ("
                + VIDEOONLINE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + VIDEOONLINE_COLUMN_NAME + " TEXT, "
                + VIDEOONLINE_COLUMN_LOCATION + " TEXT DEFAULT 'Online', "
                + VIDEOONLINE_COLUMN_URI + " TEXT,"
                + VIDEOONLINE_COLUMN_IMAGE + "TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS videoonline");
        onCreate(db);
    }

    public boolean insertVideo (String name, String location, String uri, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("location", location);
        contentValues.put("uri", uri);
        contentValues.put("image", image);
        db.insert("videoonline", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from videoonline where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, VIDEOONLINE_TABLE_NAME);
        return numRows;
    }

    public boolean updateVideo (Integer id, String name, String location, String uri, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("location", location);
        contentValues.put("uri", uri);
        contentValues.put("image", image);
        db.update("videoonline", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteVideo (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("videoonline",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllVideos() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from videoonline", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(VIDEOONLINE_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }
}