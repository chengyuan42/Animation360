package com.imooc.animation360.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.imooc.animation360.engine.FloatViewManager;

/**
 * Created by chengyuan on 16/8/13.
 */
public class MyFloatService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        FloatViewManager manager = FloatViewManager.getInstance(this);
        manager.showFloatCircleView();
        super.onCreate();
    }
}
