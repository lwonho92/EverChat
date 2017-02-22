package com.lwonho92.everchat.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lwonho92.everchat.ChatActivity;
import com.lwonho92.everchat.R;
import com.lwonho92.everchat.adapters.RoomAdapter;
import com.lwonho92.everchat.data.EverChatRoom;

/**
 * Created by MY on 2017-02-14.
 */

public class RoomFragment extends Fragment implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "RoomFragment";

    private TextView roomFragmentTextView;
    private FloatingActionsMenu famButton;
    private FloatingActionButton fabHomeland, fabCreateRoom;
    private String home;
    private String currentCountry;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference databaseReference;
    private RoomAdapter firebaseRecyclerAdapter;

    public RoomFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        home = pref.getString(getContext().getString(R.string.pref_country), getString(R.string.pref_default_country));
        currentCountry = home;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        roomFragmentTextView = (TextView) getView().findViewById(R.id.tv_room_fragment);

        famButton = (FloatingActionsMenu) getView().findViewById(R.id.fam_button);
        fabHomeland = (FloatingActionButton) getView().findViewById(R.id.fab_homeland);
        fabCreateRoom = (FloatingActionButton) getView().findViewById(R.id.fab_create_room);
        fabHomeland.setOnClickListener(this);
        fabCreateRoom.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("room_names");

        recyclerView = (RecyclerView) getView().findViewById(R.id.rv_room);
        linearLayoutManager = new LinearLayoutManager(getContext());

        firebaseRecyclerAdapter = new RoomAdapter(getContext(),
                EverChatRoom.class,
                R.layout.item_room,
                RoomAdapter.RoomAdapterViewHolder.class,
                databaseReference.child(currentCountry), currentCountry);

        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyRoomCount = firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyRoomCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public void setCountry(String str) {
        currentCountry = str;
        if(roomFragmentTextView != null && currentCountry != null)
            roomFragmentTextView.setText(currentCountry);

        if(firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.cleanup();

        firebaseRecyclerAdapter = new RoomAdapter(getContext(),
                EverChatRoom.class,
                R.layout.item_room,
                RoomAdapter.RoomAdapterViewHolder.class,

//                Check this point(java.lang.NullPointerException: Attempt to invoke virtual method
//                'com.google.firebase.database.DatabaseReference com.google.firebase.database.DatabaseReference.child(java.lang.String)' on a null object reference)
                databaseReference.child(currentCountry), currentCountry);
        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyRoomCount = firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyRoomCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });

        recyclerView.swapAdapter(firebaseRecyclerAdapter, true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        famButton.collapse();
        switch(id) {
            case R.id.fab_create_room:
//                Toast.makeText(getContext(), "fab_create_room clicked", Toast.LENGTH_LONG).show();
                final EditText roomNameEditText = new EditText(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                roomNameEditText.setLayoutParams(lp);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getString(R.string.alert_dialog_title)).setMessage(R.string.alert_dialog_message).setCancelable(true)
                    .setView(roomNameEditText)
                    .setPositiveButton(getString(R.string.alert_dialog_positive), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            String roomId = databaseReference.child(currentCountry).push().getKey();
                            String roomName = roomNameEditText.getText().toString();
                            databaseReference.child(currentCountry).child(roomId).setValue(new EverChatRoom(roomName, ""));

                            Intent intent = new Intent(getContext(), ChatActivity.class);
                            intent.putExtra(getString(R.string.room_id), roomId);
                            intent.putExtra(getString(R.string.room_name), roomName);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(getString(R.string.alert_dialog_negative), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dialog.cancel();
                        }
                    });
                AppCompatDialog alert = builder.create();
                alert.show();

                break;
            case R.id.fab_homeland:
                setCountry(home);
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key == getString(R.string.pref_country)) {
            home = sharedPreferences.getString(key, getString(R.string.pref_default_country));
        }
        Log.e(TAG, "Change: " + key);
    }
}
