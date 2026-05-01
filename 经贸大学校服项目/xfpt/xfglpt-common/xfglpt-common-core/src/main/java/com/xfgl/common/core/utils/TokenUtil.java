package com.xfgl.common.core.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class TokenUtil {
    /***
     * MD5加码 生成32位md5码
     */
    public static String string2MD5(String inStr) throws NoSuchAlgorithmException, UnsupportedEncodingException {
            byte[] hash;
            hash = MessageDigest.getInstance("MD5").digest(inStr.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10){
                    hex.append("0");
                }
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
    }

    /**
     * 加密解密算法 执行一次加密，两次解密
     */
    public static String convertMD5(String inStr){

        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++){
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;

    }
    /**
     * tokenValue值的生成
     */
    public static String tokenValue(String app_id,String timestamp,String trans_id,String app_secret) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String tokenValue = "";
        StringBuffer bf = new StringBuffer();
        bf.append("appCode"+app_id);
        bf.append("appSecret"+app_secret);
        bf.append("timestamp"+timestamp);
        bf.append("transId"+trans_id);

        String s = bf.toString();
        tokenValue = string2MD5(s);
        return tokenValue;
    }
    // 测试主函数
    public static void main(String args[]) throws IOException, NoSuchAlgorithmException {

        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        //计算token的四个值
        String appcode="";//应用编码
        String transId=null;//时间戳+随机数
        String timestamp=df.format(new Date());//时间戳
        String app_secret="";//应用秘钥

        //生成随机数
        String s = String.valueOf((Math.random() * 9 + 1) * 100000).substring(0,6);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(sdf.format(new Date()));
        stringBuffer.append(s);
        transId=stringBuffer.toString();
        //
        String a = tokenValue(appcode, timestamp, transId, app_secret);
        //输出
        System.out.println("appcode是："+appcode);
        System.out.println("timestamp是："+timestamp);
        System.out.println("transId是："+transId);
        System.out.println("计算的token是："+a);
    }
}
