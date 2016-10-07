package com.example.jeanduong.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    Bitmap croped_image;
    String storage_location = Environment.getExternalStorageDirectory().toString();

    final static String LABEL_EXTRA_CAPTURED_IMAGE = "CAPTURED_IMAGE";
    final static String LABEL_EXTRA_CROPED_IMAGE = "CAPTURED_IMAGE";

    final static int SNAPSHOT_REQUEST_CODE = 1;
    final static int MUTILATION_REQUEST_CODE = 2;

    private static final String TAG = "Main activity"; // For log output

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*** Custom stuffs ***/

    static final int TAKE_PHOTO_REQUEST = 1;  // Request code

    // Called when the user clicks the help button
    // Display some (hopefully) useful instructions
    public void run_help(View view) {
        Intent itt = new Intent(this, HelpActivity.class);
        startActivity(itt);
    }

    // Photo activity
    // Called when the user clicks the photo capture button
    public void capture_photo(View view) {
        Intent itt_snap = new Intent(this, PhotoActivity.class);
        startActivityForResult(itt_snap, SNAPSHOT_REQUEST_CODE);
    }

    // Collect results from activities
    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data_intent)
    {
        super.onActivityResult(request_code, result_code, data_intent);

        if (request_code == SNAPSHOT_REQUEST_CODE)
        {
            if (data_intent != null)
            {

                if (data_intent.hasExtra(LABEL_EXTRA_CAPTURED_IMAGE))
                    Log.d(TAG, "Missing extra (array of bytes)");
                else
                    Log.d(TAG, "Extra available (array of bytes)");

/*
                Intent itt_crop = new Intent(this, CropActivity.class);
                itt_crop.putExtra(LABEL_EXTRA_CAPTURED_IMAGE, data_intent.getByteArrayExtra(LABEL_EXTRA_CAPTURED_IMAGE));
                startActivityForResult(itt_crop, MUTILATION_REQUEST_CODE);
*/


                Intent itt = new Intent(this, DisplayActivity.class);
                itt.putExtra(LABEL_EXTRA_CAPTURED_IMAGE, data_intent.getByteArrayExtra(LABEL_EXTRA_CAPTURED_IMAGE));
                startActivity(itt);
            }
        }
        else if (request_code == MUTILATION_REQUEST_CODE)
        {
            if (data_intent != null)
            {
                croped_image = (Bitmap) (data_intent.getExtras()).get(LABEL_EXTRA_CROPED_IMAGE);
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
