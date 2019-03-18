package com.voicewave.android.voicewave;

/**
 * Created by Roy on 7/17/2015.
 */
public class MainpageListItem {
    private String title;
    private String username;
    private String musicPath;

    //{"id":23,"user_id":5,"user_nick_name":"페페이크2","title":"아예","path":"/audios/23/018_EXID_-_%EC%95%84%EC%98%88_%28Ah_Yeah%29.mp3?1437101182"}
    public MainpageListItem(String title, String username, String musicPath) {
        super();
        this.title = title;
        this.username = username;
        this.musicPath = musicPath;
    }
    public String getTitle(){
        return this.title;
    }
    public String getUsername(){
        return this.username;
    }
    public String getMusicPath(){
        return this.musicPath;
    }
}
