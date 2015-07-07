
package com.adventiststem.hopess;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.adventiststem.hopess.Utils.BrightcoveAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by allanmarube on 5/25/15.
 */
public class LessonActivity extends Activity implements PlayListCallBack  {

    private String offline_name;

    private List<LessonItem> mItems; // ListView items list
    private LessonAdapter mAdapter;
    private ListView listView;
    private BrightcoveAPI brightcoveAPI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_activity);

        String year = getIntent().getStringExtra("year");
        int quarter = getIntent().getIntExtra("quarter",-1);

        offline_name = "arc_" + year + "_" + quarter + ".dat";

        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LessonActivity.this, LessonDetailActivity.class);

                LessonItem item = mItems.get(position);
                intent.putExtra("id", item.id);
                intent.putExtra("LessonName",item.title);
                intent.putExtra("VideoUrl", item.videoURL);
                intent.putExtra("title", item.title);
                intent.putExtra("description", item.description);
                intent.putExtra("AudioUrl", item.audioURL);
                intent.putExtra("PdfUrl", item.pdfURL);
                intent.putExtra("date", item.date);
                startActivity(intent);
            }
        });

        mItems = new ArrayList<LessonItem>();
        mAdapter = new LessonAdapter(this, mItems);
        listView.setAdapter(mAdapter);
        //Check Connection
        Context context = this;
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            //Log.i("LessonGridViewFragment:", "Not Connected!?");
            retrieveCachedList();
            return;
        }

        brightcoveAPI = new BrightcoveAPI(this);
        brightcoveAPI.setReceiver(this);
        brightcoveAPI.searchVideos(year, quarter);

        // Toast.makeText(this, "Year: "+year+" Quarter: "+quarter+"", Toast.LENGTH_SHORT).show();


    }

    /**
     * Saves the list to our directory for viewing when we don't have an internet connection
     * @param items The list of items to save.
     */
    private void saveItems(ArrayList<LessonItem> items) {
        items = new ArrayList<>(items);
        File file = new File(getExternalFilesDir(null), offline_name);
        if(getExternalFilesDir(null) == null)
            file = new File(getFilesDir(), offline_name);
        try {
            if(!file.exists())
                file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(items);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * No internet connection? Retrieve our list that we saved from a working session.
     */
    private void retrieveCachedList() {
        File file = new File(getExternalFilesDir(null), offline_name);
        if(getExternalFilesDir(null) == null)
            file = new File(getFilesDir(), offline_name);
        try {
            if(file == null || !file.exists()) {
                Toast.makeText(this, "Make sure you have an internet connection before opening this archive for the first time.", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(this, "No internet connection detected. You may only view or listen to lessons you have downloaded.", Toast.LENGTH_LONG).show();
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<LessonItem> items = (ArrayList<LessonItem>) ois.readObject();
            ois.close();
            receiveLessonItems(items);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveLessonItems(ArrayList<LessonItem> items) {
        saveItems(items);
        System.out.println("LessonReceiverCalled" + items.size());
        if (items.size() == 0){
            Toast.makeText(this, "No content available for this category", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        for (LessonItem lesson: items){
            mItems.add(lesson);
            mAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void receivePDFItems(File file) {

    }
}



