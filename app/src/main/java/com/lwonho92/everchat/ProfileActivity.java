package com.lwonho92.everchat;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    private String selectedUid;

    ImageView pictureImageView;
    TextView countryTextView, languageTextView, profileTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        if(intent != null) {
            selectedUid = intent.getStringExtra(getString(R.string.selected_user_id));
        }

        pictureImageView = (ImageView) findViewById(R.id.im_profile_photo);
        countryTextView = (TextView) findViewById(R.id.tv_profile_country);
        languageTextView = (TextView) findViewById(R.id.tv_profile_language);
        profileTextView = (TextView) findViewById(R.id.tv_profile_intro);

        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("auth").child(myUid).child("stars");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    HashMap<String, Boolean> stars = (HashMap<String, Boolean>) dataSnapshot.getValue();
                    if(stars.containsKey(selectedUid)) {
                        profileTextView.setText("나 추천 받음");
                        return;
                    }
                }
                profileTextView.setText("나 추천 못받음.");

                Log.e(TAG, "Check my stars.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
