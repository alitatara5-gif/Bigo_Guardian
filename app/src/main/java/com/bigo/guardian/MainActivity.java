package com.bigo.guardian;

import android.app.Activity;
import android.os.*;
import android.util.Log;
import android.widget.*;

public class MainActivity extends Activity {
    TextView txtStatus;
    // Daftar mesin baru Abang (Total 10 file .so)
    String[] libs = {
        "c++_shared", "avutil", "swresample", "avcodec", 
        "avformat", "swscale", "avfilter", "avdevice", 
        "ffmpegkit_abidetect", "ffmpegkit"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtStatus = findViewById(R.id.txtStatus);

        checkEngine();
    }

    private void checkEngine() {
        StringBuilder sb = new StringBuilder("=== DASHBOARD FFmpegKit ===\n");
        boolean allOk = true;

        for (String lib : libs) {
            try {
                System.loadLibrary(lib);
                sb.append("✅ ").append(lib).append("\n");
            } catch (Throwable e) {
                sb.append("❌ ").append(lib).append(" (Missing)\n");
                allOk = false;
                Log.e("BIGO_DEBUG", "Error load " + lib + ": " + e.getMessage());
            }
        }

        if (allOk) {
            sb.append("\n🔥 STATUS: MESIN SIAP TEMPUR!\n");
            // FFmpegKit biasanya punya fungsi native untuk cek versi
            // Tapi untuk sekarang kita pastikan load library sukses dulu.
        } else {
            sb.append("\n⚠️ STATUS: ADA KOMPONEN HILANG!\n");
        }

        txtStatus.setText(sb.toString());
        txtStatus.setTextColor(allOk ? 0xFF00FF00 : 0xFFFF0000);
    }
}
