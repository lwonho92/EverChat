package com.lwonho92.everchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lwonho92.everchat.adapters.RoomAdapter;
import com.lwonho92.everchat.datas.EverChatRoom;

public class RoomActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final String TAG = "RoomActivity";
    private static String COUNTRY;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private GoogleApiClient googleApiClient;

    private DatabaseReference databaseReference;
    private RoomAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Intent intent = getIntent();
        if(intent != null) {
            COUNTRY = intent.getAction();
            setTitle(COUNTRY + " rooms");
        }

        recyclerView = (RecyclerView) findViewById(R.id.room_recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseRecyclerAdapter = new RoomAdapter(this,
                EverChatRoom.class,
                R.layout.item_room,
                RoomAdapter.RoomAdapterViewHolder.class,
                databaseReference.child("room_names/"+COUNTRY));

        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyRoomCount = firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyRoomCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabButton.setOnClickListener(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Called onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        newRoom();
    }

    public void newRoom() {
//        databaseReference.child
    }
}
