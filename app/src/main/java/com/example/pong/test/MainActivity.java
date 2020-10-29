package com.example.pong.test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    List<BluetoothDevice> deviceList = new ArrayList<>();
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT=1;//打開藍芽常數intent
    private int ENABLE_INDICATION_VALUE=1;
    private Handler mHandler= new Handler();//未使用
    Boolean  mScanning=true;//判斷掃描狀態布林變數
    private ArrayAdapter<String> deviceName;
    public BluetoothGatt mBluetoothGatt=null;
    public String str="",str2="",bleAddress_re="";
    public int i=0;         //給掃描到的裝置暫存給vData[i]
    public String[] vData =new String[100];//給掃描到的裝置暫存資料
    private static final String serviceid = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";//0000AAAA-0000-1000-8000-00805f9b34fb //6e400001-b5a3-f393-e0a9-e50e24dcca9e
    private static final String charaid   = "0000AAAD-0000-1000-8000-00805f9b34fb";
    private static final String notifyid  = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";//0000AAAC-0000-1000-8000-00805f9b34fb //6e400003-b5a3-f393-e0a9-e50e24dcca9e
    private static final String writeid   = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";//0000AAAB-0000-1000-8000-00805F9B34FB //6e400002-b5a3-f393-e0a9-e50e24dcca9e
    private static final String descrid  =  "00002902-0000-1000-8000-00805f9b34fb";//00002902-0000-1000-8000-00805f9b34fb
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mcontext=MainActivity.this;
        /*************************取得Adapter初始設定*************************************/
        bluetoothManager=(BluetoothManager) getSystemService(MainActivity.this.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        /*************************判斷藍芽是否開啟並跑出訊息詢問開啟*************************************/
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        show("藍芽已開啟!");
        /*************************藍芽開始掃描************************************************/
        scanLeDevice(mScanning);//這邊會先判斷是否開啟中，沒有的話則開始掃
    }


    // BLE 5.0+版本-----------------------------------------
   /* private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d("測試:","callbackType："+callbackType+"  result："+result);
            // callbackType：确定这个回调是如何触发的
            // result：包括4.3版本的蓝牙信息，信号强度rssi，和广播数据scanRecord

            ListView listview = (ListView) findViewById(R.id.listview);
            if (!deviceList.contains(result)) {
                //将设备加入列表数据中
                deviceList.add(result);
                String name=result.getName();
                if(result.getAddress().equals("D3:30:0F:06:D6:4B") ||result.getAddress().equals("DA:3A:0E:47:21:68")||device.getAddress().equals("EC:4F:61:6F:C2:68")){
                    name="CYUT_EMG";
                }
                deviceName.add(name + "\n" + result.getAddress());
                vData[i] = result.getAddress();
                i++;
            }
            listview.setAdapter(deviceName);
            //以下獲取listview的點擊
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parnet, android.view.View v, final int position, long id) {
                    Log.d("測試","deviceName: "+vData[position]);
                    Log.d("測試", String.valueOf(position));
                    // mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止搜索 ~關閉掃描~~
                    String bleAddress =vData[position];//要連接的裝置~要能依照listview的選擇而變
                    bleAddress_re=bleAddress;           //bleAddress_re 應該是用來儲存之後重連MAC 地址
                    BluetoothDevice mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(bleAddress);
                    show("嘗試連接...:"+vData[position]);
                    //啟動連接
                    mBluetoothGatt = mBluetoothDevice.connectGatt(MainActivity.this,true,mGattCallback);//啟動連接
                    Log.d("測試:","進入連接藍芽call back!");
                }
            });
        }
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            // 批量回调，一般不推荐使用，使用上面那个会更灵活
        }
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            // 扫描失败，并且失败原因
        }
    };
    */
    //-----------------------------------------------------
    private void scanLeDevice(final boolean enable) {
        show("開始掃描");
        BluetoothLeScanner scaner = mBluetoothAdapter.getBluetoothLeScanner();  // android5.0把扫描方法单独弄成一个对象了
        if (enable) {
            mScanning = true;
            //scaner.startScan(mScanCallback);  // 开始扫描 5.0以上 ，SDK需在21以上
            mBluetoothAdapter.startLeScan(mLeScanCallback); //开始搜索 安卓5.0以下
            Log.d("測試:","開始搜尋");
            deviceName = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1);
        }else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止搜索
            //scaner.stopScan(mScanCallback);   // 停止扫描 5.0以上
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

                    ListView listview = (ListView) findViewById(R.id.listview);
                    if (!deviceList.contains(device)) {
                        //将设备加入列表数据中
                        deviceList.add(device);
                            String name=device.getName();
                            if(device.getAddress().equals("D3:30:0F:06:D6:4B") ||device.getAddress().equals("DA:3A:0E:47:21:68")||device.getAddress().equals("EC:4F:61:6F:C2:68")){
                                name="CYUT_EMG";
                            }
                            deviceName.add(name + "\n" + device.getAddress());
                            vData[i] = device.getAddress();
                            i++;
                    }
                    listview.setAdapter(deviceName);
                    //以下獲取listview的點擊
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parnet, android.view.View v, final int position, long id) {
                            Log.d("測試","deviceName: "+vData[position]);
                            Log.d("測試", String.valueOf(position));
                            // mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止搜索 ~關閉掃描~~
                            String bleAddress =vData[position];//要連接的裝置~要能依照listview的選擇而變
                            bleAddress_re=bleAddress;
                            BluetoothDevice mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(bleAddress);
                            show("嘗試連接...:"+vData[position]);
                            /************************啟動連接*************************************/
                            mBluetoothGatt = mBluetoothDevice.connectGatt(MainActivity.this,true,mGattCallback);//啟動連接
                            Log.d("測試:","進入連接藍芽call back!");
                        }
                    });
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
            String h = "";//Level變數
            String h1 = "";//RPM變數
            /*for(int i = 0; i < arrayOfByte.length; i++){
                String temp = Integer.toHexString(arrayOfByte[i] & 0xFF);
                if(temp.length() == 1){
                    temp = "0" + temp;
                }
                h = h + temp;
            }*/
             h = Integer.toHexString(arrayOfByte[2] & 0xFF);//Level 把帶入的數字回傳16進制
             h1 = Integer.toHexString(arrayOfByte[3] & 0xFF);//RPM

            Log.e("測試", " H1 收到: " + h+"  H2 收到: "+h1);

            str=Integer.valueOf(h,16).toString();//應該是將16進制轉成10進制的字串
            str2=Integer.valueOf(h1,16).toString();

            Log.d("測試", " str 收到: " + str+"  str2 收到: "+ str2);
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
        GlobalVariable map_data = (GlobalVariable)getApplicationContext();//全域變數設定
        TextView rec_data=(TextView) findViewById(R.id.textView);
        TextView rec_data2=(TextView) findViewById(R.id.textView2);


        //加入判斷如果RPM=130則顯示 " - "號表示不穩定(若不穩定感測器會送130)
            if(Integer.valueOf(str) ==130) {//Integer.valueOf()將字串轉為十進制才能和130整數比較
                rec_data.setText("Level: --");
            }else{
                rec_data.setText("Level: " + str);
            }
            if(Integer.valueOf(str2) ==130 ){//Integer.valueOf()將字串轉為十進制才能和130整數比較
                rec_data2.setText("RPM: --");
            }else{
                rec_data2.setText("RPM: " + str2);
            }

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
            case R.id.btn_stop:
                if(mScanning==true){
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止搜索
                    Toast.makeText(this, "Scan Stop", Toast.LENGTH_SHORT).show();
                    mScanning=false;
                } else {
                    mBluetoothAdapter.startLeScan(mLeScanCallback);//開始搜索
                    Toast.makeText(this, "Scan Start", Toast.LENGTH_SHORT).show();
                    mScanning=true;
                }
                break;
        }
    }


}
