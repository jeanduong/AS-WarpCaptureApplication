package com.example.jeanduong.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {

    public enum Crop_mode{QUAD, RECT}

    final static String LABEL_EXTRA_CAPTURED_BYTES = "CAPTURED_BYTES";

    final static int SNAPSHOT_REQUEST_CODE = 11;
    final static int CHOOSE_CROP_REQUEST_CODE = 13;
    final static int RECT_CROP_REQUEST_CODE = 17;
    final static int QUAD_CROP_REQUEST_CODE = 19;
    final static int TEXT_ZOI_SELECTION_REQUEST_CODE = 23;

    private static final String TAG = "Main activity"; // For log output

    final static String ROOT_FILE_NAME = Environment.getExternalStorageDirectory() + File.separator;

    final static String SNAPSHOT_IMAGE_FILE_NAME = ROOT_FILE_NAME + "snapshot.jpg";
    static String CURRENT_IMAGE_BASE_NAME = new String();
    static String CURRENT_IMAGE_FULL_NAME = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 42);
        }
    }

    ///////////////
    // Do stuffs //
    ///////////////

    // Called when the user clicks the help button
    // Display some (hopefully) useful instructions
    public void run_help(View view) {
        Intent itt_help = new Intent(this, HelpActivity.class);

        // Verify the intent will resolve to at least one activity
        if (itt_help.resolveActivity(getPackageManager()) != null)
            startActivity(itt_help);
    }

    // Photo activity
    // Called when the user clicks the photo capture button
    public void capture_photo(View view) {
        Intent itt_snap = new Intent(this, PhotoActivity.class);

        // Verify the intent will resolve to at least one activity
        if (itt_snap.resolveActivity(getPackageManager()) != null)
            startActivityForResult(itt_snap, SNAPSHOT_REQUEST_CODE);
    }

    // Called when the user clicks the quit button. This closes current activity.
    // This should close the application since it is the main activity.
    public void suicide(View view) {
        finish();
    }

    /////////////////////////////////////
    // Collect results from activities //
    /////////////////////////////////////

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data_intent)
    {
        super.onActivityResult(request_code, result_code, data_intent);

        if (request_code == SNAPSHOT_REQUEST_CODE)
        {
            if (result_code == Activity.RESULT_OK)
            {
                Intent itt_choose_crop = new Intent(this, CropChooserActivity.class);

                if (itt_choose_crop.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(itt_choose_crop, CHOOSE_CROP_REQUEST_CODE);
            }
            else
            {
                Toast.makeText(this, "Photo activity failed", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Photo activity failed");
            }
        }
        else if (request_code == CHOOSE_CROP_REQUEST_CODE)
        {
            Object cm = data_intent.getExtras().get("crop_mode");

            if (cm == Crop_mode.QUAD) {
                Intent itt_crop = new Intent(this, QuadCropActivity.class);

                if (itt_crop.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(itt_crop, QUAD_CROP_REQUEST_CODE);
            }

            else if (cm == Crop_mode.RECT) {
                Intent itt_crop = new Intent(this, RectCropActivity.class);

                if (itt_crop.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(itt_crop, RECT_CROP_REQUEST_CODE);
            }
        }
        else if (request_code == RECT_CROP_REQUEST_CODE ||
                request_code == QUAD_CROP_REQUEST_CODE) {
            if (result_code == Activity.RESULT_OK) {
                Log.d(TAG, "Crop done");

                Intent itt_select_text_zois = new Intent(this, SelectArea.class);

                if (itt_select_text_zois.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(itt_select_text_zois, TEXT_ZOI_SELECTION_REQUEST_CODE);
            }
        }
    }
}
