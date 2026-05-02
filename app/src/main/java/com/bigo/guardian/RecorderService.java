package com.bigo.guardian;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import java.io.File;

public class RecorderService extends Service {
    public static boolean isRecording = false;
    public static long startTime = 0;
    public static String currentFile = "";

    // Load library FFmpegKit
    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("avutil");
        System.loadLibrary("avcodec");
        System.loadLibrary("avformat");
        System.loadLibrary("ffmpegkit");
    }

    // Fungsi native untuk menjalankan perintah FFmpeg
    public native int runFFmpeg(String cmd);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("url");
        startTime = System.currentTimeMillis();
        isRecording = true;

        File downloadDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BigoGuardian");
        if (!downloadDir.exists()) downloadDir.mkdirs();

        currentFile = "Bigo_" + (startTime/1000) + ".mp4";
        String savePath = new File(downloadDir, currentFile).getAbsolutePath();

        startForeground(1, createNotification());

        new Thread(() -> {
            // PERINTAH SAKTI UNTUK MEREKAM M3U8 KE MP4
            // -y (overwrite), -i (input), -c copy (tanpa encoding ulang/cepat), -bsf:a (fix audio)
            String ffmpegCommand = "-y -i " + url + " -c copy -bsf:a aac_adtstoasc " + savePath;
            Log.d("BIGO", "Menjalankan FFmpeg: " + ffmpegCommand);
            
            runFFmpeg(ffmpegCommand);
            
            isRecording = false;
            stopSelf();
        }).start();

        return START_STICKY;
    }

    private Notification createNotification() {
        NotificationChannel chan = new NotificationChannel("recorder", "Bigo Recorder", NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(chan);
        return new NotificationCompat.Builder(this, "recorder")
                .setContentTitle("Bigo Guardian (FFmpegKit)")
                .setContentText("Sedang merekam stream...")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .build();
    }

    @Override
    public void onDestroy() {
        isRecording = false;
        startTime = 0;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
