package com.lwonho92.everchat.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.lwonho92.everchat.ChatActivity;
import com.lwonho92.everchat.R;
import com.lwonho92.everchat.data.EverChatRoom;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MY on 2017-02-08.
 */

public class RoomAdapter extends FirebaseRecyclerAdapter<EverChatRoom, RoomAdapter.RoomAdapterViewHolder> {
    private static Context mContext;
    private static String country;

    public static class RoomAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        CircleImageView
        private CircleImageView roomPhotoImageView;
        private TextView roomNameTextView;
        private TextView messageTextView;

        public String id;

        public RoomAdapterViewHolder(View itemView) {
            super(itemView);

            roomPhotoImageView = (CircleImageView) itemView.findViewById(R.id.im_room_photo);
            roomNameTextView = (TextView) itemView.findViewById(R.id.last_messengerTextView);
            messageTextView = (TextView) itemView.findViewById(R.id.last_messageTextView);

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
//                    Toast.makeText(mContext, id.toString(), Toast.LENGTH_LONG).show();
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
        }
        return everChatRoom;
    }

    @Override
    protected void populateViewHolder(RoomAdapterViewHolder viewHolder, EverChatRoom everChatRoom, int position) {
        viewHolder.bind(everChatRoom);
//        write this message to the on-device index
        /*FirebaseAppIndex.getInstance().update(getMessageIndexable(everChatMessage));
        FirebaseUserActions.getInstance().end(getMessageViewAction(everChatMessage));*/
    }
}
