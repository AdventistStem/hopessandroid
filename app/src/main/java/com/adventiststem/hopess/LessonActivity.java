package com.adventiststem.hopess;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.adventiststem.hopess.Utils.BrightcoveAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by allanmarube on 5/25/15.
 */
public class LessonActivity extends Activity implements PlayListCallBack  {

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
                startActivity(intent);
            }
        });

        mItems = new ArrayList<LessonItem>();
        mAdapter = new LessonAdapter(this, mItems);
        listView.setAdapter(mAdapter);

        brightcoveAPI = new BrightcoveAPI(this);
        brightcoveAPI.setReceiver(this);
        brightcoveAPI.searchVideos(year, quarter);

       // Toast.makeText(this, "Year: "+year+" Quarter: "+quarter+"", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void receiveLessonItems(ArrayList<LessonItem> items) {
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