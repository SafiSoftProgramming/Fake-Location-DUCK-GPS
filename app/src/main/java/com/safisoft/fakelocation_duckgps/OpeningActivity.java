package com.safisoft.fakelocation_duckgps;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class OpeningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final int min = 2 * 600;
        new CountDownTimer(min, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent intent = new Intent(OpeningActivity.this,MapsActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();






    }
}
