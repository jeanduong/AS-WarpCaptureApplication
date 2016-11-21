package com.example.jeanduong.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

    private static final String TAG = "Main activity"; // For log output

    final static String ROOT_FILE_NAME = Environment.getExternalStorageDirectory() + File.separator;
    final static String ZOI_FILE_NAME = Environment.getExternalStorageDirectory() + File.separator + "zoi.jpg";
    final static String SNAPSHOT_FILE_NAME = Environment.getExternalStorageDirectory() + File.separator + "snapshot.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                Log.e(TAG, "****** Photo activity failed");
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
                Log.e(TAG, "****** Crop done");

                Intent itt_display_crop = new Intent(this, DisplayCropActivity.class);

                startActivity(itt_display_crop);
            }
        }
    }
}
