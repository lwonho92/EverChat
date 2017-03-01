package com.lwonho92.everchat.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lwonho92.everchat.LicenseActivity;
import com.lwonho92.everchat.R;
import com.lwonho92.everchat.SettingsActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by MY on 2017-02-14.
 */

public class MoreFragment extends Fragment {
    public MoreFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @OnClick({R.id.bt_settings, R.id.bt_license, R.id.bt_gmail, R.id.bt_github})
    public void onClick(View v) {
        int id = v.getId();

        switch(id) {
            case R.id.bt_settings:
                Intent settingIntent = new Intent(getContext(), SettingsActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.bt_license:
                Intent licenseIntent = new Intent(getContext(), LicenseActivity.class);
                startActivity(licenseIntent);
                break;
            case R.id.bt_gmail:
                Uri contactUri = Uri.parse("mailto:lwonho92@gmail.com");
                Intent contactIntent = new Intent(Intent.ACTION_SENDTO, contactUri);
                startActivity(contactIntent);
                break;
            case R.id.bt_github:
                Uri githubUri = Uri.parse("http://github.com/lwonho92");
                Intent githubIntent = new Intent(Intent.ACTION_VIEW, githubUri);
                startActivity(githubIntent);
                break;
        }
    }
}
