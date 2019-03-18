package org.voicewave.voicewave;

import android.widget.ImageButton;

/**
 * Created by Roy on 8/5/2015.
 */
public class Main_ExpandList_Item {

    private ImageButton openRecordBtn, likeBtn;

    public Main_ExpandList_Item(){
        super();
        this.openRecordBtn =null;
        this.likeBtn=null;
    }

    public void setOpenRecordBtn(ImageButton btn){
        this.openRecordBtn = btn;
    }
    public void setLikeBtn(ImageButton btn){
        this.likeBtn = btn;
    }
    public ImageButton getOpenRecordBtn(){
        return openRecordBtn;
    }
    public ImageButton getLikeBtn(){
        return likeBtn;
    }
}
