package com.efeproductions.soundrecorder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void callPlayback(View v) {
        Intent playback = new Intent(this, PlaybackActivity.class);
        startActivity(playback);
    }

    public void callRecording(View v) {
        Intent record = new Intent(this, RecordingActivity.class);
        startActivity(record);
    }
}

