package com.efeproductions.soundrecorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.FileObserver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlaybackActivity extends AppCompatActivity {

    ListView myListViewForSongs;
    String[] items;
    String[] dates;
    final int REQUEST_PERMISSION_CODE = 1000;

    //ZA SWIPE
    float x1, x2, y1, y2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        myListViewForSongs = (ListView) findViewById(R.id.myListView);
        display();

    }

    public ArrayList<File> findSong(File file){
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();

        for(File singleFile: files){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                arrayList.addAll(findSong(singleFile));
            }
            else{
                if(singleFile.getName().endsWith(".3gp")){
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }

    void display(){
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());

        items = new String[mySongs.size()];
        dates = new String[mySongs.size()];

        HashMap<String, String> NaslovDatum = new HashMap<>();

        for(int i = 0; i < mySongs.size(); i++){
            Date lastModDate = new Date(mySongs.get(i).lastModified());
            dates[i] = lastModDate.toString();
            Objects.requireNonNull(items[i] = mySongs.get(i).getName().replace(".3gp", ""));

            NaslovDatum.put(items[i], dates[i]);
        }

        List<HashMap<String, String>> listItems = new ArrayList<>();

        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item, new String[]{"First Line", "Second Line"}, new int[]{R.id.text1, R.id.text2});

        Iterator it = NaslovDatum.entrySet().iterator();

        while(it.hasNext()){
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }

        myListViewForSongs.setAdapter(adapter);

        //ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        //myListViewForSongs.setAdapter(myAdapter);
    }


    public void callHome(View v){
        Intent home = new Intent(this, MainActivity.class);
        startActivity(home);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;

            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 < x2) {
                    Intent i = new Intent(PlaybackActivity.this, MainActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
                }
                break;
        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
