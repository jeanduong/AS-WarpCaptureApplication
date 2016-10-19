package com.example.jeanduong.myapplication;

import android.app.Activity;
import android.content.Intent;
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
        Bitmap bm = BitmapFactory.decodeFile(MainActivity.SNAPSHOT_FILE_NAME);

        // Perform rotation
        Matrix mt = new Matrix();
        mt.postRotate(90);

        final Bitmap rotated_bm = Bitmap.createBitmap(bm , 0, 0, bm.getWidth(), bm.getHeight(), mt, true);

        // Set for display
        snapshot_layer.setImageBitmap(rotated_bm);

        ((Button) findViewById(R.id.crop_button)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Embedding rectangle for selected zone
                int x_1 = drag_layer.x_1, x_2 = drag_layer.x_2;
                int x_3 = drag_layer.x_3, x_4 = drag_layer.x_4;
                int y_1 = drag_layer.y_1, y_2 = drag_layer.y_2;
                int y_3 = drag_layer.y_3, y_4 = drag_layer.y_4;

                double scale_x = rotated_bm.getWidth() / snapshot_layer.getWidth();
                double scale_y = rotated_bm.getHeight() / snapshot_layer.getHeight();

                Log.e(TAG, "Bitmap width  = " + rotated_bm.getWidth() + " | View width  = " + snapshot_layer.getWidth());
                Log.e(TAG, "Bitmap height = " + rotated_bm.getHeight() + " | View height = " + snapshot_layer.getHeight());

                int x_min = min(x_1, x_4);
                int x_max = max(x_2, x_3);
                int y_min = min(y_1, y_2);
                int y_max = max(y_3, y_4);

                double target_width = x_max - x_min + 1.0;
                double target_height = y_max - y_min + 1.0;

                Bitmap targetBitmap = Bitmap.createBitmap(rotated_bm, (int) (x_min * scale_x),
                        (int)(y_min * scale_y), (int)(target_width * scale_x), (int)(target_height * scale_y));

                try {
                    final File file = new File(MainActivity.ZOI_FILE_NAME);

                    OutputStream fOut = null;

                    // 100 means no compression, the lower you go, the stronger the compression
                    targetBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                }
                catch (Exception e) {
                    Log.d(TAG, "Failed to save image");
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
