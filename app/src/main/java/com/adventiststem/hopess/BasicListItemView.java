package com.adventiststem.hopess;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.RequestCreator;

public class BasicListItemView extends RelativeLayout {
    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    // Request
    //private RequestCreator mRequest;
    private int mWidth;
    private int mHeight;
    public BasicListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mImageView = (ImageView) findViewById(R.id.lesson_thumbnail);
        mTitleTextView = (TextView) findViewById(R.id.lesson_title);
        mDescriptionTextView = (TextView) findViewById(R.id.lesson_description);
        final Resources resources = getResources();
        mWidth = resources.getDimensionPixelSize(R.dimen.thumbnail_image_width);
        mHeight = resources.getDimensionPixelSize(R.dimen.thumbnail_image_height);
    }

    /*public void bindView(Picasso picasso, Video video) {
        mTitleTextView.setText(video.getSnippet().getTitle());
        mDescriptionTextView.setText(video.getSnippet().getDescription());
        mRequest = picasso.load(video.getSnippet().getThumbnails().getMedium().getUrl());
        requestLayout();
    }
    */
    public void bindView() {
        mTitleTextView.setText("Lesson 1");
        mDescriptionTextView.setText("Lesson Description");
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*
        if (mRequest != null) {
            mRequest.resize(mWidth, mHeight).noFade().centerCrop().into(mImageView);
            mRequest = null;
        }
        */
    }
}
