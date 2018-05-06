package com.example.dispensable.popal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import eu.basicairdata.bluetoothhelper.BluetoothHelper;

public class BlutoothActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //定义
    private BluetoothAdapter mBluetoothAdapter;
    private TextView text,text2,text3;
    private Button botton;
    private List<String> bluetoothList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blutooth);


        text=(TextView) this.findViewById(R.id.textView);  //已配对
        text2= (TextView) this.findViewById(R.id.textView2); //状态信息
        text3= (TextView) this.findViewById(R.id.textView3); //未配对
        botton=(Button) this.findViewById(R.id.button);

        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver,filter);
        IntentFilter filter2=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver,filter2);

        botton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {

                if(!mBluetoothAdapter.isEnabled())
                {
                    mBluetoothAdapter.enable();

                }

                mBluetoothAdapter.startDiscovery();
                text2.setText("正在搜索...");

            }


        });


    }


    public void onDestroy() {

        super.onDestroy();
        //解除注册
        unregisterReceiver(mReceiver);
        Log.e("destory","解除注册");
    }



    //定义广播接收
    private BroadcastReceiver mReceiver=new BroadcastReceiver(){



        @Override
        public void onReceive(Context context, Intent intent) {

            String action=intent.getAction();

            Log.e("ywq", action);
            if(action.equals(BluetoothDevice.ACTION_FOUND))
            {
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device.getBondState()==BluetoothDevice.BOND_BONDED)
                {    //显示已配对设备
                    text.append("\n"+device.getName()+"==>"+device.getAddress()+"\n");
                }else if(device.getBondState()!= BluetoothDevice.BOND_BONDED)
                {
                    text3.append("\n"+device.getName()+"==>"+device.getAddress()+"\n");
                }

                bluetoothList.add(device.getName());

            }else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){

                text2.setText("搜索完成...");

                // add a bluttooth list to the view
                Spinner spinner = (Spinner) findViewById(R.id.spinner2);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(BlutoothActivity.this, android.R.layout.simple_spinner_item, bluetoothList);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(BlutoothActivity.this);

                LinearLayout mainLayout = (LinearLayout) findViewById(R.id.bluetooth_view);
                LayoutInflater layoutInflater = LayoutInflater.from(BlutoothActivity.this);
                View bluetoothDeviceListLayout = layoutInflater.inflate(R.layout.blutooth_device_list, null);
                mainLayout.addView(bluetoothDeviceListLayout);
            }

        }


    };


    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String deviceName = (String) parent.getItemAtPosition(pos);
        Toast.makeText(BlutoothActivity.this, "Device Selected:" + deviceName, Toast.LENGTH_LONG).show();
        BluetoothHelper mBluetoothHelper = new BluetoothHelper();
        mBluetoothHelper.Connect(deviceName);

        mBluetoothHelper.setBluetoothHelperListener(new BluetoothHelper.BluetoothHelperListener() {
            @Override
            public void onBluetoothHelperMessageReceived(BluetoothHelper bluetoothhelper, final String message) {
                // Do something with the message received
                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         // Update your UI
                         Toast.makeText(BlutoothActivity.this, "Message recieved: " + message, Toast.LENGTH_LONG).show();
                     }
                 });
            }

            @Override
            public void onBluetoothHelperConnectionStateChanged(BluetoothHelper bluetoothhelper, boolean isConnected) {
                // Do something, depending on the new connection status
                Toast.makeText(BlutoothActivity.this, "Device Connection:" + isConnected, Toast.LENGTH_LONG).show();
            }
        });

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


}
