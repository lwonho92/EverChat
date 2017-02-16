package com.lwonho92.everchat.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lwonho92.everchat.R;
import com.lwonho92.everchat.datas.Utils;
import com.lwonho92.everchat.fragments.SearchFragment;

import static com.lwonho92.everchat.datas.Utils.setupItem;
import static com.lwonho92.everchat.fragments.SearchFragment.COUNTRY_INDEX;

/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class HorizontalPagerAdapter extends PagerAdapter {

    private final Utils.LibraryObject[] LIBRARIES = new Utils.LibraryObject[]{
            new Utils.LibraryObject(
                    R.drawable.kr,
                    "KR"
            ),
            new Utils.LibraryObject(
                    R.drawable.cn,
                    "CN"
            ),
            new Utils.LibraryObject(
                    R.drawable.jp,
                    "JP"
            ),
            new Utils.LibraryObject(
                    R.drawable.us,
                    "US"
            )
    };

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SearchFragment.SelectCountryListener mListener;

    public HorizontalPagerAdapter(final Context context, SearchFragment.SelectCountryListener listener) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    @Override
    public int getCount() {
        return LIBRARIES.length;
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view;
        view = mLayoutInflater.inflate(R.layout.item_country, container, false);
        setupItem(view, LIBRARIES[position]);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.setSelectedCountry(COUNTRY_INDEX[position]);
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }
}
