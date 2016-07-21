package com.morgenworks.utilexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.morgenworks.alchemistutil.FileUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileUtils.createGetContentIntent();
    }
}
