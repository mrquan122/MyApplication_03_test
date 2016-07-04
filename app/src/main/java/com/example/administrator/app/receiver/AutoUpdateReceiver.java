package com.example.administrator.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.administrator.app.service.AutoUpdateService;

/**
 * Created by Administrator on 2016/3/23.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {

    public void onReceive (Context context, Intent intent){
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
