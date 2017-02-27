package com.lwonho92.everchat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lwonho92.everchat.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MY on 2017-02-28.
 */

public class LicenseAdapter extends RecyclerView.Adapter<LicenseAdapter.LicenseAdapterViewHolder> {
    private String[] licenses;

    class LicenseAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_license_item) TextView licenseItemTextView;

        public LicenseAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int index) {
            if(licenses != null)
                licenseItemTextView.setText(licenses[index]);
        }
    }

    @Override
    public LicenseAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int listId = R.layout.item_license;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean immdy = false;

        View view = layoutInflater.inflate(listId, viewGroup, immdy);

        return new LicenseAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LicenseAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(licenses != null)
            return licenses.length;
        else
            return 0;
    }

    public void setString(String[] licenses) {
        this.licenses = licenses;

        notifyDataSetChanged();
    }
}
