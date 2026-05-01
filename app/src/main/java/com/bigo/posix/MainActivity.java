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

    static {
        System.loadLibrary("native-lib");
    }

    public native String stringFromJNI();
    public native int startRecording(String url, String output);
    public native void stopRecording();

    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("BIGO GUARDIAN POSIX");
        tvTitle.setTextSize(24);
        layout.addView(tvTitle);

        TextView tvInfo = new TextView(this);
        tvInfo.setText(stringFromJNI());
        tvInfo.setPadding(0, 0, 0, 40);
        layout.addView(tvInfo);

        final EditText etUrl = new EditText(this);
        etUrl.setHint("Masukkan URL Stream...");
        layout.addView(etUrl);

        final Button btnRecord = new Button(this);
        btnRecord.setText("MULAI REKAM");
        layout.addView(btnRecord);

        final Button btnStop = new Button(this);
        btnStop.setText("STOP REKAM");
        btnStop.setEnabled(false); // Disable dulu sebelum rekam
        layout.addView(btnStop);

        tvStatus = new TextView(this);
        tvStatus.setText("\nStatus: Ready...");
        ScrollView scroller = new ScrollView(this);
        scroller.addView(tvStatus);
        layout.addView(scroller);

        setContentView(layout);

        btnRecord.setOnClickListener(v -> {
            String url = etUrl.getText().toString().trim();
            if (url.isEmpty()) return;

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(new Date());
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String outputPath = new File(downloadDir, "Bigo_" + timeStamp + ".mp4").getAbsolutePath();

            int result = startRecording(url, outputPath);
            if (result == 0) {
                tvStatus.append("\n[Mulai] Rekaman berjalan...");
                btnRecord.setEnabled(false);
                btnStop.setEnabled(true);
            }
        });

        btnStop.setOnClickListener(v -> {
            stopRecording();
            tvStatus.append("\n[Berhenti] Menyimpan file...");
            btnRecord.setEnabled(true);
            btnStop.setEnabled(false);
            Toast.makeText(this, "Rekaman Berhenti!", Toast.LENGTH_SHORT).show();
        });
    }
}
