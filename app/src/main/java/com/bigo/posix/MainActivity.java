package com.bigo.posix;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // LOAD PASUKAN 8 SILINDER (Urutan adalah Kunci!)
    static {
        try {
            System.loadLibrary("crypto");
            System.loadLibrary("ssl");
            System.loadLibrary("android-glob");
            System.loadLibrary("avutil");
            System.loadLibrary("swresample");
            System.loadLibrary("avcodec");
            System.loadLibrary("swscale");
            System.loadLibrary("avformat");
            System.loadLibrary("native-lib");
        } catch (UnsatisfiedLinkError e) {
            android.util.Log.e("BigoGuardian", "Gagal load library: " + e.getMessage());
        }
    }

    public native String stringFromJNI();
    public native int startRecording(String url, String output);
    public native void stopRecording();

    private TextView tvStatus;
    private Button btnStart, btnStop;

    public void updateStatusFromNative(final String message) {
        runOnUiThread(() -> {
            if (tvStatus != null) {
                tvStatus.append("\n" + message);
                final ScrollView scrollView = (ScrollView) tvStatus.getParent();
                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- UI DINAMIS ---
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("BIGO GUARDIAN POSIX v1.3");
        tvTitle.setTextSize(22);
        layout.addView(tvTitle);

        TextView tvEngine = new TextView(this);
        tvEngine.setText(stringFromJNI());
        tvEngine.setPadding(0, 0, 0, 30);
        layout.addView(tvEngine);

        final EditText etUrl = new EditText(this);
        etUrl.setHint("Masukkan URL m3u8/https...");
        layout.addView(etUrl);

        btnStart = new Button(this);
        btnStart.setText("MULAI REKAM");
        layout.addView(btnStart);

        btnStop = new Button(this);
        btnStop.setText("STOP REKAM");
        btnStop.setEnabled(false);
        layout.addView(btnStop);

        tvStatus = new TextView(this);
        tvStatus.setText("--- LOG AKTIVITAS ---");
        
        ScrollView scroller = new ScrollView(this);
        scroller.addView(tvStatus);
        layout.addView(scroller);

        setContentView(layout);

        // --- TOMBOL MULAI ---
        btnStart.setOnClickListener(v -> {
            String url = etUrl.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(this, "URL kosong, Bang!", Toast.LENGTH_SHORT).show();
                return;
            }

            // SIMPAN KE FOLDER MOVIES (Gampang dicari Galeri)
            File moviesDir = new File("/sdcard/Movies");
            if (!moviesDir.exists()) moviesDir.mkdirs();
            
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String path = new File(moviesDir, "Bigo_" + timeStamp + ".mp4").getAbsolutePath();
            
            updateStatusFromNative("[Info] Output: " + path);
            
            int result = startRecording(url, path);
            if (result == 0) {
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                updateStatusFromNative("[Status] Menghubungkan ke Stream...");
            } else {
                updateStatusFromNative("[Error] Gagal memicu mesin!");
            }
        });

        // --- TOMBOL STOP ---
        btnStop.setOnClickListener(v -> {
            stopRecording();
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            updateStatusFromNative("[Status] Rekaman dihentikan, menyimpan file...");
            Toast.makeText(this, "File tersimpan di folder Movies!", Toast.LENGTH_LONG).show();
        });
    }
}
