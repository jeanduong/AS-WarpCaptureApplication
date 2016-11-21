package com.example.jeanduong.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }

    public void gently_escape(View view) {
        // Exterminate current activity
        finish();
    }
}
