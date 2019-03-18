package com.voicewave.android.voicewave;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Roy on 7/17/2015.
 */
public class MainpageList extends ArrayAdapter<MainpageListItem> {

    private  Context context;
    private  ArrayList<MainpageListItem> itemsArrayList;
    private MediaPlayer mediaPlayer;

    private String url, musicPath, currentMusic;


    //variables for musicplayer

    private TextView playerTitle;
    private SeekBar playerSeekbar;
    private Button playerBtn;

    public MainpageList(Context context, ArrayList<MainpageListItem> itemsArrayList){
        super(context, R.layout.row, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;

    }

    public void initPlayer(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mainView = inflater.inflate(R.layout.activity_mainpage,null);

        playerTitle = (TextView) mainView.findViewById(R.id.playerTitle);
        playerSeekbar = (SeekBar)mainView.findViewById(R.id.playerSeekbar);
        playerBtn = (Button)mainView.findViewById(R.id.playerBtn);
    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        final View rowView = inflater.inflate(R.layout.row, parent, false);


        // 3. Get the two text view from the rowView
        final TextView titleView = (TextView) rowView.findViewById(R.id.title);
        TextView usernameView = (TextView) rowView.findViewById(R.id.username);
        final Button playBtn = (Button)rowView.findViewById(R.id.playBtn);



        // 4. Set the text for textView
        titleView.setText(itemsArrayList.get(position).getTitle());
        usernameView.setText(itemsArrayList.get(position).getUsername());


        //set up musicplayer

        rowView.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                initPlayer();
                Log.i("test row","rowclicked");
                musicPath = itemsArrayList.get(position).getMusicPath();
                url = "http://192.168.0.103:3000"+ musicPath;
                //url = "https://voicewave.org"+musicPath;

                if (currentMusic != musicPath){
                    playMusic(url);
                    playerTitle.setText(itemsArrayList.get(position).getTitle());

                    Log.i("test title", (String) playerTitle.getText());
                    Log.i("test titleget",itemsArrayList.get(position).getTitle());
                    currentMusic = musicPath;
                }else{
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.pause();

                    }else{
                        mediaPlayer.start();


                    }
                }
            }
        });

        // 5. retrn rowView
        return rowView;
    }



    public void playMusic (String url ){
        //stopplayer if playn
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;

        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
