package com.hridayapp.trial3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

public class splashactivity extends AppCompatActivity {

    private MediaPlayer shank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashactivity);
        shank = MediaPlayer.create(getApplicationContext(), R.raw.spalshmusic);
        shank.start();
        shank.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition (R.anim.slide_up, R.anim.slide_down_reverse);
                finish();
            }
        });




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

            shank.release();


    }
}