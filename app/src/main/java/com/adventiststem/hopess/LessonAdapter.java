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

public class LessonAdapter extends ArrayAdapter<LessonItem> {

    public LessonAdapter(Context context, List<LessonItem> items) {
        super(context, R.layout.lesson_list_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.lesson_list_item, parent, false);
            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            convertView.setTag(viewHolder);

        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // update the item view
        LessonItem item = getItem(position);
        //viewHolder.ivIcon.setImageDrawable(item.icon);
        Picasso.with(getContext()).load(item.thumbnailURL).into(viewHolder.ivIcon);
        //viewHolder.tvTitle.setText(item.title);
        viewHolder.tvDescription.setText(item.title.split(" ")[1]+" -"+item.description);
        viewHolder.tvDate.setText(item.date);
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