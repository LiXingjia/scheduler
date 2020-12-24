package com.in.timelinenested.socket;

import android.os.StrictMode;
import android.util.Log;


import com.in.timelinenested.utility.AcountUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by HSDN on 2018/5/26.
 */

public class RegisterSocket {
    public static final String bm="GBK";
    public String register(String phone,String password){
        Socket client;
        BufferedWriter bw;
        BufferedReader br;
        String msg = "";
        String userId = "";
        Boolean flag = false;
        Boolean isRunning = true;
        try{
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
            client = new Socket("39.108.90.114",9999);//localhost  39.108.90.114
            bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),bm));
            br = new BufferedReader(new InputStreamReader(client.getInputStream(),bm));
            while(isRunning) {
                bw.write(AcountUtil.createToken());
                bw.newLine();
                bw.flush(); //强制刷新
                isRunning = false;
            }
            isRunning = true;
            while(isRunning) {
                userId = AcountUtil.createToken();
                bw.write("@sql:select * from user where phone='"+phone+"';");
                bw.newLine();
                bw.flush(); //强制刷新
                isRunning = false;
            }
            isRunning = true;
            while(isRunning) {
                msg = br.readLine();
                if(!msg.trim().equals(""))
                    isRunning = false;
            }
            userId = msg.substring(msg.indexOf("userId=")+7,msg.indexOf(", phone"));
            if(userId.equals("null")) {
                isRunning = true;
                while (isRunning) {
                    userId = AcountUtil.createToken();
                    bw.write("@sql:insert into user(userId,userName,password,phone) values('" + userId + "','随便逛逛的游客','" + password + "','" + phone + "');");
                    //@sql:insert into user(userId,userName,password,phone) values('a138b92259734df080425358a1330669','tt','fyc19970120','13567301663');
                    bw.newLine();
                    bw.flush(); //强制刷新
                    isRunning = false;
                }
                flag=true;
            }
            bw.close();
            br.close();
            client.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(flag){
                return "success"+userId+";";
            }else {
                return "failed";
            }
        }
    }
}