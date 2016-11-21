package com.example.jeanduong.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class QuadCropActivity extends Activity {

    private static final String TAG = "Quad crop activity"; // For log output

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quad_crop);

        final ImageView snapshot_layer = (ImageView) findViewById(R.id.display_view);
        final QuadDragView drag_layer = (QuadDragView) findViewById(R.id.drag_view);

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

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat simple_format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                Date present = cal.getTime();
                String str_date = simple_format.format(present);

                // Compute perspective transform, stretch quadrilateral
                // portion of image to rectangular one before saving it

                try {
                    // Estimate perspective transformation between drag point
                    // quadrilateral and their enclosing rectangle

                    Mat src_points = new Mat(4, 1, CvType.CV_32FC2);
                    Mat dst_points = new Mat(4, 1, CvType.CV_32FC2);

                    x_1 = (int) ((x_1 / (float)drag_layer.getWidth()) * bm.getWidth());
                    x_2 = (int) ((x_2 / (float)drag_layer.getWidth()) * bm.getWidth());
                    x_3 = (int) ((x_3 / (float)drag_layer.getWidth()) * bm.getWidth());
                    x_4 = (int) ((x_4 / (float)drag_layer.getWidth()) * bm.getWidth());

                    y_1 = (int)(heighDiff / 2f) + (int)((y_1 / (float)drag_layer.getHeight()) * bm.getWidth() * aspectRatioH);
                    y_2 = (int)(heighDiff / 2f) + (int)((y_2 / (float)drag_layer.getHeight()) * bm.getWidth() * aspectRatioH);
                    y_3 = (int)(heighDiff / 2f) + (int)((y_3 / (float)drag_layer.getHeight()) * bm.getWidth() * aspectRatioH);
                    y_4 = (int)(heighDiff / 2f) + (int)((y_4 / (float)drag_layer.getHeight()) * bm.getWidth() * aspectRatioH);

                    src_points.put(0, 0, x_1, y_1, x_2, y_2, x_3, y_3, x_4, y_4);
                    dst_points.put(0, 0, x, y, x + width - 1, y, x + width - 1, y + height - 1, x, y + height - 1);

                    Mat persp = Imgproc.getPerspectiveTransform(src_points, dst_points);

                    // Load portion of image bounded by drag points enclosing rectangle

                    Bitmap src_img = Bitmap.createBitmap(bm, x, y, width, height);

                    // Apply perspective transform using matrices

                    Mat src_mat = new Mat(width, height, CvType.CV_8U, new Scalar(4));
                    Mat dst_mat = new Mat(width, height, CvType.CV_8U, new Scalar(4));

                    Utils.bitmapToMat(src_img, src_mat); // Put ZOI into matrix for processing

                    Imgproc.warpPerspective(src_mat, dst_mat, persp, new Size(width, height));

                    // Retrieve bitmap from resulting matrix

                    Bitmap dst_img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(dst_mat, dst_img);

                    FileOutputStream output_stream = new FileOutputStream(MainActivity.ZOI_FILE_NAME);
                    dst_img.compress(Bitmap.CompressFormat.JPEG, 100, output_stream);
                    output_stream.flush();
                    output_stream.close();

                    Toast.makeText(QuadCropActivity.this, "Image saved at " + str_date , Toast.LENGTH_SHORT).show();
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

    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
