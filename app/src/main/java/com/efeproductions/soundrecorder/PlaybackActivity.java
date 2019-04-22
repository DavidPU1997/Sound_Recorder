package com.efeproductions.soundrecorder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PlaybackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);
    }

    public void callHome(View v){
        Intent home = new Intent(this, MainActivity.class);
        startActivity(home);
    }
}
