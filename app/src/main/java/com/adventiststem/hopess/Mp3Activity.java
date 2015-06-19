package com.adventiststem.hopess;

import android.app.Activity;
import android.os.Bundle;
import android.widget.VideoView;

/**
 * Created by allanmarube on 6/14/15.
 */
public class Mp3Activity extends Activity {
    private String audioUrl;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.mp3_layout);
        audioUrl = getIntent().getStringExtra("AudioUrl");
        VideoView videoView = (VideoView) findViewById(R.id.mp3VideoView);
        videoView.setVideoPath(audioUrl);
        videoView.start();
    }
}
