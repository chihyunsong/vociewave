package org.voicewave.voicewave;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class RecordProfileActivity extends AppCompatActivity {
    private int record_id;
    private String token, musicPath;
    private String user_id;
    private int record_user;
    private TextView textv_detail, playtime, txtusernickname;
    private ImageView profile_pic;

    private EditText txtcomment;
    private String comment;
    private String profile_path;
    private String nickname, description, title;
    private Main_PlayerFragment player;
    private String strptime;
    //private ProgressBar loading;
    private ArrayList<Comment> comments;
    //private Button plybtn;
    private boolean isComment;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_profile);

        token = LoginControl.getPrefUserKey(this);
        isComment = false;
        user_id = String.valueOf(LoginControl.getPrefUserName(this));
        record_id = getIntent().getIntExtra("record_id", -1);
        Toolbar profileToolbar = (Toolbar) findViewById(R.id.record_profile_toolbar);
        setSupportActionBar(profileToolbar);
        getSupportActionBar().setTitle("보이스정보 불러오는중..");
        textv_detail = (TextView) findViewById(R.id.txt_record_detail);
        txtcomment = (EditText) findViewById(R.id.txt_comment);
        txtusernickname = (TextView) findViewById(R.id.txt_user_nickname);
        profile_pic = (ImageView) findViewById(R.id.record_user_img);
        playtime = (TextView) findViewById(R.id.txtPlaytime);
        //loading = (ProgressBar) findViewById((R.id.record_profile_loading));
        lv = (ListView) findViewById(R.id.lv_comments);
        //plybtn = (Button) findViewById(R.id.record_profile_play_btn);
        //plybtn.setVisibility(View.GONE);
        /***findViewById(R.id.record_profile_play_btn).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        playMusic();
                    }
                });*/
        findViewById(R.id.button_comment).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        comment();
                    }
                });
        findViewById(R.id.record_user_img).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        user_profile();
                    }
                });
        findViewById(R.id.txt_user_nickname).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        user_profile();
                    }
                });

        new getRecordProfile().execute();


    }
    private void user_profile(){
        Intent k = new Intent(this, UserProfileActivity.class);
        k.putExtra("user_id", record_user);
        startActivity(k);

    }
    private void playMusic() {
        VoiceWaveApplication mApp = ((VoiceWaveApplication) getApplicationContext());
        if (!mApp.getMusicPath().equals(musicPath)) {
            mApp.setMusicId(record_id);
            player.changeMusic(musicPath);
            mApp.setMusicTitle(title);
            mApp.setPlayable(true);

        }


    }

    private void comment() {
        comment = txtcomment.getText().toString();
        if (comment.equals("") || comment == null) {

        } else {
            new writeComment().execute();
            txtcomment.setText("");
        }
    }

    private void getFragment() {
        player = new Main_PlayerFragment();
        Bundle args = new Bundle();
        args.putBoolean("is_new", false);
        //placing fragment on screen
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        player.setArguments(args);
        transaction.add(R.id.record_profile_player, player);
        transaction.commit();
    }

    private void setlistview() {
        lv.setAdapter(new CommentAdapter(this, comments));
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


    class getRecordProfile extends AsyncTask<Void, Void, String> {
        private HttpURLConnection urlConnection;

        //request data from server
        @Override
        protected String doInBackground(Void... params) {
            // got this from http://blog.naver.com/javaking75/140196018323

            urlConnection = null;
            String url = "https://voicewave.org";
            String str = "";
            InputStream content;
            try {
                String addUrl = "/api/v1/records/" + String.valueOf(record_id);

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

            try {
                comments = getComments(result);
                JSONObject jsonObj = new JSONObject(result);
                JSONObject dataObj = jsonObj.getJSONObject("data");
                nickname = dataObj.getString("nick_name");

                profile_path = dataObj.getString("profile_path");
                JSONObject recordObj = dataObj.getJSONObject("record");
                record_user = recordObj.getInt("user_id");
                title = recordObj.getString("title");
                description = recordObj.getString("description");
                musicPath = "https://voicewave.org" + dataObj.getString("path");
                if (description.equals("") || description.equals("null")) {
                    description = "보이스 소개가 없습니다.";
                }
                strptime = String.valueOf(recordObj.getInt("view"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!isComment) {
                new GetImage().execute();
            } else {
                //loading.setVisibility(View.GONE);
                setlistview();
            }
            //progress.setVisibility(View.GONE);
            getSupportActionBar().setTitle("보이스: " + title);
            urlConnection.disconnect();
        }
    }

    class GetImage extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... params) {
            String url = "https://voicewave.org/";
            if (profile_path.equals("Raseone-Record.png")) {
                url = url + "assets/" + profile_path;
            } else {
                url = url + profile_path;
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
            textv_detail.setText(description);
            txtusernickname.setText(nickname);
            profile_pic.setImageBitmap(result);
            playtime.setText("재생횟수:" + String.valueOf(strptime));
            getFragment();
            super.onPostExecute(result);
            //loading.setVisibility(View.GONE);
            setlistview();
            //plybtn.setVisibility(View.VISIBLE);


        }
    }

    private ArrayList<Comment> getComments(String jsonStr) throws JSONException {
        ArrayList<Comment> comments = new ArrayList<Comment>();
        JSONObject object = new JSONObject(jsonStr);
        JSONObject dataObject = object.getJSONObject("data");
        JSONArray array = dataObject.getJSONArray("comments");
        for (int i = 0; i < array.length(); i++) {

            String nick_name = array.getJSONObject(i).getString("nick_name") + ": ";
            String description = array.getJSONObject(i).getString("description");
            comments.add(new Comment(nick_name, description));

        }
        return comments;
    }


    class writeComment extends AsyncTask<Void, Void, String> {
        private HttpURLConnection urlConnection;

        //request data from server
        @Override
        protected String doInBackground(Void... params) {
            // got this from http://blog.naver.com/javaking75/140196018323
            isComment = true;
            urlConnection = null;

            String url = "https://voicewave.org";
            String str = "";
            InputStream content;
            try {
                String encodedComment = URLEncoder.encode(comment,"UTF-8");
                String addUrl = "/api/v1/comments/?user_id=" + user_id + "&token=" + token + "&record_id=" + String.valueOf(record_id) +
                        "&comment=" + encodedComment;

                URL profilePathUrl = new URL(url + addUrl);
                urlConnection = (HttpsURLConnection) profilePathUrl.openConnection();
                urlConnection.setRequestMethod("POST");

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
            Toast.makeText(getApplicationContext(), R.string.comment_success, Toast.LENGTH_SHORT).show();

            //loading.setVisibility(View.VISIBLE);
            new getRecordProfile().execute();
            txtcomment.setText("");
        }
    }
}
