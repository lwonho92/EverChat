package com.lwonho92.everchat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lwonho92.everchat.R;

/**
 * Created by MY on 2017-02-14.
 */

public class FirstFragment extends Fragment {
    TextView firstFragmentTextView;
    String str;

    public FirstFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firstFragmentTextView = (TextView) getView().findViewById(R.id.tv_first_fragment);
    }

    public void viewArticle(String str) {
        firstFragmentTextView.setText("Changed: " + str);
    }
}
