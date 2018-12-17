package com.example.bilyli.myapplication;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = findViewById(R.id.deviceId);

        final TintEditLayout layout = findViewById(R.id.edit_layout);
        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final String deviceId  =
//                        TextUtils.isEmpty(DeviceUtils.getUniquePsuedoID()) ?
//                                DeviceUtils.getAndroidId(getApplicationContext()):
//                                DeviceUtils.getUniquePsuedoID();
//                textView.setText(deviceId);
//                TestInstance.getInstance().test(new TestInstance.Iback() {
//                    @Override
//                    public void back() {
//                        Toast.makeText(MainActivity.this, "设备号已生成，可长按设备号复制" , 1).show();
//                    }
//                });
                layout.setErrotText("error show");
            }
        });

        final LoadingButton button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.showLoading();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        button.post(new Runnable() {
                            @Override
                            public void run() {
                                button.hideLoading();
                            }
                        });
                    }
                }).start();
            }
        });
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ClipboardManager cm =(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(textView.getText().toString());
                Toast.makeText(MainActivity.this, "设备号已复制到剪切板，快去粘贴吧~", 1).show();
                return false;
            }
        });
    }

    public static String getUniquePsuedoID() {
        String serial = null;
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


}
