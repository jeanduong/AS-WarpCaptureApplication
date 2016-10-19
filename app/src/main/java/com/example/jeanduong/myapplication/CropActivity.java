package com.example.jeanduong.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class CropActivity extends Activity {

    private static final String TAG = "Crop activity"; // For log output

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        final ImageView snapshot_layer = (ImageView) findViewById(R.id.display_view);
        final DragView drag_layer = (DragView) findViewById(R.id.drag_view);

        // Load image from file
        final Bitmap bm = BitmapFactory.decodeFile(MainActivity.SNAPSHOT_FILE_NAME);

        // Set for display
        snapshot_layer.setImageBitmap(bm);

        ((Button) findViewById(R.id.crop_button)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Embedding rectangle for selected zone
                int x_1 = drag_layer.x_1, x_2 = drag_layer.x_2;
                int x_3 = drag_layer.x_3, x_4 = drag_layer.x_4;
                int y_1 = drag_layer.y_1, y_2 = drag_layer.y_2;
                int y_3 = drag_layer.y_3, y_4 = drag_layer.y_4;

                float aspectRatioH = (float)drag_layer.getHeight() / (float)drag_layer.getWidth();
                //
                float heighDiff = bm.getHeight() - bm.getWidth() * aspectRatioH;
                //Convert pixel to percent
                float left = min(x_1, x_4) / (float)drag_layer.getWidth();
                float top = min(y_1, y_2) / (float)drag_layer.getHeight();
                float right = max(x_2, x_3) / (float)drag_layer.getWidth();
                float bottom = max(y_3, y_4) / (float)drag_layer.getHeight();

                int x = (int)(left * bm.getWidth());
                int width = (int)((right - left) * bm.getWidth());
                int y = (int)(heighDiff / 2f) + (int)(top * bm.getWidth() * aspectRatioH);
                int height = (int)((bottom - top) * bm.getWidth() * aspectRatioH);

                Bitmap targetBitmap =  Bitmap.createBitmap(bm, x, y, width, height);

                int new_x_1 = (int)(x_1 / (float)drag_layer.getWidth() * bm.getWidth()) - x;
                new_x_1 = max(0, new_x_1);
                int new_x_2 = (int)(x_2 / (float)drag_layer.getWidth() * bm.getWidth()) - x;
                new_x_2 = min(new_x_2, width - 1);
                int new_x_3 = (int)(x_3 / (float)drag_layer.getWidth() * bm.getWidth()) - x;
                new_x_3 = min(new_x_3, width - 1);
                int new_x_4 = (int)(x_4 / (float)drag_layer.getWidth() * bm.getWidth()) - x;
                new_x_4 = max(0, new_x_4);

                int new_y_1 = (int)(y_1 / (float)drag_layer.getHeight() * bm.getWidth() * aspectRatioH) - (int)(top * bm.getWidth() * aspectRatioH);
                new_y_1 = max(0, new_y_1);
                int new_y_2 = (int)(y_2 / (float)drag_layer.getHeight() * bm.getWidth() * aspectRatioH) - (int)(top * bm.getWidth() * aspectRatioH);
                new_y_2 = max(0, new_y_2);
                int new_y_3 = (int)(y_3 / (float)drag_layer.getHeight() * bm.getWidth() * aspectRatioH) - (int)(top * bm.getWidth() * aspectRatioH);
                new_y_3 = min(new_y_3, height - 1);
                int new_y_4 = (int)(y_4 / (float)drag_layer.getHeight() * bm.getWidth() * aspectRatioH) - (int)(top * bm.getWidth() * aspectRatioH);
                new_y_4 = min(new_y_4, height - 1);






                try {
                    FileOutputStream output = new FileOutputStream(MainActivity.ZOI_FILE_NAME);
                    targetBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                    output.flush();
                    output.close();
                }
                catch (Exception e) {
                    Log.d(TAG, "Failed to save image");
                } finally {
                    File tmp = new File(MainActivity.ZOI_FILE_NAME);
                    if (tmp.exists()) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" + MainActivity.ZOI_FILE_NAME), "image/*");
                        startActivity(intent);
                    }
                }

                Intent result_intent = new Intent();

                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, result_intent);
                }
                else {
                    getParent().setResult(Activity.RESULT_OK, result_intent);
                }

                finish();
            }
        });



    }



}
