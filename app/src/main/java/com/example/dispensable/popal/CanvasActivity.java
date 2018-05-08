package com.example.dispensable.popal;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


public class CanvasActivity extends BlunoLibrary {
    private boolean hasConnected = false;
    private AudioRecordDemo audioRecordDemo;
    private static Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        onCreateProcess();														//onCreate Process by BlunoLibrary
        serialBegin(115200);
        connectToBluno();

        mainHandler = new Handler()
        {
            public void dispatchMessage(android.os.Message msg) {
                Toast.makeText(CanvasActivity.this, "Get: " + msg.what, Toast.LENGTH_LONG).show();
                if (msg.what == 1) {
                    Log.e("VVVVVVVV", "1");
                    serialSend("1");
                } else if (msg.what == 2) {
                    Log.e("VVVVVVVV", "2");
                    serialSend("2");
                }
            }
        };
    }

    @Override
    public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
        // when you get msg from arduino
        Log.i("recieved:", theString);
    }

    public void connectToBluno() {
        buttonScanOnClickProcess();
    }

    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
        switch (theConnectionState) {											//Four connection state
            case isConnected:
                Log.e("BBB:", "connected!");
                hasConnected = true;
                audioRecordDemo = new AudioRecordDemo(70, 5, mainHandler);
                audioRecordDemo.getNoiseLevel();
                break;
            case isConnecting:
                hasConnected = false;
                Log.e("BBB:", "connecting!");
                break;
            case isToScan:
                hasConnected = false;
                Log.e("BBB:", "scan!");
                break;
            case isScanning:
                hasConnected = false;
                Log.e("BBB:", "scanning");
                break;
            case isDisconnecting:
                hasConnected = false;
                Log.e("BBB:", "disconnected!");
                break;
            default:
                break;
        }
    }

    /**
     * 设置隐藏标题栏
     *
     * @param activity
     */
    public static void setNoTitleBar(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * 设置全屏
     *
     * @param activity
     */
    public static void setFullScreen(Activity activity) {
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 取消全屏
     *
     * @param activity
     */
    public static void cancelFullScreen(Activity activity) {
        activity.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void startAudioRecording() {
        audioRecordDemo = new AudioRecordDemo(70, 5, mainHandler);
        audioRecordDemo.getNoiseLevel();
    }
}
