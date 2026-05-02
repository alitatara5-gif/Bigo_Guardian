package com.arthenica.ffmpegkit;

import android.util.Log;

public class FFmpegKitConfig {
    static {
        // Daftar library yang harus dimuat sesuai urutan dependensi
        String[] libs = {"c++_shared", "avutil", "swresample", "avcodec", "avformat", "swscale", "avfilter", "avdevice", "ffmpegkit_abidetect", "ffmpegkit"};
        for (String lib : libs) {
            try {
                System.loadLibrary(lib);
            } catch (Throwable e) {
                Log.e("FFMPEG_ERROR", "Gagal muat: " + lib);
            }
        }
    }

    public static native int nativeFFmpegExecute(long sessionId, String command);
    public static native void nativeFFmpegCancel(long sessionId);
    public static native void ignoreNativeSignal(int signal);
}
