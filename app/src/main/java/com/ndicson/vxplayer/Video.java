package com.ndicson.vxplayer;

public class Video {

    public static final String LOCALDATA = "vdatalocal.json";
    public static final String ONLINEDATA = "videodata.json";

    public static final String TABLE_NAME = "video";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_LOCATION = "location";


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TITLE + " TEXT,"
                    + COLUMN_LINK + " CHAR,"
                    + COLUMN_IMAGE + " CHAR,"
                    + COLUMN_LOCATION + " TEXT"
                    + ")";

    int id;
    String title;
    String image;
    String link;
    String location;

    public Video() {
    }

    public Video(int id, String title, String image, String link, String location){
        this.id = id;
        this.title = title;
        this.image = image;
        this.link = link;
        this.location = location;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
