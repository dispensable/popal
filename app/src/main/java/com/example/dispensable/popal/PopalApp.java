package com.example.dispensable.popal;

import android.app.Application;

public class PopalApp extends Application {
    private String myState;
    public MainActivity main;

    public String getState() {
        return myState;
    }
    public void setState(String s) {
        myState = s;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
