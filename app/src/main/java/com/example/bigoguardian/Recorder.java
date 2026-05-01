package com.example.bigoguardian;

import android.util.Log;

public class Recorder {
    private static final String TAG = "BIGO_JAVA";

    static {
        try {
            // Urutan ini HARAM hukumnya kalau terbalik, Bang
            System.loadLibrary("avutil");
            System.loadLibrary("swresample");
            System.loadLibrary("avcodec");
            System.loadLibrary("avformat");
            System.loadLibrary("swscale");
            System.loadLibrary("avfilter");
            System.loadLibrary("avdevice");
            
            // Terakhir baru muat jembatan buatan kita
            System.loadLibrary("bigoguardian_engine");
            
            Log.i(TAG, "✅ SEMUA 7 PASUKAN .SO BERHASIL DIMUAT!");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "❌ GAGAL MUAT LIBRARY: " + e.getMessage());
        }
    }

    // Fungsi tes simpel untuk cek versi FFmpeg
    public native String getFFmpegVersion();
}
