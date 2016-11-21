package com.example.jeanduong.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class DisplayCropActivity extends Activity {
    private static final String TAG = "Display"; // For log output

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_crop);

        // Pick image from file and display in view

        ImageView v = (ImageView) findViewById(R.id.visualizer);

        v.setBackgroundColor(Color.WHITE);
        v.setImageBitmap(BitmapFactory.decodeFile(MainActivity.ZOI_FILE_NAME));

        // Button simply closes the activity
        (findViewById(R.id.close_crop_display_button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }
}
