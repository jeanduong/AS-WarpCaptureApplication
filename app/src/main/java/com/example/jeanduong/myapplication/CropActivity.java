package com.example.jeanduong.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.graphics.Matrix;

public class CropActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        ImageView snapshot_layer = (ImageView) findViewById(R.id.display_view);
        DragView drag_layer = (DragView) findViewById(R.id.drag_view);

        //snapshot_layer.setImageBitmap(BitmapFactory.decodeFile(MainActivity.SNAPSHOT_FILE_NAME));

        Bitmap bm = BitmapFactory.decodeFile(MainActivity.SNAPSHOT_FILE_NAME);

        Matrix mt = new Matrix();
        mt.postRotate(90);

        bm = Bitmap.createBitmap(bm , 0, 0, bm.getWidth(), bm.getHeight(), mt, true);

        snapshot_layer.setImageBitmap(bm);
    }

}
