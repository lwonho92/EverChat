package com.lwonho92.everchat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lwonho92.everchat.R;
import com.lwonho92.everchat.adapters.RoomAdapter;
import com.lwonho92.everchat.datas.EverChatRoom;

/**
 * Created by MY on 2017-02-14.
 */

public class RoomFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "RoomFragment";

    TextView roomFragmentTextView;
    FloatingActionButton fabButton;
    String country = "KR";

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference databaseReference;
    private RoomAdapter firebaseRecyclerAdapter;

    public RoomFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        roomFragmentTextView = (TextView) getView().findViewById(R.id.tv_room_fragment);
        fabButton = (FloatingActionButton) getView().findViewById(R.id.fab_button);
        fabButton.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = (RecyclerView) getView().findViewById(R.id.rv_room);
        linearLayoutManager = new LinearLayoutManager(getContext());

        firebaseRecyclerAdapter = new RoomAdapter(getContext(),
                EverChatRoom.class,
                R.layout.item_room,
                RoomAdapter.RoomAdapterViewHolder.class,
                databaseReference.child("room_names").child(country));

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
        country = str;
        if(roomFragmentTextView != null && country != null)
            roomFragmentTextView.setText(country);

        if(firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.cleanup();

        firebaseRecyclerAdapter = new RoomAdapter(getContext(),
                EverChatRoom.class,
                R.layout.item_room,
                RoomAdapter.RoomAdapterViewHolder.class,
                databaseReference.child("room_names").child(country));
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

        switch(id) {
            case R.id.fab_button:
                Toast.makeText(getContext(), "fab_button clicked", Toast.LENGTH_LONG).show();

                break;
        }
    }
}
