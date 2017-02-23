package com.lwonho92.everchat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.lwonho92.everchat.adapters.ChatAdapter;
import com.lwonho92.everchat.data.EverChatMessage;
import com.lwonho92.everchat.data.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener, PermissionListener {
    private static final String TAG = "ChatActivity";
    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    private Toolbar toolbar;

    private Button sendButton;
    private ImageButton pictureImageButton, translateImageButton;
    private EditText editText;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private String mUsername;
    private String mPhotoUrl;

    private String roomId = "";
    private String roomCountry = "";
    private String roomName = "";
    private String prefLanguage = "";

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
            roomCountry = intent.getStringExtra(getString(R.string.room_country));
            setTitle(roomName);
        }
        prefLanguage = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_language), getString(R.string.pref_default_language));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mUsername = firebaseUser.getDisplayName();
        mPhotoUrl = firebaseUser.getPhotoUrl().toString();

        translateImageButton = (ImageButton) findViewById(R.id.ib_translate);
        sendButton = (Button) findViewById(R.id.send_button);
        pictureImageButton = (ImageButton) findViewById(R.id.ib_picture);
        editText = (EditText) findViewById(R.id.messageEditText);

        recyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseRecyclerAdapter = new ChatAdapter(this,
                EverChatMessage.class,
                R.layout.item_message,
                ChatAdapter.ChatAdapterViewHolder.class,
                databaseReference.child("messages").child(roomId));

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
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        translateImageButton.setOnClickListener(this);
        boolean isOnTranslate = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_translate), getResources().getBoolean(R.bool.pref_default_translate));
        if(isOnTranslate) {
            visiableTranslation();
        } else {
            invisiableTranslation();
        }
        sendButton.setOnClickListener(this);
        pictureImageButton.setOnClickListener(this);

        databaseReference.child("room_names").child(roomCountry).child(roomId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ;
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Toast.makeText(ChatActivity.this, "Expire this room.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                ;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch(itemId) {
            case R.id.action_translation_on:
                item.setTitle("Off");
                return true;
            case R.id.action_translation_off:
                item.setTitle("Off");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getImages() {
        Config config = new Config();
        config.setToolbarTitleRes(R.string.custom_title);
        config.setSelectionMin(1);
        config.setSelectionLimit(5);

        ImagePickerActivity.setConfig(config);

        Intent intent  = new Intent(this, ImagePickerActivity.class);
        startActivityForResult(intent,INTENT_REQUEST_GET_IMAGES);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK ) {

            final ArrayList<Uri> image_uris = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    //do something
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://everchat-6ce20.appspot.com");

                    for (Uri uri : image_uris) {
                        final String key = databaseReference.child("messages").child(roomId).push().getKey();
                        StorageReference saveRef = storageRef.child(roomId).child(key).child(uri.getLastPathSegment());
                        try {
                            InputStream stream = new FileInputStream(new File(uri.toString()));
                            UploadTask uploadTask = saveRef.putStream(stream);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.e(TAG, "Upload Failed");
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                                    Save gs://<bucket>/<roomId>/<messageId>/<file_name>
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    Log.e(TAG, "Upload Success: " + downloadUrl.toString());

                                    EverChatMessage everChatMessage = new EverChatMessage(mUsername, mPhotoUrl, prefLanguage, FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    everChatMessage.setPicture(downloadUrl.toString());
                                    Map<String, Object> postValues = everChatMessage.toMap();

                                    Map<String, Object> childUpdates = new HashMap<>();
                                    childUpdates.put("/messages/" + roomId + "/" + key, postValues);
                                    childUpdates.put("/room_names/" + roomCountry + "/" + roomId + "/text", "(picture)");

                                    databaseReference.updateChildren(childUpdates);
                                }
                            });
                        } catch(Exception ex) {
                            Log.e(TAG, "Upload Exception: " + ex.toString());
                        }
                    }

                    return null;
                }
            }.execute();
        }
    }

    private Menu menu;
    private String inBedMenuTitle = "Set to 'In bed'";
    private String outOfBedMenuTitle = "Set to 'Out of bed'";
    private boolean inBed = false;

    private void updateMenuTitles() {
        MenuItem bedMenuItem = menu.findItem(R.id.action_translation_on);
        if (inBed) {
            bedMenuItem.setTitle(outOfBedMenuTitle);
        } else {
            bedMenuItem.setTitle(inBedMenuTitle);
        }
        inBed = !inBed;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        updateMenuTitles();

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key == getString(R.string.pref_language)) {
            prefLanguage = sharedPreferences.getString(key, getString(R.string.pref_default_language));
        }
    }

    @Override
    public void onClick(View v) {
        int selectedId = v.getId();

        switch(selectedId) {
            case R.id.ib_translate:
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = pref.edit();

                boolean isOnTranslate = pref.getBoolean(getString(R.string.pref_translate), getResources().getBoolean(R.bool.pref_default_translate));
                if(isOnTranslate) {
                    invisiableTranslation();
                } else {
                    visiableTranslation();
                }
                editor.putBoolean(getString(R.string.pref_translate), !isOnTranslate);
                editor.commit();

                firebaseRecyclerAdapter.notifyDataSetChanged();
//                recyclerView.swapAdapter(firebaseRecyclerAdapter, true);

                break;
            case R.id.ib_picture:
                new TedPermission(this)
                        .setPermissionListener(this)
                        .setDeniedMessage(getString(R.string.permission_deny_guide))
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .check();
                break;
            case R.id.send_button:
                databaseReference.child("room_names").child(roomCountry).child(roomId).child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long timestamp = dataSnapshot.getValue(Long.class);
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(timestamp);
                        cal.add(Calendar.MINUTE, 10);

                        if(cal.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
//                            Room made expire life. (Over 10 Minutes)
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/messages/" + roomId, null);
                            childUpdates.put("/room_names/" + roomCountry + "/" + roomId, null);
                            databaseReference.updateChildren(childUpdates);

//                            Remove storage pictures.
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
                                    //do something
                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                    StorageReference storageRef = storage.getReferenceFromUrl("gs://everchat-6ce20.appspot.com");

                                    StorageReference removeRef = storageRef.child(roomId);
                                    try {
//                                        TODO gsutil rm gs://bucket/subdir/**
//                                        https://cloud.google.com/storage/docs/json_api/v1/objects/list
//                                        Unfortunately, firebase do not provide delete directory. I'll find solution which issue.

                                        removeRef.delete().addOnSuccessListener(new OnSuccessListener() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                Log.e(TAG, "Remove Room Success");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Log.e(TAG, "Remove Room Failure: " + exception.toString());
                                            }
                                        });
                                    } catch(Exception ex) {
                                        Log.e(TAG, "Remove Exception: " + ex.toString());
                                    }
                                    return null;
                                }
                            }.execute();

                            Toast.makeText(ChatActivity.this, "Expire this room.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
//                            Room made still live. (Not Over 10 Minutes)
                            String key = databaseReference.child("messages").child(roomId).push().getKey();
                            EverChatMessage everChatMessage = new EverChatMessage(mUsername, mPhotoUrl, prefLanguage, FirebaseAuth.getInstance().getCurrentUser().getUid());
                            everChatMessage.setMessage(editText.getText().toString());
                            Map<String, Object> postValues = everChatMessage.toMap();

                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/messages/" + roomId + "/" + key, postValues);
                            childUpdates.put("/room_names/" + roomCountry + "/" + roomId + "/text", everChatMessage.getMessage());

                            databaseReference.updateChildren(childUpdates);
                        }

                        editText.setText("");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                break;
        }
    }

    private void visiableTranslation() {
        translateImageButton.setAlpha(1.0F);
    }
    private void invisiableTranslation() {
        translateImageButton.setAlpha(0.5F);
    }

    @Override
    public void onPermissionGranted() {
        Toast.makeText(ChatActivity.this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
        getImages();
    }

    @Override
    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        Toast.makeText(ChatActivity.this, getString(R.string.permission_denied) + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
    }
}