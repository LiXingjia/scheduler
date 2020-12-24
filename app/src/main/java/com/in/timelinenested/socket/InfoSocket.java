package com.in.timelinenested.socket;

import android.os.StrictMode;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by HSDN on 2018/5/28.
 */

public class InfoSocket {
    public static final String bm="GBK";
    public String getInfo(String userId){
        Socket client;
        BufferedWriter bw;
        BufferedReader br;
        String msg = "";
        Boolean isRunning = true;
        try{
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
            client = new Socket("39.108.90.114",9999);//localhost  39.108.90.114
            bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),bm));
            br = new BufferedReader(new InputStreamReader(client.getInputStream(),bm));
            while(isRunning) {
                bw.write(userId);
                bw.newLine();
                bw.flush(); //强制刷新
                isRunning = false;
            }
            isRunning = true;
            while(isRunning) {
                bw.write("@sql:select * from user where userId='"+userId+"';");
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
            bw.close();
            br.close();
            client.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return msg;
    }
}

