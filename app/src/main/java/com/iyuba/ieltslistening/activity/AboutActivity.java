package com.iyuba.ieltslistening.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.iyuba.ieltslistening.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        this.setTitle("关于");
    }
}