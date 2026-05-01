package com.example.bigoguardian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.ArrayList;

public class MainActivity extends Activity {
    ArrayList<RecordModel> list = new ArrayList<>();
    BaseAdapter adapter;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText input = findViewById(R.id.inputUrl);
        ListView listView = findViewById(R.id.listRecordings);

        adapter = new BaseAdapter() {
            @Override public int getCount() { return list.size(); }
            @Override public Object getItem(int i) { return list.get(i); }
            @Override public long getItemId(int i) { return i; }
            @Override public View getView(int i, View v, ViewGroup p) {
                if (v == null) v = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item, p, false);
                RecordModel m = list.get(i);
                ((TextView)v.findViewById(R.id.txtUrl)).setText(m.url);
                TextView txtDur = v.findViewById(R.id.txtDuration);
                txtDur.setText("Durasi: " + m.getDurationStr());
                
                v.findViewById(R.id.btnStop).setOnClickListener(view -> {
                    Intent it = new Intent(MainActivity.this, RecorderService.class);
                    it.setAction("STOP");
                    it.putExtra("id", m.id);
                    startService(it);
                    list.remove(i);
                    notifyDataSetChanged();
                });
                return v;
            }
        };

        listView.setAdapter(adapter);

        findViewById(R.id.btnStart).setOnClickListener(v -> {
            String url = input.getText().toString();
            if (!url.isEmpty()) {
                RecordModel m = new RecordModel(counter++, url);
                list.add(m);
                Intent it = new Intent(this, RecorderService.class);
                it.putExtra("id", m.id);
                it.putExtra("url", url);
                startService(it);
                adapter.notifyDataSetChanged();
                input.setText("");
            }
        });

        // Loop untuk update durasi real-time
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override public void run() {
                for (RecordModel m : list) m.seconds++;
                adapter.notifyDataSetChanged();
                h.postDelayed(this, 1000);
            }
        }, 1000);
    }

    class RecordModel {
        int id, seconds = 0;
        String url;
        RecordModel(int id, String url) { this.id = id; this.url = url; }
        String getDurationStr() {
            int h = seconds / 3600, m = (seconds % 3600) / 60, s = seconds % 60;
            return String.format("%02d:%02d:%02d", h, m, s);
        }
    }
}
