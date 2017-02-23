package com.example.thinkpaduser.myapplication.StaticMethod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * Created by ThinkPad User on 2016/7/13.
 */
public class NetworkUtil {
    private  final static String LOG_TAG = "NetworkUtil";
   public static String sendPostRequest(String u,Map<String ,String> parmas){//参数是key—value形式的，用map来存储
           InputStream is = null;
           OutputStream os = null;
           try {
               URL url = new URL(u);
               HttpURLConnection conn = (HttpURLConnection) url.openConnection();
               conn.setRequestMethod("POST");
               conn.setDoOutput(true);
               conn.setDoInput(true);
               conn.setConnectTimeout(10 * 1000);
               conn.setReadTimeout(10 * 1000);
               StringBuilder sb = new StringBuilder();
               Set<Map.Entry<String,String>> set = parmas.entrySet();
               for (Map.Entry<String,String> entry : set){//循环遍历
                   sb.append(entry.getKey());
                   sb.append("=");
                   sb.append(entry.getValue());
                   sb.append("&");
               }
               os = conn.getOutputStream();//打开输出流
               os.write(new String(sb).getBytes());
               os.flush();
               //获取结果
               if (conn.getResponseCode() == 200) {
                   is = conn.getInputStream();//打开输入流
                   int len = 0;
                   byte[] buf = new byte[1024];
                   ByteArrayOutputStream bos = new ByteArrayOutputStream();
                   while ((len = is.read(buf)) != -1) {
                       //等于-1就是没有读的了，不是-1就是说有的读，读多少写多少
                       bos.write(buf, 0, len);
                   }
                   //将读取出来的数据转换字符串
                   String text = new String (bos.toByteArray());
                   return text;
               }
           } catch (MalformedURLException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }  finally {
               try {
                   if (is != null)
                       is.close();
                   if (os != null)
                       os.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
           return null;
       //这里返回一个null，就是说只有发生异常情况下才会发送空，没有异常就不走这一步所以就不发送
   }

}
