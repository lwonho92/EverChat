package com.lwonho92.everchat.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.lwonho92.everchat.ChatActivity;
import com.lwonho92.everchat.ProfileActivity;
import com.lwonho92.everchat.data.EverChatMessage;
import com.lwonho92.everchat.R;
import com.lwonho92.everchat.data.EverChatProfile;
import com.lwonho92.everchat.data.EverChatRoom;
import com.lwonho92.everchat.data.Utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MY on 2017-02-08.
 */

public class ChatAdapter extends FirebaseRecyclerAdapter<EverChatMessage, ChatAdapter.ChatAdapterViewHolder> {
    private static final String TAG = "ChatAdapter";
    private static final String clientId = "4XY1BAMwckenaG2O2vvy";//애플리케이션 클라이언트 아이디값";
    private static final String clientSecret = "CU7gWgfDcw";//애플리케이션 클라이언트 시크릿값";

    private static Context mContext;
    private final static String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public static class ChatAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout linearLayout;
        public CircleImageView messengerImageView;
        public TextView messengerTextView;
        public TextView messageTextView;
        public TextView timestampTextView;
        public String uid;

        public ChatAdapterViewHolder(View itemView) {
            super(itemView);

            linearLayout = (LinearLayout) itemView.findViewById(R.id.ll_message);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.im_messenger);
            messengerTextView = (TextView) itemView.findViewById(R.id.tv_messenger);
            messageTextView = (TextView) itemView.findViewById(R.id.tv_message);
            timestampTextView = (TextView) itemView.findViewById(R.id.tv_timestamp);

