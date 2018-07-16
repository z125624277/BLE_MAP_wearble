package com.example.pong.test;

import android.app.Application;

/**
 * Created by PONG on 2017/12/16.
 */
public class GlobalVariable extends Application {
    public String level="",rpm="",gps="";     //要傳送的字串
    public Float x=0.0f,y=0.0f,z=0.0f;
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

}
