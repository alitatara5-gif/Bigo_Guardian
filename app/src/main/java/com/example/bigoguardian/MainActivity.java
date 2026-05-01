package com.example.bigoguardian;

import android.app.Activity;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.widget.*;
import android.Manifest;
import android.content.pm.PackageManager;
import java.util.ArrayList;

public class MainActivity extends Activity {
    TextView txtStatus;
    ArrayList<RecordModel> list = new ArrayList<>();
    BaseAdapter adapter;
    RecorderService mService;
    boolean mBound = false;

    String[] libs = {"c++_shared", "avutil", "swresample", "avcodec", "avformat", "swscale", "bigoguardian_engine"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        txtStatus = findViewById(R.id.txtStatus); // Pastikan ID ini ada di layout
        
        // --- STEP 1: TODONG IJIN ---
        checkPermissions();

        // --- STEP 2: CEK 7 FILE .SO ---
        StringBuilder libStatus = new StringBuilder("Status Mesin:\n");
        boolean allGood = true;
        for (String lib : libs) {
            try {
                System.loadLibrary(lib);
                libStatus.append("✅ ").append(lib).append(" LOADED\n");
            } catch (UnsatisfiedLinkError e) {
                libStatus.append("❌ ").append(lib).append(" MISSING\n");
                allGood = false;
            }
        }
        txtStatus.setText(libStatus.toString());

        if (!allGood) {
            Toast.makeText(this, "MESIN BELUM LENGKAP! Cek jniLibs!", Toast.LENGTH_LONG).show();
        }

        // --- STEP 3: LANJUT OPERASI ---
        try {
            bindService(new Intent(this, RecorderService.class), connection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) { e.printStackTrace(); }

        setupUI();
    }

    private void checkPermissions() {
        // Ijin Notifikasi (Android 13)
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        // Ijin Akses Semua File (Android 11+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                }
            }
        }
    }

    private void setupUI() {
        EditText input = findViewById(R.id.inputUrl);
        ListView listView = findViewById(R.id.listRecordings);
        adapter = new BaseAdapter() {
            @Override public int getCount() { return list.size(); }
            @Override public Object getItem(int i) { return list.get(i); }
            @Override public long getItemId(int i) { return i; }
            @Override public android.view.View getView(int i, android.view.View v, android.view.ViewGroup p) {
                if (v == null) v = android.view.LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item, p, false);
                RecordModel m = list.get(i);
                ((TextView)v.findViewById(R.id.txtUrl)).setText(m.url);
                long curDur = (mBound && mService != null) ? mService.getNativeDuration((int)m.id) : 0;
                ((TextView)v.findViewById(R.id.txtDuration)).setText("Durasi: " + String.format("%02d:%02d:%02d", curDur / 3600, (curDur % 3600) / 60, curDur % 60));
                v.findViewById(R.id.btnStop).setOnClickListener(view -> {
                    if (mBound) mService.stopNativeRecording((int)m.id);
                    list.remove(i); notifyDataSetChanged();
                });
                return v;
            }
        };
        listView.setAdapter(adapter);
        findViewById(R.id.btnStart).setOnClickListener(v -> {
            String url = input.getText().toString();
            if (!url.isEmpty()) {
                int id = (int)(System.currentTimeMillis() % 10000);
                list.add(new RecordModel(id, url));
                Intent it = new Intent(this, RecorderService.class);
                it.putExtra("id", id); it.putExtra("url", url);
                startService(it);
                adapter.notifyDataSetChanged();
                input.setText("");
            }
        });
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override public void run() {
                if (mBound) adapter.notifyDataSetChanged();
                new Handler(Looper.getMainLooper()).postDelayed(this, 1000);
            }
        }, 1000);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName n, IBinder s) {
            mService = ((RecorderService.LocalBinder) s).getService();
            mBound = true;
        }
        @Override public void onServiceDisconnected(ComponentName n) { mBound = false; }
    };

    class RecordModel { long id; String url; RecordModel(long id, String url) { this.id = id; this.url = url; } }
}