            messengerImageView.setOnClickListener(this);
        }

        public void bind(final EverChatMessage everChatMessage, int type) {
            timestampTextView.setText(Utils.getMillisToStr(everChatMessage.getTimestampLong()));
            uid = everChatMessage.getUid();
            GradientDrawable drawable = (GradientDrawable) messageTextView.getBackground();
            if(type == 0) {
                linearLayout.setGravity(Gravity.END);
                messengerImageView.setVisibility(View.INVISIBLE);
                drawable.setColor(Color.YELLOW);
            }
            else {
                linearLayout.setGravity(Gravity.START);
                messengerTextView.setText(everChatMessage.getName());
                drawable.setColor(Color.WHITE);
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
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String prefDefaultLanguage = mContext.getString(R.string.pref_default_language);
                    String source = everChatMessage.getLanguage();
                    String target = PreferenceManager.getDefaultSharedPreferences(mContext).getString(mContext.getString(R.string.pref_language), prefDefaultLanguage);
                    String message = everChatMessage.getMessage();

                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
                    boolean isOnTranslate = pref.getBoolean(mContext.getString(R.string.pref_translate), mContext.getResources().getBoolean(R.bool.pref_default_translate));

                    if(isOnTranslate == false || source.equals(target)) {
//                        Both languages are same.
                        return message;
                    }
                    else if(source.equals(prefDefaultLanguage) || target.equals(prefDefaultLanguage)) {
//                        At least a language is Korean.
                        return translateMessage(source, target, message);
                    } else {
//                        Both languages are not Korean.
                        String tmp = translateMessage(source, prefDefaultLanguage, message);
                        return translateMessage(prefDefaultLanguage, target, tmp);
                    }
                }

                @Override
                protected void onPostExecute(String str) {
                    messageTextView.setText(str);
                }
            }.execute();
        }

        private String translateMessage(String source, String target, String message) {
            try {
                String utfMessage = URLEncoder.encode(message, "UTF-8");
                String apiURL = mContext.getString(R.string.naver_api_url);
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty(mContext.getString(R.string.naver_client_id), clientId);
                con.setRequestProperty(mContext.getString(R.string.naver_client_secret), clientSecret);
                // post request
                String postParams = "source=" + source + "&target=" + target + "&text=" + utfMessage;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
//                            http code 400 / errorCode TR05 : source와 target이 동일.
//                            http code 400 / errorCode TR06 : source와 target 쌍이 적절하지 않습니다.
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                con.disconnect();

                JSONObject translatedText = new JSONObject(response.toString()).getJSONObject("message").getJSONObject("result");

                return translatedText.getString("translatedText");
            } catch (Exception  e) {
                Log.d(TAG, "Called onConnectionFailed:" + e.toString());
            }

            return "";
        }

        @Override
        public void onClick(View v) {
//            int adapterPosition = getAdapterPosition();
            int viewId = v.getId();

            switch(viewId) {
                case R.id.im_messenger:
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    intent.putExtra(mContext.getString(R.string.selected_user_id), uid);
                    mContext.startActivity(intent);
                    /*final Button button = new Button(mContext);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    button.setLayoutParams(lp);
                    button.setText("Star");
                    button.setTag("Hello");

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            final String tmpUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("auth").child(uid);
                            ref.runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    EverChatProfile everChatProfile = mutableData.getValue(EverChatProfile.class);

                                    if(everChatProfile == null)
                                        return Transaction.success(mutableData);

                                    if(everChatProfile.getStars() == null || !everChatProfile.getStars().containsKey(tmpUid)) {
                                        everChatProfile.getStars().put(tmpUid, true);
                                    } else {
                                        everChatProfile.getStars().remove(tmpUid);
                                    }
                                    mutableData.setValue(everChatProfile);

                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                                    if(databaseError == null) {
                                        EverChatProfile everChatProfile = (EverChatProfile)dataSnapshot.getValue(EverChatProfile.class);
                                        if(everChatProfile == null)
                                            return ;

                                        if(everChatProfile.getStars() == null)
                                            button.setText("0");
                                        else {
                                            try {
                                                button.setText(everChatProfile.getStars().size() + "");
                                            } catch(Exception ex) {
                                                Log.e(TAG, ex.toString());
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    });

                    final ToggleButton toggleButton = new ToggleButton(mContext);
                    toggleButton.setTextOn("줬");
                    toggleButton.setTextOff("뺏");
                    toggleButton.setChecked(true);
                    toggleButton.setLayoutParams(lp);
//                    toggleButton.setBackground(mContext.getResources().getDrawable(R.drawable.toggle_background));

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(mContext.getString(R.string.alert_dialog_title)).setMessage(R.string.alert_dialog_message).setCancelable(true)
                            .setView(button)
//                            .setView(toggleButton)
                            .setPositiveButton(mContext.getString(R.string.alert_dialog_positive), new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                            *//*String roomId = databaseReference.child(currentCountry).push().getKey();
                                            String roomName = roomNameEditText.getText().toString();
                                            databaseReference.child(currentCountry).child(roomId).setValue(new EverChatRoom(roomName, ""));

                                            Intent intent = new Intent(getContext(), ChatActivity.class);
                                            intent.putExtra(getString(R.string.room_id), roomId);
                                            intent.putExtra(getString(R.string.room_name), roomName);
                                            startActivity(intent);*//*
                                }
                            })
                            .setNegativeButton(mContext.getString(R.string.alert_dialog_negative), new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    dialog.cancel();
                                }
                            });
                    AppCompatDialog alert = builder.create();
                    alert.show();
*/
                    break;
            }
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
    public int getItemViewType(int position) {
        EverChatMessage everChatMessage = getItem(position);

        if(uid.equals(everChatMessage.getUid()))
//            My message
            return 0;
        else
//        Others message
            return 1;
    }

    @Override
    protected void populateViewHolder(ChatAdapterViewHolder viewHolder, EverChatMessage everChatMessage, int position) {
        int type = getItemViewType(position);

        viewHolder.bind(everChatMessage, type);
//        write this message to the on-device index
        /*FirebaseAppIndex.getInstance().update(getMessageIndexable(everChatMessage));
        FirebaseUserActions.getInstance().end(getMessageViewAction(everChatMessage));*/
    }

    @Override
    public void onBindViewHolder(ChatAdapterViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }
}
