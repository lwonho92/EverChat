package com.lwonho92.everchat;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.lwonho92.everchat.adapters.LicenseAdapter;
import com.lwonho92.everchat.data.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LicenseActivity extends AppCompatActivity {
    private Toolbar toolbar;
    @BindView(R.id.recyclerview_license) RecyclerView recyclerView;
    private LicenseAdapter licenseAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        ButterKnife.bind(this);

        Utils.setCalligraphyConfig(this);

        toolbar = (Toolbar) findViewById(R.id.tb_license);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_license);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        licenseAdapter= new LicenseAdapter();

        recyclerView.setAdapter(licenseAdapter);

        String[] licensesArray = getResources().getStringArray(R.array.opensource_licenses);
        licenseAdapter.setString(licensesArray);
    }
}