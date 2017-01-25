package com.example.jeanduong.myapplication;

        import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.util.Xml;
        import android.view.View;
        import android.widget.ImageButton;
        import android.widget.ImageView;

        import org.xmlpull.v1.XmlSerializer;

        import java.io.StringWriter;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.StringTokenizer;

public class SelectArea extends Activity {
    private static final String TAG = "Text ZOI selection";
    private SelectAreaView areaView;
    private Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_area);
        ImageView myImage = (ImageView) findViewById(R.id.preview_camera);
        areaView = (SelectAreaView)findViewById(R.id.AreaView);

        try {
            //Load saved image
            img = BitmapFactory.decodeFile(MainActivity.CURRENT_IMAGE_FULL_NAME);
            //Set saved image as background
            myImage.setImageBitmap(img);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        ImageButton btnCrop = (ImageButton)findViewById(R.id.buttonSave);
        btnCrop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {Export();}
        });
    }

    private void Export() {
        //SelectAreaView.Area[] tmpAreas = areaView.GetAllAreas();
        //for (SelectAreaView.Area ar : tmpAreas)

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);

            for (SelectAreaView.Area ar : areaView.GetAllAreas())
            {
                float left = ar._upLeft.x;
                float right = ar._downRight.x;
                float top = ar._upLeft.y;
                float bottom = ar._downRight.y;

                // Order for arguments and returned coordinates
                // is chosen to comply with android Rect class
                int[] coordinates = ratio2coordinates(left, top, right, bottom);

                serializer.startTag(null, "text_area");
                serializer.startTag(null, "vertex");
                serializer.attribute(null, "x_ratio", Double.toString(left));
                serializer.attribute(null, "y_ratio", Double.toString(top));
                serializer.attribute(null, "x_coord", Integer.toString(coordinates[0]));
                serializer.attribute(null, "y_coord", Integer.toString(coordinates[1]));
                serializer.attribute(null, "relative_position", "top_left");
                serializer.endTag(null, "vertex");

                serializer.startTag(null, "vertex");
                serializer.attribute(null, "x_ratio", Double.toString(right));
                serializer.attribute(null, "y_ratio", Double.toString(bottom));
                serializer.attribute(null, "x_coord", Integer.toString(coordinates[2]));
                serializer.attribute(null, "y_coord", Integer.toString(coordinates[3]));
                serializer.attribute(null, "relative_position", "bottom_right");
                serializer.endTag(null, "vertex");
                serializer.endTag(null, "text_area");
            }

            serializer.endDocument();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Convert a 4-uple of percent ratios (float)
    // to a 4-uple of image coordinates (integer)

    private int[] ratio2coordinates(float left, float top, float right, float bottom)
    {
        float aspectRatioH = (float)areaView.getHeight() / (float)areaView.getWidth();
        float heightDiff = img.getHeight() - img.getWidth() * aspectRatioH;

        //Convert percent of the image to pixels
        int x = (int)(left * img.getWidth());
        int width = (int)((right - left) * img.getWidth());
        int y = (int)(heightDiff / 2f) + (int)(top * img.getWidth() * aspectRatioH);
        int height = (int)((bottom - top) * img.getWidth() * aspectRatioH);
        int xx = x + width;
        int yy = y + height;

        int[] coords = {x, y, xx, yy};

        return coords;
    }
}