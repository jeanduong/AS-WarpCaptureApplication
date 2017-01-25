package com.example.jeanduong.myapplication;

        import android.app.Activity;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.ImageButton;
        import android.widget.ImageView;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.StringTokenizer;

public class SelectArea extends Activity {
    private String _capturePath;
    private SelectAreaView areaView;
    private Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_area);
        ImageView myImage = (ImageView) findViewById(R.id.preview_camera);
        areaView = (SelectAreaView)findViewById(R.id.AreaView);

        try {
            _capturePath = getIntent().getStringExtra("CapturePath");
            //Load saved image
            img = BitmapFactory.decodeFile(_capturePath);
            //Set saved image as background
            myImage.setImageBitmap(img);
        } catch (Exception e) {
            Log.d(e.toString(), "v");
        }

        ImageButton btnCrop = (ImageButton)findViewById(R.id.buttonSave);
        btnCrop.setOnClickListener(new View.OnClickListener(){
                                       @Override
                                       public void onClick(View v) {
                                           Export();
                                       }
                                   }
        );
    }

    private void Export() {
        SelectAreaView.Area[] tmpAreas = areaView.GetAllAreas();
        for (SelectAreaView.Area ar : tmpAreas) {
            ExportAreas(ar._upLeft.x, ar._upLeft.y, ar._downRight.y, ar._downRight.x);
            //TODO mettre les infos dans un fichier
        }
    }

    //percent to pixels of the image
    private int[] ExportAreas(float top, float left, float bottom, float right) {
        float aspectRatioH = (float)areaView.getHeight() / (float)areaView.getWidth();
        float heightDiff = img.getHeight() - img.getWidth() * aspectRatioH;

        //Convert percent of the image to pixels
        int x = (int)(left * img.getWidth());
        int width = (int)((right - left) * img.getWidth());
        int y = (int)(heightDiff / 2f) + (int)(top * img.getWidth() * aspectRatioH);
        int height = (int)((bottom - top) * img.getWidth() * aspectRatioH);

        int[] ret = {x, width, y, height};
        return ret;
    }
}