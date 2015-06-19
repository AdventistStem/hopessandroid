package com.adventiststem.hopess;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.adventiststem.hopess.Utils.BrightcoveAPI;
import com.brightcove.player.view.BrightcoveVideoView;

//2015-04-26, djp - for lesson details
public class LessonDetailActivity extends Activity implements PlayListCallBack{

    private static final String MP3_DIR = "hopess_mp3";
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
	private DownloadManager dm;
	private long enqueue;
	private TextView downloadTextView;


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

        
        downloadTextView = (TextView) findViewById(R.id.lesson_download_mp3);
        
        File file = new File(Environment.getExternalStorageDirectory()
	                + "/"+MP3_DIR+"/"+ audioUrl.substring(audioUrl.lastIndexOf("/"))); 
		if(file.exists())
			downloadTextView.setText(getString(R.string.mp3_delete_download_button));
		else
			downloadTextView.setText(getString(R.string.mp3_download_button));
    }
    
    
    public void onDownloadAudioClick(View v)
    {
    	  Log.e("URL", ""+audioUrl);
    	
    	  if(!downloaded())
    	  {    		  
    		  BroadcastReceiver receiver = new BroadcastReceiver() {
    	            @Override
    	            public void onReceive(Context context, Intent intent) {
    	                String action = intent.getAction();
    	                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
    	                	
    	                	downloadTextView.setClickable(true);
    	                	
    	                	Toast.makeText(LessonDetailActivity.this, "Download completed. Saved for offline listning!", Toast.LENGTH_LONG).show();
    	                	if(downloaded())
    	            			downloadTextView.setText(getString(R.string.mp3_delete_download_button));
    	            		else
    	            			downloadTextView.setText(getString(R.string.mp3_download_button));
    	                }
    	            }
    	        };

    	        registerReceiver(receiver, new IntentFilter(
    	                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    		  
    		  File direct = new File(Environment.getExternalStorageDirectory()
    	                + "/"+MP3_DIR);

    	        if (!direct.exists()) {
    	            direct.mkdirs();
    	        }

    	        DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

    	        Uri downloadUri = Uri.parse(audioUrl);
    	        DownloadManager.Request request = new DownloadManager.Request(
    	                downloadUri);

    	        request.setAllowedNetworkTypes(
    	                DownloadManager.Request.NETWORK_WIFI
    	                        | DownloadManager.Request.NETWORK_MOBILE)
    	                .setDestinationInExternalPublicDir("/"+MP3_DIR, ""+audioUrl.substring(audioUrl.lastIndexOf("/")));

    	        mgr.enqueue(request);
    		  
    	        Toast.makeText(this, "Downloading started...", Toast.LENGTH_LONG).show();
    	        downloadTextView.setClickable(false);
    		  
    	  }else{
    		  
    		 
    		  
    		  new AlertDialog.Builder(this)
    		  .setTitle("Confirmation")
    		  .setMessage("Do you really want to DELETE audio?")
    		  .setIcon(android.R.drawable.ic_dialog_alert)
    		  .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

    		      public void onClick(DialogInterface dialog, int whichButton) {
    		    	  File file = new File(Environment.getExternalStorageDirectory()
    		    	            + "/"+MP3_DIR+"/"+ audioUrl.substring(audioUrl.lastIndexOf("/"))); 
    		    	  if(file.exists())
    	    			  file.delete();
    	    		  Toast.makeText(LessonDetailActivity.this, "Audio Deleted from phone!", Toast.LENGTH_LONG).show();
    	    		  
    	    		  if(downloaded())
    	      			downloadTextView.setText(getString(R.string.mp3_delete_download_button));
    	      		else
    	      			downloadTextView.setText(getString(R.string.mp3_download_button));
    		      }})
    		   .setNegativeButton(android.R.string.no, null).show();
    		  
    		  
    		  
    		  
    		  
    	  }
    }

    private boolean downloaded() {
		try{
			File file = new File(Environment.getExternalStorageDirectory()
  	                + "/"+MP3_DIR+"/"+ audioUrl.substring(audioUrl.lastIndexOf("/"))); 
			if(file.exists())
				return true;
		}catch(Exception e){
			
		}
		return false;
	}


	public void onClickAudio(View view){

        brightcoveVideoView.pause();
        
        
        if(downloaded())
        {
        	Intent viewMediaIntent = new Intent();   
        	viewMediaIntent.setAction(android.content.Intent.ACTION_VIEW);   
        	File file = new File(Environment.getExternalStorageDirectory()
        			+ "/"+MP3_DIR+"/"+ audioUrl.substring(audioUrl.lastIndexOf("/")));  
        	viewMediaIntent.setDataAndType(Uri.fromFile(file), "audio/*");   
        	viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        	startActivity(viewMediaIntent); 
        }else{
	        Uri myUri = Uri.parse(audioUrl);
	        Intent intent = new Intent(android.content.Intent.ACTION_VIEW); 
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	        intent.setDataAndType(myUri, "audio/*"); 
	        startActivity(intent);
        }

//        Intent intent = new Intent(this, Mp3Activity.class);
//        intent.putExtra("AudioUrl", audioUrl);
//        startActivity(intent);
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