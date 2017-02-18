package com.lwonho92.everchat.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.lwonho92.everchat.datas.EverChatMessage;
import com.lwonho92.everchat.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MY on 2017-02-08.
 */

public class ChatAdapter extends FirebaseRecyclerAdapter<EverChatMessage, ChatAdapter.ChatAdapterViewHolder> {
    private static final String TAG = "ChatAdapter";
    public static Context mContext = null;
    private static final String clientId = "4XY1BAMwckenaG2O2vvy";//애플리케이션 클라이언트 아이디값";
    private static final String clientSecret = "CU7gWgfDcw";//애플리케이션 클라이언트 시크릿값";

    public static class ChatAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        CircleImageView
        public TextView messengerTextView;
        public TextView messageTextView;
        public CircleImageView messengerImageView;

        public ChatAdapterViewHolder(View itemView) {
            super(itemView);

            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);

            itemView.setOnClickListener(this);
        }

        public void bind(EverChatMessage everChatMessage) {
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
            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... params) {
                    try {
                        String text = URLEncoder.encode(params[0], "UTF-8");
                        String apiURL = "https://openapi.naver.com/v1/language/translate";
                        URL url = new URL(apiURL);
                        HttpURLConnection con = (HttpURLConnection)url.openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("X-Naver-Client-Id", clientId);
                        con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                        // post request
                        String postParams = "source=en&target=ko&text=" + text;
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
//                everChatMessage.getText()
                        return response.toString();
                    } catch (Exception  e) {
                        Log.d(TAG, "Called onConnectionFailed:" + e.toString());
                    }

                    return "";
                }

                @Override
                protected void onPostExecute(String s) {
                    try {
                        JSONObject translatedText = new JSONObject(s).getJSONObject("message").getJSONObject("result");

                        messageTextView.setText(translatedText.getString("translatedText"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.execute(everChatMessage.getText());
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
