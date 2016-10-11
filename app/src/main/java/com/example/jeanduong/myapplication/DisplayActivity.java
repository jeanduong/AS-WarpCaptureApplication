package com.example.jeanduong.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class DisplayActivity extends AppCompatActivity {

    private static final String TAG = "Display activity"; // For log output

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        if (this.getIntent().hasExtra(MainActivity.LABEL_EXTRA_CAPTURED_IMAGE))
        {
            Log.d(TAG, "Array of bytes found");
            Log.d(TAG, "Data length = " + this.getIntent().getByteArrayExtra(MainActivity.LABEL_EXTRA_CAPTURED_IMAGE).length);
        }
        else
        {
            Log.d(TAG, "No array of bytes to build image");
        }

        //byte[] data = this.getIntent().getByteArrayExtra(MainActivity.LABEL_EXTRA_CAPTURED_IMAGE);
        //Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
        //ImageView iv = (ImageView) findViewById(R.id.vizualizer);

        //iv.setImageBitmap(bm);

        ((Button) findViewById(R.id.close_display_button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
