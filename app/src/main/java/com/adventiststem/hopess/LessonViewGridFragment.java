package com.adventiststem.hopess;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
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
public class LessonViewGridFragment extends Fragment implements PlayListCallBack {

    private static final String OFFLINE_LIST_NAME = "offline.dat";
    private List<LessonItem> mItems; // ListView items list
    private ArrayAdapter mAdapter;
    private GridView gridView;
    private BrightcoveAPI brightcoveAPI;

    private String currYear;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mItems = new ArrayList<LessonItem>();
        mAdapter = new LessonGridAdapter(getActivity(), mItems);
        //Let's see if we have an internet connection
        Context context = getActivity();
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            //Log.i("LessonGridViewFragment:", "Not Connected!?");
            retrieveCachedList();
            return;
        }
        //Log.i("LessonGridViewFragment:", "Supposedly Connected");
        brightcoveAPI = new BrightcoveAPI(getActivity());
        brightcoveAPI.setReceiver(this);

    }

    /**
     * Saves the list to our directory for viewing when we don't have an internet connection
     * @param items The list of items to save.
     */
    private void saveItems(ArrayList<LessonItem> items) {
        items = new ArrayList<>(items);
        if(getActivity() == null)
            return;
        File file = new File(getActivity().getExternalFilesDir(null), OFFLINE_LIST_NAME);
        if(getActivity().getExternalFilesDir(null) == null)
            file = new File(getActivity().getFilesDir(), OFFLINE_LIST_NAME);
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
        if(getActivity() == null)
            return;
        File file = new File(getActivity().getExternalFilesDir(null), OFFLINE_LIST_NAME);
        if(getActivity().getExternalFilesDir(null) == null)
            file = new File(getActivity().getFilesDir(), OFFLINE_LIST_NAME);
        try {
            if(file == null || !file.exists()) {
                Toast.makeText(getActivity(), "Make sure you have an internet connection before opening this app for the first time.", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(getActivity(), "No internet connection detected. You may only view or listen to lessons you have downloaded.", Toast.LENGTH_LONG).show();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.grid_view_frag_layout, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gridView = (GridView) getActivity().findViewById(R.id.gridviewA);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //start an activity to display videos for this year

                // retrieve theListView item
                LessonItem item = mItems.get(position);

                //Launch LessonDetailActivity with necessary video info
                Intent intent = new Intent(getActivity(), LessonDetailActivity.class);
                intent.putExtra("LessonName",item.title);
                intent.putExtra("VideoUrl", item.videoURL);
                intent.putExtra("title", item.title);
                intent.putExtra("description", item.description);
                intent.putExtra("date", item.date);
                intent.putExtra("AudioUrl", item.audioURL);
                intent.putExtra("PdfUrl", item.pdfURL);
                intent.putExtra("id", item.id);
                startActivity(intent);

            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
        brightcoveAPI.retrieveVideos();

    }

    //checks to see if lesson pulled already exists locally
    public boolean isLessonPresent(LessonItem item){
        System.out.println("CHECKING FOR DUPLICATES");
        for (LessonItem lesson: mItems) {
            if (lesson.id.compareTo(item.id)==0)
                return true;
        }
        return false;

    }

    @Override
    public void receiveLessonItems(ArrayList<LessonItem> items) {
        System.out.println("LessonReceiverCalled" + items.size());
        saveItems(items);
        for (LessonItem lesson: items){

            if (!isLessonPresent(lesson)) {
                mItems.add(lesson);
            }

            mAdapter.notifyDataSetChanged();

            //System.out.println("TITLE:"+lesson.title);

            if (lesson.title.compareTo("Lesson 1 ") == 0){
                break;
            }
        }
    }

    @Override
    public void receivePDFItems(File file) {

    }
}