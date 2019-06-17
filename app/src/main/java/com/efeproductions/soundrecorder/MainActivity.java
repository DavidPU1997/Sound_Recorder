package com.efeproductions.soundrecorder;
        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.os.Environment;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.Manifest;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.graphics.Color;
        import android.graphics.drawable.ColorDrawable;
        import android.media.MediaPlayer;
        import android.media.MediaRecorder;
        import android.support.annotation.NonNull;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.ActionBar;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.LayoutInflater;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.Button;
        import android.widget.Chronometer;
        import android.widget.EditText;
        import android.widget.LinearLayout;
        import android.widget.PopupWindow;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;
        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.OutputStreamWriter;
        import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    //declare variables
    Button btnRecord, btnStopRecord, btnCancel;
    String pathSave = "";
    MediaRecorder mediaRecorder;
    Chronometer timer;
    final int REQUEST_PERMISSION_CODE = 1000;
    Context maContext;

    PopupWindow popUp;
    LinearLayout layout;
    TextView tv;
    LinearLayout.LayoutParams params;
    Button but;
    boolean click = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Request Runtime permissions
        if(!checkPermissionFromDevice())
            requestPermissions();

        //init view
        timer = findViewById(R.id.timer);
        btnRecord = findViewById(R.id.btnStartRecord);
        btnStopRecord = findViewById(R.id.btnStopRecord);

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnRecord.setVisibility(View.GONE);
                btnStopRecord.setVisibility(View.VISIBLE);
                timer.setVisibility(View.VISIBLE);
                //from Android M , you need request Run time permission
                if (checkPermissionFromDevice()) {
                    //stevilo recordingov
                    int stRec = countRecordings();
                    stRec++;
                    // direktorij, ime datoteke
                    pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundRecorder/" + "New Recording " + stRec + ".3gp";


                    setupMediaRecorder();
                    try{
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Recording...", Toast.LENGTH_SHORT).show();
                }
                else {
                    requestPermissions();
                }

            }
        });

        btnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                //btnRecord.setVisibility(View.VISIBLE);
                //btnStopRecord.setVisibility(View.INVISIBLE);
                //timer.setVisibility(View.INVISIBLE);

                showPopupWindow(v);

                //display the keyboard
                /*EditText editText = (EditText) findViewById(R.id.editTextName);
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);*/

            }
        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }


    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }


    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

    private static int countRecordings() {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundRecorder/");
        File[] files = dir.listFiles();
        if(files == null){
            return 0;
        }
        else {
            return files.length;
        }
    }

    public void callPlayback(View v) {
        Intent playback = new Intent(this, PlaybackActivity.class);
        startActivity(playback);
    }



    /*public void showPopupWindow(View view) {

        btnRecord.setEnabled(false); //da ne mores po nesreci kliknit gumba record
        btnStopRecord.setEnabled(false);

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setElevation(20);
        //popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        btnCancel = findViewById(R.id.cancelRecording);
    }*/

    public void showPopupWindow(View v){
        AlertDialog.Builder nameFileBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_name_file, null);

        final EditText input = (EditText) view.findViewById(R.id.new_name);

        nameFileBuilder.setTitle(this.getString(R.string.dialog_title_name));
        nameFileBuilder.setCancelable(true);
        nameFileBuilder.setPositiveButton(this.getString(R.string.dialog_action_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Log.d("pritisnil si ok", "jajaja");

                        } catch (Exception e) {
                            Log.d("Error pri temule", e.toString());
                        }

                        dialog.cancel();
                    }
                });
        nameFileBuilder.setNegativeButton(this.getString(R.string.dialog_action_delete),
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