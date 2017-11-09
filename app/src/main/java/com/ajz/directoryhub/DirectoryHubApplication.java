package com.ajz.directoryhub;

import android.app.Application;
import android.content.Context;

/**
 * Created by adamzarn on 11/9/17.
 */

public class DirectoryHubApplication extends Application {

    private static DirectoryHubApplication instance;
    private static Context mContext;

    public static Context getContext(){
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        instance = this;
    }

    public static synchronized DirectoryHubApplication getInstance() {
        return instance;
    }

}
