package com.example.smartplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SmartPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //give audio permissions
    private void checkVoiceCommandPermisstion(){

    }
}
