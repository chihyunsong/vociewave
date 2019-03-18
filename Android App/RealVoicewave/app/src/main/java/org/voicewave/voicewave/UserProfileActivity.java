package org.voicewave.voicewave;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class UserProfileActivity extends AppCompatActivity implements UserMusicListFragment.CustomOnClickListener,UserMusicListFragment.openRecord {
    private Main_PlayerFragment player;
    private Bundle args ;
    private String musicPath;
    private UserProfileFragment profile;

    private ProgressBar progress;
    private TextView textv_profile;
    private  TextView textv_voicelist;
    private String description;
    private String pic;
    private int selected;
    private int user_id;
    private int id;
    private String token;
    String url;
    private String nickname;
    private VoiceWaveApplication mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = "https://voicewave.org";
        mApp = ((VoiceWaveApplication)getApplicationContext());


        getFragment();
        getActionBar();

        setContentView(R.layout.activity_user_profile);
        Toolbar profileToolbar = (Toolbar) findViewById(R.id.profileToolbar);
        setSupportActionBar(profileToolbar);
        progress = (ProgressBar) findViewById(R.id.profileLoading);
        progress.setVisibility(View.VISIBLE);
        textv_profile = (TextView) findViewById(R.id.tab_profile);
        textv_voicelist = (TextView) findViewById(R.id.tab_voicelist);
        textv_profile.setShadowLayer(2, 0, 0, Color.BLACK);
        textv_profile.setTextColor(Color.parseColor("#FFF44336"));
        selected = 0;
        musicPath="yes";
        id = getIntent().getIntExtra("user_id",-1);
        getSupportActionBar().setTitle("프로필 가져오는중...");
        findViewById(R.id.tab_profile).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectProfile();
                    }
                });
        findViewById(R.id.tab_voicelist).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        selectList();
                    }
                });

        token = LoginControl.getPrefUserKey(this);
        user_id = LoginControl.getPrefUserName(this);
        new getUserProfile().execute();

    }
    private void selectProfile(){
        if(selected != 0){
            textv_voicelist.setTextColor(Color.BLACK);
            textv_profile.setTextColor(Color.parseColor("#FFF44336"));
            textv_profile.setShadowLayer(2, 0, 0, Color.BLACK);
            textv_voicelist.setShadowLayer(0, 0, 0, Color.BLACK);
            selected = 0;


            Bundle profileid = new Bundle();
            profileid.putInt("id", id);
            FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
            profile.setArguments(profileid);
            transaction.replace(R.id.user_profile_container, profile).commit();
            progress.setVisibility(View.VISIBLE);
            new getUserProfile().execute();
            getSupportActionBar().setTitle(nickname+"님의 프로필");
        }
    }
    private void selectList(){
        if(selected != 1){

            selected = 1;
            textv_voicelist.setTextColor(Color.parseColor("#FFF44336"));
            textv_profile.setTextColor(Color.BLACK);

            textv_voicelist.setShadowLayer(2, 0, 0, Color.BLACK);
            textv_profile.setShadowLayer(0, 0, 0, Color.BLACK);

            UserMusicListFragment musicList = new UserMusicListFragment();
            Bundle args = new Bundle();
            args.putInt("user_id", id);
            if (pic.equals("Raseone-Record.png")){
                args.putString("pic","");
            }
            else{
                args.putString("pic", pic);
            }
            musicList.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.user_profile_container,
                    musicList).commit();
            getSupportActionBar().setTitle(nickname+"님의 보이스");

        }
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
            finish();
        }
        else if(id == R.id.voice_upload){
            Intent i = new Intent(this, UploadActivity.class);
            startActivity(i);
            finish();
        }
        else if (id == R.id.logout){
            LoginControl.logout(this);
            Intent i = new Intent(this, AccountActivity.class);
            startActivity(i);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    private void getFragment(){



        player = new Main_PlayerFragment();
        profile = new UserProfileFragment();


        args = new Bundle();
        args.putBoolean("is_new", false);
        Bundle profileid = new Bundle();
        profileid.putInt("id", id);

        //placing fragment on screen
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        profile.setArguments(profileid);
        transaction.add(R.id.user_profile_container, profile);
        player.setArguments(args);

        transaction.add(R.id.user_profile_player, player);
        transaction.commit();

    }

    @Override
    public void onClicked(String title, String musicPath, int recordId) {
        if(!mApp.getMusicPath().equals(musicPath)) {
            VoiceWaveApplication mApp = ((VoiceWaveApplication)getApplicationContext());
            mApp.setMusicPath(musicPath);
            mApp.setMusicId(recordId);
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
    public void onClickRecordOpen(int recordid) {
        Intent i = new Intent(this, RecordProfileActivity.class);
        i.putExtra("record_id", recordid);
        startActivity(i);
    }

    class getUserProfile extends AsyncTask<Void,Void,String> {
        private HttpURLConnection urlConnection;
        //request data from server
        @Override
        protected String doInBackground(Void... params) {
            // got this from http://blog.naver.com/javaking75/140196018323

            urlConnection = null;
            String str="";
            InputStream content;
            try {
                String addUrl = "/api/v1/users/" + String.valueOf(id) + "/?user_id=" + String.valueOf(user_id) + "&" + "token=" + token
                        + "&" + "type=profile";

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
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject dataObj = jsonObj.getJSONObject("data");
                nickname = dataObj.getString("nick_name");
                description = dataObj.getString("description");
                pic = dataObj.getString("profile_pic");

                if (description.equals("") || description.equals("null")){
                    description =  "아직 자기소개가 없습니다.";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            new GetImage().execute();
            progress.setVisibility(View.GONE);
            getSupportActionBar().setTitle(nickname + "님의 프로필");
            urlConnection.disconnect();
        }


    }
    class GetImage extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... params) {
            String url = "https://voicewave.org/";
            if (pic.equals("Raseone-Record.png")) {
                url = url + "assets/" + pic;
            } else {
                url = url + pic;
            }
            try {
                Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            profile.setProfile(nickname, description, result);
            progress.setVisibility(View.GONE);
        }
    }
}

