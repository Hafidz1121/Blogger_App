package com.example.blogapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isFirstTime();
            }
        }, 1500);
    }

    private void isFirstTime() {
        // Check app running for the very first time
        // Then save the value to shared preference
        SharedPreferences preferences = getApplication().getSharedPreferences("onBoard", Context.MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean("isFirstTime", true);

        //default value true
        if (isFirstTime) {
            // if true will change to false
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();

            // start onBoard activity
            startActivity(new Intent(MainActivity.this, onBoardActivity.class));
            finish();
        } else {
            startActivity(new Intent(MainActivity.this, authActivity.class));
            finish();
        }
    }
}