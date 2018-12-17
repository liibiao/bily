package com.example.bilyli.myapplication;

import android.app.Application;

public class AppMain extends Application {
    public static AppMain instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
