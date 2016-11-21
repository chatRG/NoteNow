package com.notenow.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.notenow.R;

public class About extends AppCompatActivity {
    final static private String VERSION = "v1.0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView app_id = (TextView) findViewById(R.id.app_id);
        TextView app_ver = (TextView) findViewById(R.id.app_ver);
        TextView app_dev = (TextView) findViewById(R.id.dev_name);

        app_id.setText(String.format("%s", getResources().getString(R.string.app_name)));
        app_ver.setText(String.format("%s", VERSION));
        app_dev.setText(String.format("%s", getResources().getString(R.string.dev_name)));
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
