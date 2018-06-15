package com.ndicson.vxplayer;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Bruce Too
 * On 7/12/16.
 * At 16:09
 */
public class VideoControllerView extends FrameLayout implements VideoGestureListener, View.OnClickListener {

    private static final String TAG = "VideoControllerView";

    private static final int HANDLER_ANIMATE_OUT = 1;// out animate
    private static final int HANDLER_UPDATE_PROGRESS = 2;//cycle update progress
    private static final long PROGRESS_SEEK = 500;
    private static final long ANIMATE_TIME = 300;

    private boolean mIsShowing;//controller view showing
    private boolean mIsDragging; //is dragging seekBar
    private boolean mCanSeekVideo;
    private boolean mCanControlVolume;
    private boolean mCanControlBrightness;
    private View mRootView; // root view of this
    private SeekBar mSeekBar; //seek bar for video
    private TextView mEndTime, mCurrentTime;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private GestureDetector mGestureDetector;//gesture detector

    private Activity mContext;
    private String mVideoTitle;
    private MediaPlayerControlListener mMediaPlayerControlListener;
    private ViewGroup mAnchorView;
    private SurfaceView mSurfaceView;
    private int indexChoice = 0;

    @DrawableRes
    private int mExitIcon;
    @DrawableRes
    private int mPauseIcon;
    @DrawableRes
    private int mPlayIcon;
    @DrawableRes
    private int mPlayListIcon;
    @DrawableRes
    private int mNextIcon;
    @DrawableRes
    private int mShrinkIcon;
    @DrawableRes
    private int mStretchIcon;

    //top layout
    private View mTopLayout;
    private ImageButton mBackButton;
    private ImageButton mPlayListButton;
    private TextView mTitleText;

    //center layout
    private View mCenterLayout;
    private View plaayListCenterLayout;
    private ImageView mCenterImage;
    private ProgressBar mCenterProgress;
    private float mCurBrightness = -1;
    private int mCurVolume = -1;
    private AudioManager mAudioManager;
    private int mMaxVolume;

    //bottom layout
    private View mBottomLayout;
    private ImageButton mPlayPauseButton;
    private ImageButton mFullscreenButton;

    private Handler mHandler = new ControllerViewHandler(this);

    // Variables
    private ArrayList<HashMap<String, String>> videoList = new ArrayList<HashMap<String, String>>();
    private int currentVideoIndex;
    private ImageButton mNextButton;

