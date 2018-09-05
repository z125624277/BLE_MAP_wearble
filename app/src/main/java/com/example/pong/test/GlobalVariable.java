package com.example.pong.test;

import android.app.Application;

/**
 * Created by PONG on 2017/12/16.
 */
public class GlobalVariable extends Application {
    //GlobalVariable繼承 Application
    public String level="",rpm="",gps="",web_data="";     //要傳送的字串
    public Float x=0.0f,y=0.0f,z=0.0f;
    public String[] web=new String[20];
    //修改 變數字串
    public void setdata(String word,String word2){
        this.level = word;
        this.rpm = word2;
    }
    public void setdata2(String word3){
        this.gps = word3;
    }
    public void setdata3(float x,float y,float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public void setdata4(String word4){
        this.web_data = word4;
    }
    //顯示 (取出)變數字串
    public String getdata() {
        return level;
    }
    public String getdata2() {
        return rpm;
    }
    public String getdata3() {
        if(gps==""){
            gps="0";
            return gps;
        }else{
        return gps;
        }
    }
    public Float getdata4(){
        return x;
    }
    public Float getdata5(){
        return y;
    }
    public Float getdata6(){
        return z;
    }
    public String getdata7(int a){
        //這裡暫時沒有把天氣資訊取出
        web[0]=web_data.substring(82,84);//天氣狀況
        web[1]=web_data.substring(82,84);//溫度
        web[2]=web_data.substring(88,89);//風 XX
        web[3]=web_data.substring(90,92);//濕度
        web[4]=web_data.substring(93,95);//降雨
        web[5]=web_data.substring(57,67);//緯度
        web[6]=web_data.substring(68,79);//經度
        return web[a];
    }
}
