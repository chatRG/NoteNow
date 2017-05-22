package com.notenow.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.notenow.R;
import com.notenow.utils.UtilTypeface;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView app_id = (TextView) findViewById(R.id.app_id);
        TextView app_ver = (TextView) findViewById(R.id.app_ver);
        TextView app_dev = (TextView) findViewById(R.id.dev_name);

        UtilTypeface.setCustomTypeface(getApplicationContext(), app_id, app_ver, app_dev);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo != null ? pInfo.versionName : null;

        app_id.setText(String.format("%s", getResources().getString(R.string.app_name)));
        app_ver.setText(String.format("v%s", version));
        app_dev.setText(String.format("%s", getResources().getString(R.string.dev_name)));
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
