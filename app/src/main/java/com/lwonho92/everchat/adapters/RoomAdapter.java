package com.lwonho92.everchat.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.lwonho92.everchat.ChatActivity;
import com.lwonho92.everchat.R;
import com.lwonho92.everchat.datas.EverChatRoom;

/**
 * Created by MY on 2017-02-08.
 */

public class RoomAdapter extends FirebaseRecyclerAdapter<EverChatRoom, RoomAdapter.RoomAdapterViewHolder> {
    public static Context mContext = null;

    public static class RoomAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView messageTextView;
        public TextView messengerTextView;
        public String id;

        public RoomAdapterViewHolder(View itemView) {
            super(itemView);

            messageTextView = (TextView) itemView.findViewById(R.id.last_messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.last_messengerTextView);

            itemView.setOnClickListener(this);
        }

        public void bind(EverChatRoom everChatRoom) {
            messageTextView.setText(everChatRoom.getText());
            messengerTextView.setText(everChatRoom.getName());

            id = everChatRoom.getId();
        }

        @Override
        public void onClick(View v) {
//            int adapterPosition = getAdapterPosition();
            int viewId = v.getId();

            switch(viewId) {
                default:
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra(ChatActivity.ROOM_ID, id);
                    mContext.startActivity(intent);
//                    Toast.makeText(mContext, id.toString(), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    public RoomAdapter(Context context, Class<EverChatRoom> modelClass, int modelLayout, Class<RoomAdapterViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);

        mContext = context;
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
