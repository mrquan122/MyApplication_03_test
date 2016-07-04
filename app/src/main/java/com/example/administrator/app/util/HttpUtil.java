package com.example.administrator.app.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/3/15.
 */
public class HttpUtil {
    public static void sendHttpRequest(final  String address,final HttpCallbacListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn =null;
                try {
                    URL url =new URL(address);                         // Log.i("province",address);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5 * 1000);
                    conn.setRequestMethod("GET");
                    conn.connect();
                    InputStream response = conn.getInputStream();
                    if (listener !=null){
                        // 回调 onFinish()
                        listener.onFinish(response);
                    }
                }catch (Exception e){
                    if (listener !=null){
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }
}

