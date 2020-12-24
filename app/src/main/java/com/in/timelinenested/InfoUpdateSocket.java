package com.in.timelinenested;

/**
 * Created by skyler on 2019/3/3.
 */

import android.os.StrictMode;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;

/**
 * Created by skyker on 2018/5/31.
 */

public class InfoUpdateSocket {
    public static final String bm="GBK";
    public void setInfo(String userId,String[] userInfo){
        Socket client;
        BufferedWriter bw;
        BufferedReader br;
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
                String userName = userInfo[0];
                String motto = userInfo[1];
                String sex = userInfo[2];
                String birthday = userInfo[3];
                String address = userInfo[4];
                int sexFlag=1;
                if(sex.equals("女")){
                    sexFlag = 0;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                //bw.write("@sql:update user set userName='fengyuchao',motto='努力总是有回报的',sex=1,birthday='1997-01-20',address='浙江省嘉兴市平湖市' where userId='b138b92259734df080425358a1330669';");
                bw.write("@sql:update user set userName='"+userName+"',motto='"+motto+"',sex="+sexFlag+",birthday='"+birthday+"',address='"+address+"' where userId='"+userId+"';");
                bw.newLine();
                bw.flush(); //强制刷新
                isRunning = false;
            }
            bw.close();
            br.close();
            client.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
