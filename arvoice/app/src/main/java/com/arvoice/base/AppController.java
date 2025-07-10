package com.arvoice.base;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

public class AppController extends MultiDexApplication {
    public static final String TAG = "AppController";
    private static AppController mInstance;
    private static Context mContext;

    public static synchronized AppController getInstance() {
        AppController appController;
        synchronized (AppController.class) {
            appController = mInstance;
        }
        return appController;
    }

    public static Context getContext() {
        return mContext;
    }

    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = getApplicationContext();
        MultiDex.install(this);
    }


}
