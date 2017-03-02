package com.lwonho92.everchat;

import android.app.Activity;
import android.content.Context;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.lwonho92.everchat.data.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, PermissionListener {
    private static final String TAG = "SettingsActivity";
    private static final int INTENT_REQUEST_GET_IMAGES = 14;
    private Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Uri photoUri;
    private DatabaseReference databaseReference;

    private EverChatProfile everChatProfile;

    @BindView(R.id.im_settings_photo) ImageView pictureImageView;
    @OnClick(R.id.bt_settings_picture)
    public void onClick() {
        new TedPermission(this)
                .setPermissionListener(this)
                .setDeniedMessage(getString(R.string.permission_deny_guide))
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)
                .check();
    }
    @BindView(R.id.sp_settings_country) Spinner countrySpinner;
    @BindView(R.id.sp_settings_language) Spinner languageSpinner;
    @BindView(R.id.et_settings_info) EditText profileEditText;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        Utils.setCalligraphyConfig(this);

        toolbar = (Toolbar) findViewById(R.id.tb_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        countrySpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_style, getResources().getStringArray(R.array.short_countries)) {
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.RIGHT);
                ((TextView) v).setGravity(Gravity.END);
                ((TextView) v).setTextSize(24);

                return v;
            }
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView,parent);
                ((TextView) v).setGravity(Gravity.RIGHT);
                ((TextView) v).setGravity(Gravity.END);
                ((TextView) v).setTextSize(24);

                return v;
            }
        });
        languageSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_style, getResources().getStringArray(R.array.short_languages)) {
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.RIGHT);
                ((TextView) v).setGravity(Gravity.END);
                ((TextView) v).setTextSize(24);

                return v;
            }
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView,parent);
                ((TextView) v).setGravity(Gravity.RIGHT);
                ((TextView) v).setGravity(Gravity.END);
                ((TextView) v).setTextSize(24);

                return v;
            }
        });

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
                        everChatProfile.setEmail(firebaseAuth.getCurrentUser().getEmail());
                        everChatProfile.setInfo(profileEditText.getText().toString());
                        databaseReference.setValue(everChatProfile);
                    }

                    @Override
                    protected Boolean doInBackground(Uri... params) {
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

                                        if(downloadUrl != null)
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
                        if(isPhotoChanged && firebaseUser.getPhotoUrl() != null) {
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

    private void getImages() {
        Config config = new Config();
        config.setToolbarTitleRes(R.string.custom_title);
        config.setSelectionMin(1);
        config.setSelectionLimit(1);

        ImagePickerActivity.setConfig(config);

        Intent intent  = new Intent(this, ImagePickerActivity.class);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK ) {
            switch(requestCode) {
                case INTENT_REQUEST_GET_IMAGES:
                    final ArrayList<Uri> image_uris = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
                    photoUri = image_uris.get(0);
                    Glide.with(SettingsActivity.this)
                            .load(photoUri.toString())
                            .into(pictureImageView);
                    break;
            }
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
