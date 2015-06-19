package com.adventiststem.hopess;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by allanmarube on 5/25/15.
 */
public class ArchiveViewFragment extends Fragment {

    private List<String> mItems; // ListView items list
    private ArrayAdapter mAdapter;
    private GridView gridView;

    private String currYear;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mItems = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        int yr = calendar.get(Calendar.YEAR);
        currYear = yr+"";

        for (int i = yr; i >= 2013; i--){
            mItems.add(i+"");

        }

        mAdapter = new ArchiveAdapter(getActivity(), mItems);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.archive_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gridView = (GridView) getActivity().findViewById(R.id.gridView);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //start an activity to display videos for this year

                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                b.setTitle("Quarter");
                //b.setIcon(R.drawable.ic_launcher);
                String[] types = {"1st", "2nd", "3rd", "4th"};
                b.setItems(types, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:
                                //first quarter
                                Intent intent = new Intent(getActivity(), LessonActivity.class);
                                intent.putExtra("quarter", 1);
                                intent.putExtra("year", mItems.get(position));
                                startActivity(intent);

                                break;
                            case 1:
                                //second quarter
                                Intent intent2 = new Intent(getActivity(), LessonActivity.class);
                                intent2.putExtra("quarter", 2);
                                intent2.putExtra("year", mItems.get(position));
                                startActivity(intent2);

                                break;
                            case 2:
                                //3rd  quarter
                                Intent intent3 = new Intent(getActivity(), LessonActivity.class);
                                intent3.putExtra("quarter", 3);
                                intent3.putExtra("year", mItems.get(position));
                                startActivity(intent3);
                                break;
                            case 3:
                                //4th quarter
                                Intent intent4 = new Intent(getActivity(), LessonActivity.class);
                                intent4.putExtra("quarter", 4);
                                intent4.putExtra("year", mItems.get(position));
                                startActivity(intent4);
                                break;
                        }

                        dialog.dismiss();
                    }

                });

                b.show();
            }
        });

    }


}