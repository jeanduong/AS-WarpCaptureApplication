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

    final static String LABEL_EXTRA_CAPTURED_BYTES = "CAPTURED_BYTES";
    final static String LABEL_EXTRA_CROPED_IMAGE = "CROPED_IMAGE";

    final static int SNAPSHOT_REQUEST_CODE = 11;
    final static int MUTILATION_REQUEST_CODE = 13;
    final static int DISPLAY_REQUEST_CODE = 17;

    private static final String TAG = "Main activity"; // For log output

    final static String SNAPSHOT_FILE_NAME = Environment.getExternalStorageDirectory() + File.separator + "snapshot.jpg";
    final static String ZOI_FILE_NAME = Environment.getExternalStorageDirectory() + File.separator + "zoi.jpg";

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
    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data_intent)
    {
        super.onActivityResult(request_code, result_code, data_intent);

        if (request_code == SNAPSHOT_REQUEST_CODE)
        {
            Log.d(TAG, "****** Result code   : " + result_code);
            Log.d(TAG, "****** Expected code : " + Activity.RESULT_OK);

            if (result_code == Activity.RESULT_OK)
            {
                if (data_intent.getExtras() != null)
                {
                    if (data_intent.hasExtra(LABEL_EXTRA_CAPTURED_BYTES))
                    {
                        Log.d(TAG, "****** Array of bytes available in extra");

                        if (data_intent.getByteArrayExtra(LABEL_EXTRA_CAPTURED_BYTES) != null)
                        {
                            Log.d(TAG, "****** Array is not null");
                        }
                        else
                        {
                            Log.d(TAG, "****** Array is null");
                        }

                    Intent itt_crop = new Intent(this, CropActivity.class);
                    itt_crop.putExtra(LABEL_EXTRA_CAPTURED_BYTES, data_intent.getByteArrayExtra(LABEL_EXTRA_CAPTURED_BYTES));

                     if (itt_crop.resolveActivity(getPackageManager()) != null)
                        startActivityForResult(itt_crop, MUTILATION_REQUEST_CODE);
                     else
                         Toast.makeText(this, "Error: No image recorded", Toast.LENGTH_LONG).show();

/*
                        Intent itt = new Intent(this, DisplayActivity.class);
                        itt.putExtra(LABEL_EXTRA_CAPTURED_BYTES, data_intent.getByteArrayExtra(LABEL_EXTRA_CAPTURED_BYTES));

                        // Verify the intent will resolve to at least one activity
                        if (itt.resolveActivity(getPackageManager()) != null)
                            startActivity(itt);
                        else
                            Toast.makeText(this, "Cannot display!", Toast.LENGTH_LONG).show();
*/
                    } else
                    {
                        Toast.makeText(this, "Missing array of bytes in extra", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "****** Missing array of bytes in extra");
                    }
                }
                else
                {
                    Toast.makeText(this, "Void result bundle", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "****** Void result bundle");
                }
            }
            else
            {
                Toast.makeText(this, "Photo activity failed", Toast.LENGTH_LONG).show();
                Log.e(TAG, "****** Photo activity failed");
            }
        }
        else if (request_code == MUTILATION_REQUEST_CODE)
        {
            Log.e(TAG, "****** Crop detected");

            Intent itt_display_zoi = new Intent(this, DisplayActivity.class);

            //if (itt_display_zoi.resolveActivity(getPackageManager()) != null)
            //    startActivity(itt_display_zoi);
        }
    }

    // Called when the user clicks the quit button
    // Close current activity. This should close the
    // application since it is the main activity.
    public void suicide(View view) {
        finish();
    }
}
