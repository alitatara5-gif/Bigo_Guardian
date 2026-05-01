package com.example.bigoguardian;

import android.app.Activity;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.widget.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

public class MainActivity extends Activity {
    TextView txtStatus;
    // Daftar ini harus sesuai dengan file .so yang kita paksa masuk tadi
    String[] libs = {"avutil", "swresample", "avcodec", "avformat", "swscale", "avfilter", "avdevice", "bigoguardian_engine"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Pastikan ID ini ada di activity_main.xml
        txtStatus = findViewById(R.id.txtStatus);

        // 1. TODONG IZIN (Penting buat Android 13/14)
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.READ_MEDIA_VIDEO
                }, 101);
            }
        }

        // 2. AKSES SEMUA FILE (Kunci FFmpeg bisa nulis file)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                }
            }
        }

        checkEngine();
        setupUI();
    }

    private void checkEngine() {
        StringBuilder sb = new StringBuilder("=== MONITOR MESIN ===\n");
        boolean ready = true;
        
        // Load c++_shared dulu sebagai pondasi
        try { System.loadLibrary("c++_shared"); } catch (Throwable t) {}

        for (String lib : libs) {
            try {
                System.loadLibrary(lib);
                sb.append("✅ ").append(lib).append("\n");
            } catch (UnsatisfiedLinkError e) {
                sb.append("❌ ").append(lib).append(" (Missing)\n");
                ready = false;
            }
        }
        
        if (txtStatus != null) {
            txtStatus.setText(sb.toString());
            txtStatus.setTextColor(ready ? 0xFF00FF00 : 0xFFFF0000);
        }
    }

    private void setupUI() {
        EditText input = findViewById(R.id.inputUrl);
        Button btnStart = findViewById(R.id.btnStart);

        if (btnStart != null) {
            btnStart.setOnClickListener(v -> {
                String url = input.getText().toString();
                if (!url.isEmpty()) {
                    Intent it = new Intent(this, RecorderService.class);
                    it.putExtra("id", (int)(System.currentTimeMillis() % 10000));
                    it.putExtra("url", url);
                    startService(it);
                    Toast.makeText(this, "Mencoba merekam...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Link kosong, Bang!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
