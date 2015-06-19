package com.adventiststem.hopess;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by allanmarube on 6/7/15.
 */
public class LessonGridAdapter extends ArrayAdapter<LessonItem> {

    public LessonGridAdapter(Context context, List<LessonItem> items) {
        super(context, R.layout.gridview_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.gridview_item, parent, false);
            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.gridpicture);
           // viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.gridtext);
           // viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            convertView.setTag(viewHolder);

        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // update the item view
        LessonItem item = getItem(position);
        //viewHolder.ivIcon.setImageDrawable(item.icon);
        Picasso.with(getContext()).load(item.videoStillURL).into(viewHolder.ivIcon);
        //viewHolder.tvTitle.setText(item.title);
        String [] titlesplit = item.title.split(" ");
        viewHolder.tvDescription.setText(titlesplit[1]+" - "+item.description);
       // viewHolder.tvDate.setText(item.date);
        return convertView;
    }
    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     *
     *  http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
     */
    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvDate;
    }
}
