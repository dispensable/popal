package com.example.dispensable.popal;

import android.app.Application;

import me.aflak.bluetooth.Bluetooth;

public class PopalApp extends Application {
    private String myState;
    private Bluetooth bluetooth;

    public String getState() {
        return myState;
    }
    public void setState(String s) {
        myState = s;
    }

    public Bluetooth getBluetooth() {
        return bluetooth;
    }

    public void setBluetooth(Bluetooth b) {
        bluetooth = b;
    }
}
