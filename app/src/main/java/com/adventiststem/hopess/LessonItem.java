package com.adventiststem.hopess;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class LessonItem implements Serializable, Comparable {
    public Drawable icon; // the drawable for the ListView item ImageView
    public String title; // the text for the ListView item title
    public String description; // the text for the ListView item description
    public String date;
    public String videoURL;
    public  String thumbnailURL;
    public String audioURL;
    public String pdfURL;
    public String id;
    public String videoStillURL;

    public LessonItem(Drawable icon, String title, String description, String date) {
        this.icon = icon;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public LessonItem(){

    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;

    }

    public void setTitle(String title){
        this.title = title;

    }
    public void setDescription(String description){
        this.description = description;

    }
    public void setDate(String date){
        this.date = date;

    }
    public void setThumbnailURL(String thumbnailURL){
        this.thumbnailURL = thumbnailURL;

    }

    public void setAudioURL(String audioURL){
        this.audioURL = audioURL;
    }

    public void setPdfURL(String pdfURL){
        this.pdfURL = pdfURL;
    }

    @Override
    public int compareTo(Object another) {
        LessonItem anotherItem = (LessonItem) another;
        return new Integer(this.title.split(" ")[1]).compareTo(new Integer(anotherItem.title.split(" ")[1]));
    }
}
