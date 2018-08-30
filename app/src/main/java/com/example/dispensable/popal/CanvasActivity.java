package com.example.dispensable.popal;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.app.Activity;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;

import java.lang.reflect.Method;


public class CanvasActivity extends BlunoLibrary implements SensorEventListener {
    private boolean hasConnected = false;
    private AudioRecordDemo audioRecordDemo;
    private static Handler mainHandler;
    private LottieAnimationView lottieAnimationView;
    private boolean use_flower = true;
    private fireflyStatus nowStatus = fireflyStatus.still;
    private SensorManager mManager;//传感器管理对象

    public enum fireflyStatus {
        still,
        whole
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        onCreateProcess();														//onCreate Process by BlunoLibrary
        serialBegin(115200);
        connectToBluno();
        setFullScreen(this);
        if (hasNavBar(this)) {
            hideBottomUIMenu();
        }
        setScreenOn();
        // set firefly still
        lottieAnimationView = (LottieAnimationView) findViewById(R.id.animation_view);

        if (!use_flower) {
            mManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mManager.registerListener(this, mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                    SensorManager.SENSOR_DELAY_NORMAL);
            showAnimation("firefly_whole_image", "firefly_whole_data.json", -1);
        } else {
            showAnimationButNotPlay("firefly_whole_image", "firefly_whole_data.json", 0);
        }
    }

    @Override
    public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
        // when you get msg from arduino
        Log.i("recieved:", theString);
        if (theString.equals("1") && use_flower && !lottieAnimationView.isAnimating()) {
            lottieAnimationView.playAnimation();
        }

        if (theString.equals("3")) {
            use_flower = !use_flower;
            if (use_flower) {
                showAnimationButNotPlay("firefly_whole_image", "firefly_whole_data.json", 0);
            } else {
                showAnimation("firefly_whole_image", "firefly_whole_data.json", -1);
                if (mManager == null) {
                    mManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                    mManager.registerListener(this, mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                            SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] its = event.values;

        Log.d("|---> ","sensor type :" + event.sensor.getType() + " proximity type:" + Sensor.TYPE_PROXIMITY);
        if (its != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY && !use_flower) {
            //经过测试，当手贴近距离感应器的时候its[0]返回值为0.0，当手离开时返回1.0
            Log.e("| ----> ", "sensor value is: " + its[0]);
            float value = its[0];
            if (value == 0.0) {
                lottieAnimationView.pauseAnimation();
                serialSend("2");
            } else {
                if (!lottieAnimationView.isAnimating()) {
                    lottieAnimationView.resumeAnimation();
                    serialSend("5");
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO 自动生成的方法存根
    }

    public void connectToBluno() {
        buttonScanOnClickProcess();
    }

    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
        switch (theConnectionState) {											//Four connection state
            case isConnected:
                Log.e("BBB:", "connected!");
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
        onDestroyProcess();
        if(mManager != null){
            mManager.unregisterListener(this);//注销传感器监听
        }//onDestroy Process by BlunoLibrary
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

    private void showAnimationButNotPlay(String imageFolder, String jsonFile, int repeatCount) {
        lottieAnimationView.setRepeatCount(repeatCount);
        lottieAnimationView.setImageAssetsFolder(imageFolder);
        lottieAnimationView.setAnimation(jsonFile, LottieAnimationView.CacheStrategy.Strong);
    }

}
