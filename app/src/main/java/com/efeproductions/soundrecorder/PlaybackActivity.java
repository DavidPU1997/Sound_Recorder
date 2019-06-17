package com.efeproductions.soundrecorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlaybackActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;
    ArrayList<String> arrayList;

    ListView listView;

    ArrayAdapter<String> adapter;




    private List<String> fileList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        if(ContextCompat.checkSelfPermission(PlaybackActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(PlaybackActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(PlaybackActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
            else{
                ActivityCompat.requestPermissions(PlaybackActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }
        else{
            doStuff();
        }
    }

    private void doStuff() {
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundRecorder/");
        File[] files = f.listFiles();
        fileList.clear();
        for(File file : files){
            fileList.add(file.getPath());
        }
        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileList);
    }

    public void callHome(View v){
        Intent home = new Intent(this, MainActivity.class);
        startActivity(home);
    }
}
