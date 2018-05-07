package com.example.dispensable.popal;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.BluetoothCallback;
import me.aflak.bluetooth.DiscoveryCallback;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.dispensable.MESSAGE";
    int yourChoice;
    public Bluetooth bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner selectedOpt = (Spinner) findViewById(R.id.spinner);
        selectedOpt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                String[] languages = getResources().getStringArray(R.array.cases);
                Toast.makeText(MainActivity.this, "你点击的是:"+languages[pos], Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        bluetooth = new Bluetooth(getApplicationContext());
        PopalApp popalApp = (PopalApp) getApplicationContext();
        popalApp.setBluetooth(bluetooth);
    }

    /** Call when the user click send button **/
    public void sendMessage(View view) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        Toast.makeText(MainActivity.this, "btn clicked!!", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    public void addScenes(View view) {
        Intent intent = new Intent(this, ScenesActivity.class);
        startActivity(intent);
    }

    private void showSingleChoiceDialog(final String[] items){
        yourChoice = -1;
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(MainActivity.this);
        singleChoiceDialog.setTitle("我是一个单选Dialog");
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yourChoice = which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (yourChoice != -1) {
                            Toast.makeText(MainActivity.this,
                                    "你选择了" + items[yourChoice],
                                    Toast.LENGTH_SHORT).show();
                            bluetooth.connectToName(items[yourChoice]);
                        }
                    }
                });
        singleChoiceDialog.show();
    }

    public void searchBluetooth(View view) {
        bluetooth.onStart();
        if(!bluetooth.isEnabled())
            bluetooth.enable();

        bluetooth.setBluetoothCallback(new BluetoothCallback() {
            @Override
            public void onBluetoothTurningOn() {}

            @Override
            public void onBluetoothOn() {}

            @Override
            public void onBluetoothTurningOff() {}

            @Override
            public void onBluetoothOff() {}

            @Override
            public void onUserDeniedActivation() {
                // when using bluetooth.showEnableDialog()
                // you will also have to call bluetooth.onActivityResult()
            }
        });

        bluetooth.setDiscoveryCallback(new DiscoveryCallback() {
            @Override public void onDiscoveryStarted() {
                Toast.makeText(MainActivity.this, "starting discovery ...", Toast.LENGTH_LONG).show();
            }

            @Override public void onDiscoveryFinished() {
                Toast.makeText(MainActivity.this, "ending discovery ...", Toast.LENGTH_LONG).show();
                List<BluetoothDevice> devices = bluetooth.getPairedDevices();
                List<String> devicesName = new ArrayList<String>();
                for(BluetoothDevice singleDevice: devices) {
                    Toast.makeText(MainActivity.this, "Get: " + singleDevice.getName(), Toast.LENGTH_LONG).show();
                    devicesName.add(singleDevice.getName());
                }

                String[] devicesNameArray = new String[devicesName.size()];
                devicesNameArray = devicesName.toArray(devicesNameArray);
                MainActivity.this.showSingleChoiceDialog(devicesNameArray);
            }
            @Override public void onDeviceFound(BluetoothDevice device) {
                Toast.makeText(MainActivity.this, "Get: " + device.getName(), Toast.LENGTH_LONG).show();
            }
            @Override public void onDevicePaired(BluetoothDevice device) {}
            @Override public void onDeviceUnpaired(BluetoothDevice device) {}
            @Override public void onError(String message) {}
        });

        bluetooth.startScanning();
    }
}
