package com.example.bigoguardian;

import android.app.Activity;
import android.content.*;
import android.os.*;
import android.widget.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.net.Uri;

public class MainActivity extends Activity {
    TextView txtStatus;
    // Logika 3: Konfirmasi 7 file .so + engine
    String[] libs = {"avutil", "swresample", "avcodec", "avformat", "swscale", "avfilter", "avdevice", "bigoguardian_engine"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtStatus = findViewById(R.id.txtStatus);

        // Logika 1: Ijin SEMUA AKSES FILE (Wajib untuk nulis hasil rekaman)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent it = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                it.setData(Uri.parse("package:" + getPackageName()));
                startActivity(it);
            }
        }

        // Logika 2: Ijin NOTIFIKASI (Wajib API 33)
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Logika 3: Cek status file .so
        checkEngine();
        
        // Logika 4: Tombol Rekam
        setupUI();
    }

    private void checkEngine() {
        StringBuilder sb = new StringBuilder("=== STATUS MESIN ===\n");
        boolean allOk = true;
        try { System.loadLibrary("c++_shared"); } catch (Throwable t) {}

        for (String lib : libs) {
            try {
                System.loadLibrary(lib);
                sb.append("✅ ").append(lib).append("\n");
            } catch (Throwable e) {
                sb.append("❌ ").append(lib).append("\n");
                allOk = false;
            }
        }
        txtStatus.setText(sb.toString());
        txtStatus.setTextColor(allOk ? 0xFF00FF00 : 0xFFFF0000);
    }

    private void setupUI() {
        Button btnStart = findViewById(R.id.btnStart);
        EditText input = findViewById(R.id.inputUrl);
        if (btnStart != null) {
            btnStart.setOnClickListener(v -> {
                String url = input.getText().toString();
                if (!url.isEmpty()) {
                    Intent it = new Intent(this, RecorderService.class);
                    it.putExtra("url", url);
                    startService(it);
                    Toast.makeText(this, "Mulai merekan...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
