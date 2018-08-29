package com.example.dispensable.popal;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;

import java.lang.reflect.Method;


public class CanvasActivity extends BlunoLibrary {
    private boolean hasConnected = false;
    private AudioRecordDemo audioRecordDemo;
    private static Handler mainHandler;
    private LottieAnimationView lottieAnimationView;
    private fireflyStatus nowStatus = fireflyStatus.still;

    public enum fireflyStatus {
        still,
        whole
    }

    private boolean use_wave = false;

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
                if (lottieAnimationView.isAnimating()) {
                    Log.i("--->>> return", "return");
                    return;
                }

                if (msg.what == 1 && !use_wave) {
                    Toast.makeText(CanvasActivity.this, "sending: " + msg.what, Toast.LENGTH_LONG).show();
                    showAnimation("firefly_out", "firefly_out.json", 0);
                    serialSend("1");
                }
            }
        };

        setFullScreen(this);
        if (hasNavBar(this)) {
            hideBottomUIMenu();
        }

        setScreenOn();

        lottieAnimationView = (LottieAnimationView) findViewById(R.id.animation_view);
        setTouchListener();

        // show wave still
        if (use_wave) {
            showAnimation("wave_whole_image", "wave_whole_image.json", 0);
        } else {
            // set firefly still
            showFirefly(fireflyStatus.whole, 0);
        }
    }

    @Override
    public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
        // when you get msg from arduino
        Log.i("recieved------------:", theString);
        if (theString.equals("3")) {

            use_wave = !use_wave;
            lottieAnimationView.cancelAnimation();
            if (!use_wave) {
                showAnimation("firefly_in", "firefly_in.json", 0);
            } else {
                showAnimation("wave_whole_image", "wave_whole_image.json", 0);
            }
        }

        if (theString.equals("1") && !use_wave) {
            showAnimation("firefly_in", "firefly_in.json", 0);
        }

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
                Intent intent = getIntent();
                int noiseValue = 70;
                try {
                    noiseValue = Integer.parseInt(intent.getStringExtra("noise_value"));
                } catch (NumberFormatException e) {
                    Toast.makeText(CanvasActivity.this, "this is not a number, will use 70", Toast.LENGTH_LONG).show();
                }

                audioRecordDemo = new AudioRecordDemo(noiseValue, 5, mainHandler);
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

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu(){
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
            decorView.setSystemUiVisibility(uiOptions);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 检查是否存在虚拟按键栏
     * @param context
     * @return
     */
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onResume(){
        super.onResume();
        System.out.println("BlUNOActivity onResume");
        onResumeProcess();														//onResume Process by BlunoLibrary
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();														//onPause Process by BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();														//onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();														//onDestroy Process by BlunoLibrary
    }

    private void setScreenOn() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void showAnimation(String imageFolder, String jsonFile, int repeatCount) {
        lottieAnimationView.setRepeatCount(repeatCount);
        lottieAnimationView.setImageAssetsFolder(imageFolder);
        lottieAnimationView.setAnimation(jsonFile, LottieAnimationView.CacheStrategy.Strong);
        lottieAnimationView.playAnimation();
    }

    private void showFirefly(fireflyStatus status, int repeatCount) {
        if (use_wave) {
            return;
        }

        switch (status) {
            case still:
                showAnimation("firefly_still_image", "firefly_still_data.json", repeatCount);
                break;
            case whole:
                showAnimation("firefly_whole_image", "firefly_whole_data.json", repeatCount);
                break;
            default:
                showAnimation("firefly_still_image", "firefly_still_data.json", repeatCount);
        }
    }

    private void setTouchListener() {
        lottieAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(">>> touch me: ", "has been touched!");
                if (!lottieAnimationView.isAnimating() && use_wave) {
                    showAnimation("wave_whole_image", "wave_whole_image.json", 0);
                    serialSend("4");
                }
            }
        });
    }

}
