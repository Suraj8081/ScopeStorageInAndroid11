package com.example.scopestorage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.scopestorage.databinding.ActivityMainBinding;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyLifecycleObserver.ActivityResult, PickiTCallbacks {

    public final String TAG = MainActivity.this.getClass().toString();
    ActivityMainBinding binding;
    public static int ACTION_PERMISSION_REQUEST = 1007;
    public static int ACTION_CAMERA_PERMISSION_REQUEST = 1008;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    MyLifecycleObserver mObserver;
    PickiT pickiT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mObserver = new MyLifecycleObserver(getActivityResultRegistry(), this);
        getLifecycle().addObserver(mObserver);

        pickiT = new PickiT(this, this, this);

        binding.btnImage.setOnClickListener(view -> {
            if (getFileMangePermission()) {
                mObserver.selectImage();

            } else {
                Toast.makeText(this, "Please Provide Storage Permission", Toast.LENGTH_SHORT).show();
            }

        });

        binding.btnAudio.setOnClickListener(view -> {
            if (getFileMangePermission()) {
                mObserver.selectAudio();
            } else {
                Toast.makeText(this, "Please Provide Storage Permission", Toast.LENGTH_SHORT).show();
            }

        });

        binding.btnVideo.setOnClickListener(view -> {
            if (getFileMangePermission()) {
                mObserver.selectVideo();
            } else {
                Toast.makeText(this, "Please Provide Storage Permission", Toast.LENGTH_SHORT).show();
            }

        });

        binding.btnDoc.setOnClickListener(view -> {
            if (getFileMangePermission()) {
                mObserver.selectDoc();
            } else {
                Toast.makeText(this, "Please Provide Storage Permission", Toast.LENGTH_SHORT).show();
            }

        });

        binding.btnCamera.setOnClickListener(view -> {
            if (getFileMangePermission()) {
                if (getCameraPermission()) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(this, "Please Provide Camera Permission", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please Provide Storage Permission", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @Override
    public void onBackPressed() {
        pickiT.deleteTemporaryFile(this);
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations()) {
            pickiT.deleteTemporaryFile(this);
        }
    }


    private boolean getFileMangePermission() {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 30) {
            if (Environment.isExternalStorageManager()) {
                result = getStoragePermission();
                // If you don't have access, launch a new activity to show the user the system's dialog
                // to allow access to the external storage
            } else {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        } else {
            result = getStoragePermission();
        }
        return result;
    }

    private boolean getStoragePermission() {
        boolean result = false;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            result = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}
                    , ACTION_PERMISSION_REQUEST);
        }
        return result;
    }

    private boolean getCameraPermission() {
        boolean result = false;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            result = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}
                    , ACTION_CAMERA_PERMISSION_REQUEST);
        }
        return result;
    }

    @Override
    public void onActivityResult(Uri uri, String type) {
        try {
            Log.d(TAG, "onActivityResult: " + uri + " Type=" + type);
            pickiT.getPath(uri, Build.VERSION.SDK_INT);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void PickiTonUriReturned() {

    }

    @Override
    public void PickiTonStartListener() {

    }

    @Override
    public void PickiTonProgressUpdate(int progress) {

    }

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {
        Log.d(TAG, "path: " + path);
        if (!TextUtils.isEmpty(path))
            binding.tvPath.setText(path);
        else binding.tvPath.setText("Null");
    }

    @Override
    public void PickiTonMultipleCompleteListener(ArrayList<String> paths, boolean wasSuccessful, String Reason) {
        Log.d(TAG, "multipath: " + paths.get(0));
    }
}