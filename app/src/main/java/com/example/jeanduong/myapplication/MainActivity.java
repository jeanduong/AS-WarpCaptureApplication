package com.example.jeanduong.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {

    Bitmap croped_image;
    String storage_location = Environment.getExternalStorageDirectory().toString();

    public enum Crop_mode{QUAD, RECT}

    final static String LABEL_EXTRA_CAPTURED_BYTES = "CAPTURED_BYTES";
    //final static String LABEL_EXTRA_CROPED_IMAGE = "CROPED_IMAGE";

    final static int SNAPSHOT_REQUEST_CODE = 11;
    final static int CHOOSE_CROP_REQUEST_CODE = 13;
    final static int RECT_CROP_REQUEST_CODE = 17;
    final static int QUAD_CROP_REQUEST_CODE = 19;

    private static final String TAG = "Main activity"; // For log output

    final static String SNAPSHOT_FILE_NAME = Environment.getExternalStorageDirectory() + File.separator + "snapshot.jpg";
    final static String ZOI_FILE_NAME = Environment.getExternalStorageDirectory() + File.separator + "zoi.jpg";
    final static String ROOT_FILE_NAME = Environment.getExternalStorageDirectory() + File.separator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

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

    // Collect results from activities
    /*
    // Backup
    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data_intent)
    {
        super.onActivityResult(request_code, result_code, data_intent);

        if (request_code == SNAPSHOT_REQUEST_CODE)
        {
            if (result_code == Activity.RESULT_OK)
            {
                Intent itt_crop = new Intent(this, CropActivity.class);
                itt_crop.putExtra(LABEL_EXTRA_CAPTURED_BYTES, data_intent.getByteArrayExtra(LABEL_EXTRA_CAPTURED_BYTES));

                if (itt_crop.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(itt_crop, MUTILATION_REQUEST_CODE);
            }
            else
            {
                Toast.makeText(this, "Photo activity failed", Toast.LENGTH_LONG).show();
                Log.e(TAG, "****** Photo activity failed");
            }
        }
        else if (request_code == MUTILATION_REQUEST_CODE) {
            if (result_code == Activity.RESULT_OK) {
                Log.e(TAG, "****** Crop done");

                Intent itt_display_zoi = new Intent(this, DisplayActivity.class);

                if (itt_display_zoi.resolveActivity(getPackageManager()) != null)
                    startActivity(itt_display_zoi);
            }
        }
    }
    */
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
            /*
            else if (cm == Crop_mode.RECT) {
                Intent itt_crop = new Intent(this, RectCropActivity.class);

                if (itt_crop.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(itt_crop, RECT_CROP_REQUEST_CODE);
            }
            */
        }
        else if (request_code == RECT_CROP_REQUEST_CODE ||
                request_code == QUAD_CROP_REQUEST_CODE) {
            if (result_code == Activity.RESULT_OK) {
                Log.e(TAG, "****** Crop done");

                Intent itt_display_zoi = new Intent(this, DisplayActivity.class);

                if (itt_display_zoi.resolveActivity(getPackageManager()) != null)
                    startActivity(itt_display_zoi);
            }
        }
    }



    // Called when the user clicks the quit button
    // Close current activity. This should close the
    // application since it is the main activity.
    public void suicide(View view) {
        finish();
    }
}
