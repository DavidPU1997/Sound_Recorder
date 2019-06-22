package com.efeproductions.soundrecorder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlaybackActivity extends AppCompatActivity {

    ListView myListViewForSongs;
    String[] items;
    String[] dates;
    String[] durations;
    final int REQUEST_PERMISSION_CODE = 1000;
    FragmentManager manager;

    //ZA SWIPE
    float x1, x2, y1, y2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);
        manager = getSupportFragmentManager();
        myListViewForSongs = (ListView) findViewById(R.id.myListView);
        display();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    private String getDate(String pathToItem) {

        // load data file
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(pathToItem);

        String out = "";
        // get mp3 info

        // convert duration to minute:seconds
        String date =
                metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
        //Log.v("date", date);

        // close object
        metaRetriever.release();

        return date;
    }

    public static String formatMediaDate(String date){
        String formattedDate = "";
        try {
            Date inputDate = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault()).parse(date);
            formattedDate = new SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(inputDate);
        }
        catch (Exception e){
            Log.w("TRARA", "error parsing date: ", e);
            try {
                Date inputDate = new SimpleDateFormat("yyyy MM dd", Locale.getDefault()).parse(date);
                formattedDate = new SimpleDateFormat("HH:mm dd MMMM yyyy", Locale.getDefault()).format(inputDate);
            } catch (Exception ex) {
                Log.e("TRSERT", "error parsing date: ", ex);
            }
        }
        return formattedDate;
    }

    void display(){
        final ArrayList<File> mySongs = findSong(new File(Environment.getExternalStorageDirectory()+ "/" + "MyRecordings/"));

        items = new String[mySongs.size()];
        dates = new String[mySongs.size()];
        durations = new String[mySongs.size()];

        HashMap<String, String> NaslovDatum = new HashMap<>();

        for(int i = 0; i < mySongs.size(); i++){
            dates[i] = formatMediaDate(getDate(mySongs.get(i).getAbsolutePath()));
            Objects.requireNonNull(items[i] = mySongs.get(i).getName().replace(".3gp", ""));


            String mediaPath = Uri.parse(mySongs.get(i).getAbsolutePath()).getPath();
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(mediaPath);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            mmr.release();

            int myNum = Integer.parseInt(duration);
            long trajanje = myNum / 1000;
            long h = trajanje / 3600;
            long m = (trajanje - h * 3600) / 60;
            long s = trajanje - (h * 3600 + m * 60);

            String minute = "";
            String sekunde = "";
            if(m < 10){
                minute = "0" + m;
            }
            else{
                minute = String.valueOf(m);
            }

            if(s < 10){
                sekunde = "0" + s;
            }
            else{
                sekunde = String.valueOf(s);
            }

            dates[i] =  minute + ":" + sekunde + "\n" + dates[i];
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

        myListViewForSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String songName = items[position];
                String path = Environment.getExternalStorageDirectory().toString() + "/" + "MyRecordings" + "/" + items[position] + ".3gp";

                try {
                    PlaybackFragment playbackFragment =
                            new PlaybackFragment().newInstance(path, songName);
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.add(playbackFragment, "dialog_playback");
                    transaction.commit();

                    /*FragmentTransaction transaction = ((FragmentActivity) myContext)
                            .getSupportFragmentManager()
                            .beginTransaction();

                    playbackFragment.show(transaction, "dialog_playback");*/

                } catch (Exception e) {
                    Log.e("Playback Time", "exception", e);
                }

            }
        });
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
