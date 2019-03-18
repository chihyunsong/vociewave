package org.voicewave.voicewave;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;


import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class MainpageActivity extends AppCompatActivity implements Main_ListFragment.CustomOnClickListener,Main_ListFragment.openRecord {


    private Bundle args = new Bundle() ;

    //variable recieved from the music list
    private String title, musicPath;
    VoiceWaveApplication mApp;
    private Main_PlayerFragment player;
    private Main_ListFragment musicList;


    //initialize on first
    // run
    private void init() {
        mApp = ((VoiceWaveApplication)getApplicationContext());
        mApp.init_player();

        //If User is not logged in should login first.
         if(LoginControl.getPrefUserKey(this).length()==0){
            Intent i = new Intent(this, AccountActivity.class);
            startActivity(i);
        }else {

         }
    }


    private void getFragment(){
        musicList = new Main_ListFragment();
        player = new Main_PlayerFragment();


        //placing fragment on screen
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentList, musicList);
        args.putBoolean("is_new", false);
        player.setArguments(args);

        transaction.replace(R.id.fragmentPlayer, player);

        transaction.commit();

    }


    @Override
    public void onClicked(String title, String musicPath, int record_id) {

        if(!mApp.getMusicPath().equals(musicPath)) {
            VoiceWaveApplication mApp = ((VoiceWaveApplication)getApplicationContext());
            mApp.setMusicPath(musicPath);
            mApp.setMusicId(record_id);
            player.changeMusic("https://voicewave.org" + musicPath);

            /*PlayerFragment newPlayer = new PlayerFragment();*/
            mApp.setPlayable(true);
            mApp.setMusicTitle(title);
            /*args.putString("musicPath", musicPath);
            args.putBoolean("is_new", true);
            newPlayer.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentPlayer, newPlayer);
            transaction.commit();*/

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "org.voicewave.voicewave",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
            }

        } catch (PackageManager.NameNotFoundException e) {



        } catch (NoSuchAlgorithmException e) {

        }
        setContentView(R.layout.activity_mainpage);


        //initialize
        init();
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(R.string.app_korname);


        getFragment();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mainpage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.my_profile) {
            Intent i = new Intent(this, UserProfileActivity.class);
            i.putExtra("user_id", LoginControl.getPrefUserName(this));
            startActivity(i);
        }
        else if(id == R.id.voice_upload){
            player.pauseMusic();
            Intent i = new Intent(this, UploadActivity.class);
            startActivity(i);
        }
        else if (id == R.id.logout){
            player.pauseMusic();
            LoginControl.logout(this);
            Intent i = new Intent(this, AccountActivity.class);
            startActivity(i);
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("종료확인")
                .setMessage("종료하시겠습니까?")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        player.killMusic();
                        finish();
                    }
                }).create().show();
    }


    @Override
    public void onClickRecordOpen(int recordid) {
        Intent i = new Intent(this, RecordProfileActivity.class);
        i.putExtra("record_id", recordid);
        startActivity(i);
    }
}
