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

                // Compute perspective transform, stretch quadrilateral
                // portion of image to rectangular one before saving it

                // Update drag point coordinates
                x_1 = new_x_1; x_2 = new_x_2; x_3 = new_x_3; x_4 = new_x_4;
                y_1 = new_y_1; y_2 = new_y_2; y_3 = new_y_3; y_4 = new_y_4;
                int l = min(x_1, x_4);
                int r = max(x_2, x_3);
                int t = min(y_1, y_2);
                int b = max(y_3, y_4);
                int h = b - t + 1;
                int w = r - l + 1;

                try {
                    // Estimate perspective transformation between drag point
                    // quadrilateral and their enclosing rectangle
                    Mat src_points = new Mat(4, 1, CvType.CV_32FC2);
                    Mat dst_points = new Mat(4, 1, CvType.CV_32FC2);

                    src_points.put(0, 0, x_1, y_1, x_2, y_2, x_3, y_3, x_4, y_4);
                    dst_points.put(0, 0, l, t, r, t, r, b, l, b);

                    Mat persp = Imgproc.getPerspectiveTransform(src_points, dst_points);

                    // Load portion of image bounded by drag points bounding enclosing rectangle
                    InputStream str = new FileInputStream(MainActivity.SNAPSHOT_FILE_NAME);
                    BitmapRegionDecoder dcd = BitmapRegionDecoder.newInstance(str, false);
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    Rect rct = new Rect(l, t, r, b);

                    opt.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    Bitmap src_img = dcd.decodeRegion(rct, opt);

                    Mat src_mat = new Mat(h, w, CvType.CV_8U, new Scalar(4));
                    Mat dst_mat = new Mat(h, w, CvType.CV_8U, new Scalar(4));

                    Utils.bitmapToMat(src_img, src_mat);

                    // Apply perspective transform using matrices

                    Imgproc.warpPerspective(src_mat, dst_mat, persp, new Size(h, w));

                    // Retrieve bitmap from resulting matrix
                    Bitmap dst_img = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(dst_mat, dst_img);

                    //FileOutputStream output_img = new FileOutputStream(MainActivity.ROOT_FILE_NAME + str_date + ".jpg");
                    FileOutputStream output_stream = new FileOutputStream(MainActivity.ZOI_FILE_NAME);
                    //targetBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output_img);
                    dst_img.compress(Bitmap.CompressFormat.JPEG, 100, output_stream);
                    output_stream.flush();
                    output_stream.close();





                    // Set for display
                    snapshot_layer.setImageBitmap(BitmapFactory.decodeFile(MainActivity.ZOI_FILE_NAME));




                    Toast.makeText(QuadCropActivity.this, "Data saved at " + str_date , Toast.LENGTH_SHORT).show();
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
