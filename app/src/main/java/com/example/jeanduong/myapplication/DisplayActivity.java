package com.example.jeanduong.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class DisplayActivity extends Activity {

    private static final String TAG = "Display activity"; // For log output

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        ImageView iv = (ImageView) findViewById(R.id.vizualizer);

        Bitmap bm = BitmapFactory.decodeFile(MainActivity.ZOI_FILE_NAME);

        iv.setImageBitmap(bm);

/*
        if (this.getIntent().hasExtra(MainActivity.LABEL_EXTRA_CAPTURED_BYTES))
        {
            Log.d(TAG, "Array of bytes found");
            Log.d(TAG, "Data length = " + this.getIntent().getByteArrayExtra(MainActivity.LABEL_EXTRA_CAPTURED_BYTES).length);

            byte[] data = this.getIntent().getByteArrayExtra(MainActivity.LABEL_EXTRA_CAPTURED_BYTES);
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);

            iv.setImageBitmap(bm);
        }
        else
        {
            Log.e(TAG, "No array of bytes to build image");
        }
*/
        ((Button) findViewById(R.id.close_display_button)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });




    }
}
