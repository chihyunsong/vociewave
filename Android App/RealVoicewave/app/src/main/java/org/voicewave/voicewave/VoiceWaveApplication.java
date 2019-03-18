package org.voicewave.voicewave;

import android.app.Application;
import android.media.MediaPlayer;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;

/**
 * Created by MPSju_000 on 2015-07-31.
 */
public class VoiceWaveApplication  extends Application {
    public MediaPlayer mediaPlayer;
    private String musicTitle, musicPath;
    private boolean playable,isPause;
    private int musicId;
    private AccessTokenTracker accessTokenTracker;
    public void setPlayable(boolean p) {
        playable = p;
    }
    public void setPause(boolean p) {
        isPause = p;
    }
    public boolean getPause() {
        return isPause;
    }
    public boolean getPlayable(){
        return playable;
    }
    public void setMusicTitle(String p) {
        musicTitle = p;
    }
    public void setMusicPath(String path){
        musicPath = path;
    }

    public String getMusicPath(){
        return musicPath;
    }

    public String getMusicTitle() {
        return musicTitle;
    }
    public void setMusicId(int i){
        musicId=i;
    }
    public int getMusicId(){
        return musicId;
    }
    public MediaPlayer getPlayer() {
        return mediaPlayer;
    }
    public AccessTokenTracker getToken(){
        return accessTokenTracker;
    }
    public void init_player() {
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        mediaPlayer = new MediaPlayer();
        playable = false;
        musicTitle="";
        musicPath="";
        musicId=-1;
    }



}
