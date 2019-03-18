package org.voicewave.voicewave;

import java.util.ArrayList;

/**
 * Created by Roy on 7/26/2015.
 */
public class Main_Musiclist_Item {
    private String title;
    private String username;
    private String musicPath;
    private Object profilePic;
    private int record_id;

    private ArrayList<Main_ExpandList_Item> expandDetails;


    public Main_Musiclist_Item(String title, String username, String musicPath, Object profilePic ,int record_id, ArrayList<Main_ExpandList_Item> Items) {

        this.title = title;
        this.username = username;
        this.musicPath = musicPath;
        this.profilePic = profilePic;
        this.record_id = record_id;

        this.expandDetails = Items;
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
    public Object getprofilePath(){
        return this.profilePic;
    }
    public int getRecord_id(){
        return this.record_id;
    }

    public ArrayList<Main_ExpandList_Item> getItems() {
        return expandDetails;
    }
    public void setItems(ArrayList<Main_ExpandList_Item> Items) {
        this.expandDetails = Items;
    }
}
