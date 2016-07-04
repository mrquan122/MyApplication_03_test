package com.example.administrator.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.administrator.app.util.HttpCallbacListener;
import com.example.administrator.app.util.HttpUtil;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/3/23.
 */
public class AutoUpdateService extends Service {
    @Override
    public IBinder onBind (Intent intent){
        return null;
    }
    @Override
    public int onStartCommand (Intent intent, int flags,int startId){
        new Thread(new Runnable() {
            @Override
            public void run() {
               updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour = 8*60*60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime()+anHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);

    }

    /**
     * 更新天气信息
     */

    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String cityCode = prefs.getString("weather_code","");
        String address="http://weather.51wnl.com/weatherinfo/GetMoreWeather?cityCode="+cityCode+"&weatherType=0";
        HttpUtil.sendHttpRequest(address, new HttpCallbacListener() {
            @Override
            public void onFinish(InputStream response) throws JSONException, XmlPullParserException, IOException {
            //    Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();

            }
        });
    }
}
