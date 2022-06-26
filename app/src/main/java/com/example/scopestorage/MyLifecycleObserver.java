package com.example.scopestorage;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

class MyLifecycleObserver implements DefaultLifecycleObserver {
    private final ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<String> mGetContent;
    private ActivityResult activityResult;
    private String type = Constant.DEFAULT;
    private Context context;

    public final String TAG = MyLifecycleObserver.this.getClass().toString();

    MyLifecycleObserver(@NonNull ActivityResultRegistry registry, Context context) {
        mRegistry = registry;
        this.context = context;
        activityResult = (ActivityResult) context;
    }

    public void onCreate(@NonNull LifecycleOwner owner) {


        mGetContent = mRegistry.register("key", owner, new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        // Handle the returned Uri
                        //Log.d(TAG, "onActivityResult: " + uri);
                        activityResult.onActivityResult(uri, type);

                    }
                });
    }

    public void selectImage() {
        // Open the activity to select an image
        type = Constant.IMAGE;
        mGetContent.launch("image/*");
    }

    public void selectDoc() {
        type = Constant.DOC;
        mGetContent.launch("application/pdf");
    }

    public void selectVideo() {
        type = Constant.VIDEO;
        mGetContent.launch("video/*");
    }

    public void selectAudio() {
        type = Constant.AUDIO;
        mGetContent.launch("audio/*");
    }

    public interface ActivityResult {
        void onActivityResult(Uri uri, String type);
    }

}