package com.ndicson.vxplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class VideoPlayerActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControlListener, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnCompletionListener {

    private final static String TAG = "MainActivity";
    ResizeSurfaceView mVideoSurface;
    MediaPlayer mMediaPlayer;
    VideoControllerView controller;
    private int mVideoWidth;
    private int mVideoHeight;
    private View mContentView;
    private View mLoadingView;
    private boolean mIsComplete;
    private int currentVideoIndex = 0;
    private VideoControllerView.MediaPlayerControlListener mMediaPlayerControlListener;

    private ArrayList<HashMap<String, String>> videoList = new ArrayList<HashMap<String, String>>();
    private List<Video> AllVideosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        mVideoSurface = (ResizeSurfaceView) findViewById(R.id.videoSurface);
        mContentView = findViewById(R.id.video_container);
        mLoadingView = findViewById(R.id.loading);
        SurfaceHolder videoHolder = mVideoSurface.getHolder();
        videoHolder.addCallback(this);

        intController("");
//        controller.setMediaPlayerControlListener(mMediaPlayerControlListener);
        controller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String i = String.valueOf(v.getId());
                Toast.makeText(getApplicationContext(),i,Toast.LENGTH_LONG);
            }
        });
        //Video list
        VideoManager videoManager = new VideoManager();
        videoList = videoManager.getPlayList();
        AllVideosList = DBHelper.getInstance(getApplicationContext()).getAllVideos();
        HashMap<String, String> video = videoList.get(currentVideoIndex);

        Intent intent = getIntent();

        if (intent.hasExtra("currentIndex")){
            int indexChoice = (int) intent.getExtras().get("currentIndex");
            String indexTitle = (String) intent.getExtras().get("currentTitle");
            String indexUri = (String) intent.getExtras().get("currentUri");
            playVideo(indexChoice, indexTitle,indexUri);
        }else {
            Video videofromlist = AllVideosList.get(currentVideoIndex);
            int indexChoice = (int) videofromlist.getId();
            String indexTitle = (String) videofromlist.getTitle();
            String indexUri = (String) videofromlist.getLink();
            playVideo(indexChoice, indexTitle, indexUri);
        }


        mVideoSurface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                controller.toggleControllerView();
                return false;
            }
        });
    }

    @Override
    public void onRestart() {

        Intent intent = getIntent();
        int indexChoice = (int) intent.getExtras().get("currentIndex");
        String indexTitle = (String) intent.getExtras().get("currentTitle");
        String indexUri = (String) intent.getExtras().get("currentUri");
        playVideo(indexChoice, indexTitle,indexUri);
        super.onRestart();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        controller.show();
        return false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        mVideoHeight = mp.getVideoHeight();
        mVideoWidth = mp.getVideoWidth();
        if (mVideoHeight > 0 && mVideoWidth > 0)
            mVideoSurface.adjustSize(mContentView.getWidth(), mContentView.getHeight(), mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mVideoWidth > 0 && mVideoHeight > 0)
            mVideoSurface.adjustSize(getDeviceWidth(this),getDeviceHeight(this),mVideoSurface.getWidth(), mVideoSurface.getHeight());
    }

    private void resetPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public static int getDeviceWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.widthPixels;
    }

    public static int getDeviceHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.heightPixels;
    }


    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        resetPlayer();
    }
// End SurfaceHolder.Callback


    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        //setup video controller view
        mLoadingView.setVisibility(View.GONE);
        mVideoSurface.setVisibility(View.VISIBLE);
        mMediaPlayer.start();
        mIsComplete = false;
    }
// End MediaPlayer.OnPreparedListener

    /**
     * Implement VideoMediaController.MediaPlayerControl
     */

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(null != mMediaPlayer)
            return mMediaPlayer.getCurrentPosition();
        else
            return 0;
    }

    @Override
    public int getDuration() {
        if(null != mMediaPlayer)
            return mMediaPlayer.getDuration();
        else
            return 0;
    }

    @Override
    public boolean isPlaying() {
        if(null != mMediaPlayer)
            return mMediaPlayer.isPlaying();
        else
            return false;
    }

    @Override
    public boolean isComplete() {
        return mIsComplete;
    }

    @Override
    public void pause() {
        if(null != mMediaPlayer) {
            mMediaPlayer.pause();
        }

    }

    @Override
    public void seekTo(int i) {
        if(null != mMediaPlayer) {
            mMediaPlayer.seekTo(i);
        }
    }

    @Override
    public void start() {
        if(null != mMediaPlayer) {
            mMediaPlayer.start();
            mIsComplete = false;
        }
    }

    @Override
    public boolean isFullScreen() {
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? true : false;
    }

    @Override
    public void toggleFullScreen() {
        if(isFullScreen()){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }



    @Override
    public void exit() {
        resetPlayer();
        finish();
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        mIsComplete = true;
    }

    // End VideoMediaController.MediaPlayerControl



    // initialise controller
    private void intController(String videoTitle){

        controller = new VideoControllerView.Builder(this, this)
                .withVideoTitle(videoTitle)
                .withVideoSurfaceView(mVideoSurface)//to enable toggle display controller view
                .canControlBrightness(true)
                .canControlVolume(true)
                .canSeekVideo(true)
                .exitIcon(R.drawable.video_top_back)
                .pauseIcon(R.drawable.btn_pause)
                .playIcon(R.drawable.btn_play)
                .nextIcon(R.drawable.btn_next)
                .shrinkIcon(R.drawable.ic_media_fullscreen_shrink)
                .stretchIcon(R.drawable.ic_media_fullscreen_stretch)
                .build((FrameLayout) findViewById(R.id.videoSurfaceContainer));//layout container that hold video play view

        mLoadingView.setVisibility(View.VISIBLE);
    }

    /**
     * Function to play a song
     * @param currentVideoIndex - index of song
     * */
    private void playVideo(int currentVideoIndex, String currentTitle, String currentUri) {

//        Video videofromlist = AllVideosList.get(currentVideoIndex);
        Toast.makeText(getApplicationContext(),"playing id: "+currentVideoIndex+" and path: "+currentUri,Toast.LENGTH_SHORT).show();

        // Play video
        try {

            mMediaPlayer = new MediaPlayer();

            // Displaying Video title
//            String videoTitle = videoList.get(currentVideoIndex).get("videoTitle");
            String vTitle = currentTitle;//videofromlist.getTitle();
            String vUri = currentUri;//videofromlist.getLink();

            controller.setVideoLable(vTitle);
//            controller.getId();

            mMediaPlayer.setOnVideoSizeChangedListener(this);

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(this, Uri.parse(vUri));//"http://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_480p_surround-fix.avi"));//videoList.get(currentVideoIndex).get("videoPath")));
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            controller.setEnabled(true);
            controller.setOnClickListener();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}