package com.example.bigoguardian;

import android.util.Log;

public class Recorder {
    static {
        try {
            System.loadLibrary("bigoguardian_engine");
            Log.i("BIGO_JAVA", "[SYSTEM] Library Engine dimuat!");
        } catch (UnsatisfiedLinkError e) {
            Log.e("BIGO_JAVA", "[ERROR] Library tidak ketemu: " + e.getMessage());
        }
    }

    // Fungsi utama untuk narik stream Bigo
    public native int executeRemux(String inputUrl, String outputPath);
}
