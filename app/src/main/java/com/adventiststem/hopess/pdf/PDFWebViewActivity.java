package com.adventiststem.hopess.pdf;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * NOTE- This class does work correctly but is not being utilized in the current revision of the application.
 * I am keeping this here in case we need it in the future.
 *
 * PDF Web View Activity
 *
 * PDF Web View Activity - Activity responsible for displaying a PDF file
 * This Activity is to be used on devices with an API level lower than 19 (< 4.4/KitKat)
 *
 * @author Jan Chris "JccJcat" Tacbianan
 */

public class PDFWebViewActivity extends Activity {


    /**
     * onCreate
     *
     * Called on creation of the Activity.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the
     *                           data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String pdfURL = extras.getString("PdfUrl"); //Retrieve PDF URL
        WebView webView=new WebView(PDFWebViewActivity.this); //Create WebView
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebViewClient(new WebViewClient() { //This  is prevent opening a new browser from within the WebView
            @Override
            public boolean shouldOverrideUrlLoading(WebView wv, String link) {
                return false;
            }
        });
        webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + pdfURL); //Get Google Doc PDF View
        setContentView(webView); //View our PDF.
    }

    /**
     * onPause
     *
     * Called when the activity is no longer being displayed.
     * We are using it to finish the activity when it is no longer in view.
     * We don't want a huge build up of PDF views.
     */
    @Override
    protected void onPause() {
        finish();
    }

}
