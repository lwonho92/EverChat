package com.lwonho92.everchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.lwonho92.everchat.adapters.LicenseAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LicenseActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LicenseAdapter licenseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        ButterKnife.bind(this);

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