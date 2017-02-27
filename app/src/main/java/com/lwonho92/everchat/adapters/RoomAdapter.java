package com.lwonho92.everchat.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.lwonho92.everchat.ChatActivity;
import com.lwonho92.everchat.R;
import com.lwonho92.everchat.data.EverChatRoom;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MY on 2017-02-08.
 */

public class RoomAdapter extends FirebaseRecyclerAdapter<EverChatRoom, RoomAdapter.RoomAdapterViewHolder> {
    private static Context mContext;
    private static String country;

    public static class RoomAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.im_room_photo) CircleImageView roomPhotoImageView;
        @BindView(R.id.tv_room_name) TextView roomNameTextView;
        @BindView(R.id.tv_last_message) TextView messageTextView;

        public String id;

        public RoomAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void bind(EverChatRoom everChatRoom) {
            String roomPhotoUrl = everChatRoom.getRoomPhotoUrl();

            String[] arrCountry = mContext.getResources().getStringArray(R.array.short_countries);
            int i;
            for(i = 0; i < 4; i++) {
                if(roomPhotoUrl.equals(arrCountry[i]))
                    break;
            }

            TypedArray drawables = mContext.getResources().obtainTypedArray(R.array.drawable_countries);
            Glide.with(mContext)
                    .load(drawables.getResourceId(i, -1))
                    .centerCrop()
                    .into(roomPhotoImageView);

            drawables.recycle();
            roomNameTextView.setText(everChatRoom.getName());
            messageTextView.setText(everChatRoom.getText());

            id = everChatRoom.getId();
        }

        @Override
        public void onClick(View v) {
//            int adapterPosition = getAdapterPosition();
            int viewId = v.getId();

            switch(viewId) {
                default:
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra(mContext.getString(R.string.room_id), id);
                    intent.putExtra(mContext.getString(R.string.room_country), country);
                    intent.putExtra(mContext.getString(R.string.room_name), roomNameTextView.getText().toString());
                    mContext.startActivity(intent);
                    break;
            }
        }
    }

    public RoomAdapter(Context context, Class<EverChatRoom> modelClass, int modelLayout, Class<RoomAdapterViewHolder> viewHolderClass, Query ref, String country) {
        super(modelClass, modelLayout, viewHolderClass, ref);

        mContext = context;
        this.country = country;
    }

    @Override
    protected EverChatRoom parseSnapshot(DataSnapshot snapshot) {
        EverChatRoom everChatRoom = super.parseSnapshot(snapshot);
        if(everChatRoom != null) {
            everChatRoom.setId(snapshot.getKey());
        } else {
            Log.e("check", "hello");
        }
        return everChatRoom;
    }

    @Override
    protected void populateViewHolder(RoomAdapterViewHolder viewHolder, EverChatRoom everChatRoom, int position) {
        viewHolder.bind(everChatRoom);
    }

}
