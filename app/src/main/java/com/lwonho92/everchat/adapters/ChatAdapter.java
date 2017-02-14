package com.lwonho92.everchat.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.lwonho92.everchat.datas.EverChatMessage;
import com.lwonho92.everchat.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MY on 2017-02-08.
 */

public class ChatAdapter extends FirebaseRecyclerAdapter<EverChatMessage, ChatAdapter.ChatAdapterViewHolder> {
    public static Context mContext = null;

    public static class ChatAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView messageTextView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;

        public ChatAdapterViewHolder(View itemView) {
            super(itemView);

            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);

            itemView.setOnClickListener(this);
        }

        public void bind(EverChatMessage everChatMessage) {
            messageTextView.setText(everChatMessage.getText());
            messengerTextView.setText(everChatMessage.getName());
            if (everChatMessage.getPhotoUrl() == null) {
                messengerImageView.setImageDrawable(ContextCompat.getDrawable(
                        ChatAdapter.mContext,
                        R.drawable.ic_account_circle_black_36dp));
            } else {
                Glide.with(ChatAdapter.mContext)
                        .load(everChatMessage.getPhotoUrl())
                        .into(messengerImageView);
            }
        }

        @Override
        public void onClick(View v) {
//            int adapterPosition = getAdapterPosition();
            int viewId = v.getId();

//            switch(viewId) {
//                case R.id.messengerImageView:
                    Toast.makeText(mContext, messengerTextView.getText().toString(), Toast.LENGTH_LONG).show();
//                    break;
//            }
        }
    }

    public ChatAdapter(Context context, Class<EverChatMessage> modelClass, int modelLayout, Class<ChatAdapterViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);

        mContext = context;
    }

    @Override
    protected EverChatMessage parseSnapshot(DataSnapshot snapshot) {
        EverChatMessage everChatMessage = super.parseSnapshot(snapshot);
        if(everChatMessage != null) {
            everChatMessage.setId(snapshot.getKey());
        }
        return everChatMessage;
    }

    @Override
    protected void populateViewHolder(ChatAdapterViewHolder viewHolder, EverChatMessage everChatMessage, int position) {
        viewHolder.bind(everChatMessage);
//        write this message to the on-device index
        /*FirebaseAppIndex.getInstance().update(getMessageIndexable(everChatMessage));
        FirebaseUserActions.getInstance().end(getMessageViewAction(everChatMessage));*/
    }
}
