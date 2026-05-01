package com.example.bigoguardian;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView tv = new TextView(this);
        Recorder recorder = new Recorder();
        
        try {
            String ver = recorder.getFFmpegVersion();
            tv.setText("🚀 FFmpeg Berhasil Dimuat!\nVersi: " + ver);
        } catch (Exception e) {
            tv.setText("💀 Gagal memanggil C++: " + e.getMessage());
        }
        
        setContentView(tv);
    }
}
