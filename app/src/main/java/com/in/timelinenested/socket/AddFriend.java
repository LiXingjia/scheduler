package com.in.timelinenested.socket;

import android.os.StrictMode;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by HSDN on 2018/6/4.
 */

public class AddFriend {
    public static final String bm="GBK";
    public String add(String userId,String myUserId,String remark,String myGroup,String description){
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
                bw.write(myUserId);
                bw.newLine();
                bw.flush(); //强制刷新
                isRunning = false;
            }
            isRunning = true;
            while(isRunning) {
                bw.write("@SQL-FRIEND:insert into friend(userId,myUserId,flag,remark,myGroup,description) values('"+userId+"','"+myUserId+"',0,'"+remark+"','"+myGroup+"','"+description+"');");
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

