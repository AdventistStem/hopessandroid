package com.adventiststem.hopess.Utils;

import android.content.Context;
import android.util.Log;
import com.adventiststem.hopess.LessonItem;
import com.adventiststem.hopess.PlayListCallBack;
import com.loopj.android.http.*;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by allanmarube on 4/27/15.
 */
public class BrightcoveAPI {

    private static String year;
    private static String currYear;
    //private static String quarter = "2";
    //private static String URL = "http://api.brightcove.com/services/library?token=MrqqXrGUW0ROWQ_ptCOuh1H4FVfUQpUQYQkEB4IUXsYF-INls6OJaw..&custom_fields=series_title,episode_number,person,category_primary,category_secondary,original_air_date&command=search_videos&all=custom_fields:Hope%20Sabbath%20School&all=custom_fields:Episode%20Full&exact=true&all=category_primary:"+year+"&all=category_secondary:"+quarter+"&sort_by=start_date:desc,publish_date:desc%22";
    private String latestVideosURL;
    private ArrayList<LessonItem> responseBody;

    private static String TAG = "BRIGHTCOVE_API";

    private PlayListCallBack playListCallBack;

    private AsyncHttpClient client;
    private Context context;

    public BrightcoveAPI(Context ctx) {
        context = ctx;
        client = new AsyncHttpClient();
        Calendar calendar = Calendar.getInstance();
        int yr = calendar.get(Calendar.YEAR);
        currYear = yr+"";
        latestVideosURL =  "http://api.brightcove.com/services/library?token=MrqqXrGUW0ROWQ_ptCOuh1H4FVfUQpUQYQkEB4IUXsYF-INls6OJaw..&custom_fields=series_title,episode_number,person,category_primary,category_secondary,original_air_date&command=search_videos&all=custom_fields:Hope%20Sabbath%20School&all=custom_fields:Episode%20Full&exact=true&all=category_primary:"+currYear+"&sort_by=start_date:desc,publish_date:desc%22";
    }

    public void setReceiver(PlayListCallBack playListCallBack){
        this.playListCallBack = playListCallBack;
    }

