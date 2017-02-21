package com.lwonho92.everchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.lwonho92.everchat.data.EverChatProfile;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ProfileActivity";

    private Toolbar toolbar;

    private String selectedUid;
    private String myUid;

    ImageView pictureImageView;
    TextView countryTextView, languageTextView, infoTextView;
    ToggleButton startToggleButton;

    DatabaseReference selectedStarsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.tb_profile);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Intent intent = getIntent();
        if(intent != null) {
            selectedUid = intent.getStringExtra(getString(R.string.profile_selected_uid));
        }
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        pictureImageView = (ImageView) findViewById(R.id.im_profile);
        countryTextView = (TextView) findViewById(R.id.tv_profile_country);
        languageTextView = (TextView) findViewById(R.id.tv_profile_language);
        infoTextView = (TextView) findViewById(R.id.tv_profile_info);
        startToggleButton = (ToggleButton) findViewById(R.id.tb_profile_stars);

        final DatabaseReference myStarsRef = FirebaseDatabase.getInstance().getReference("auth").child(myUid).child("stars");
        myStarsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> stars = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if(stars != null) {
                    if (stars.containsKey(selectedUid)) {
//                        Selected User like me!
                        infoTextView.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                infoTextView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, getString(R.string.firebase_iterate_value_error) + " : " + myStarsRef.toString());
            }
        });
        selectedStarsRef = FirebaseDatabase.getInstance().getReference("auth").child(selectedUid);
        selectedStarsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EverChatProfile everChatProfile = (EverChatProfile) dataSnapshot.getValue(EverChatProfile.class);
                if(everChatProfile == null)
                    return;

                Glide.with(ProfileActivity.this)
                        .load(everChatProfile.getPhotoUrl())
                        .into(pictureImageView);
                countryTextView.setText(everChatProfile.getCountry());
                languageTextView.setText(everChatProfile.getLanguage());
                infoTextView.setText(everChatProfile.getInfo());

                HashMap<String, Boolean> stars = everChatProfile.getStars();
                if(stars != null) {
                    startToggleButton.setTextOn(stars.size() + "");
                    startToggleButton.setTextOff(stars.size() + "");
                    startToggleButton.setChecked(stars.containsKey(myUid));
                    return;
                }
                startToggleButton.setTextOff("0");
                startToggleButton.setChecked(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, getString(R.string.firebase_single_value_error) + " : " + databaseError.toString());
            }
        });
        startToggleButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch(viewId) {
            case R.id.tb_profile_stars:
                updateStars();
                break;
        }
    }

    private void updateStars() {
        selectedStarsRef.runTransaction(new Transaction.Handler() {
            public Transaction.Result doTransaction(MutableData mutableData) {
                EverChatProfile everChatProfile = mutableData.getValue(EverChatProfile.class);
                if(everChatProfile == null)
                    return Transaction.success(mutableData);

                if (everChatProfile.getStars() == null || !everChatProfile.getStars().containsKey(myUid)) {
                    everChatProfile.getStars().put(myUid, true);
                } else {
                    everChatProfile.getStars().remove(myUid);
                }
                mutableData.setValue(everChatProfile);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if(databaseError == null) {
                    EverChatProfile everChatProfile = (EverChatProfile)dataSnapshot.getValue(EverChatProfile.class);
                    if(everChatProfile == null)
                        return ;

                    if(everChatProfile.getStars() == null) {
                        startToggleButton.setTextOn("0");
                        startToggleButton.setTextOff("0");
                        startToggleButton.setChecked(false);
                    }
                    else {
                        try {
                            startToggleButton.setTextOn(everChatProfile.getStars().size() + "");
                            startToggleButton.setTextOff(everChatProfile.getStars().size() + "");
                            startToggleButton.setChecked(everChatProfile.getStars().containsKey(myUid));
                        } catch(Exception ex) {
                            Log.e(TAG, ex.toString());
                        }
                    }
                } else {
                    Log.e(TAG, getString(R.string.firebase_transaction_error) + " : " + databaseError.toString());
                }
            }
        });
    }
}
