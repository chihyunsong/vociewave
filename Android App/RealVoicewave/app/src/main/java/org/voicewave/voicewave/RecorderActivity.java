package org.voicewave.voicewave;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class RecorderActivity extends AppCompatActivity {


    private TextView runtime, filename, recordBtn ;
    private double timeElapsed=0;
    private Handler durationHandler = new Handler();
    private String path;

    private MediaRecorder VWRecorder;
    private boolean isRecording = false;

    private Runnable upDateTime = new Runnable() {
        @Override
        public void run() {
            runtime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed), TimeUnit.MILLISECONDS.toSeconds((long) timeElapsed) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed))));
            timeElapsed = timeElapsed+100;
            durationHandler.postDelayed(this, 100);
        }
    };

    private void init(){
        runtime = (TextView) findViewById(R.id.runtime);
        filename = (TextView)findViewById(R.id.filename);
        recordBtn = (TextView)findViewById(R.id.recordBtn);
        recordBtn.setText(getResources().getString(R.string.start_recording));

        path =  Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"VoiceWave";
        File dir = new File(path);
        if(!(dir.exists() && dir.isDirectory())){
            dir.mkdirs();
        }


        VWRecorder = new MediaRecorder();
        VWRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        VWRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        VWRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
    }

    private String checkFilename(String str){

        //check for filenames already exist in sounds folder

        final ArrayList<String> values = new ArrayList<String>();
        File dir = new File(path);
        String[] list = dir.list();
        int num = 1;
        String origin = str;
        if (list != null) {
            while(Arrays.asList(list).contains(str+".m4a")){
                str = origin + String.format(" %03d", num);
                num+=1;
            }
        }
        return str;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(R.string.app_voiceRecord);

        init();
        String defaultFileName = "VW 녹음파일";
        defaultFileName = checkFilename(defaultFileName);
        filename.setText(defaultFileName);

        filename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder rename = new AlertDialog.Builder(RecorderActivity.this);
                rename.setTitle("이름 변경");
                rename.setMessage("파일 이름");
                final EditText input = new EditText(RecorderActivity.this);
                input.setId(0);
                rename.setView(input);
                rename.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String temp = checkFilename(String.valueOf(input.getText()));
                        if (!temp.equals(String.valueOf(input.getText()))) {
                            Toast toast = Toast.makeText(getApplicationContext(), "사용 중인 이름입니다.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
                rename.setNegativeButton("취소", null);
                rename.show();

            }
        });
        path =  path+File.separator+filename.getText().toString()+".m4a";
        recordBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recordBtn.getText().equals(getResources().getString(R.string.start_recording))){ // record start

                    VWRecorder.setOutputFile(path);
                    recordBtn.setText(getResources().getString(R.string.stop_recording));
                    isRecording = true;
                    try{
                        VWRecorder.prepare();
                        VWRecorder.start();
                    }catch (IllegalStateException e) {

                        e.printStackTrace();
                    }

                    catch (IOException e) {

                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "녹음 중입니다", Toast.LENGTH_LONG).show();
                    durationHandler.postDelayed(upDateTime, 100);

                }else{//end record
                    recordBtn.setText(getResources().getString(R.string.start_recording));
                    //stop runnable thread

                    VWRecorder.stop();
                    VWRecorder.release();
                    VWRecorder = null;

                    durationHandler.removeCallbacks(upDateTime);

                    Intent intent = new Intent(RecorderActivity.this, UploadConfirmActivity.class);
                    intent.removeExtra("path");
                    intent.putExtra("path",path);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mainpage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.my_profile) {
            goBack();
            Intent i = new Intent(this, UserProfileActivity.class);
            i.putExtra("user_id", LoginControl.getPrefUserName(this));
            startActivity(i);
            finish();
        }
        else if(id == R.id.voice_upload){
            goBack();
            Intent i = new Intent(this, UploadActivity.class);
            startActivity(i);
            finish();
        }
        else if (id == R.id.logout){

            LoginControl.logout(this);
            goBack();
            Intent i = new Intent(this, AccountActivity.class);
            startActivity(i);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed(){
        goBack();
        Intent intent = new Intent(RecorderActivity.this, UploadActivity.class);
        startActivity(intent);

        finish();
    }
    public void goBack(){
        if (isRecording) {
            VWRecorder.stop();
            VWRecorder.release();
            VWRecorder = null;
            File file = new File(path);
            file.delete();
            durationHandler.removeCallbacks(upDateTime);
        }
    }
}
