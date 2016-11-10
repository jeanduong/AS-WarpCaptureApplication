package com.example.jeanduong.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class CropChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_chooser);

        final Bitmap bm = BitmapFactory.decodeFile(MainActivity.SNAPSHOT_FILE_NAME);
        final ImageView snapshot_layer = (ImageView) findViewById(R.id.display_view);

        snapshot_layer.setImageBitmap(bm);
    }
}
