package com.in.timelinenested.utility;

import android.util.Base64;

import java.security.MessageDigest;
import java.util.UUID;


public class AcountUtil {
	public static String createId(){
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
	public static String createToken(){
		return createId().replaceAll("-", "");//去掉"-"
	}
	public static String md5(String msg){
		//基于摘要算法
		try {
			//将msg消息进行md5处理
			//通过信息摘要单例的构造函数获取摘要对象:md5
			MessageDigest md = MessageDigest.getInstance("MD5");
			//信息摘要对象是对字节数组进行摘要的,所以先获取字符串的字节数组
			byte[] input = msg.getBytes();
			//信息摘要对象对字节数组进行摘要,得到摘要字节数组
			byte[] output = md.digest(input);
			//用base64算法将output加密后的字节数组转成字符串
			return Base64.encodeToString(output,0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/* 测试
	public static void main(String[] args){
		System.out.println(md5("123456"));
		System.out.println(createId());
		System.out.println(createToken());
	}*/
}
