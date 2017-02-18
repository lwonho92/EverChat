package com.lwonho92.everchat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lwonho92.everchat.datas.EverChatProfile;
import com.lwonho92.everchat.fragments.SearchFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "SettingsActivity";
    private Toolbar toolbar;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference ref;

    private GoogleApiClient googleApiClient;

    ImageView pictureImageView;
    Spinner countrySpinner, languageSpinner;
    EditText profileEditText;

    EverChatProfile everChatProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);*/
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.tb_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pictureImageView = (CircleImageView) findViewById(R.id.im_settings);
        countrySpinner = (Spinner) findViewById(R.id.sp_country);
        languageSpinner = (Spinner) findViewById(R.id.sp_language);
        profileEditText = (EditText) findViewById(R.id.et_intro);

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("/auth").child(firebaseUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Glide.with(SettingsActivity.this)
                        .load(firebaseUser.getPhotoUrl())
                        .into(pictureImageView);
                if(dataSnapshot.exists()) {
                    everChatProfile = (EverChatProfile) dataSnapshot.getValue(EverChatProfile.class);

                    for(int i = 0; i < SearchFragment.COUNTRY_INDEX.length; i++) {
                        if(SearchFragment.COUNTRY_INDEX[i].equals(everChatProfile.getCountry())) {
                            countrySpinner.setSelection(i);
                            break;
                        }
                    }
                } else {
                    everChatProfile = new EverChatProfile();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();

        switch(selectedItem) {
            case R.id.action_apply:
                everChatProfile.setUserName(firebaseUser.getDisplayName());
                everChatProfile.setPhotoUrl(firebaseUser.getPhotoUrl().toString());
                everChatProfile.setCountry(countrySpinner.getSelectedItem().toString());
                everChatProfile.setLanguage(languageSpinner.getSelectedItem().toString());
                everChatProfile.setProfile(profileEditText.getText().toString());
                ref.setValue(everChatProfile);
                finish();
            case R.id.action_cancel:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Called onConnectionFailed:" + connectionResult);
    }
}
