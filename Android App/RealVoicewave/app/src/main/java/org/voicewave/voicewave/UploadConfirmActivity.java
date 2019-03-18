package org.voicewave.voicewave;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class UploadConfirmActivity extends AppCompatActivity {

    private UploadPlayerFragment player;
    private Bundle args ;

    private EditText titleET,contentET, tagET;
    private TextView uploadBtn, category;
    private CheckBox privateCheck;

    private String musicPath, title, content,tag, isPrivate, selCategory, url;
    private int user_id;
    private String key;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_confirm);
        getFragment();

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(R.string.app_voiceUpload);

        musicPath = getIntent().getStringExtra("path");
        titleET = (EditText)findViewById(R.id.titleET);
        contentET = (EditText)findViewById(R.id.contentET);
        tagET = (EditText)findViewById(R.id.tagET);
        privateCheck = (CheckBox)findViewById(R.id.privatecheck);
        category = (TextView)findViewById(R.id.category);
        uploadBtn = (TextView)findViewById(R.id.uploadBtn);
        uploadBtn.setText(getResources().getString(R.string.upload_confirm));
        selCategory = "0";

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(UploadConfirmActivity.this, category);
                popup.getMenuInflater().inflate(R.menu.menu_category, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals(getResources().getString(R.string.song))) {
                            selCategory = "1";
                            category.setText(getResources().getString(R.string.song));
                        } else if (item.getTitle().equals(getResources().getString(R.string.compose))) {
                            selCategory = "2";
                            category.setText(getResources().getString(R.string.compose));
                        } else if (item.getTitle().equals(getResources().getString(R.string.instrumental))) {
                            selCategory = "3";
                            category.setText(getResources().getString(R.string.instrumental));
                        } else if (item.getTitle().equals(getResources().getString(R.string.voiceact))) {
                            selCategory = "4";
                            category.setText(getResources().getString(R.string.voiceact));
                        } else if (item.getTitle().equals(getResources().getString(R.string.humor))) {
                            selCategory = "5";
                            category.setText(getResources().getString(R.string.humor));
                        } else {
                            selCategory = "6";
                            category.setText(getResources().getString(R.string.etc));
                        }
                        return true;
                    }

                });
                popup.show();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selCategory.equals("0")){
                    AlertDialog catNotSel = new AlertDialog.Builder(UploadConfirmActivity.this).create();
                    catNotSel.setTitle("장르를 선택해주세요");
                    catNotSel.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    catNotSel.show();
                }else{
                    new UploadMusic().execute();
                }

            }
        });


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

        player = new UploadPlayerFragment();
        args = new Bundle();

        args.putString("path",getIntent().getStringExtra("path"));
        //placing fragment on screen
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        //args.putBoolean("is_new", false);
        player.setArguments(args);
        transaction.replace(R.id.musicPlayer, player);
        transaction.commit();

    }
    @Override
    public void onBackPressed() {
        player.killMusic();
        Intent intent = new Intent(UploadConfirmActivity.this, UploadActivity.class);
        startActivity(intent);
        finish();
    }

    public void getContents(){
        title = titleET.getText().toString();
        content = contentET.getText().toString();
        tag = tagET.getText().toString();
        user_id = LoginControl.getPrefUserName(this);
        key = LoginControl.getPrefUserKey(this);
        url = "https://voicewave.org/api/v1/records";
        if(privateCheck.isChecked()){
            isPrivate = "1";
        }else {
            isPrivate = "0";
        }
    }

    class UploadMusic extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute(){
            pd = new ProgressDialog(UploadConfirmActivity.this);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("업로드중입니다...");
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            getContents();
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            FileBody fileBody = new FileBody(new File(musicPath));

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            entityBuilder.addTextBody("user_id", String.valueOf(user_id));
            entityBuilder.addTextBody("token", key);
            try {
                entityBuilder.addTextBody("title", URLEncoder.encode(title, "UTF-8"));
                entityBuilder.addTextBody("description", URLEncoder.encode(content, "UTF-8"));
                entityBuilder.addTextBody("private", URLEncoder.encode(isPrivate , "UTF-8"));
                entityBuilder.addTextBody("tag", URLEncoder.encode(tag, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            entityBuilder.addTextBody("category_id", selCategory);
            entityBuilder.addPart("file", fileBody);

            HttpEntity entity = entityBuilder.build();
            post.setEntity(entity);
            HttpResponse response = null;
            try {
                response = client.execute(post);
                HttpEntity httpEntity = response.getEntity();
                String result = EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            pd.dismiss();
            Toast toast = Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_LONG);
            toast.show();
            Intent intent = new Intent(UploadConfirmActivity.this, MainpageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }


}
