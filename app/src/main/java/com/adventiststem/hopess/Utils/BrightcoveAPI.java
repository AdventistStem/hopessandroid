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
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by allanmarube on 4/27/15.
 */
public class BrightcoveAPI {

    private static String TAG = "BRIGHTCOVE_API";
    private static String BrightcoveOauthEndpoint= "https://oauth.brightcove.com/v4/access_token";
    private static String BrightcoveClientId = "b187fdbe-ace7-45c4-9eef-e16e8b8c9a4e";
    private static String BrightcoveClientSecret = "xYOHyZJft2wtSODYBe35JRDVsRuLltxzot3WJidjyucQrpAve0wd2bvZ3BYBDyurqYNrEf33YHQwBbcd2_M8FA";

    private PlayListCallBack playListCallBack;

    private AsyncHttpClient client;
    private AsyncHttpClient oauthClient;
    private Context context;

    public BrightcoveAPI(Context ctx) {
        context = ctx;
        oauthClient = new AsyncHttpClient();
        oauthClient.setBasicAuth(BrightcoveClientId, BrightcoveClientSecret);
        oauthClient.setLoggingLevel(Log.ERROR);
        oauthClient.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client = new AsyncHttpClient();
        client.setLoggingLevel(Log.ERROR);
        client.setURLEncodingEnabled(false);
    }

    private void getAccessToken(JsonHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("grant_type", "client_credentials");
        oauthClient.post(BrightcoveOauthEndpoint, params, handler);
    }

    public void setReceiver(PlayListCallBack playListCallBack){
        this.playListCallBack = playListCallBack;
    }

