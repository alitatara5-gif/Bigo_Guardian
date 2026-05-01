package com.bigo.posix;

import android.os.Bundle;
import android.os.Environment;
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

    // Load library native-lib.cpp
    static {
        System.loadLibrary("native-lib");
    }

    // Deklarasi fungsi C++
    public native String stringFromJNI();
    public native int startRecording(String url, String output);

    private TextView tvStatus;
    private EditText etUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- MEMBUAT UI TANPA XML (Supaya simpel di Termux) ---
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("BIGO GUARDIAN POSIX");
        tvTitle.setTextSize(24);
        tvTitle.setPadding(0, 0, 0, 20);
        layout.addView(tvTitle);

        TextView tvInfo = new TextView(this);
        tvInfo.setText(stringFromJNI()); // Menampilkan versi FFmpeg
        tvInfo.setPadding(0, 0, 0, 40);
        layout.addView(tvInfo);

        etUrl = new EditText(this);
        etUrl.setHint("Masukkan URL Stream (m3u8/flv)...");
        layout.addView(etUrl);

        Button btnRecord = new Button(this);
        btnRecord.setText("MULAI REKAM");
        btnRecord.setPadding(0, 20, 0, 20);
        layout.addView(btnRecord);

        // Tempat Log/Status
        tvStatus = new TextView(this);
        tvStatus.setText("\nStatus: Ready...");
        
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(tvStatus);
        layout.addView(scrollView);

        setContentView(layout);

        // --- LOGIKA TOMBOL REKAM ---
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = etUrl.getText().toString().trim();
                
                if (url.isEmpty()) {
                    Toast.makeText(MainActivity.this, "URL jangan kosong, Bang!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Buat nama file otomatis: Bigo_20260501_1800.mp4
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(new Date());
                File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String outputPath = new File(downloadDir, "Bigo_" + timeStamp + ".mp4").getAbsolutePath();

                tvStatus.append("\n\n[Mulai] Menghubungkan ke stream...");
                tvStatus.append("\n[Simpan] " + outputPath);

                // Jalankan fungsi native (C++)
                // Di tahap ini, ini masih memanggil fungsi 'dummy' kita tadi
                int result = startRecording(url, outputPath);

                if (result == 0) {
                    tvStatus.append("\n[Sukses] Mesin perekam dipicu!");
                } else {
                    tvStatus.append("\n[Error] Gagal memicu mesin!");
                }
            }
        });
    }
}
