package com.adventiststem.hopess;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.adventiststem.hopess.Utils.BrightcoveAPI;
import com.adventiststem.hopess.pdf.PDFViewerActivity;
import com.brightcove.player.view.BrightcoveVideoView;

import org.apache.http.util.ByteArrayBuffer;

//2015-04-26, djp - for lesson details
public class LessonDetailActivity extends Activity implements PlayListCallBack{

    private static final String MP3_DIR = "hopess_mp3";
	private static final String PDF_DIR = "hopess_pdf";
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
	private TextView pdfDownloadView;


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
        
        File file = new File(getApplicationContext().getExternalFilesDir(MP3_DIR), audioUrl.substring(audioUrl.lastIndexOf("/")));
		if(file.exists())
			downloadTextView.setText(getString(R.string.mp3_delete_download_button));
		else
			downloadTextView.setText(getString(R.string.mp3_download_button));
		pdfDownloadView = (TextView) findViewById(R.id.lesson_download_pdf);
		if(!pdfDownloaded())
			pdfDownloadView.setText("Click to download the study guide.");
		else
			pdfDownloadView.setText("Click to delete the study guide.");

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
    	                	
    	                	Toast.makeText(LessonDetailActivity.this, "Download completed. Saved for offline listening!", Toast.LENGTH_LONG).show();
    	                	if(downloaded())
    	            			downloadTextView.setText(getString(R.string.mp3_delete_download_button));
    	            		else
    	            			downloadTextView.setText(getString(R.string.mp3_download_button));
    	                }
    	            }
    	        };

    	        registerReceiver(receiver, new IntentFilter(
    	                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    		  
    		  File direct = getApplicationContext().getExternalFilesDir(MP3_DIR);

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
    	                .setDestinationInExternalFilesDir(getApplicationContext(), "/" + MP3_DIR ,audioUrl.substring(audioUrl.lastIndexOf("/")));

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
    		    	  File file = new File(getApplicationContext().getExternalFilesDir(MP3_DIR), audioUrl.substring(audioUrl.lastIndexOf("/")));
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
			File file = new File(getApplicationContext().getExternalFilesDir(MP3_DIR), audioUrl.substring(audioUrl.lastIndexOf("/")));
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
        	File file = new File(getApplicationContext().getExternalFilesDir(MP3_DIR), audioUrl.substring(audioUrl.lastIndexOf("/")));
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

	/**
	 * OnClickPDF
	 * Called upon clicking the PDF button.
	 * @param view
	 */
	public void onClickPdf(View view) {
        brightcoveVideoView.pause(); //Pause the Video before displaying our PDF

        BrightcoveAPI api = new BrightcoveAPI(this);
        api.setReceiver(this);

		boolean downloaded = false;

		String tempLoc = getCacheDir().getPath() + "/temp.pdf"; //Location of the Temporary PDF.

        /*
         * Check to see if the PDF downloaded correctly.
         */
		if(!pdfDownloaded()) {
			File temp = tempDownload(pdfUrl);
			if(temp == null) {
				displayPDFDownloadError();
				return;
			}

		} else {
			downloaded = true;
		}
		Intent intent = new Intent(this, PDFViewerActivity.class);
		intent.putExtra("pdfLoc", downloaded?(new File(getApplicationContext().getExternalFilesDir(PDF_DIR), pdfUrl.substring(pdfUrl.lastIndexOf("/"))).toString()):tempLoc);
		startActivity(intent);

    }

	/**
	 * Download PDF Click.
	 * Called upon the user wanting to download a PDF. - Will update this when we figure out how we want downloads to appear.
	 */
	public void downloadPDFClick(View view) {
		if(!pdfDownloaded()) {
			Toast.makeText(LessonDetailActivity.this, "Downloading study guide for offline viewing.", Toast.LENGTH_LONG).show();
			File download = pdfDownload(pdfUrl);
			if(download == null) {
				displayPDFDownloadError();
				return;
			}
			Toast.makeText(LessonDetailActivity.this, "Download complete. Study guide successfully downloaded!", Toast.LENGTH_LONG).show();
		} else {
			new AlertDialog.Builder(this)
					.setTitle("Deletion Confirmation")
					.setMessage("Are you sure you want to delete the study guide?")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int whichButton) {
							File file = new File(getApplicationContext().getExternalFilesDir(PDF_DIR), pdfUrl.substring(pdfUrl.lastIndexOf("/")));
							if(file.exists())
								file.delete();
							Toast.makeText(LessonDetailActivity.this, "Study guide successfully deleted.", Toast.LENGTH_LONG).show();
							pdfDownloadView.setText("Click to download the study guide.");
						}})
					.setNegativeButton(android.R.string.no, null).show();
		}
	}

	/**
	 * Displays an error message if the download for a PDF file Fails
	 */
	private void displayPDFDownloadError() {
		Toast.makeText(LessonDetailActivity.this, "There was an error downloading the study guide. Check your connection or try again later.", Toast.LENGTH_LONG).show();
		/*
		AlertDialog.Builder errorBox  = new AlertDialog.Builder(this);
		errorBox.setMessage("There was an error downloading the study guide. Check your connection or try again later.");
		errorBox.setTitle("Download Error");
		errorBox.setPositiveButton("Continue", null);
		errorBox.setCancelable(true);
		errorBox.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		errorBox.create().show();
		*/
	}

	/**
	 * Checking if the PDF exists.
	 * @return A boolean value denoting whether or not the PDF for this lesson exists.
	 */
	private boolean pdfDownloaded() {
		File pdfLocation = new File(getApplicationContext().getExternalFilesDir(PDF_DIR), pdfUrl.substring(pdfUrl.lastIndexOf("/")));
		return pdfLocation.exists();
	}

	/**
	 * PDF Download
	 *
	 * Used to download pdf to the external cache directory.
	 * @param pdfLoc The URL of the PDF to download
	 */
	private File pdfDownload(final String pdfLoc) {
		File result = null;
		try {
			result = new AsyncTask<String, Void, File>() {

				@Override
				protected File doInBackground(String... params) {
					try {
						File location = getApplicationContext().getExternalFilesDir(PDF_DIR);
						File pdf = new File(location, pdfLoc.substring(pdfLoc.lastIndexOf("/")));
						pdf.createNewFile();
						URL url = new URL(pdfLoc);
						BufferedInputStream input = new BufferedInputStream(url.openConnection().getInputStream());
						ByteArrayBuffer buffer = new ByteArrayBuffer(75000);
						int val = 0;
						while ((val = input.read()) != -1) {
							buffer.append((byte) val);
						}
						FileOutputStream output = new FileOutputStream(pdf);
						output.write(buffer.toByteArray());
						output.close();
						return pdf;
					} catch (IOException ex) {
					}
					return null;
				}
			}.execute(pdfLoc).get();
		} catch (InterruptedException | ExecutionException e) {
			//TODO: Error Logic
			return null;
		}
		return result;
	}

	/**
	 * Download Temporary PDF
	 *
	 * Used to download pdf to the internal cache directory.
	 * @param pdfLoc The URL of the PDF to download
	 */
	private File tempDownload(final String pdfLoc) {
		File result = null;
		try {
			result = new AsyncTask<String, Void, File>() {

                @Override
                protected File doInBackground(String... params) {
                    try {
                        File cache = getCacheDir();
                        File pdf = new File(cache, "temp.pdf");
                        pdf.createNewFile();
                        URL url = new URL(pdfLoc);
                        BufferedInputStream input = new BufferedInputStream(url.openConnection().getInputStream());
                        ByteArrayBuffer buffer = new ByteArrayBuffer(75000);
                        int val = 0;
                        while ((val = input.read()) != -1) {
                            buffer.append((byte) val);
                        }
                        FileOutputStream output = new FileOutputStream(pdf);
                        output.write(buffer.toByteArray());
                        output.close();
                        return pdf;
                    } catch (IOException ex) {
                    }
                    return null;
                }
            }.execute(pdfLoc).get();
		} catch (InterruptedException | ExecutionException e) {
			//Error Logic - Taken care of in a higher method.
			return null;
		}
		return result;
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