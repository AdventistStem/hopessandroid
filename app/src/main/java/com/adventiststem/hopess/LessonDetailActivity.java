package com.adventiststem.hopess;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import com.adventiststem.hopess.Utils.BrightcoveAPI;
import com.brightcove.player.analytics.Analytics;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.DeliveryType;
import com.brightcove.player.media.PlaylistListener;
import com.brightcove.player.media.VideoListener;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveVideoView;
import com.brightcove.player.view.SeamlessVideoView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

//2015-04-26, djp - for lesson details
public class LessonDetailActivity extends Activity implements PlayListCallBack{

    private String audioUrl;
    private String videoUrl;
    private String pdfUrl;
    private String id;
    private String title;
    private String description;
    private String date;

    private BrightcoveVideoView brightcoveVideoView;
    private TextView tVtitle;
    private TextView tVdescription;
    private TextView tVdate;
    private MediaController controller;

    private static String TAG = "LessonDetailActivity";
    private VideoView videoView;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.lesson_detail);

        tVtitle = (TextView)findViewById(R.id.lesson_detail_title);
        tVdescription = (TextView)findViewById(R.id.lesson_detail_description);
        tVdate = (TextView)findViewById(R.id.lesson_detail_date);



        videoUrl = getIntent().getStringExtra("VideoUrl");
        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");
        date = getIntent().getStringExtra("date");
        audioUrl = getIntent().getStringExtra("AudioUrl");
        pdfUrl = getIntent().getStringExtra("PdfUrl");
        id = getIntent().getStringExtra("id");

        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);

        controller = new MediaController(this);
        brightcoveVideoView.setMediaController(controller);


        tVtitle.setText(title);
        tVdescription.setText(description);
        tVdate.setText(date);

//        Catalog catalog = new Catalog("MrqqXrGUW0S_eq7p1I9S_Fv46q0O0K6L8BzFt9q09sBfsMCUHB67ZA..");
//        catalog.findVideoByID(id, new VideoListener() {
//                    @Override
//                    public void onVideo(Video video) {
//                        brightcoveVideoView.add(video);
//
//                        brightcoveVideoView.start();
//
//
//                    }
//
//                    @Override
//                    public void onError(String s) {
//
//                    }
//                });

        brightcoveVideoView.setVideoPath(videoUrl);
        //brightcoveVideoView.start();

    }

    public void onClickAudio(View view){

        brightcoveVideoView.pause();

        Intent intent = new Intent(this, Mp3Activity.class);
        intent.putExtra("AudioUrl", audioUrl);
        startActivity(intent);
       // brightcoveVideoView.clear();

//        brightcoveVideoView.stopPlayback();
//        brightcoveVideoView.clear();
//        brightcoveVideoView.add(Video.createVideo(audioUrl));
//        System.out.println("AUDIOURL:" + audioUrl);
//        brightcoveVideoView.start();


//        videoView.setVideoURI(Uri.parse(audioUrl));
//        videoView.start();



        //example call to get a pdf
       // api.getPDF("2015", "Q3", "E13");



    }


    @Override
    public void onResume(){
        super.onResume();
    }

    public void onClickPdf(View view){

        brightcoveVideoView.pause();

        BrightcoveAPI api = new BrightcoveAPI(this);
        api.setReceiver(this);

//        Intent intent = new Intent(this, PDFViewerActivity.class);
//        intent.putExtra("PdfUrl", pdfUrl);
//        startActivity(intent);
    }


    @Override
    public void receiveLessonItems(ArrayList<LessonItem> items) {

    }

    //pdf document is received here
    @Override
    public void receivePDFItems(File file) {

        Log.i(TAG, "PDF received into lesson details page...");
        

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause(){
        super.onPause();
        brightcoveVideoView.pause();
    }

}