package com.example.jeanduong.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class PhotoActivity extends Activity {
    // Visible elements for UI

    private Button shutter_button;
    private TextureView preview_area;
    private ImageView grid_guide;

    private static final String TAG = "Snapshot activity"; // For log output

    // Camera device stuffs

    private String camera_Id;
    protected CameraDevice camera_device;
    protected CameraCaptureSession camera_capture_sessions;
    //protected CaptureRequest capture_request;
    protected CaptureRequest.Builder capture_request_builder;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean flash_supported;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    // Image and file for save
    private Size image_dimension;
    private int width; // width of the TextureView
    private int height;
    private ImageReader image_reader;
    private File file;

    // Utilities to catch date and time (used as file root name)
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Background thread to prevent application from freezing
    private Handler background_handler;
    private HandlerThread background_thread;

    public Intent result_intent = new Intent();
    public byte[] image_arry_bytes = null;

    /////////////////////////////////////
    // Setup when activity is launched //
    /////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        // Create UI elements

        preview_area = (TextureView) findViewById(R.id.preview_area);
        shutter_button = (Button) findViewById(R.id.shutter_button);
        grid_guide = (ImageView) findViewById(R.id.grid);

        assert preview_area != null;
        assert shutter_button != null;
        assert grid_guide != null;

        // Draw a green grid
        DisplayMetrics dimension = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dimension);
        int w =  dimension.widthPixels;
        int h =  dimension.heightPixels;

        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        Canvas cv = new Canvas(bm);
        grid_guide.setImageBitmap(bm);

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(1);

        int delta_w = 50;
        int nb_steps_w = w / delta_w;
        int tmp_w = 0;

        for (int p = 1; p <= nb_steps_w; ++p)
        {
            tmp_w += delta_w;
            cv.drawLine(tmp_w, 0, tmp_w, h, paint);
        }

        int delta_h = 50;
        int nb_steps_h = h / delta_h;
        int tmp_h = 0;

        for (int p = 1; p <= nb_steps_h; ++p)
        {
            tmp_h += delta_h;
            cv.drawLine(0, tmp_h, w, tmp_h, paint);
        }

        //////////////////////////////////////////////////////
        // Things to be done when button is clicked by user //
        shutter_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                do_tedious_things_just_to_capture_a_picture(); // Code too long to be placed here

                //String formatted_date = date_format.format(cal.getTime());
                //Toast.makeText(PhotoActivity.this, "Snapshot at " + formatted_date, Toast.LENGTH_SHORT).show();

                Bundle bd = new Bundle();

                bd.putByteArray(MainActivity.LABEL_EXTRA_CAPTURED_BYTES, image_arry_bytes);
                result_intent.putExtras(bd);

                if (image_arry_bytes == null)
                    Log.e(TAG, "****** Image byte array is null!!!");
                else
                {
                    Log.e(TAG, "****** Image byte array is valid");
                    Log.e(TAG, "****** Size = " + image_arry_bytes.length);
                }

                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, result_intent);
                }
                else {
                    getParent().setResult(Activity.RESULT_OK, result_intent);
                }

                finish();
            }
        });
        //////////////////////////////////////////////////////
    }

    ///////////////////////////////////////////////////////
    // Define utilities to activate and play with camera //
    ///////////////////////////////////////////////////////

    // Listener for SurfaceTexture

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            // Open your camera here

            openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    // State callback for camera device

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.v(TAG, "onOpened");
            camera_device = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camera_device.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            camera_device.close();
            camera_device = null;
        }
    };

    // Callback for capture session

    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            createCameraPreview();
        }
    };

    // Background thread to prevent application from freezing

    protected void startBackgroundThread() {
        background_thread = new HandlerThread("Camera Background");
        background_thread.start();
        background_handler = new Handler(background_thread.getLooper());
    }

    protected void stopBackgroundThread() {
        background_thread.quitSafely();
        try {
            background_thread.join();
            background_thread = null;
            background_handler = null;
        }
        catch (InterruptedException e) {e.printStackTrace();}
    }

    //////////////////////
    // Tricky part here //
    //////////////////////

    protected void do_tedious_things_just_to_capture_a_picture() {
        if(camera_device == null) {
            Log.e(TAG, "camera_device is null");
            return;
        }

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(camera_device.getId());
            Size[] jpegSizes = null;

            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }

            // Default dimensions (if jpegSizes is void or characteristics is null)
            int width = 640;
            int height = 480;

            if ((jpegSizes != null) && (jpegSizes.length > 0)) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(preview_area.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = camera_device.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            //final File file = new File(Environment.getExternalStorageDirectory() + "/pic.jpg");
            final File file = new File(MainActivity.SNAPSHOT_FILE_NAME);

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer bb = image.getPlanes()[0].getBuffer();

                        image_arry_bytes = new byte[bb.capacity()];

                        bb.get(image_arry_bytes);
                        save(image_arry_bytes);


                        if (image_arry_bytes == null)
                            Log.e(TAG, "************ Image byte array is null!!!");
                        else
                        {
                            Log.e(TAG, "************ Image byte array is valid");
                            Log.e(TAG, "************ Size = " + image_arry_bytes.length);
                        }
                    }
                    catch (FileNotFoundException e) {e.printStackTrace();}
                    catch (IOException e) {e.printStackTrace();}
                    finally {if (image != null) {image.close();}}
                }

                private void save(byte[] bts) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bts);
                        Log.e(TAG, "*************** BYTES SAVED IN FILE");
                    }
                    finally {if (output != null) {output.close();}}
                }

            };

            reader.setOnImageAvailableListener(readerListener, background_handler);

            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    createCameraPreview();
                }
            };

            camera_device.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {session.capture(captureBuilder.build(), captureListener, background_handler);}
                    catch (CameraAccessException e) {e.printStackTrace();}
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {}
            }, background_handler);
        } catch (CameraAccessException e) {e.printStackTrace();}
    }

    // Create a preview to display what the camera is staring at

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = preview_area.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(image_dimension.getWidth(), image_dimension.getHeight());
            Surface surface = new Surface(texture);
            capture_request_builder = camera_device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);





            capture_request_builder.addTarget(surface);

            camera_device.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback(){
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (camera_device == null) {return;}
                    // When the session is ready, we start displaying the preview.
                    camera_capture_sessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(PhotoActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        }
        catch (CameraAccessException e) {e.printStackTrace();}
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.v(TAG, "is camera open");

        try {
            camera_Id = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(camera_Id);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            assert map != null;
            image_dimension = map.getOutputSizes(SurfaceTexture.class)[0];

            // Add permission for camera and let user grant the permission

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PhotoActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }

            //Log.e(TAG, "camera id = " + camera_Id);

            manager.openCamera(camera_Id, stateCallback, null);
        } catch (CameraAccessException e) {e.printStackTrace();}

        Log.v(TAG, "openCamera X");
    }

    protected void updatePreview() {
        if(camera_device == null) {Log.e(TAG, "updatePreview error, return");}

        capture_request_builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        try {
            camera_capture_sessions.setRepeatingRequest(capture_request_builder.build(), null, background_handler);}
        catch (CameraAccessException e) {e.printStackTrace();}
    }

    private void closeCamera() {
        if (camera_device != null) {
            camera_device.close();
            camera_device = null;
        }
        if (image_reader != null) {
            image_reader.close();
            image_reader = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(PhotoActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        startBackgroundThread();

        if (preview_area.isAvailable()) {
            Log.v(TAG, "preview area available");
            openCamera();
        } else {
            Log.v(TAG, "preview area to be prepared");
            preview_area.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        closeCamera(); // WHY ???
        stopBackgroundThread();
        super.onPause();
    }
}
