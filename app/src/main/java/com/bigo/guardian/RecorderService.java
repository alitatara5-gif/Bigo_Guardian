package com.bigo.guardian;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.arthenica.ffmpegkit.FFmpegKitConfig;
import java.io.File;

public class RecorderService extends Service {
    private long sessionId = 0;
    private PowerManager.WakeLock wakeLock;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("url");
        if (url == null) return START_NOT_STICKY;

        sessionId = System.currentTimeMillis();
        
        // Matikan sinyal SIGPIPE (13) agar tidak crash
        try { FFmpegKitConfig.ignoreNativeSignal(13); } catch (Throwable t) {}

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Bigo:RecLock");
        wakeLock.acquire();

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BigoGuardian");
        if (!dir.exists()) dir.mkdirs();
        String path = new File(dir, "Rec_" + (sessionId/1000) + ".mp4").getAbsolutePath();

        NotificationChannel chan = new NotificationChannel("rec", "Bigo Recorder", NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(chan);
        startForeground(1, new NotificationCompat.Builder(this, "rec")
                .setContentTitle("Bigo Guardian")
                .setContentText("Merekam ke MP4...")
                .setSmallIcon(android.R.drawable.ic_media_play).build());

        new Thread(() -> {
            // Format CMD bersih tanpa escape character berlebih
            String cmd = "-y -i " + url + " -c copy -bsf:a aac_adtstoasc " + path;
            Log.d("BIGO_DEBUG", "Menjalankan: " + cmd);
            
            try {
                FFmpegKitConfig.nativeFFmpegExecute(sessionId, cmd);
            } catch (Throwable e) {
                Log.e("BIGO_DEBUG", "JNI Crash: " + e.getMessage());
            }
            stopSelf();
        }).start();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        try { FFmpegKitConfig.nativeFFmpegCancel(sessionId); } catch (Throwable t) {}
        if (wakeLock != null && wakeLock.isHeld()) wakeLock.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
