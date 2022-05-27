package com.example.robot001.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.robot001.R;

public class ControlActivity extends AppCompatActivity {
    private static final String TAG = ControlActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {

    }

}
