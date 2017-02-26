package com.lwonho92.everchat.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lwonho92.everchat.R;
import com.lwonho92.everchat.SettingsActivity;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by MY on 2017-02-14.
 */

public class MoreFragment extends Fragment implements View.OnClickListener {
    FancyButton settingsButton, copyrightButton, contactButton, githubButton;

    public MoreFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        settingsButton = (FancyButton) getView().findViewById(R.id.bt_settings);
        copyrightButton = (FancyButton) getView().findViewById(R.id.bt_copy_right);
        contactButton = (FancyButton) getView().findViewById(R.id.bt_contact);
        githubButton = (FancyButton) getView().findViewById(R.id.bt_github);
        settingsButton.setOnClickListener(this);
        copyrightButton.setOnClickListener(this);
        contactButton.setOnClickListener(this);
        githubButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id) {
            case R.id.bt_settings:
                Intent settingIntent = new Intent(getContext(), SettingsActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.bt_copy_right:
                Toast.makeText(getView().getContext(), "Copy Right is soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_contact:
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
