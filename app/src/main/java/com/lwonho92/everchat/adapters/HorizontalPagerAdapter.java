package com.lwonho92.everchat.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lwonho92.everchat.R;
import com.lwonho92.everchat.fragments.SearchFragment;

/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class HorizontalPagerAdapter extends PagerAdapter {
    String TAG = "HorizontalPagerAdapter";
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SearchFragment.SelectCountryListener mListener;

    private final int[] drawableCountry = {R.drawable.kr, R.drawable.cn, R.drawable.jp, R.drawable.us};
    private final String[] fullCountry;
    private final String[] country;
    private TypedArray flags;

    public HorizontalPagerAdapter(final Context context, SearchFragment.SelectCountryListener listener) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mListener = listener;
        TypedArray flags = mContext.getResources().obtainTypedArray(R.array.drawable_countries);
//        drawableCountry = flags.get;
//        Log.e(TAG, drawableCountry.toString());
        fullCountry = mContext.getResources().getStringArray(R.array.full_countries);
        country = mContext.getResources().getStringArray(R.array.short_countries);
    }

    @Override
    public int getCount() {
        return drawableCountry.length;
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view;
        view = mLayoutInflater.inflate(R.layout.item_country, container, false);
        setupItem(view, position);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.setSelectedCountry(country[position]);
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

    public void setupItem(final View view, int position) {
        ImageView flag = (ImageView) view.findViewById(R.id.im_card_view);
        TextView text = (TextView) view.findViewById(R.id.tv_card_view);

//        Log.e(TAG, drawableCountry[position]+"");
        Glide.with(mContext)
                .load(drawableCountry[position])
                .into(flag);
//        flag.setImageResource(drawableCountry[position]);
        text.setText(fullCountry[position]);
    }
}
