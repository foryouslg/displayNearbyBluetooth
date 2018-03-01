package com.face2face.displaynearbybluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final int  REQUEST_ENABLE_BT = 1;
    Button btEnableBluetooth,btDisableBluetooth,btEnableBluetoothLocation;
    BluetoothAdapter bluetoothAdapter;
    public final int REQUEST_COARSE_LOCATION = 2;
    ListView bluetoothList;
    List<String> data;
    ArrayAdapter<String> arrayAdapter;

    BroadcastReceiver  mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            final BluetoothDevice device;
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                data.add(device.getName() + ">" + device.getAddress());
//                arrayAdapter.add(device.getName() + ">" + device.getAddress());
                Log.i("TEST", device.getName() + "=====" + device.getAddress());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();                 //初始化
        openBluetooth();        //开启蓝牙
                                //发现附近蓝牙设备
        getBluetoothList();     //显示可用蓝牙设备列表

    }
    //开启蓝牙按键
    public void enableBluetooth(View view){
        openBluetooth();
    }

    //启动蓝牙发现。。。
    public void startBluetoothDisplay(View view){
        data.clear();
//        arrayAdapter.notifyDataSetChanged();
        if(bluetoothAdapter.startDiscovery()){


            Toast.makeText(this,"正在扫描...",Toast.LENGTH_SHORT).show();
        }
    }

    //开启设备可见
    public void enableDiscoverability(View view){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

        // 0，自身设备始终可以被发现（意味着将十分消耗设备资源，如电源）
        // 第二个参数可设置的范围是0~3600秒，在此时间区间（窗口期）内可被发现
        // 任何不在此区间的值都将被自动设置成120秒。
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);

        startActivity(discoverableIntent);
    }



    //获取蓝牙列表
    public void getBluetoothList(){
        data = new ArrayList<>();
        data.add("test:mobike>F0:A2:93:16:67:97");
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,data);
        bluetoothList.setAdapter(arrayAdapter);
    }



    //关闭蓝牙按键
    public void disableBluetooth(View view){
        if(bluetoothAdapter != null){
            if(bluetoothAdapter.isEnabled()){
                bluetoothAdapter.disable();
                Toast.makeText(this,"bluetooth is disabling",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"bluetooth is disabled",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this,"设备不支持蓝牙",Toast.LENGTH_SHORT).show();
        }
    }

    //开启定位权限按键
    public void enableBluetoothLocation(View view){
        int a = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(a != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_COARSE_LOCATION);
        }else {
            Toast.makeText(this,"Location is enabled",Toast.LENGTH_SHORT).show();
        }
    }

    //开启定位权限结果处理
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION:       //case 的变量必须是final
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"定位权限已开启",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this,"定位权限已禁止",Toast.LENGTH_SHORT).show();
                }
                break;
            case BluetoothAdapter.ERROR:
                break;
        }

    }

    //初始化
    public void init(){
        btEnableBluetooth = findViewById(R.id.btEnableBluetooth);
        btEnableBluetoothLocation = findViewById(R.id.btEnableBluetoothLocaton);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothList = findViewById(R.id.bluetoothList);
        btDisableBluetooth = findViewById(R.id.btDisableBluetooth);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);        //发现设备
        //        filter.addAction(BluetoothDevice.ACTION_CLASS_CHANGED);
        //        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        //        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //        filter.addAction(BluetoothAdapter.EXTRA_STATE);
        registerReceiver(mReceiver, filter);

    }

    //打开蓝牙方法
    public void openBluetooth(){
        if(bluetoothAdapter != null){
            if(!bluetoothAdapter.isEnabled()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);  //启动intent,并发送request_code
            }else {
                Toast.makeText(this,"bluetooth is enabled",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this,"设备不支持蓝牙",Toast.LENGTH_SHORT).show();
        }
    }

    //
    @Override
    protected void onResume() {
        super.onResume();


    }


    @Override
    protected void onStop() {
        super.onStop();
//        unregisterReceiver(mReceiver);    //必须注销mReceiver,否则即出应用会报错
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);    //必须注销mReceiver,否则即出应用会报错
    }

}
