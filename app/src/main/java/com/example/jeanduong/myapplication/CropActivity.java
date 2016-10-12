package com.example.jeanduong.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class CropActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        ImageView snapshot_layer = (ImageView) findViewById(R.id.display_view);
        DragView drag_layer = (DragView) findViewById(R.id.drag_view);

        snapshot_layer.setImageBitmap(BitmapFactory.decodeFile(MainActivity.SNAPSHOT_FILE_NAME));

    }

}
