package com.adventiststem.hopess;

//import android.app.ListFragment;
import android.content.Context;
import android.support.v4.app.ListFragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.adventiststem.hopess.Utils.BrightcoveAPI;
import com.brightcove.player.media.Catalog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LessonViewFragment extends ListFragment implements PlayListCallBack{
    private List<LessonItem> mItems; // ListView items list
    private LessonAdapter mAdapter;
    private BrightcoveAPI brightcoveAPI;

    private static String EVENT_VIEW_FRAGMENT = "EVENTVIEWFRAGMENT";
    public static int CREATE_EVENT_INTENT = 3;

    public List<LessonItem> getEventsList(){
        return mItems;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize the items list
        mItems = new ArrayList<LessonItem>();
        Resources resources = getResources();

        mAdapter = new LessonAdapter(getActivity(), mItems);

        //Catalog catalog = new Catalog("MrqqXrGUW0ROWQ_ptCOuh1H4FVfUQpUQYQkEB4IUXsYF-INls6OJaw..");
        brightcoveAPI = new BrightcoveAPI(getActivity());
        brightcoveAPI.setReceiver(this);
        brightcoveAPI.retrieveVideos();


        //mItems.add(new LessonItem(resources.getDrawable(R.drawable.ic_launcher), "Lesson 4", "The Call to Discipleship", "Q2 2015"));
        //mItems.add(new LessonItem(resources.getDrawable(R.drawable.ic_launcher), "Lesson 3", "Who is Jesus Christ", "Q2 2015"));
        //mItems.add(new LessonItem(resources.getDrawable(R.drawable.ic_launcher), "Lesson 2", "Baptism and the Tempatations", "Q2 2015"));
        //mItems.add(new LessonItem(resources.getDrawable(R.drawable.ic_launcher), "Lesson 1", "The Coming of Jesus", "Q2 2015"));

        //DataBaseUtil.getInstance().get_event();
        // initialize and set the list adapter
        setListAdapter(mAdapter);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

    }

    @Override
    public void receiveLessonItems(ArrayList<LessonItem> items) {
        System.out.println("LessonReceiverCalled" + items.size());
        for (LessonItem lesson: items){
            mItems.add(lesson);
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