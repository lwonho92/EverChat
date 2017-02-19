package com.lwonho92.everchat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.lwonho92.everchat.R;
import com.lwonho92.everchat.adapters.HorizontalPagerAdapter;

/**
 * Created by MY on 2017-02-14.
 */

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    SelectCountryListener mListener;

    private HorizontalInfiniteCycleViewPager infiniteCycleViewPager;

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

//        ViewPagerAdapter
        infiniteCycleViewPager = (HorizontalInfiniteCycleViewPager) getView().findViewById(R.id.hicvp);
        infiniteCycleViewPager.setAdapter(new HorizontalPagerAdapter(getContext(), mListener));
        infiniteCycleViewPager.setInterpolator(
                AnimationUtils.loadInterpolator(getContext(), android.R.anim.overshoot_interpolator)
        );
    }

    @Override
    public void onResume() {
        super.onResume();

        infiniteCycleViewPager.startAutoScroll(true);
    }

    @Override
    public void onPause() {
        infiniteCycleViewPager.stopAutoScroll();

        super.onPause();
    }

    public interface SelectCountryListener {
        public void setSelectedCountry(String str);
    }
}
