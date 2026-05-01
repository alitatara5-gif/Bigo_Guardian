package com.example.bigoguardian;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.graphics.Color;
import android.widget.ScrollView;

public class MainActivity extends Activity {
    StringBuilder log = new StringBuilder();

    private void loadLib(String name) {
        try {
            System.loadLibrary(name);
            log.append("✅ ").append(name).append(" loaded\n");
        } catch (UnsatisfiedLinkError e) {
            log.append("❌ ").append(name).append(" FAILED: ").append(e.getMessage()).append("\n");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Urutan muat yang paling aman
        loadLib("avutil");
        loadLib("swresample");
        loadLib("avcodec");
        loadLib("avformat");
        loadLib("swscale");
        loadLib("avfilter");
        loadLib("avdevice");
        loadLib("bigoguardian_engine");

        ScrollView sv = new ScrollView(this);
        TextView tv = new TextView(this);
        tv.setTextSize(14);
        tv.setPadding(30, 30, 30, 30);
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundColor(Color.BLACK);
        
        try {
            if (log.toString().contains("❌")) {
                tv.setText("💀 ADA MASALAH KONEKSI .SO:\n\n" + log.toString());
            } else {
                tv.setText("🚀 SEMUA OTOT SIAP!\n\n" + testOtot());
            }
        } catch (Exception e) {
            tv.setText("🔥 CRASH DI JNI: " + e.getMessage());
        }

        sv.addView(tv);
        setContentView(sv);
    }

    public native String testOtot();
}
