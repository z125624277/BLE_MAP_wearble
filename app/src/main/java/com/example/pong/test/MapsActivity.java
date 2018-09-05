package com.example.pong.test;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Handler handler = new Handler(); //每秒定時執行的方法
    public String str_level="",str2_rpm="",str3_gps="";//接收的LEVEL RPM字串
    public String web_data_rec[]=new String[20];//用來儲存網頁收到的資料
    private SensorManager sensorMgr; //感測器管理宣告
    private float xyz[] = new float[3]; //宣告暫存的感測器xyz數值
    public double Lat,Long;
    private double currentLatitude = 0;
    private double currentLongitude = 0;
    private LocationManager mLocationManager;
    private static final int LOCATION_UPDATE_MIN_DISTANCE = 0;//多少距離
    private static final int LOCATION_UPDATE_MIN_TIME = 1000;//多少時間(毫秒)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //設定Fragment地圖
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //設定GoogleMap的方法
        mLocationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        getCurrentLocation();

        handler.postDelayed(runnable, 1000);//每2s執行runnable
        //取得感應器服務
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

    }
    //建立監聽(感測)物件並得到數值x,y,z三軸加速度
    /*SensorEventListener listener =new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            //感測器數值改變時呼叫此方法
            //小數點值轉字串 str4_x = String.format("%f", location.getLatitude());
            xyz[0] = event.values[0];//得到感測數值
            xyz[1] = event.values[1];
            xyz[2] = event.values[2];
            GlobalVariable map_data = (GlobalVariable)getApplicationContext();//全域變數設定
            map_data.setdata3(xyz[0],xyz[1],xyz[2]);//傳送到全域變數
            Log.d("測試","X:xyz[0]="+xyz[0]);
            Log.d("測試","Y:xyz[1]="+xyz[1]);
            Log.d("測試","Z:xyz[2]="+xyz[2]);
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //感測器精準度改變時會呼叫此方法
        }
    };
    @Override
    protected void onResume(){//當頁面離開再回來會執行此方法來刷新
        super.onResume();
        sensorMgr.registerListener(listener,//註冊監聽
                sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),//感測器種類(加速度)
                sensorMgr.SENSOR_DELAY_UI);//更新速度
    }*/
    protected void onPause()//離開APP頁面都會執行
    {
        // TODO Auto-generated method stub
        //取消註冊SensorEventListener  當退出時可讓感測器 x ,y ,z取消

        //sensorMgr.unregisterListener(listener); //感測器的監聽停止(x,y,z)
        //mBluetoothGatt.close();
        Toast.makeText(this, "Unregister accelerometerListener", Toast.LENGTH_LONG).show();
        super.onPause();
    }

    //定時執行接收數值以及傳送到網頁
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            GlobalVariable map_data = (GlobalVariable)getApplicationContext();
            str_level = map_data.getdata();
            str2_rpm=map_data.getdata2();
            str3_gps=map_data.getdata3();
            xyz[0]=map_data.getdata4();
            xyz[1]=map_data.getdata5();
            xyz[2]=map_data.getdata6();
            //web_data_rec=map_data.getdata7();//抓出網頁的值，暫時沒用
            TextView map_level = (TextView) findViewById(R.id.text_LEVEL);
            TextView map_rpm = (TextView) findViewById(R.id.text_RPM);
            TextView map_level2 = (TextView) findViewById(R.id.text_LEVEL2);//第二位人員資料
            TextView map_rpm2 = (TextView) findViewById(R.id.text_RPM2);
            TextView weather = (TextView) findViewById(R.id.weather);//天氣資訊

            weather.setText("天氣:"+web_data_rec[5]+"     溫度:"+web_data_rec[6]+"°C"+//14 15 16 17
                    "\n濕度:"+web_data_rec[7]+"%"+" 降雨機率:"+web_data_rec[8]+"%");


            if(str_level.equals("130")){//.equals才能比內容 用==是比位址
                map_level.setText("Level:--");
                //map_level2.setText("Level:--");
            }else{
                map_level.setText("Level:" +str_level );
                //map_level2.setText("Level:" +web_data_rec[13]);
            }
            if(str2_rpm.equals("130")){
                map_rpm.setText("RPM:--");
                //map_rpm2.setText("RPM:--");
            }else {
                map_rpm.setText("RPM:" + str2_rpm);
                //map_rpm2.setText("RPM:" +web_data_rec[12]);
            }
            handler.postDelayed(this, 1000);

            //傳送到PHP
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        //str_level="100";//暫用
                        //str2_rpm="999";//暫用
                        //str3_gps="24.159455,120 .693808";//暫用
                        str3_gps=str3_gps.replace(" ","");//去除空格
                    doPostRequest(str_level,str2_rpm,str3_gps,xyz[0],xyz[1],xyz[2]);//好像是新執行續才能啟動傳送
                    } catch (Exception e) {
                        Log.d("測試","錯誤?");
                        e.printStackTrace();
                    }
                }}).start();
        }
    };

    //傳到PHP的方法 再執行續裡面被呼叫
    private void doPostRequest(String level,String rpm,String gps,Float x,Float y,Float z) {
        //HttpClient httpClient = new DefaultHttpClient();

        //沒經過驗證 就可以傳資料(方法在下方)!
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        mBuilder.sslSocketFactory(createSSLSocketFactory());
        mBuilder.hostnameVerifier(new TrustAllHostnameVerifier());

       OkHttpClient client = mBuilder.build(); //okhttp3函數庫(build.gradle加入 compile 'com.squareup.okhttp3:okhttp:3.6.0')
        String id = "106318047",id2="102310036",result="";
        String emg;
        emg=level;
        try {
            String url="https://lab416.hopto.org/?";//http://163.17.21.131/helloword.php/?(校)
            //https://lab416.hopto.org/?uuid=106318047&gps=24.0454186,120.6872071&rpm=1&emg=2
            String url_data=url+"uuid="+id+"&gps="+gps+"&rpm="+rpm+"&emg="+emg;//目前xyz先不傳
            Log.d("測試","GET傳送的網址:"+url_data);
            Request request = new Request.Builder()
                    .url(url_data)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();//請求並獲得回應(同步execute)
            Log.d("測試","GET回傳是否順利連線200成功，404未找到："+response);
            if (!response.isSuccessful()) throw new IOException("測試傳送錯誤Unexpected code " + response);//判斷請求是否成功

            /*Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                //System.out.println("測試responseHeaders.name:"+responseHeaders.name(i) + ": " + responseHeaders.value(i));
               //顯示網頁類型、時間、編碼方式等等
            }*/
            GlobalVariable map_data = (GlobalVariable)getApplicationContext();//建立全域變數物件
            String web_data=response.body().string();//抓到回傳的網頁資料,注意response.body()只能執行一次不然跑不出來

            Log.d("測試","抓到的網頁內容response.body()："+web_data);

            String web_data_get = web_data.substring(483,578);//指定字串範圍抓出
            Log.d("測試","網頁內容分割完以後的字串:"+web_data_get);
            map_data.setdata4(web_data_get);//存入全域變數
            web_data_rec=web_data_get.split(",");//遇到逗號就分割，存成字串陣列

            Lat=Double.valueOf(web_data_rec[10]);//把緯度帶入浮點數
            Long=Double.valueOf(web_data_rec[11]);

            /*HttpGet request = new HttpGet(url_data);
            HttpResponse response = httpClient.execute(request);
            HttpEntity resEntity = response.getEntity();//判斷是否有回傳?或是連線狀態
            */
            /*if (resEntity != null) {
                result = EntityUtils.toString(resEntity);//應該是如果有回傳就...
                Log.d("測試","GET回傳="+result);
            }*/
        } catch (Exception e) {
            Log.d("測試","GET有問題！！！有問題！！！！！！");
            e.printStackTrace();
        }
    }
    //使用網路定位或GPS定位
    private void getCurrentLocation() {
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location location = null;
        if (!(isGPSEnabled || isNetworkEnabled)) {
            // location_provider error
        } else {
            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //類似檢查憑證
                    return;
                }
                Toast.makeText(getApplicationContext(), "啟用網路定位", Toast.LENGTH_SHORT).show();
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_MIN_TIME,
                        LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);//會呼叫mLocationListener
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else if (isGPSEnabled) {
                Toast.makeText(getApplicationContext(), "啟用GPS定位", Toast.LENGTH_SHORT).show();
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_MIN_TIME,
                        LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (location != null) {
            Log.d("location",String.format("getCurrentLocation(%f, %f)", location.getLatitude(), location.getLongitude()));
        }
    }
    //被呼叫的位置訊息
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //当位置更新时调用，并传入 对应的Location对象
            if (location != null) {
                str3_gps = String.format("%f, %f", location.getLatitude(), location.getLongitude());//獲得經(Longitude)緯(Latitude)度
                GlobalVariable map_data = (GlobalVariable)getApplicationContext();//全域變數設定
                map_data.setdata2(str3_gps);//傳送GPS到全域變數
                Toast.makeText(getApplicationContext(), str3_gps, Toast.LENGTH_SHORT).show();//顯示在畫面上
                LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());//設定座標經緯度
                LatLng latlong = new LatLng(Lat, Long);//設定座標經緯度 Lat Long  24.073373, 120.715190
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(sydney).title("您的位置"));//紅色座標名稱
                mMap.addMarker(new MarkerOptions().position(latlong).title("第2位置").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,16.0f));//範圍在2.0到21.0之間讓畫面顯示位置(放大)


                //mMap_1.clear();
                //LatLng latlong = new LatLng(24.159772,120.692855);//設定座標經緯度
                //mMap_1.addMarker(new MarkerOptions().position(latlong).title("您的位置").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                //藍色座標名稱
                //mMap_1.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong,15.0f));//範圍在2.0到21.0之間讓畫面顯示位置(放大)
            } else {
                // Logger.d("Location is null");
                Toast.makeText(getApplicationContext(), "Location is null", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //当状态发生改变时调用
            Log.d("測試","當狀態發生改變跑來這");
        }
        @Override
        public void onProviderEnabled(String s) {
            //当所选的Location Provider可用时调用
            Log.d("測試","當能提供位置功能時跑來這");
        }
        @Override
        public void onProviderDisabled(String s) {
            //当所选的Location Provider不可用时调用
        }
    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    public void Onclick(View view){
        switch (view.getId()){
            case R.id.btn_back:
                //Intent i=new Intent(MapsActivity.this, MainActivity.class);
                //startActivity(i);
                finish();
                break;
        }
    }

    //讓SSL驗證全部信任的方法
    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        @Override
        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
    }
    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
	    return true;
        }
    }
    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null,  new TrustManager[] { new TrustAllCerts() }, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return ssfFactory;
    }
    //讓SSL驗證全部信任的方法

}
