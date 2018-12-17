package com.example.bilyli.myapplication;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.lang.ref.WeakReference;

public class TestInstance {
    private static TestInstance instance = new TestInstance();
    public static TestInstance getInstance(){
        return instance;
    }
    WeakReference<Iback> back;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            back.get().back();
        }
    };
    public void test(Iback iback){
        back = new WeakReference<>(iback);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(5000);
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    interface Iback{
        void back();
    }

}
