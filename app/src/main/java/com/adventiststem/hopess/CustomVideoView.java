package com.adventiststem.hopess;

import android.content.Context;
import android.util.AttributeSet;

import com.brightcove.player.view.BrightcoveVideoView;

/**
 * Created by allanmarube on 6/29/15.
 */
public class CustomVideoView extends BrightcoveVideoView{
    private PlayPauseListener mListener;

    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPlayPauseListener(PlayPauseListener listener) {
        mListener = listener;
    }

    @Override
    public void pause() {
        super.pause();
        if (mListener != null) {
            mListener.onPauseVideo();
        }
    }

    @Override
    public void start() {
        super.start();
        if (mListener != null) {
            mListener.onPlayVideo();
        }
    }

    public static interface PlayPauseListener {
        void onPlayVideo();
        void onPauseVideo();
    }
}
