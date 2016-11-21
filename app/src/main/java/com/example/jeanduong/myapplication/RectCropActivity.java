package com.example.jeanduong.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class RectCropActivity extends Activity {

    private static final String TAG = "Rect crop activity"; // For log output

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rect_crop);

        final ImageView snapshot_layer = (ImageView) findViewById(R.id.display_view);
        final RectDragView drag_layer = (RectDragView) findViewById(R.id.drag_view);

        // Load image from file
        final Bitmap bm = BitmapFactory.decodeFile(MainActivity.SNAPSHOT_FILE_NAME);

        // Set for display
        snapshot_layer.setImageBitmap(bm);

        (findViewById(R.id.reshoot_button)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent result_intent = new Intent();

                if (getParent() == null) {
                    setResult(Activity.RESULT_CANCELED, result_intent);
                }
                else {
                    getParent().setResult(Activity.RESULT_CANCELED, result_intent);
                }

                finish();
            }
        });

        (findViewById(R.id.crop_button)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Embedding rectangle for selected zone
                int x_1 = drag_layer.x_4, x_2 = drag_layer.x_2;
                int x_3 = drag_layer.x_2, x_4 = drag_layer.x_4;
                int y_1 = drag_layer.y_1, y_2 = drag_layer.y_1;
                int y_3 = drag_layer.y_3, y_4 = drag_layer.y_3;

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

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat simple_format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                Date present = cal.getTime();
                String str_date = simple_format.format(present);

                try {
                    FileOutputStream output_img = new FileOutputStream(MainActivity.ROOT_FILE_NAME + str_date + ".jpg");
                    targetBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output_img);
                    output_img.flush();
                    output_img.close();

                    File tmp = new File(MainActivity.SNAPSHOT_FILE_NAME);
                    tmp.deleteOnExit();

                    // Duplicate in ZOI image file
                    FileInputStream fis = new FileInputStream(new File(MainActivity.ROOT_FILE_NAME + str_date + ".jpg"));
                    FileOutputStream fos = new FileOutputStream(new File(MainActivity.ZOI_FILE_NAME));
                    FileChannel fic = fis.getChannel();
                    FileChannel foc = fos.getChannel();
                    fic.transferTo(0, fic.size(), foc);
                    fis.close();
                    fos.close();

                    Toast.makeText(RectCropActivity.this, "Image saved at " + str_date , Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Log.e(TAG, "Failed to save image");
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
