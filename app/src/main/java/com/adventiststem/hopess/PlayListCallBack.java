package com.adventiststem.hopess;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by allanmarube on 4/27/15.
 */
public interface PlayListCallBack {
    void receiveLessonItems(ArrayList<LessonItem> items);
    void receivePDFItems(File file);
}
