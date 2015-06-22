package com.adventiststem.hopess.pdf;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.os.Bundle;
import android.webkit.WebView;

import com.adventiststem.hopess.R;
import com.joanzapata.pdfview.PDFView;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * PDF Viewer Activity
 *
 * PDF Viewer Activity - Activity responsible for rendering and displaying a PDF file.
 * This Activity utilizes the built in PDF document classes in Andriod API 19+.
 *
 * @author Jan Chris "JccJcat" Tacbianan
 */

public class PDFViewerActivity  extends Activity {
    /**
     * onCreate
     *
     * Called on creation of the Activity.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains
     *                           the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        final String pdfLocation = extras.getString("pdfLoc"); //Retrieving PDF Location
        setContentView(R.layout.pdf_layout);
        final PDFView view = (PDFView) findViewById(R.id.pdf_view);
        File pdf = new File(pdfLocation);
        //Log.i("PDFViewerActivity: ", "Prog: " + pdf.exists());
        view.fromFile(pdf).defaultPage(1).showMinimap(false).enableSwipe(true).load();
    }

}
