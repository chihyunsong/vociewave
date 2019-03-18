package org.voicewave.voicewave;



import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;


import android.os.Handler;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;

import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Roy on 7/26/2015.
 */
public class Main_PlayerFragment extends Fragment {

    private String url = "https://voicewave.org";
    private View current;
    private TextView title;
    private SeekBar seekBar;
    private ImageButton prevBtn, nextBtn, playBtn;
    private Bundle args;
    private String musicTitle, musicPath;
    private MediaPlayer mediaPlayer;
    private double timeElapsed=0, finalTime=0;
    private Handler durationHandler = new Handler();
    private VoiceWaveApplication mApp;
    private String user_id;
    private String token;
    private String record_id;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        current = inflater.inflate(R.layout.fragment_music_player, container, false);

        args = getArguments();
        user_id = String.valueOf(LoginControl.getPrefUserName(getActivity()));
        token = LoginControl.getPrefUserKey(getActivity());




        mApp = (VoiceWaveApplication)getActivity().getApplicationContext();
        musicTitle = mApp.getMusicTitle();
        if(musicTitle.equals("")){
            mApp.setMusicTitle("음악을 선택해주세요.");
        }
        record_id = String.valueOf(mApp.getMusicId());
        mediaPlayer = mApp.getPlayer();
        return current;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MediaPlayer.OnCompletionListener cListner = (new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                durationHandler.removeCallbacks(updateSeekBarTime);
                playBtn.setImageResource(R.drawable.play);
                seekBar.setProgress(0);
                mApp.setMusicPath("");
                mApp.setMusicId(-1);
                mApp.setMusicTitle("");
                mApp.setPlayable(false);
                title.setText("음악을 선택해주세요.");

            }
        });
        mediaPlayer.setOnCompletionListener(cListner);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
                mp.start();
                finalTime = mediaPlayer.getDuration();
                seekBar.setMax((int) finalTime);
                playBtn.setImageResource(R.drawable.pause);
                VoiceWaveApplication mApp = (VoiceWaveApplication) getActivity().getApplicationContext();
                mApp.setMusicPath(url);
                musicTitle = mApp.getMusicTitle();
                title.setText(musicTitle);
                timeElapsed = mediaPlayer.getCurrentPosition();
                seekBar.setProgress((int) timeElapsed);
                durationHandler.postDelayed(updateSeekBarTime, 100);

            }
        });
        mApp = (VoiceWaveApplication) getActivity().getApplicationContext();
        title = (TextView) current.findViewById(R.id.musicTitle);
        title.setSelected(true);
        seekBar = (SeekBar) current.findViewById(R.id.seekBar);
        seekBar.setProgress(0);
        playBtn = (ImageButton) current.findViewById(R.id.playBtn);
        progressBar = (ProgressBar) current.findViewById(R.id.loading);
        progressBar.setVisibility(View.GONE);
        if (mediaPlayer.isPlaying()) {
            playBtn.setImageResource(R.drawable.pause);
        } else {
            playBtn.setImageResource(R.drawable.play);
        }
        //musicplayer control section~~~~~~~~~~~~~~~~~~~~~~~~~
        if (mApp.getPlayable()) {

            if (mediaPlayer.isPlaying()) {
                playBtn.setImageResource(R.drawable.pause);
                finalTime = mediaPlayer.getDuration();
                seekBar.setMax((int) finalTime);
                timeElapsed = mediaPlayer.getCurrentPosition();
                seekBar.setProgress((int) timeElapsed);
                durationHandler.postDelayed(updateSeekBarTime, 100);
                title.setText(mApp.getMusicTitle());
            }
            else if(mApp.getPause()){
                playBtn.setImageResource(R.drawable.play);
                finalTime = mediaPlayer.getDuration();
                seekBar.setMax((int) finalTime);
                timeElapsed = mediaPlayer.getCurrentPosition();
                seekBar.setProgress((int) timeElapsed);
                durationHandler.postDelayed(updateSeekBarTime, 100);
                title.setText(mApp.getMusicTitle());
            }
            else {
                playBtn.setImageResource(R.drawable.play);
                progressBar.setVisibility(View.VISIBLE);
                title.setText("음악을 로딩중입니다.");
            }



        }


        //when finished
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    playBtn.setImageResource(R.drawable.play);
                    durationHandler.removeCallbacks(updateSeekBarTime);
                    mediaPlayer.pause();
                    mApp.setPause(true);
                } else {
                    playBtn.setImageResource(R.drawable.pause);
                    durationHandler.postDelayed(updateSeekBarTime, 100);
                    mApp.setPause(true);
                    mediaPlayer.start();
                }
            }
        });


        //when music ends


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
    public void changeMusic(String musicPath){
        mApp.setPause(false);
        progressBar.setVisibility(View.VISIBLE);
        record_id = String.valueOf(mApp.getMusicId());
        mediaPlayer.stop();
        mediaPlayer.reset();
        url = musicPath;

        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new incView().execute();


        mediaPlayer.prepareAsync();

    }

    public void pauseMusic(){
        durationHandler.removeCallbacks(updateSeekBarTime);
        mediaPlayer.pause();
        mApp.setPause(true);
    }

    public void killMusic(){
        durationHandler.removeCallbacks(updateSeekBarTime);
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private Runnable updateSeekBarTime = new Runnable() {
        @Override
        public void run() {
            //set time elapsed to seekbar
            timeElapsed = mediaPlayer.getCurrentPosition();
            seekBar.setProgress((int) timeElapsed);

            //display time remaining on textview
            //double timeRemaining = finalTime - timeElapsed;
            //duration.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));
            //run this thread every 100ms
            durationHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onDestroy() {
        //getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        durationHandler.removeCallbacks(updateSeekBarTime);
        super.onDestroy();



    }
    @Override
    public void onResume(){
        MediaPlayer.OnCompletionListener cListner = (new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                durationHandler.removeCallbacks(updateSeekBarTime);
                playBtn.setImageResource(R.drawable.play);
                seekBar.setProgress(0);
                mApp.setMusicPath("");
                mApp.setMusicId(-1);
                mApp.setMusicTitle("");
                mApp.setPlayable(false);
                title.setText("음악을 선택해주세요.");

            }
        });
        mediaPlayer.setOnCompletionListener(cListner);
        title = (TextView) current.findViewById(R.id.musicTitle);
        VoiceWaveApplication mApp = (VoiceWaveApplication) getActivity().getApplicationContext();
        musicTitle = mApp.getMusicTitle();
        mediaPlayer = mApp.getPlayer();
        title.setText(musicTitle);
        if (mApp.getPlayable()) {
            if (mediaPlayer.isPlaying()) {
                progressBar.setVisibility(View.GONE);
                finalTime = mediaPlayer.getDuration();
                seekBar.setMax((int) finalTime);
                timeElapsed = mediaPlayer.getCurrentPosition();
                seekBar.setProgress((int) timeElapsed);
                playBtn.setImageResource(R.drawable.pause);
                durationHandler.postDelayed(updateSeekBarTime, 100);
            } else if (mApp.getPause()) {
                playBtn.setImageResource(R.drawable.play);
                finalTime = mediaPlayer.getDuration();
                seekBar.setMax((int) finalTime);
                timeElapsed = mediaPlayer.getCurrentPosition();
                seekBar.setProgress((int) timeElapsed);
                durationHandler.postDelayed(updateSeekBarTime, 100);
            } else {
                title.setText("음악을 로딩중입니다.");
                playBtn.setImageResource(R.drawable.play);
                progressBar.setVisibility(View.VISIBLE);
            }
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
                mp.start();
                finalTime = mediaPlayer.getDuration();
                seekBar.setMax((int) finalTime);
                playBtn.setImageResource(R.drawable.pause);
                VoiceWaveApplication mApp = (VoiceWaveApplication) getActivity().getApplicationContext();
                mApp.setMusicPath(url);
                musicTitle = mApp.getMusicTitle();
                title.setText(musicTitle);
                timeElapsed = mediaPlayer.getCurrentPosition();
                seekBar.setProgress((int) timeElapsed);
                durationHandler.postDelayed(updateSeekBarTime, 100);

            }
        });



        super.onResume();
    }
    public void onPause(){
        super.onPause();
    }

    class incView extends AsyncTask<Void, Void, String> {
        private HttpURLConnection urlConnection;

        //request data from server
        @Override
        protected String doInBackground(Void... params) {
            // got this from http://blog.naver.com/javaking75/140196018323

            urlConnection = null;
            String url = "https://voicewave.org";
            String str = "";
            InputStream content;

            record_id = String.valueOf(mApp.getMusicId());
            try {
                String addUrl = "/api/v1/increment_view?user_id=" + user_id + "&token=" + token + "&record_id=" + record_id;

                URL profilePathUrl = new URL(url + addUrl);
                urlConnection = (HttpsURLConnection) profilePathUrl.openConnection();
                urlConnection.setRequestMethod("GET");

                content = (InputStream) urlConnection.getContent();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s;
                while ((s = buffer.readLine()) != null) {
                    str += s;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

}
