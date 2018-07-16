package com.example.pong.test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT=1;//打開藍芽常數intent
    private int ENABLE_INDICATION_VALUE=1;
    private Handler mHandler= new Handler();//未使用
    Boolean  mScanning=true;//判斷掃描狀態布林變數
    private ArrayAdapter<String> deviceName;//未使用
    public BluetoothGatt mBluetoothGatt=null;
    public String str="",str2="";
    private static final String serviceid = "0000AAAA-0000-1000-8000-00805f9b34fb";//0000AAAA-0000-1000-8000-00805f9b34fb //6e400001-b5a3-f393-e0a9-e50e24dcca9e
    private static final String charaid   = "0000AAAD-0000-1000-8000-00805f9b34fb";
    private static final String notifyid  = "0000AAAC-0000-1000-8000-00805f9b34fb";//0000AAAC-0000-1000-8000-00805f9b34fb //6e400003-b5a3-f393-e0a9-e50e24dcca9e
    private static final String writeid   = "0000AAAB-0000-1000-8000-00805F9B34FB";//0000AAAB-0000-1000-8000-00805F9B34FB //6e400002-b5a3-f393-e0a9-e50e24dcca9e
    private static final String descrid  =  "00002902-0000-1000-8000-00805f9b34fb";//00002902-0000-1000-8000-00805f9b34fb
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mcontext=MainActivity.this;
        /*************************取得Adapter初始設定*************************************/
        bluetoothManager=(BluetoothManager) getSystemService(MainActivity.this.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        /*************************判斷藍芽是否開啟並主動開啟*************************************/
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        show("藍芽已開啟!");
        /*************************藍芽開始掃描************************************************/
        scanLeDevice(mScanning);
    }
    private void scanLeDevice(final boolean enable) {
        show("開始掃描");
        if (enable) {
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback); //开始搜索
            Log.d("測試:","開始搜尋");
            deviceName = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1);
        }else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止搜索
        }
    }
    /*************************************掃描的Call Back****************************************/
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //show("搜尋中....");
                    Log.d("測試:","搜尋中");
                            //SBC21120054為藍芽設備名稱  //D2 01 11 F9 8F B3 //00:08:F4:01:8A:12舊的
                    if(device.getAddress().equals("00:08:F4:01:8A:12")==true ) {
                        /**********************要連接的設備放到listview*******************************/
                        deviceName.add(device.getName() + "\n" + device.getAddress());
                        ListView listview = (ListView) findViewById(R.id.listview);
                        listview.setAdapter(deviceName);
                        /**************************停止搜尋**************************************/
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止搜索 ~關閉掃描~~
                        String bleAddress = device.getAddress();
                        BluetoothDevice mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(bleAddress);
                        show("嘗試連接...:"+device);
                        /************************啟動連接*************************************/
                       mBluetoothGatt = mBluetoothDevice.connectGatt(MainActivity.this,false,mGattCallback);//啟動連接
                        Log.d("測試:","進入連接藍芽call back!");
                    }
                }
            });
        }
    };
    /*****************************藍芽連接的Call Back****************************************/
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback(){
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // 當連線或斷線將會進到這裡

            super.onConnectionStateChange(gatt, status, newState);
            if(status == BluetoothGatt.GATT_SUCCESS){//GATT_SUCCESS=0才成功
                Log.d("測試:", "還沒服務!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!:"+status+"連線狀態:"+newState);
                if (newState == BluetoothGatt.STATE_CONNECTED) {//0為斷線 2為可連接
                    Log.d("測試:", "開始服務!!!!!!status="+status+" newstate"+newState);
                    mBluetoothGatt.discoverServices();//執行onServicesDiscovered
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    Log.d("測試:", "斷開連接" + newState);
                }
            }
        }
        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("測試:", "發現服務...Status:" + status);
                BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(serviceid));
                BluetoothGattCharacteristic characteristic= service.getCharacteristic(UUID.fromString(notifyid));
                BluetoothGattCharacteristic writ_characteristic= service.getCharacteristic(UUID.fromString(writeid));//未使用
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(descrid));
                //serviceid="0000AAAA-0000-1000-8000-00805f9b34fb"  服務(舊)
                //notifyid= "0000AAAC-0000-1000-8000-00805f9b34fb"  N通知(舊)
                //descrid=  "00002902-0000-1000-8000-00805f9b34fb" descr描述(舊新通用)
                //通知步驟 開啟+setvalue+writ(描述)

                boolean notification=mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                Log.i("測試", "setCharactNotify: "+notification);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);

            }else {
                Log.e("測試", "onservicesdiscovered收到: " + status);
            }
            super.onServicesDiscovered(gatt, status);
        }
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            Log.d("測試:","進入!通知onCharacteristicChanged收到的值:"+characteristic.getValue());
            byte[] arrayOfByte = characteristic.getValue();//arrayOfByte.length=2
            String h = "";
            String h1 = "";
            /*for(int i = 0; i < arrayOfByte.length; i++){
                String temp = Integer.toHexString(arrayOfByte[i] & 0xFF);
                if(temp.length() == 1){
                    temp = "0" + temp;
                }
                h = h + temp;
            }*/
             h = Integer.toHexString(arrayOfByte[0] & 0xFF);//Level 把帶入的數字回傳16進制
             h1 = Integer.toHexString(arrayOfByte[1] & 0xFF);//RPM

            Log.e("測試", " H1 收到: " + h+"  H1 收到: "+h1);

            str=Integer.valueOf(h,16).toString();//應該是將16進制轉成10進制的字串
            str2=Integer.valueOf(h1,16).toString();
            Log.e("測試", " str 收到: " + str+"  str2 收到: "+ str2);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    receivedata();
                }
            });
            Log.d("測試:","[經過轉換]進入!通知onCharacteristicChanged:"+str);
        }
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] bytes=characteristic.getValue();
                Log.e(" 測試:", "CharacteristicRead的讀取" +bytes);
                Log.e(" 測試:", "CharacteristicRead的讀取" +characteristic.getValue());
            }else{
                Log.e("錯誤", "失敗..status != BluetoothGatt.GATT_SUCCESS");
            }
            super.onCharacteristicRead(gatt, characteristic, status);
        }
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.e("測試", "onDescriptorWrite寫入裡面!!!:"+descriptor.getValue());
        }
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.e("測試", "onDescriptorReaq讀取裡面!!!:"+descriptor.getValue());
        }
    };
    /*****************************顯示文字****************************************/
    public void show(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
    public void receivedata(){
        TextView rec_data=(TextView) findViewById(R.id.textView);
        TextView rec_data2=(TextView) findViewById(R.id.textView2);
        rec_data.setText("Level: "+str);
        rec_data2.setText("RPM: "+str2);
        GlobalVariable map_data = (GlobalVariable)getApplicationContext();//全域變數設定
            map_data.setdata(str,str2);//傳送level 和 Rpm到全域變數
    }
    /*****************************離開頁面關閉掃描****************************************/
    //需要注意的是，需加入一個stopLeScan在onPause()中，當按返回鍵或關閉程式時，需停止搜尋BLE
    @Override
    protected void onPause() {
        super.onPause();
        show("跳出");
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
       // mBluetoothGatt.disconnect(); //這兩行可能讓藍芽斷線
        //mBluetoothGatt.close();
        Log.e("測試", "我有跑到OnPause了!!!!!!OnPause!!!!!!!!!!!");
    }
    /*****************************GoogleMap按鈕****************************************/
    //顯性INTENT就是直接指定某人動作，隱性就是有符合的人都可以讓使用選擇去做
    public void Onclick(View view){
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.btn_map:
                intent.setClass(this,MapsActivity.class);
                //mBluetoothAdapter.stopLeScan(mLeScanCallback);
                //intent.putExtra("LEVEL",str);
                this.startActivity(intent);
                break;
        }
    }



}
