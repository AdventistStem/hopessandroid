package com.adventiststem.hopess;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;
import com.adventiststem.hopess.Utils.BrightcoveAPI;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by allanmarube on 5/25/15.
 */
public class LessonViewGridFragment extends Fragment implements PlayListCallBack {

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
        brightcoveAPI = new BrightcoveAPI(getActivity());
        brightcoveAPI.setReceiver(this);
        brightcoveAPI.retrieveVideos();

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