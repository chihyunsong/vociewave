package org.voicewave.voicewave;



import android.graphics.Bitmap;
import android.os.Bundle;


import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * Created by Roy on 7/26/2015.
 */
public class UserProfileFragment extends Fragment {
    private View current;
    private int user_id;
    private ProgressBar picload;
    private TextView txtviewnick,txtviewdescrip;
    private ImageView profilepic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user_id = getArguments().getInt("id",-1);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        current = inflater.inflate(R.layout.fragment_user_profile, container, false);

        return current;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        picload = (ProgressBar) current.findViewById(R.id.pictureLoading);
        txtviewnick = (TextView) current.findViewById(R.id.textview_profile_nickname);
        txtviewdescrip = (TextView) current.findViewById(R.id.user_profile_detail);
        profilepic = (ImageView) current.findViewById(R.id.user_profile_img);
        picload.setVisibility(View.GONE);
    }
    @Override
    public void onDestroy() {
        //getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        txtviewdescrip.setText("");
        super.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    public void onPause(){
        super.onPause();
    }
    public void setProfile(String nick, String des, Bitmap pic){

        profilepic.setImageBitmap(pic);
        txtviewnick.setText(nick);
        txtviewdescrip.setText(des);
    }

}