    public VideoControllerView(Builder builder) {
        super(builder.context);
        this.mContext = builder.context;
        this.mMediaPlayerControlListener = builder.mediaPlayerControlListener;

        this.mVideoTitle = builder.videoTitle;
        this.mCanSeekVideo = builder.canSeekVideo;
        this.mCanControlVolume = builder.canControlVolume;
        this.mCanControlBrightness = builder.canControlBrightness;
        this.mExitIcon = builder.exitIcon;
        this.mPlayListIcon = builder.playListIcon;
        this.mNextIcon = builder.nextIcon;
        this.mPauseIcon = builder.pauseIcon;
        this.mPlayIcon = builder.playIcon;
        this.mStretchIcon = builder.stretchIcon;
        this.mShrinkIcon = builder.shrinkIcon;
        this.mSurfaceView = builder.surfaceView;

        setAnchorView(builder.anchorView);
        this.mSurfaceView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                toggleControllerView();
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play_pause:
                System.out.print("play button clicked");
        }
    }

    public static class Builder {

        private Activity context;
        private boolean canSeekVideo = true;
        private boolean canControlVolume = true;
        private boolean canControlBrightness = true;
        private String videoTitle = "";
        private MediaPlayerControlListener mediaPlayerControlListener;
        private ViewGroup anchorView;
        private SurfaceView surfaceView;
        @DrawableRes
        private int exitIcon = R.drawable.video_top_back;
        @DrawableRes
        private int pauseIcon = R.drawable.ic_media_pause;
        @DrawableRes
        private int playIcon = R.drawable.ic_media_play;
        @DrawableRes
        private int shrinkIcon = R.drawable.ic_media_fullscreen_shrink;
        @DrawableRes
        private int stretchIcon = R.drawable.ic_media_fullscreen_stretch;
        @DrawableRes
        private int playListIcon = R.drawable.btn_playlist;
        @DrawableRes
        private int nextIcon = R.drawable.img_btn_next;

        //Required
        public Builder(@Nullable Activity context, @Nullable MediaPlayerControlListener mediaControlListener) {
            this.context = context;
            this.mediaPlayerControlListener = mediaControlListener;
        }

        public Builder with(@Nullable Activity context) {
            this.context = context;
            return this;
        }

        public Builder withMediaControlListener(@Nullable MediaPlayerControlListener mediaControlListener) {
            this.mediaPlayerControlListener = mediaControlListener;
            return this;
        }

        //Options
        public Builder withVideoTitle(String videoTitle) {
            this.videoTitle = videoTitle;
            return this;
        }

        public Builder withVideoSurfaceView(@Nullable SurfaceView surfaceView) {
            this.surfaceView = surfaceView;
            return this;
        }

        public Builder exitIcon(@DrawableRes int exitIcon) {
            this.exitIcon = exitIcon;
            return this;
        }

        public Builder playListIcon(@DrawableRes int playListIcon) {
            this.playListIcon = playListIcon;
            return this;
        }

        public Builder pauseIcon(@DrawableRes int pauseIcon) {
            this.pauseIcon = pauseIcon;
            return this;
        }

        public Builder playIcon(@DrawableRes int playIcon) {
            this.playIcon = playIcon;
            return this;
        }

        public Builder nextIcon(@DrawableRes int nextIcon) {
            this.nextIcon = nextIcon;
            return this;
        }

        public Builder shrinkIcon(@DrawableRes int shrinkIcon) {
            this.shrinkIcon = shrinkIcon;
            return this;
        }

        public Builder stretchIcon(@DrawableRes int stretchIcon) {
            this.stretchIcon = stretchIcon;
            return this;
        }

        public Builder canSeekVideo(boolean canSeekVideo) {
            this.canSeekVideo = canSeekVideo;
            return this;
        }

        public Builder canControlVolume(boolean canControlVolume) {
            this.canControlVolume = canControlVolume;
            return this;
        }

        public Builder canControlBrightness(boolean canControlBrightness) {
            this.canControlBrightness = canControlBrightness;
            return this;
        }

        public VideoControllerView build(@Nullable ViewGroup anchorView) {
            this.anchorView = anchorView;
            return new VideoControllerView(this);
        }

    }

    /**
     * Handler prevent leak memory.
     */
    private static class ControllerViewHandler extends Handler {
        private final WeakReference<VideoControllerView> mView;

        ControllerViewHandler(VideoControllerView view) {
            mView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoControllerView view = mView.get();
            if (view == null || view.mMediaPlayerControlListener == null) {
                return;
            }

            int pos;
            switch (msg.what) {
                case HANDLER_ANIMATE_OUT:
                    view.hide();
                    break;
                case HANDLER_UPDATE_PROGRESS://cycle update seek bar progress
                    pos = view.setSeekProgress();
                    if (!view.mIsDragging && view.mIsShowing && view.mMediaPlayerControlListener.isPlaying()) {//just in case
                        //cycle update
                        msg = obtainMessage(HANDLER_UPDATE_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    }

    /**
     * Inflate view from exit xml layout
     *
     * @return the root view of {@link VideoControllerView}
     */
    private View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflate.inflate(R.layout.media_controller, null);
        initControllerView();

        return mRootView;
    }

    /**
     * find all views inside {@link VideoControllerView}
     * and init params
     */
    private void initControllerView() {
        //top layout
        mTopLayout = mRootView.findViewById(R.id.layout_top);
        mPlayListButton = (ImageButton) mRootView.findViewById(R.id.btnPlaylist);
        mPlayListButton.setImageResource(mPlayListIcon);
        if (mPlayListButton != null) {
            mPlayListButton.requestFocus();
            mPlayListButton.setOnClickListener(myViewListener);
        }
        mBackButton = (ImageButton) mRootView.findViewById(R.id.top_back);
        mBackButton.setImageResource(mExitIcon);
        if (mBackButton != null) {
            mBackButton.requestFocus();
            mBackButton.setOnClickListener(mBackListener);
        }

        mTitleText = (TextView) mRootView.findViewById(R.id.top_title);

        //center layout
        mCenterLayout = mRootView.findViewById(R.id.layout_center);
        mCenterLayout.setVisibility(GONE);
        mCenterImage = (ImageView) mRootView.findViewById(R.id.image_center_bg);
        mCenterProgress = (ProgressBar) mRootView.findViewById(R.id.progress_center);
        //playlist
        plaayListCenterLayout = mRootView.findViewById(R.id.playlist_center);
        plaayListCenterLayout.setVisibility(GONE);

        //bottom layout
        mBottomLayout = mRootView.findViewById(R.id.layout_bottom);
        mPlayPauseButton = (ImageButton) mRootView.findViewById(R.id.btn_play_pause);
        if (mPlayPauseButton != null) {
            mPlayPauseButton.requestFocus();
            mPlayPauseButton.setOnClickListener(mPauseListener);
        }

        mFullscreenButton = (ImageButton) mRootView.findViewById(R.id.bottom_fullscreen);
        if (mFullscreenButton != null) {
            mFullscreenButton.requestFocus();
            mFullscreenButton.setOnClickListener(mFullscreenListener);
        }

//        mNextButton = (ImageButton) mRootView.findViewById(R.id.btnNext);
//        if (mNextButton != null) {
//            mNextButton.requestFocus();
//            mNextButton.setOnClickListener(mNextListener);
//        }

        mSeekBar = (SeekBar) mRootView.findViewById(R.id.bottom_seekbar);
        if (mSeekBar != null) {
            mSeekBar.setOnSeekBarChangeListener(mSeekListener);
            mSeekBar.setMax(1000);
        }

        mEndTime = (TextView) mRootView.findViewById(R.id.bottom_time);
        mCurrentTime = (TextView) mRootView.findViewById(R.id.bottom_time_current);

        //init formatter
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    /**
     * show controller view
     */
    private void show() {

        if (!mIsShowing && mAnchorView != null) {

            //add controller view to bottom of the AnchorView
            LayoutParams tlp = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mAnchorView.addView(VideoControllerView.this, tlp);

            ViewAnimator.putOn(mTopLayout)
                    .waitForSize(new ViewAnimator.Listeners.Size() {
                        @Override
                        public void onSize(ViewAnimator viewAnimator) {
                            viewAnimator.animate()
                                    .translationY(-mTopLayout.getHeight(), 0)
                                    .duration(ANIMATE_TIME)
                                    .andAnimate(mBottomLayout)
                                    .translationY(mBottomLayout.getHeight(), 0)
                                    .duration(ANIMATE_TIME)
                                    .start(new ViewAnimator.Listeners.Start() {
                                        @Override
                                        public void onStart() {
                                            mIsShowing = true;
                                            mHandler.sendEmptyMessage(HANDLER_UPDATE_PROGRESS);
                                        }
                                    });
                        }
                    });
        }

        setSeekProgress();
        if (mPlayPauseButton != null) {
            mPlayPauseButton.requestFocus();
        }
        togglePausePlay();
        toggleFullScreen();
        //update progress
        mHandler.sendEmptyMessage(HANDLER_UPDATE_PROGRESS);

    }

    /**
     * toggle {@link VideoControllerView} show or not
     * this can be called when {@link View#onTouchEvent(MotionEvent)} happened
     */
    public void toggleControllerView() {
        if (!isShowing()) {
            show();
            plaayListCenterLayout.setVisibility(GONE);
        } else {
            //animate out controller view
            Message msg = mHandler.obtainMessage(HANDLER_ANIMATE_OUT);
            //remove exist one first
            mHandler.removeMessages(HANDLER_ANIMATE_OUT);
            mHandler.sendMessageDelayed(msg, 100);
        }
    }


    /**
     * if {@link VideoControllerView} is visible
     *
     * @return showing or not
     */
    public boolean isShowing() {
        return mIsShowing;
    }

    /**
     * hide controller view with animation
     * With custom animation
     */
    private void hide() {
        if (mAnchorView == null) {
            return;
        }

        ViewAnimator.putOn(mTopLayout)
                .animate()
                .translationY(-mTopLayout.getHeight())
                .duration(ANIMATE_TIME)

                .andAnimate(mBottomLayout)
                .translationY(mBottomLayout.getHeight())
                .duration(ANIMATE_TIME)
                .end(new ViewAnimator.Listeners.End() {
                    @Override
                    public void onEnd() {
                        mAnchorView.removeView(VideoControllerView.this);
                        mHandler.removeMessages(HANDLER_UPDATE_PROGRESS);
                        mIsShowing = false;
                    }
                });
    }

    /**
     * convert string to time
     *
     * @param timeMs time to be formatted
     * @return 00:00:00
     */
    private String stringToTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * set {@link #mSeekBar} progress
     * and video play time {@link #mCurrentTime}
     *
     * @return current play position
     */
    private int setSeekProgress() {
        if (mMediaPlayerControlListener == null || mIsDragging) {
            return 0;
        }

        int position = mMediaPlayerControlListener.getCurrentPosition();
        int duration = mMediaPlayerControlListener.getDuration();
        if (mSeekBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mSeekBar.setProgress((int) pos);
            }
            //get buffer percentage
            int percent = mMediaPlayerControlListener.getBufferPercentage();
            //set buffer progress
            mSeekBar.setSecondaryProgress(percent * 10);
        }

        if (mEndTime != null)
            mEndTime.setText(stringToTime(duration));
        if (mCurrentTime != null) {
            Log.e(TAG, "position:" + position + " -> duration:" + duration);
            mCurrentTime.setText(stringToTime(position));
            if (mMediaPlayerControlListener.isComplete()) {
                mCurrentTime.setText(stringToTime(duration));
            }
        }
        mTitleText.setText(mVideoTitle);
        return position;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                mCurVolume = -1;
                mCurBrightness = -1;
                mCenterLayout.setVisibility(GONE);
//                break;// do need bread,should let gestureDetector to handle event
            default://gestureDetector handle other MotionEvent
                if (mGestureDetector != null)
                    mGestureDetector.onTouchEvent(event);
        }
        return true;

    }


    public void setOnClickListener() {
    }
    /**
     * toggle pause or play
     */
    private void togglePausePlay() {
        if (mRootView == null || mPlayPauseButton == null || mMediaPlayerControlListener == null) {
            return;
        }

        if (mMediaPlayerControlListener.isPlaying()) {
            mPlayPauseButton.setImageResource(mPauseIcon);
        } else {
            mPlayPauseButton.setImageResource(mPlayIcon);
        }
    }

    /**
     * toggle full screen or not
     */
    public void toggleFullScreen() {
        if (mRootView == null || mFullscreenButton == null || mMediaPlayerControlListener == null) {
            return;
        }

        if (mMediaPlayerControlListener.isFullScreen()) {
            mFullscreenButton.setImageResource(mShrinkIcon);
        } else {
            mFullscreenButton.setImageResource(mStretchIcon);
        }
    }

    private void doPauseResume() {
        if (mMediaPlayerControlListener == null) {
            return;
        }

        if (mMediaPlayerControlListener.isPlaying()) {
            mMediaPlayerControlListener.pause();
        } else {
            mMediaPlayerControlListener.start();
        }
        togglePausePlay();
    }

    private void doToggleFullscreen() {
        if (mMediaPlayerControlListener == null) {
            return;
        }

        mMediaPlayerControlListener.toggleFullScreen();
    }

    /**
     * Seek bar drag listener
     */
    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            show();
            mIsDragging = true;
            mHandler.removeMessages(HANDLER_UPDATE_PROGRESS);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (mMediaPlayerControlListener == null) {
                return;
            }

            if (!fromuser) {
                return;
            }

            long duration = mMediaPlayerControlListener.getDuration();
            long newPosition = (duration * progress) / 1000L;
            mMediaPlayerControlListener.seekTo((int) newPosition);
            if (mCurrentTime != null)
                mCurrentTime.setText(stringToTime((int) newPosition));
        }

        public void onStopTrackingTouch(SeekBar bar) {
            mIsDragging = false;
            setSeekProgress();
            togglePausePlay();
            show();
            mHandler.sendEmptyMessage(HANDLER_UPDATE_PROGRESS);
        }
    };

    @Override
    public void setEnabled(boolean enabled) {
        if (mPlayPauseButton != null) {
            mPlayPauseButton.setEnabled(enabled);
        }
        if (mSeekBar != null) {
            mSeekBar.setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }


    /**
     * set video view click listener
     */
    private OnClickListener myViewListener = new OnClickListener() {
        public void onClick(View v) {
            plaayListCenterLayout.setVisibility(VISIBLE);
            indexChoice = showPlayList();
        }
    };


    /**
     * set top back click listener
     */
    private OnClickListener mBackListener = new OnClickListener() {
        public void onClick(View v) {
            mMediaPlayerControlListener.exit();
        }
    };

    private OnClickListener mNextListener = new OnClickListener() {
        public void onClick(View v) {

            // no repeat or shuffle ON - play next song
            if(currentVideoIndex < (videoList.size() - 1)){
                currentVideoIndex = currentVideoIndex + 1;
                startNewActivity(currentVideoIndex);
            }else{
                // play first song
                currentVideoIndex = 0;
                startNewActivity(currentVideoIndex);
            }
        }
    };

    /**
     * set pause click listener
     */
    private OnClickListener mPauseListener = new OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show();
        }
    };

    /**
     * set full screen click listener
     */
    private OnClickListener mFullscreenListener = new OnClickListener() {
        public void onClick(View v) {
            doToggleFullscreen();
            show();
        }
    };

    /**
     * setMediaPlayerControlListener update play state
     *
     * @param mediaPlayerListener self
     */
    public void setMediaPlayerControlListener(MediaPlayerControlListener mediaPlayerListener) {
        mMediaPlayerControlListener = mediaPlayerListener;
        togglePausePlay();
        toggleFullScreen();
    }

    /**
     * set anchor view
     *
     * @param view view that hold controller view
     */
    private void setAnchorView(ViewGroup view) {
        mAnchorView = view;
        LayoutParams frameParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        //remove all before add view
        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);

        setGestureListener();
    }

    /**
     * set gesture listen to control media player
     * include screen brightness and volume of video
     * and seek video play
     */
    private void setGestureListener() {

        if (mCanControlVolume) {
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }

        mGestureDetector = new GestureDetector(mContext, new ViewGestureListener(mContext, this));
    }


    @Override
    public void onSingleTap() {
        toggleControllerView();
    }

    @Override
    public void onHorizontalScroll(boolean seekForward) {
        if (mCanSeekVideo) {
            if (seekForward) {// seek forward
                seekForWard();
            } else {  //seek backward
                seekBackWard();
            }
        }
    }

    private void seekBackWard() {
        if (mMediaPlayerControlListener == null) {
            return;
        }

        int pos = mMediaPlayerControlListener.getCurrentPosition();
        pos -= PROGRESS_SEEK;
        mMediaPlayerControlListener.seekTo(pos);
        setSeekProgress();

        show();
    }

    private void seekForWard() {
        if (mMediaPlayerControlListener == null) {
            return;
        }

        int pos = mMediaPlayerControlListener.getCurrentPosition();
        pos += PROGRESS_SEEK;
        mMediaPlayerControlListener.seekTo(pos);
        setSeekProgress();

        show();
    }

    @Override
    public void onVerticalScroll(float percent, int direction) {
        if (direction == ViewGestureListener.SWIPE_LEFT) {
            if (mCanControlBrightness) {
                mCenterImage.setImageResource(R.drawable.video_bright_bg);
                updateBrightness(percent);
            }
        } else {
            if (mCanControlVolume) {
                mCenterImage.setImageResource(R.drawable.video_volume_bg);
                updateVolume(percent);
            }
        }
    }

    /**
     * update volume by seek percent
     *
     * @param percent seek percent
     */
    private void updateVolume(float percent) {

        mCenterLayout.setVisibility(VISIBLE);

        if (mCurVolume == -1) {
            mCurVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mCurVolume < 0) {
                mCurVolume = 0;
            }
        }

        int volume = (int) (percent * mMaxVolume) + mCurVolume;
        if (volume > mMaxVolume) {
            volume = mMaxVolume;
        }

        if (volume < 0) {
            volume = 0;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

        int progress = volume * 100 / mMaxVolume;
        mCenterProgress.setProgress(progress);
    }


    /**
     * update brightness by seek percent
     *
     * @param percent seek percent
     */
    private void updateBrightness(float percent) {

        if (mCurBrightness == -1) {
            mCurBrightness = mContext.getWindow().getAttributes().screenBrightness;
            if (mCurBrightness <= 0.01f) {
                mCurBrightness = 0.01f;
            }
        }

        mCenterLayout.setVisibility(VISIBLE);

        WindowManager.LayoutParams attributes = mContext.getWindow().getAttributes();
        attributes.screenBrightness = mCurBrightness + percent;
        if (attributes.screenBrightness >= 1.0f) {
            attributes.screenBrightness = 1.0f;
        } else if (attributes.screenBrightness <= 0.01f) {
            attributes.screenBrightness = 0.01f;
        }
        mContext.getWindow().setAttributes(attributes);

        float p = attributes.screenBrightness * 100;
        mCenterProgress.setProgress((int) p);

    }


    /**
     * update video title
     *
     * @param videoLable seek percent
     */
    public void setVideoLable(String videoLable) {
        mVideoTitle = videoLable;
        mTitleText.setText(videoLable);
    }


    /**
     * update video title
     *
     */
    public int getChoice() {
        return indexChoice;
    }


    /**
     * Interface of Media Controller View Which can be callBack
     * when {@link MediaPlayer} or some other media
     * players work
     */
    public interface MediaPlayerControlListener {
        /**
         * start play video
         */
        void start();

        /**
         * pause video
         */
        void pause();

        /**
         * get video total time
         *
         * @return total time
         */
        int getDuration();

        /**
         * get video current position
         *
         * @return current position
         */
        int getCurrentPosition();

        /**
         * seek video to exactly position
         *
         * @param position position
         */
        void seekTo(int position);

        /**
         * video is playing state
         *
         * @return is video playing
         */
        boolean isPlaying();

        /**
         * video is complete
         *
         * @return complete or not
         */
        boolean isComplete();

        /**
         * get buffer percent
         *
         * @return percent
         */
        int getBufferPercentage();

        /**
         * video is full screen
         * in order to control image src...
         *
         * @return fullScreen
         */
        boolean isFullScreen();

        /**
         * toggle fullScreen
         */
        void toggleFullScreen();

        /**
         * show play list
         */
        /**
         * show play list
         */
//        void getChoice();

        /**
         * show set video lable
         */
//        void setVideoLable(String videoLable);

        /**
         * exit media player
         */
        void exit();
    }


    //    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public int showPlayList() {

        ArrayList<HashMap<String, String>> videoListData = new ArrayList<HashMap<String, String>>();

        VideoManager plm = new VideoManager();
        // get all video from sdcard
        this.videoList = plm.getPlayList();

        // looping through playlist
        for (int i = 0; i < videoList.size(); i++) {
            // creating new HashMap
            HashMap<String, String> video = videoList.get(i);

            // adding HashList to ArrayList
            videoListData.add(video);
        }

        // Adding menuItems to ListView
        ListAdapter adapter = new SimpleAdapter(getContext(), videoListData,
                R.layout.playlist_item, new String[]{"videoTitle"}, new int[]{
                R.id.videoTitle});
        ListView playlistview = (ListView) mRootView.findViewById(R.id.playlist_center);

        playlistview.setAdapter(adapter);

        // listening to single listitem click
        playlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting listitem index
                currentVideoIndex = position;
                plaayListCenterLayout.setVisibility(GONE);
                toggleControllerView();

                startNewActivity(currentVideoIndex);

            }


        });
        return currentVideoIndex;
    }

    private void startNewActivity(int currentVideoIndex) {
        Intent intent = new Intent(getContext(),VideoPlayerActivity.class);
        intent.putExtra("currentIndex",currentVideoIndex);
        mContext.startActivity(intent);
        mContext.finish();
        toggleControllerView();
    }
}