package com.voicewave.android.voicewave;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class RecorderActivity extends ActionBarActivity {

    private String path;

    private Button recordBtn, cancelBtn;
    private TextView runtime, filename;
    private double timeElapsed=0;
    private Handler durationHandler = new Handler();


    private MediaRecorder VWRecorder;

    protected void initialize(){

        recordBtn.setText("RECORD");

        cancelBtn.setEnabled(false);

        cancelBtn.setVisibility(View.INVISIBLE);
    }

    private Runnable upDateTime = new Runnable() {
        @Override
        public void run() {
            runtime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed), TimeUnit.MILLISECONDS.toSeconds((long) timeElapsed) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed))));
            timeElapsed = timeElapsed+100;
            durationHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        getSupportActionBar().hide();

        recordBtn = (Button) findViewById(R.id.record);

        cancelBtn = (Button)findViewById(R.id.cancel);
        runtime = (TextView)findViewById(R.id.runtime);
        filename = (TextView)findViewById(R.id.filename);

        path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"Sounds";
        String defaultFileName = "음성 녹음 ";
        String defaultFileNum = "001";
        int num = 1;
        //check for filenames already exist in sounds folder

        final ArrayList<String> values = new ArrayList<String>();
        File dir = new File(path);
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                Log.i("test filename", file.substring(0,5));
                //exclude hidden files
                if (!file.startsWith(".")) {
                    if (file.matches(".*\\.(mp3|m4a|wav|mid|ogg|wma)")) {
                        values.add(file.substring(0,9));
                        if (file.substring(0,6).equals(defaultFileName)) {

                            if(file.substring(6,9).equals(defaultFileNum)) {
                                num += 1;
                                defaultFileNum = String.format("%03d", num);

                            }
                        }
                    }
                }
            }
        }
        defaultFileName = defaultFileName + defaultFileNum;
        filename.setText(defaultFileName);

        VWRecorder = new MediaRecorder();
        VWRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        VWRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        VWRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        initialize();

        filename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder rename = new AlertDialog.Builder(RecorderActivity.this);
                rename.setTitle("이름 변경");
                rename.setMessage("파일 이름");
                final EditText input = new EditText(RecorderActivity.this);
                input.setId(0);
                rename.setView(input);

                rename.setPositiveButton("확인", null);
                rename.setNegativeButton("취소", null);

                final AlertDialog alert2 = rename.create();

                alert2.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button pos = alert2.getButton(AlertDialog.BUTTON_POSITIVE);
                        pos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String newname = input.getText().toString();
                                if(values.contains(newname)){
                                    Toast toast = Toast.makeText(getApplicationContext(),"사용 중인 이름입니다.",Toast.LENGTH_LONG);
                                    toast.show();
                                }else{
                                    filename.setText(newname);
                                    alert2.dismiss();
                                }
                            }
                        });

                        Button neg = alert2.getButton(AlertDialog.BUTTON_NEGATIVE);
                        neg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alert2.dismiss();
                            }
                        });
                    }
                });
                alert2.show();
            }
        });

        recordBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recordBtn.getText().equals("RECORD")){ // record start
                    VWRecorder.setOutputFile(path + File.separator + filename.getText().toString() + ".m4a");
                    Log.i("record path", path + File.separator + filename.getText().toString() + ".m4a");
                    recordBtn.setText("Stop");

                    Log.i ("record test", "enter");
                    try{
                        VWRecorder.prepare();
                        VWRecorder.start();
                        Log.i ("record test", "passed");
                    }catch (IllegalStateException e) {

                        e.printStackTrace();
                    }

                    catch (IOException e) {

                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
                    durationHandler.postDelayed(upDateTime, 100);

                }else{//end record
                    recordBtn.setText("Record");
                    VWRecorder.stop();
                    VWRecorder.release();
                    VWRecorder = null;
                    durationHandler.removeCallbacks(upDateTime);
                    Intent intent = new Intent(RecorderActivity.this, PlayerActivity.class);
                    intent.removeExtra("musicTitle");
                    intent.putExtra("musicTitle", path+File.separator+filename.getText().toString()+".m4a");
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);


                }
            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(RecorderActivity.this);
                builder.setTitle("녹음취소");
                builder.setMessage("녹음을 중지하고 파일을 저장하지 않습니다.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initialize();
                        timeElapsed =0;
                        runtime.setText("00:00");
                        durationHandler.removeCallbacks(upDateTime);
                        VWRecorder.stop();
                        VWRecorder.release();
                        VWRecorder = null;
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert =builder.create();
                alert.show();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recorder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
