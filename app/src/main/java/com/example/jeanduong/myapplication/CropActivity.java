package com.example.jeanduong.myapplication;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class CropActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);


        ImageView snapshot_layer = (ImageView) findViewById(R.id.displayView);

        snapshot_layer.setImageBitmap((Bitmap) getIntent().getExtras().get(MainActivity.LABEL_EXTRA_CAPTURED_BYTES));

    }

}
