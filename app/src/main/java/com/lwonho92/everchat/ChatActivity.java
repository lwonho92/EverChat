package com.lwonho92.everchat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.lwonho92.everchat.adapters.ChatAdapter;
import com.lwonho92.everchat.data.EverChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener, PermissionListener {
    private static final String TAG = "ChatActivity";
    public static final String COUNTRY_ID = "country_id";
    public static final String ROOM_ID = "room_id";
    private static final String ANONYMOUS = "anonymous";

    private Toolbar toolbar;

    private Button sendButton, translateButton;
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

        mUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        mPhotoUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();

        translateButton = (Button) findViewById(R.id.translate_button);
        sendButton = (Button) findViewById(R.id.send_button);
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

        translateButton.setOnClickListener(this);
        boolean isOnTranslate = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_translate), getResources().getBoolean(R.bool.pref_default_translate));
        if(isOnTranslate) {
            visiableTranslation();
        } else {
            invisiableTranslation();
        }
        sendButton.setOnClickListener(this);
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
            case R.id.action_tedpicker:

                new TedPermission(this)
                        .setPermissionListener(this)
                        .setDeniedMessage(getString(R.string.permission_deny_guide))
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .check();
                return true;
        }

//        (TEST) Using Apache Commons-io for loading image from http url, then file(cache) upload to Firebase storage.
        /*FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://everchat-6ce20.appspot.com");
        final StorageReference mountainsRef = storageRef.child("mountains.png");

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL url = new URL("https://www.google.co.kr/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
                    String tDir = System.getProperty("java.io.tmpdir");
                    Log.e(TAG, tDir.toString());
                    String path = tDir + "tmp" + ".png";
                    File file = new File(path);
                    file.deleteOnExit();
//                    FileUtils.copyURLToFile(url, file);

                    InputStream stream = new FileInputStream(file);
                    UploadTask uploadTask = mountainsRef.putStream(stream);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Log.e(TAG, "Upload Failed");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.e(TAG, "Upload Success: " + downloadUrl.toString());
                        }
                    });
                } catch(Exception ex) {
                    Log.e(TAG, "Upload Exception: " + ex.toString());
                }
                return null;
            }
        };*/

        return super.onOptionsItemSelected(item);
    }

    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private void getImages() {
        Config config = new Config();
        config.setToolbarTitleRes(R.string.custom_title);
        config.setSelectionMin(0);
        config.setSelectionLimit(4);

        ImagePickerActivity.setConfig(config);

        Intent intent  = new Intent(this, ImagePickerActivity.class);
        startActivityForResult(intent,INTENT_REQUEST_GET_IMAGES);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK ) {

            ArrayList<Uri> image_uris = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            String str = "";

            //do something
            for(Uri uri : image_uris) {
                Log.e(TAG, uri.toString());
            }
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
        updateMenuTitles();

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
            case R.id.translate_button:
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
            case R.id.send_button:
                String key = databaseReference.child("messages").child(roomId).push().getKey();
                EverChatMessage everChatMessage = new EverChatMessage(mUsername, mPhotoUrl, editText.getText().toString(), prefLanguage, FirebaseAuth.getInstance().getCurrentUser().getUid());
                Map<String, Object> postValues = everChatMessage.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/messages/" + roomId + "/" + key, postValues);
                childUpdates.put("/room_names/" + roomCountry + "/" + roomId + "/text", everChatMessage.getMessage());

                databaseReference.updateChildren(childUpdates);
//                databaseReference.child("messages").child(roomId).child(key).setValue(everChatMessage);

                editText.setText("");
                break;
        }
    }

    private void visiableTranslation() {
        translateButton.setAlpha(1.0F);
    }
    private void invisiableTranslation() {
        translateButton.setAlpha(0.5F);
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