    //retrieve latest videos
    public void retrieveVideos() {
        responseBody = new ArrayList<LessonItem>();

        client.get(latestVideosURL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){

                //System.out.println(response);
                try {
                    JSONArray items = response.getJSONArray("items");
                    int size = items.length();
                    for (int i = 0; i < size; i++) {

                        LessonItem lessonItem = new LessonItem();

                        JSONObject videoItem = (JSONObject)items.get(i);


                        lessonItem.id = videoItem.getString("id");
                        lessonItem.setThumbnailURL(videoItem.getString("thumbnailURL"));
                        lessonItem.videoStillURL = videoItem.getString("videoStillURL");
                        //lessonItem.setVideoURL(getVideoLink(videoItem.getString("shortDescription")));
                        //lessonItem.setAudioURL(getAudioLink(videoItem.getString("shortDescription")));
                        //lessonItem.setPdfURL(getAudioLink(videoItem.getString("shortDescription")).replace(".mp3", ".pdf"));

                        String nameAndDescription = videoItem.getString("name").replace(")", "");
                        String [] nameDescrArr = nameAndDescription.split("\\(");
                        String [] lessonNumberAndTitle = nameDescrArr[0].split("-");
                        lessonItem.setTitle(lessonNumberAndTitle[0]);
                        lessonItem.setDescription(lessonNumberAndTitle[1]);
                        lessonItem.setDate(nameDescrArr[1]);

                        responseBody.add(lessonItem);

                    }

                    playListCallBack.receiveLessonItems(responseBody);



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            public void onFailure(int statusCode, Header [] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {

            }

        });


    }




    //search videos by year and quarter
    public void searchVideos(String year, int quarter) {

        responseBody = new ArrayList<LessonItem>();

        //special case where there content is stored in different format in brightcove
//        if (year.compareTo("2013") == 0 && quarter == 1) {
//            playListCallBack.receiveLessonItems(responseBody);
//            return;
//        }

       String searchURL =  "http://api.brightcove.com/services/library?token=MrqqXrGUW0ROWQ_ptCOuh1H4FVfUQpUQYQkEB4IUXsYF-INls6OJaw..&custom_fields=series_title,episode_number,person,category_primary,category_secondary,original_air_date&command=search_videos&all=custom_fields:Hope%20Sabbath%20School&all=custom_fields:Episode%20Full&exact=true&all=category_primary:"+year+"&all=category_secondary:"+quarter+"&sort_by=start_date:desc,publish_date:desc%22";
        //String tempURL = "http://api.brightcove.com/services/library?token=MrqqXrGUW0S_eq7p1I9S_Fv46q0O0K6L8BzFt9q09sBfsMCUHB67ZA..&video_fields=HLSURL,thumbnailURL,id,name,tags&custom_fields=series_title,episode_number,url,person,category_primary,category_secondary,original_air_date&command=search_videos&all=custom_fields:Hope%20Sabbath%20School&all=custom_fields:Episode%20Full&exact=true&all=category_primary:2014&all=category_secondary:1&sort_by=start_date:desc,publish_date:desc";

        client.get(searchURL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){

                System.out.println(response);
                try {
                    JSONArray items = response.getJSONArray("items");
                    int size = items.length();
                    for (int i = 0; i < size; i++) {

                        LessonItem lessonItem = new LessonItem();

                        JSONObject videoItem = (JSONObject)items.get(i);


                        System.out.println("HLSURL is: "+ videoItem.optString("HLSURL"));

                        lessonItem.id = videoItem.getString("id");
                        lessonItem.videoStillURL = videoItem.getString("videoStillURL");
                        lessonItem.setThumbnailURL(videoItem.getString("thumbnailURL"));
                        //lessonItem.setVideoURL(getVideoLink(videoItem.getString("shortDescription")));
                        //lessonItem.setAudioURL(getAudioLink(videoItem.getString("shortDescription")));
                        //lessonItem.setPdfURL(getAudioLink(videoItem.getString("shortDescription")).replace(".mp3", ".pdf"));

                        String nameAndDescription = videoItem.getString("name").replace(")", "");
                        String [] nameDescrArr = nameAndDescription.split("\\(");
                        String [] lessonNumberAndTitle = nameDescrArr[0].split("-");
                        lessonItem.setTitle(lessonNumberAndTitle[0]);
                        lessonItem.setDescription(lessonNumberAndTitle[1]);
                        lessonItem.setDate(nameDescrArr[1]);

                        responseBody.add(lessonItem);

                    }

                    playListCallBack.receiveLessonItems(responseBody);



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            public void onFailure(int statusCode, Header [] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {

            }

        });


    }





    //extract videoURL from the description key
    public String getVideoLink(String st){
            if (st.isEmpty()){
                return null;
            }

       // System.out.println(st);

        Pattern p = Pattern.compile("href=\"(.*?)\"");
        Matcher matcher = p.matcher(st);
        String videoUrl = null;
        if (matcher.find()) {
            videoUrl = matcher.group(1); // this variable should contain the link URL
        }
        matcher.find();
       // System.out.println(matcher.group(1));
        return videoUrl;

    }

    //extract audioURL from the description key
    public String getAudioLink(String st){
        if (st.isEmpty()){
            return null;
        }
        Pattern p = Pattern.compile("href=\"(.*?)\"");
        Matcher matcher = p.matcher(st);
        String audioUrl = null;
        if (matcher.find()) {
        //videoUrl found here
        }

        if (matcher.find()) {
            audioUrl = matcher.group(1); // this variable should contain the link URL for audio
        }

       // System.out.println("audiourl:"+audioUrl);
        return audioUrl;
    }

    /* Gets pdf based on year-quarter-episode triple. Sends the pdf to
        activity that implements playlistCallBack */
    public void getPDF(String year, String quarter, String episode){

        //example URL:"http://cdn.hopetv.org/download/podcast/HSS-2015-Q3-E13.pdf"
        client.get("http://cdn.hopetv.org/download/podcast/HSS-"+year+"-"+quarter+"-"+episode+".pdf", new FileAsyncHttpResponseHandler(context) {
            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {

                //do something
                Log.i(TAG,"PDF download failed");


            }

            @Override
            public void onSuccess(int i, Header[] headers, File file) {
                Log.i(TAG,"PDF download successful");
                playListCallBack.receivePDFItems(file);


            }
        });
    }

    //download pdf from url string
    public void getPDF(String url){
        //example URL:"http://cdn.hopetv.org/download/podcast/HSS-2015-Q3-E13.pdf"
        client.get(url, new FileAsyncHttpResponseHandler(context) {
            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                //do something
                Log.i(TAG,"PDF download failed");


            }

            @Override
            public void onSuccess(int i, Header[] headers, File file) {
                Log.i(TAG,"PDF download successful");
                playListCallBack.receivePDFItems(file);


            }
        });
    }


}