    //retrieve latest videos
    public void retrieveVideos() {
        final ArrayList<LessonItem> responseBody = new ArrayList<LessonItem>();
        getAccessToken(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String accessToken = response.optString("access_token");
                client.addHeader("Authorization", "Bearer "+accessToken);

                int currYear = Calendar.getInstance().get(Calendar.YEAR);
                String latestVideosURL = "https://cms.api.brightcove.com/v1/accounts/753071706001/videos?q=%2Bseries_title:%22Hope%20Sabbath%20School%22%2Bvideo_type:%22Episode%20Full%22%2Bcategory_primary:%22" + currYear + "%22%2Bschedule.starts_at:..NOW&sort=%2Dschedule_starts_at";
                client.get(latestVideosURL, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray items){
                        Log.i(TAG, items.toString());
                        try {
                            int size = items.length();

                            for (int i = 0; i < size; i++) {

                                LessonItem lessonItem = new LessonItem();

                                JSONObject videoItem = (JSONObject)items.get(i);

                                lessonItem.id = videoItem.getString("id");
                                String thumbnailURL = videoItem.getJSONObject("images").getJSONObject("thumbnail").getString("src");
                                lessonItem.setThumbnailURL(thumbnailURL);
                                lessonItem.videoStillURL = thumbnailURL;

                                try {
                                    String nameAndDescription = videoItem.getString("name").replace(")", "");
                                    String[] nameDescrArr = nameAndDescription.split("\\(");

                                    String[] lessonNumberAndTitle = nameDescrArr[0].split("-");
                                    lessonItem.setTitle(lessonNumberAndTitle[0].trim());
                                    lessonItem.setDescription(lessonNumberAndTitle[1]);
                                    lessonItem.setDate(nameDescrArr[1]);

                                    lessonItem.setVideoURL(getVideoLink(videoItem.getString("description")));
                                    lessonItem.setAudioURL(getAudioLink(videoItem.getString("description")));
                                    lessonItem.setPdfURL(getAudioLink(videoItem.getString("description")).replace(".mp3", ".pdf"));

                                    responseBody.add(lessonItem);

                                    if (lessonItem.title.compareTo("Lesson 1") == 0){
                                        break;
                                    }
                                } catch (ArrayIndexOutOfBoundsException a) {
                                    a.printStackTrace();
                                    continue;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            playListCallBack.receiveLessonItems(responseBody);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header [] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.e(TAG, statusCode+" : " + errorResponse.toString());
                        playListCallBack.receiveLessonItems(responseBody);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.e(TAG, responseString);
                        playListCallBack.receiveLessonItems(responseBody);
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, responseString);
                playListCallBack.receiveLessonItems(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header [] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, statusCode+" : " + errorResponse.toString());
                playListCallBack.receiveLessonItems(responseBody);
            }
        });
    }

    //search videos by year and quarter
    public void searchVideos(final String year, final int quarter) {
        final ArrayList<LessonItem> responseBody = new ArrayList<LessonItem>();
        getAccessToken(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, response.toString());
                String accessToken = response.optString("access_token");
                client.addHeader("Authorization", "Bearer "+accessToken);

                //special case where there content is stored in different format in brightcove
                if (year.compareTo("2013") == 0 && quarter == 1) {
                    playListCallBack.receiveLessonItems(responseBody);
                    return;
                }
                String searchURL = "https://cms.api.brightcove.com/v1/accounts/753071706001/videos?q=%2Bseries_title:%22Hope%20Sabbath%20School%22%2Bvideo_type:%22Episode%20Full%22%2Bcategory_primary:%22" + year + "%22%2Bcategory_secondary:%22"+ quarter +"%22%2Bschedule.starts_at:..NOW&sort=%2Dschedule_starts_at";
                client.get(searchURL, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray items){
                        try {
                            int size = items.length();
                            for (int i = 0; i < size; i++) {
                                LessonItem lessonItem = new LessonItem();
                                JSONObject videoItem = (JSONObject)items.get(i);
                                lessonItem.id = videoItem.getString("id");
                                String thumbnailURL = videoItem.getJSONObject("images").getJSONObject("thumbnail").getString("src");
                                lessonItem.videoStillURL = thumbnailURL;
                                lessonItem.setThumbnailURL(thumbnailURL);
                                lessonItem.setVideoURL(getVideoLink(videoItem.getString("description")));
                                lessonItem.setAudioURL(getAudioLink(videoItem.getString("description")));
                                lessonItem.setPdfURL(getAudioLink(videoItem.getString("description")).replace(".mp3", ".pdf"));
                                try {
                                    String nameAndDescription = videoItem.getString("name").replace(")", "");
                                    //corner case
                                    if (!nameAndDescription.contains("Lesson")) {
                                        continue;
                                    }
                                    String[] nameDescrArr = nameAndDescription.split("\\(");
                                    String[] lessonNumberAndTitle = nameDescrArr[0].split("-");
                                    lessonItem.setTitle(lessonNumberAndTitle[0]);
                                    lessonItem.setDescription(lessonNumberAndTitle[1]);
                                    if (nameDescrArr.length > 1) {
                                        lessonItem.setDate(nameDescrArr[1]);
                                    }
                                    responseBody.add(lessonItem);
                                } catch (ArrayIndexOutOfBoundsException a) {
                                    a.printStackTrace();
                                    continue;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            playListCallBack.receiveLessonItems(responseBody);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.e(TAG, responseString);
                        playListCallBack.receiveLessonItems(responseBody);
                    }

                    @Override
                    public void onFailure(int statusCode, Header [] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.e(TAG, statusCode+" : " + errorResponse.toString());
                        playListCallBack.receiveLessonItems(responseBody);
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, responseString);
                playListCallBack.receiveLessonItems(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header [] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, statusCode+" : " + errorResponse.toString());
                playListCallBack.receiveLessonItems(responseBody);
            }
        });
    }

    //extract videoURL from the description key
    public String getVideoLink(String st){
        if (st.isEmpty()){
            return null;
        }
        Pattern p = Pattern.compile("href=\"(.*?)\"");
        Matcher matcher = p.matcher(st);
        String videoUrl = null;
        if (matcher.find()) {
            videoUrl = matcher.group(1); // this variable should contain the link URL
        }
        matcher.find();
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
        return audioUrl;
    }

    /* Gets pdf based on year-quarter-episode triple. Sends the pdf to
        activity that implements playlistCallBack */
    public void getPDF(String year, String quarter, String episode){
        client.get("http://cdn.hopetv.org/download/podcast/HSS-"+year+"-"+quarter+"-"+episode+".pdf", new FileAsyncHttpResponseHandler(context) {
            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
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
        client.get(url, new FileAsyncHttpResponseHandler(context) {
            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
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
