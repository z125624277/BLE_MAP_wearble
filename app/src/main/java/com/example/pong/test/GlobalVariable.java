package com.example.pong.test;

import android.app.Application;

/**
 * Created by PONG on 2017/12/16.
 */
public class GlobalVariable extends Application {
    //GlobalVariable繼承 Application
    public String level="",rpm="",gps="";     //要傳送的字串
    public Float x=0.0f,y=0.0f,z=0.0f;
    public String[] web=new String[]{(""),(""),(""),(""),(""),(""),(""),(""),(""),(""),(""),(""),(""),(""),(""),(""),(""),(""),(""),("")};
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
    public void setdata4(String[] word4){
        this.web = word4;
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
    /*public Float getdata4(){
        return x;
    }
    public Float getdata5(){
        return y;
    }
    public Float getdata6(){
        return z;
    }*/
    public String getdata7(int a){
        //這裡暫時沒有把天氣資訊取出
        if(web[a].equals("0") || web[a].equals("1"))  web[a]="晴天";
        if(web[a].equals("2") || web[a].equals("3")|| web[a].equals("4"))  web[a]="多雲";
        if(web[a].equals("5") || web[a].equals("6"))  web[a]="陰天";
        if(web[a].equals("7") || web[a].equals("8")|| web[a].equals("9"))  web[a]="陣雨";
        return web[a];
    }
}
