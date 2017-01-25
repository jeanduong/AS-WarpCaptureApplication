package com.example.jeanduong.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class CropChooserActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_chooser);

        final Bitmap bm = BitmapFactory.decodeFile(MainActivity.SNAPSHOT_IMAGE_FILE_NAME);
        final ImageView snapshot_layer = (ImageView) findViewById(R.id.display_view);

        snapshot_layer.setImageBitmap(bm);

        (findViewById(R.id.rect_crop_button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent result_intent = new Intent();

                result_intent.putExtra("crop_mode", MainActivity.Crop_mode.RECT);

                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, result_intent);
                }
                else {
                    getParent().setResult(Activity.RESULT_OK, result_intent);
                }

                finish();
            }
        });

        (findViewById(R.id.quad_crop_button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent result_intent = new Intent();

                result_intent.putExtra("crop_mode", MainActivity.Crop_mode.QUAD);

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
