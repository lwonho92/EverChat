package com.lwonho92.everchat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lwonho92.everchat.R;
import com.lwonho92.everchat.SettingsActivity;

/**
 * Created by MY on 2017-02-14.
 */

public class MoreFragment extends Fragment implements View.OnClickListener {
    Button settingsButton;
    ImageView settingsImageView;

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

        settingsButton = (Button) getView().findViewById(R.id.bt_settings);
        settingsButton.setOnClickListener(this);
        settingsImageView = (ImageView) getView().findViewById(R.id.im_settings);
//        (TEST) Load image that stored in Firebase Storage with URL.
        /*Glide.with(getContext())
                .load("https://firebasestorage.googleapis.com/v0/b/everchat-6ce20.appspot.com/o/mountains.png?alt=media&token=a8fe05ef-2c9f-43ff-ab6c-643f11e4771e")
                .into(settingsImageView);*/
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id) {
            case R.id.bt_settings:
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
