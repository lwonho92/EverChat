package com.lwonho92.everchat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lwonho92.everchat.R;

/**
 * Created by MY on 2017-02-14.
 */

public class SearchFragment extends Fragment {
    private TextView textView;
    private RadioButton krRadioButton, usRadioButton;
    private Button summitButton;
    SelectCountryListener mListener;

    public SearchFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (SelectCountryListener) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(e.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        textView = (TextView) getView().findViewById(R.id.profile_textview);
        krRadioButton = (RadioButton) getView().findViewById(R.id.kr_radio_button);
        usRadioButton = (RadioButton) getView().findViewById(R.id.us_radio_button);
        summitButton = (Button) getView().findViewById(R.id.summit_button);
        summitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "";
                if(krRadioButton.isChecked())
                    str = krRadioButton.getText().toString();
                else if(usRadioButton.isChecked())
                    str = usRadioButton.getText().toString();
                mListener.setSelectedCountry(str);
            }
        });
    }
    public interface SelectCountryListener {
        public void setSelectedCountry(String str);
    }
}
