package com.example.jeanduong.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

        iv.setBackgroundColor(Color.BLACK);
        iv.setImageBitmap(bm);
        
        (findViewById(R.id.close_display_button)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }
}
