package com.voicewave.android.voicewave;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;


public class PlayerActivity extends ActionBarActivity {

    private TextView musicTitleV;
    private MediaPlayer mediaPlayer;
    private Button controlBtn;
    private SeekBar seekbar;
    private TextView duration;
    private double timeElapsed=0, finalTime=0;
    private Handler durationHandler = new Handler();
    private String musicPath,musicTitle, title, detail, privateCheck;


    //move seekbar along with time remaining for every 100ms
    private Runnable updateSeekBarTime = new Runnable() {
        @Override
        public void run() {
            //set time elapsed to seekbar
            timeElapsed = mediaPlayer.getCurrentPosition();
            seekbar.setProgress((int) timeElapsed);

            //display time remaining on textview
            double timeRemaining = finalTime - timeElapsed;
            duration.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));
            //run this thread every 100ms
            durationHandler.postDelayed(this, 100);
        }
    };

    protected void initialize(){
        musicPath = getIntent().getStringExtra("musicTitle");
        musicTitle = musicPath.substring(musicPath.lastIndexOf("/")+1, musicPath.lastIndexOf("."));

        //center the title and make it able to scroll horizontally
        musicTitleV =(TextView) findViewById(R.id.musicTitle);
        musicTitleV.setText(musicTitle);
        musicTitleV.setGravity(Gravity.CENTER);
        musicTitleV.setSelected(true);

        //mediaplayer config
        mediaPlayer = MediaPlayer.create(this, Uri.parse("file://"+ musicPath));

        duration = (TextView) findViewById(R.id.timer);
        finalTime = mediaPlayer.getDuration();
        //seek bar config
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setMax((int) finalTime);
        seekbar.setClickable(true);

        Spinner category =(Spinner)findViewById(R.id.category);
        ArrayAdapter adapter =ArrayAdapter.createFromResource(
                this, R.array.category, android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);

        controlBtn = (Button)findViewById(R.id.playBtn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_player);
        //keyboard setting
        setupUI(findViewById(R.id.parent));

        initialize();

        // controller config for PLAY BUTTON
        controlBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String checker = controlBtn.getText().toString();
                        // Pause/Play On click
                        if (checker.equals("PLAY")) {
                            controlBtn.setText("PAUSE");
                            mediaPlayer.start();
                            timeElapsed = mediaPlayer.getCurrentPosition();
                            seekbar.setProgress((int) timeElapsed);
                            durationHandler.postDelayed(updateSeekBarTime, 100);
                        } else {
                            controlBtn.setText("PLAY");
                            durationHandler.removeCallbacks(updateSeekBarTime);
                            mediaPlayer.pause();
                        }

                    }
                }
        );

        //seekbar reset on reaching end
        MediaPlayer.OnCompletionListener cListener = (
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        durationHandler.removeCallbacks(updateSeekBarTime);
                        controlBtn.setText("PLAY");
                        seekbar.setProgress(0);
                    }
                }
        );
        mediaPlayer.setOnCompletionListener(cListener);

        //Listener for Change in seekbar
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

    }



    //hiding keyboard
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    // hiding keyboard ON clicking anywhere else other than textviews
    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(PlayerActivity.this);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }


    //go back to previous page and terminates everything thats going on
    public void goBack(View v){
        durationHandler.removeCallbacks(updateSeekBarTime);
        mediaPlayer.release();
        Intent intent = new Intent(PlayerActivity.this, MainPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //overide for phone's back button
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack(this.findViewById(android.R.id.content));
        }
        return super.onKeyDown(keyCode, event);
    }

    //Action to be taken when upload button is pressed
    public void uploadMusic(View v){
        //get title and detail from the user input
        EditText titleV = (EditText)findViewById(R.id.title);
        title = titleV.getText().toString();
        if (title.equals("")){
            title = musicTitle;
        }
        EditText detailV= (EditText)findViewById(R.id.detail);
        detail = detailV.getText().toString();
        if(detail.equals("")){
            detail="내용이 없습니다";
        }
        detail = detail+"-안드로이드 폰에서 작성";

        //get if the post is private or not
        CheckBox privateChecker = (CheckBox) findViewById(R.id.privateCheck);
        if (privateChecker.isChecked()){
            privateCheck = "1";
        }else{
            privateCheck = "0";
        }

        //todo: category caller

        //upload to server
        new Upload().execute();
    }

    //upload part
    class Upload extends AsyncTask<Void,Void,Void>{
        private final String url = "http://192.168.0.103:3000/api/v1/records";
        @Override
        protected Void doInBackground(Void... params) {


            DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
            HttpPost localHttpPost = new HttpPost(url);
            MultipartEntity localMultipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            //target file to be uploaded
            FileBody localFileBody = new FileBody(new File(musicPath));
            Spinner category = (Spinner)findViewById(R.id.category);

            try
            {
                //TESTER CODE----------------------------------------------------------------------
                localMultipartEntity.addPart("email",new StringBody("seungq92@gmail.com"));
                localMultipartEntity.addPart("token",new StringBody("8QpTBGsvVN8iUlhtQSFoFQtt"));
                //---------------------------------------------------------------------------------

                //Formatting stuff to be uploaded
                localMultipartEntity.addPart("title", new StringBody(URLEncoder.encode(title, "UTF-8")));
                localMultipartEntity.addPart("description", new StringBody(detail));
                localMultipartEntity.addPart("private", new StringBody(privateCheck));
                localMultipartEntity.addPart("file", localFileBody);
                //todo: category
                localMultipartEntity.addPart("category_id", new StringBody(String.valueOf(category.getSelectedItem())));

                localHttpPost.setEntity(localMultipartEntity);
                HttpResponse localHttpResponse = localDefaultHttpClient.execute(localHttpPost);


                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                 //testing purpose code
                try {
                    localMultipartEntity.writeTo(bytes);
                } catch ( IOException e ) {
                    Log.e( "hi", "MultipartEntity print", e );
                }
                Log.e( "hi", "Multipart=" + bytes.toString() );

                System.out.println("responsecode " + localHttpResponse.getStatusLine().getStatusCode());

            }
            catch (Exception e)
            {
                Log.d("exception", e.toString());

            }
            return null;
        }

    }

}
