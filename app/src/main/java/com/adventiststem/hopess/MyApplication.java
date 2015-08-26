package com.adventiststem.hopess;

import android.app.Application;

import com.flurry.android.FlurryAgent;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

/**
 * Created by allanmarube on 8/26/15.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {


        super.onCreate();

        String MY_FLURRY_APIKEY = "6WWMTQ72D2CD8HJW4GTV";

        System.out.println("Enabling Flurry Analytics....");

        // configure Flurry
        FlurryAgent.setLogEnabled(true);

        // init Flurry
        FlurryAgent.init(this, MY_FLURRY_APIKEY);

        //Initializing Parse push notification service
        Parse.initialize(this, "KIYanTB8nEID6slbFVoN5XpOqG4YbsEuj2igqw8O", "0orFOvHQE3XfOYWm9EWCa1rS1K5GaKZ6gQaD8IlZ");
        ParseInstallation.getCurrentInstallation().saveInBackground();




    }


}
