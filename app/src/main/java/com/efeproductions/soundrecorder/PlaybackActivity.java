package com.efeproductions.soundrecorder;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
    String[] items2;
    String[] dates;
    String[] durations;
    final int REQUEST_PERMISSION_CODE = 1000;
    FragmentManager manager;
    CheckBox check;
    List<HashMap<String, String>> listItems;
    MenuItem deleteSelectedRecordings;
    String pathSave = "";
    boolean renameStop = false;
    boolean deleteMode = false;
    boolean editMode = false;
    boolean [] deleteItems;

    boolean[] rename_bool_array;
    //ZA SWIPE
    float x1, x2, y1, y2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        manager = getSupportFragmentManager();
        myListViewForSongs = (ListView) findViewById(R.id.myListView);
        display();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        deleteSelectedRecordings = menu.findItem(R.id.deleteRecordings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        myListViewForSongs.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {


                if(mLastFirstVisibleItem>firstVisibleItem || mLastFirstVisibleItem<firstVisibleItem)
                {
                    if(editMode){
                        for(int i = 0; i < myListViewForSongs.getLastVisiblePosition() - myListViewForSongs.getFirstVisiblePosition() + 1; i++) {
                            View rowView = myListViewForSongs.getChildAt(i);
                            if (rowView != null) {
                                CheckBox checkBox = (CheckBox)rowView.findViewById(R.id.checkbox);
                                //if(checkBox.getTag() == null) {
                                    checkBox.setTag(i+firstVisibleItem);
                                    checkBox.setVisibility(View.VISIBLE);
                                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                int position = (int) buttonView.getTag();
                                                pathSave = Environment.getExternalStorageDirectory() + "/" + "MyRecordings/" + items2[position] + ".3gp";
                                                showPopupWindow();
                                                CheckBox checkBox = (CheckBox)buttonView.findViewById(R.id.checkbox);
                                                checkBox.setChecked(false);
                                            }
                                        }
                                    );
                                //}
                            }
                        }
                    }
                    else if(deleteMode){
                        for(int i = 0; i < myListViewForSongs.getLastVisiblePosition() - myListViewForSongs.getFirstVisiblePosition() + 1; i++) {
                            View rowView = myListViewForSongs.getChildAt(i);
                            if (rowView != null) {
                                CheckBox checkBox = (CheckBox)rowView.findViewById(R.id.checkbox);
                                checkBox.setTag(i+firstVisibleItem);
                                checkBox.setVisibility(View.VISIBLE);
                                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            int position = (int) buttonView.getTag();
                                            pathSave = Environment.getExternalStorageDirectory() + "/" + "MyRecordings/" + items2[position] + ".3gp";
                                            showPopupWindowDelete();
                                            CheckBox checkBox = (CheckBox)buttonView.findViewById(R.id.checkbox);
                                            checkBox.setChecked(false);
                                        }
                                    }
                                );
                            }
                        }
                    }
                }
            }
        });


        switch (item.getItemId()) {
            case R.id.deleteMenu:
                deleteMode = true;
                editMode = false;
                for (int i = 0; i < myListViewForSongs.getLastVisiblePosition() - myListViewForSongs.getFirstVisiblePosition() + 1; i++) {
                    View rowView = myListViewForSongs.getChildAt(i);
                    if(rowView != null) {

                        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);
                        checkBox.setTag(i + myListViewForSongs.getFirstVisiblePosition());
                        checkBox.setVisibility(View.VISIBLE);
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    int position = (int) buttonView.getTag();
                                    pathSave = Environment.getExternalStorageDirectory() + "/" + "MyRecordings/" + items2[position] + ".3gp";
                                    showPopupWindowDelete();
                                    CheckBox checkBox = (CheckBox) buttonView.findViewById(R.id.checkbox);
                                    checkBox.setChecked(false);
                                }
                            }
                        );
                    }
                }
                deleteSelectedRecordings.setVisible(true);
                return true;
            case R.id.renameMenu:
                editMode = true;
                deleteMode = false;
                if (deleteSelectedRecordings.isVisible()) {
                    deleteSelectedRecordings.setVisible(false);
                }

                for (int i = 0; i < myListViewForSongs.getLastVisiblePosition() - myListViewForSongs.getFirstVisiblePosition() + 1; i++) {
                    View rowView = myListViewForSongs.getChildAt(i);
                    if(rowView != null) {
                        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);
                        checkBox.setTag(i + myListViewForSongs.getFirstVisiblePosition());
                        checkBox.setVisibility(View.VISIBLE);
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    int position = (int) buttonView.getTag();
                                    pathSave = Environment.getExternalStorageDirectory() + "/" + "MyRecordings/" + items2[position] + ".3gp";
                                    showPopupWindow();
                                    CheckBox checkBox = (CheckBox)buttonView.findViewById(R.id.checkbox);
                                    checkBox.setChecked(false);
                                }
                            }
                        );
                    }
                }

                return true;
            case R.id.deleteRecordings:
                DeleteMyRecordings();
                display();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void DeleteMyRecordings(){
        for(int i = 0; i < deleteItems.length; i++) {
            if(deleteItems[i]){
                delete(Environment.getExternalStorageDirectory()+ "/" + "MyRecordings/" + items2[i] + ".3gp");
            }
        }
        deleteSelectedRecordings.setVisible(false);
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
        items2 = new String[mySongs.size()];
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

        listItems = new ArrayList<>();

        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item, new String[]{"First Line", "Second Line"}, new int[]{R.id.text1, R.id.text2});

        Iterator it = NaslovDatum.entrySet().iterator();

        int iterator_i = 0;

        while(it.hasNext()){
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
            items2[iterator_i] = pair.getKey().toString();
            iterator_i++;
        }

        myListViewForSongs.setAdapter(adapter);

        myListViewForSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String songName = items2[position];
                String path = Environment.getExternalStorageDirectory().toString() + "/" + "MyRecordings" + "/" + items2[position] + ".3gp";

                try {
                    PlaybackFragment playbackFragment =
                            new PlaybackFragment().newInstance(path, songName);
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.add(playbackFragment, "dialog_playback");
                    transaction.commit();

                } catch (Exception e) {
                    Log.e("Playback Time", "exception", e);
                }

            }
        });

        deleteItems = new boolean[myListViewForSongs.getAdapter().getCount()];
        editMode = false;
        deleteMode = false;
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

    public void onCheckboxClicked(View view) {
        SparseBooleanArray checked = myListViewForSongs.getCheckedItemPositions();

        for (int i = 0; i < myListViewForSongs.getAdapter().getCount(); i++) {
            if (checked.get(i)) {
                // Do something
            }
        }
    }

    public void delete(String path){
        File file = new File(path);
        boolean deleted;

        try {
            if (file.exists()) {
                deleted = file.delete();
                Log.d("MainActivity", "Deletion Succesfull");
            }
        } catch (Exception e){
            Log.d("Error pri brisanju", e.toString());
        }
    }

    public void rename(String pathToItemRename, String name) {

        String pathDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "MyRecordings/";
        /* File (or directory) with old name */
        File file = new File(pathToItemRename);

        /* File (or directory) with new name */
        File file2 = new File(pathDir + name);

        if (file2.exists()) {
            try {
                throw new java.io.IOException("File already exists!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /* Rename file */
        boolean success = file.renameTo(file2);
        if (!success) {
            Log.e("Rename File","Couldn't rename file!");
        } else {
            Log.i("Rename File","File renamed successfully!");
        }
    }
    public void showPopupWindow(){
        AlertDialog.Builder nameFileBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_name_file, null);

        final EditText input = (EditText) view.findViewById(R.id.new_name);

        nameFileBuilder.setTitle(this.getString(R.string.dialog_title_name));
        nameFileBuilder.setCancelable(false);
        nameFileBuilder.setPositiveButton(this.getString(R.string.dialog_action_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            String value = input.getText().toString().trim() + ".3gp";
                            if(!value.equals(".3gp")){
                                rename(pathSave, value);
                            }
                            Log.d("pritisnil si ok", "jajaja");

                        } catch (Exception e) {
                            Log.d("Error pri temule", e.toString());
                        }

                        dialog.cancel();
                        display();
                    }
                });
        nameFileBuilder.setNegativeButton(this.getString(R.string.dialog_action_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        nameFileBuilder.setView(view);
        AlertDialog alert = nameFileBuilder.create();
        alert.show();

    }




    public void showPopupWindowDelete(){
        AlertDialog.Builder nameFileBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_name_file_delete, null);

        final EditText input = (EditText) view.findViewById(R.id.delete_question);

        nameFileBuilder.setTitle(this.getString(R.string.delete_question_a));
        nameFileBuilder.setCancelable(false);
        nameFileBuilder.setPositiveButton(this.getString(R.string.dialog_action_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {

                            Log.d("pritisnil si ok", "jajaja");

                        } catch (Exception e) {
                            Log.d("Error pri temule", e.toString());
                        }

                        dialog.cancel();
                        display();
                    }
                });
        nameFileBuilder.setNegativeButton(this.getString(R.string.dialog_action_cancel_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        nameFileBuilder.setView(view);
        AlertDialog alert = nameFileBuilder.create();
        alert.show();

    }
}
