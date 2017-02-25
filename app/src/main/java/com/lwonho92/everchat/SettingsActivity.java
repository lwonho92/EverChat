package com.lwonho92.everchat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.lwonho92.everchat.data.EverChatProfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, PermissionListener {
    private static final String TAG = "SettingsActivity";
    private static final int INTENT_REQUEST_GET_IMAGES = 14;
    private Toolbar toolbar;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Uri photoUri;
    DatabaseReference databaseReference;

    private GoogleApiClient googleApiClient;

    ImageView pictureImageView;
    ImageButton pictureImageButton;
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
        pictureImageButton = (ImageButton) findViewById(R.id.btn_picture);
        countrySpinner = (Spinner) findViewById(R.id.sp_country);
        languageSpinner = (Spinner) findViewById(R.id.sp_language);
        profileEditText = (EditText) findViewById(R.id.et_info);

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        photoUri = null;
        databaseReference = FirebaseDatabase.getInstance().getReference("/auth").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    everChatProfile = (EverChatProfile) dataSnapshot.getValue(EverChatProfile.class);

                    Glide.with(SettingsActivity.this)
                            .load(everChatProfile.getPhotoUrl())
                            .into(pictureImageView);

                    String[] fullCountries = getResources().getStringArray(R.array.short_countries);
                    String[] fullLanguages = getResources().getStringArray(R.array.short_languages);

                    for(int i = 0; i < fullCountries.length; i++) {
                        if(fullCountries[i].equals(everChatProfile.getCountry())) {
                            countrySpinner.setSelection(i);
                            break;
                        }
                    }
                    for(int i = 0; i < fullLanguages.length; i++) {
                        if(fullLanguages[i].equals(everChatProfile.getLanguage())) {
                            languageSpinner.setSelection(i);
                            break;
                        }
                    }
                    profileEditText.setText(everChatProfile.getInfo());
                    countrySpinner.setEnabled(false);
                } else {
                    everChatProfile = new EverChatProfile();
                    Glide.with(SettingsActivity.this)
                            .load(firebaseUser.getPhotoUrl())
                            .into(pictureImageView);
                    countrySpinner.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        pictureImageButton.setOnClickListener(this);
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
                new AsyncTask<Uri, Void, Boolean>() {
                    @Override
                    protected void onPreExecute() {
                        everChatProfile.setUserName(firebaseUser.getDisplayName());
                        everChatProfile.setCountry(countrySpinner.getSelectedItem().toString());
                        everChatProfile.setLanguage(languageSpinner.getSelectedItem().toString());
                        everChatProfile.setInfo(profileEditText.getText().toString());
                        databaseReference.setValue(everChatProfile);
                    }

                    @Override
                    protected Boolean doInBackground(Uri... params) {
                        //do something
                        Uri uri = params[0];
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://everchat-6ce20.appspot.com");

                        if(uri != null) {
                            String fileName = uri.getLastPathSegment();
                            String extension = fileName.substring(fileName.lastIndexOf("."));
                            StorageReference saveRef = storageRef.child("profile").child(firebaseUser.getUid() + extension);

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
//                                    Save gs://<bucket>/profile/<uId>.<extension>
                                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        Log.e(TAG, "Upload Success: " + downloadUrl.toString());

                                        databaseReference.child("photoUrl").setValue(downloadUrl.toString());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                            } catch(Exception ex) {
                                Log.e(TAG, "Upload Exception: " + ex.toString());
                            }
                            return false;
                        }

                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean isPhotoChanged) {
                        if(isPhotoChanged) {
                            databaseReference.child("photoUrl").setValue(firebaseUser.getPhotoUrl().toString());
                        }
                    }
                }.execute(photoUri);

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.pref_country), countrySpinner.getSelectedItem().toString());
                editor.putString(getString(R.string.pref_language), languageSpinner.getSelectedItem().toString());
                editor.commit();

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

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch(viewId) {
            case R.id.btn_picture:
                new TedPermission(this)
                        .setPermissionListener(this)
                        .setDeniedMessage(getString(R.string.permission_deny_guide))
                        .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)
                        .check();
            break;
        }
    }

    private void getImages() {
        Config config = new Config();
        config.setToolbarTitleRes(R.string.custom_title);
        config.setSelectionMin(1);
        config.setSelectionLimit(1);

        ImagePickerActivity.setConfig(config);

        Intent intent  = new Intent(this, ImagePickerActivity.class);
        startActivityForResult(intent,INTENT_REQUEST_GET_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK ) {
            final ArrayList<Uri> image_uris = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            photoUri = image_uris.get(0);
            Glide.with(SettingsActivity.this)
                    .load(photoUri.toString())
                    .into(pictureImageView);
        }
    }

            @Override
    public void onPermissionGranted() {
        Toast.makeText(SettingsActivity.this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
        getImages();
    }

    @Override
    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        Toast.makeText(SettingsActivity.this, getString(R.string.permission_denied) + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
    }
}
