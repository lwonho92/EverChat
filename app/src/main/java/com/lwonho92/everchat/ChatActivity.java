package com.lwonho92.everchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import com.lwonho92.everchat.adapters.ChatAdapter;
import com.lwonho92.everchat.data.EverChatMessage;

public class ChatActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "ChatActivity";
    public static final String COUNTRY_ID = "country_id";
    public static final String ROOM_ID = "room_id";
    private static final String ANONYMOUS = "anonymous";

    private Toolbar toolbar;

    private Button button;
    private EditText editText;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private String mUsername;
    private String mPhotoUrl;

    private String roomId = "";
    private String roomName = "";
    private String prefLanguage = "";

//    Google instance variables
    private GoogleApiClient googleApiClient;

//    Firebase instance variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatabaseReference databaseReference;
    private ChatAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.tb_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if(intent != null) {
            roomId = intent.getStringExtra(getString(R.string.room_id));
            roomName = intent.getStringExtra(getString(R.string.room_name));
            setTitle(roomName);
        }
        prefLanguage = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_language), getString(R.string.pref_default_language));

        mUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        mPhotoUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();

        button = (Button) findViewById(R.id.sendButton);
        editText = (EditText) findViewById(R.id.messageEditText);

        recyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        databaseReference = FirebaseDatabase.getInstance().getReference("messages");

        firebaseRecyclerAdapter = new ChatAdapter(this,
                EverChatMessage.class,
                R.layout.item_message,
                ChatAdapter.ChatAdapterViewHolder.class,
                databaseReference.child(roomId));

        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EverChatMessage everChatMessage = new EverChatMessage(mUsername, mPhotoUrl, editText.getText().toString(), prefLanguage);
                databaseReference.child(roomId).push().setValue(everChatMessage);
                editText.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case R.id.action_signout:
                firebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(googleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Called onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key == getString(R.string.pref_language)) {
            prefLanguage = sharedPreferences.getString(key, getString(R.string.pref_default_language));
        }
        Log.e(TAG, "Change: " + key);
    }
}