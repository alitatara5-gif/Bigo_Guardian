package com.bigo.guardian;

import android.app.Activity;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.Manifest;
import android.content.pm.PackageManager;

public class MainActivity extends Activity {
    private static final String TAG = "BIGO_DEBUG";
    TextView txtStatus, listRekaman;
    Button btnStart, btnStop;
    EditText inputUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "MainActivity: onCreate dimulai");

        txtStatus = findViewById(R.id.txtStatus);
        listRekaman = findViewById(R.id.listRekaman);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        inputUrl = findViewById(R.id.inputUrl);

        checkEngine();

        btnStart.setOnClickListener(v -> {
            Log.d(TAG, "Tombol MULAI diklik");
            startRecording();
        });

        btnStop.setOnClickListener(v -> {
            Log.d(TAG, "Tombol STOP diklik");
            stopRecording();
        });
    }

    private void checkEngine() {
        String[] libs = {"c++_shared", "avutil", "swresample", "avcodec", "avformat", "swscale", "avfilter", "avdevice", "bigoguardian_engine"};
        StringBuilder sb = new StringBuilder("=== KONFIRMASI MESIN ===\n");
        for (String lib : libs) {
            try {
                System.loadLibrary(lib);
                sb.append("✅ ").append(lib).append("\n");
                Log.d(TAG, "Library loaded: " + lib);
            } catch (Throwable e) {
                sb.append("❌ ").append(lib).append("\n");
                Log.e(TAG, "Gagal load library: " + lib + " | Error: " + e.getMessage());
            }
        }
        txtStatus.setText(sb.toString());
    }

    private void startRecording() {
        try {
            String url = inputUrl.getText().toString();
            if (url.isEmpty()) {
                Log.w(TAG, "URL kosong, membatalkan rekam");
                return;
            }

            Log.d(TAG, "Mencoba startForegroundService ke URL: " + url);
            Intent it = new Intent(this, RecorderService.class);
            it.putExtra("url", url);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(it);
            } else {
                startService(it);
            }
            
            btnStart.setVisibility(View.GONE);
            btnStop.setVisibility(View.VISIBLE);
            Log.d(TAG, "Service berhasil dipanggil");
        } catch (Exception e) {
            Log.e(TAG, "CRASH di startRecording: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        Log.d(TAG, "Menghentikan service...");
        stopService(new Intent(this, RecorderService.class));
        btnStop.setVisibility(View.GONE);
        btnStart.setVisibility(View.VISIBLE);
    }
}
