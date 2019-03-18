package org.voicewave.voicewave;

import android.media.MediaPlayer;
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

import java.io.IOException;

/**
 * Created by Roy on 8/2/2015.
 */
public class UploadPlayerFragment extends Fragment {
    private View current;
    private Bundle args;
    private MediaPlayer mediaPlayer;
    private TextView title;
    private SeekBar seekBar;
    private ProgressBar progressBar;
    private ImageButton playBtn;
    private String musicTitle, musicPath;
    private Handler durationHandler = new Handler();
    private double timeElapsed=0, finalTime=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        current = inflater.inflate(R.layout.fragment_music_player, container, false);
        args = getArguments();
        musicPath = args.getString("path");
        musicTitle = musicPath.substring(musicPath.lastIndexOf("/")+1,musicPath.length());
        mediaPlayer = new MediaPlayer();
        return current;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        title = (TextView) current.findViewById(R.id.musicTitle);
        title.setSelected(true);
        seekBar = (SeekBar) current.findViewById(R.id.seekBar);
        seekBar.setProgress(0);
        playBtn = (ImageButton) current.findViewById(R.id.playBtn);
        progressBar = (ProgressBar) current.findViewById(R.id.loading);
        progressBar.setVisibility(View.GONE);

        try {
            mediaPlayer.setDataSource(musicPath);
            title.setText(musicTitle);
            mediaPlayer.prepare();
            finalTime = mediaPlayer.getDuration();
            seekBar.setMax((int) finalTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    playBtn.setImageResource(R.drawable.play);
                    durationHandler.removeCallbacks(updateSeekBarTime);
                    mediaPlayer.pause();
                } else {
                    playBtn.setImageResource(R.drawable.pause);
                    durationHandler.postDelayed(updateSeekBarTime, 100);
                    mediaPlayer.start();
                }
            }
        });

        MediaPlayer.OnCompletionListener cListner = (new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                durationHandler.removeCallbacks(updateSeekBarTime);
                playBtn.setImageResource(R.drawable.play);
                seekBar.setProgress(0);

            }
        });
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

    public void killMusic(){
        durationHandler.removeCallbacks(updateSeekBarTime);
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
