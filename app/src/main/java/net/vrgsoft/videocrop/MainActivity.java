package net.vrgsoft.videocrop;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import net.vrgsoft.videcrop.VideoCropActivity;

import java.io.File;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_PERMISSIONS = 1;
    private static final int CROP_REQUEST = 200;
    private static final int PICK_VIDEO_REQUEST = 1;
    private AppCompatButton pickVideoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pickVideoButton = findViewById(R.id.pickVideo);
        pickVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {

                    pickVideo();
                }
            }
        });
    }

    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    private boolean checkPermissions() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
                return false;
            }
        }
        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickVideo();
            } else {
                Log.e(TAG, "Permissions not granted.");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CROP_REQUEST && resultCode == RESULT_OK){
            //crop successful
        } else  if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedVideoUri = data.getData();
            String inputPath = UriUtils.getPathFromUri(this, selectedVideoUri);
            String videoFilePath = getVideoFilePath(this);
            if (videoFilePath != null) {
                Log.d("MainActivity", "Video file path: " + videoFilePath);
                startActivityForResult(VideoCropActivity.createIntent(this, inputPath, videoFilePath), CROP_REQUEST);
            } else {
                Log.e("MainActivity", "Failed to get video file path");
            }
        }
    }
    public static String getVideoFilePath(Context context) {

        File dirPathFile = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString() + "/Meeturfriends/Video"
        );

        System.out.println("dirPathFile.exists() before mkdirs(): " + dirPathFile.exists());
        boolean dirsCreated = dirPathFile.mkdirs();
        System.out.println("dirPathFile.mkdirs() : " + dirsCreated);
        System.out.println("dirPathFile.exists() after mkdirs(): " + dirPathFile.exists());

        // Create the storage directory if it does not exist
        if (!dirPathFile.exists()) {
            if (!dirPathFile.mkdirs()) {
                // Handle the error case here
                System.out.println("Failed to create directories");
            }
        }
        File savedFile = new File(dirPathFile, "Edited_video_" + System.currentTimeMillis() + ".mp4");
        System.out.println("Saved file path: " + savedFile.getAbsolutePath());
        return savedFile.getPath();
    }
}
