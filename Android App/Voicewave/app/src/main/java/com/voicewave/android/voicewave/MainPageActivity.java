package com.voicewave.android.voicewave;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MainPageActivity extends ListActivity {

    private Button test;
    private String jsonStr="";
    private Button popupBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        popupBtn=(Button) findViewById(R.id.mainmenu);
        popupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup =new PopupMenu(MainPageActivity.this, popupBtn);



                if(LoginControl.getPrefUserName(MainPageActivity.this).length()==0) {
                    //B4 login
                    popup.getMenu().add("로그인");
                }else{
                    popup.getMenu().add("내 정보");
                    popup.getMenu().add("보이스 녹음/업로드");
                    popup.getMenu().add("로그아웃");
                }


                //MENU implementation
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("로그인")){

                            Intent LoginIntent = new Intent(MainPageActivity.this, LoginActivity.class);
                            startActivity(LoginIntent);
                        }

                        if (item.getTitle().equals("로그아웃")){
                            LoginControl.setUser(MainPageActivity.this, "",0);
                        }

                        if(item.getTitle().equals("내 정보")){
                            //do something
                            //Intent profileIntent = new Intent(MainPageActivity.this, ProfileActivity.class);
                            //startActivity(profileIntent);
                        }

                        if(item.getTitle().equals("보이스 녹음/업로드")){
                            Intent uploadIntent = new Intent(MainPageActivity.this, UploadActivity.class);
                            startActivity(uploadIntent);
                        }

                        return true;
                    }
                });
                popup.show();
            }
        });


        //music list json call
        AsyncTask task = new Connect().execute();

        ArrayList<MainpageListItem> mainpageListItems = new ArrayList<MainpageListItem>();

        try {
            jsonStr= (String) task.get();
            JSONObject object = new JSONObject(jsonStr);

            //creating mainpage listing of music
            JSONArray array = object.getJSONArray("data");
            for(int i = 0 ; i < array.length() ; i++){
                String title = array.getJSONObject(i).getString("title");
                String userNickname = array.getJSONObject(i).getString("user_nick_name");
                String musicPath = array.getJSONObject(i).getString("path");
                mainpageListItems.add(new MainpageListItem(title, userNickname, musicPath));
                //list.add(array.getJSONObject(i).getString("id"));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        task.cancel(true);
        //music listing
        MainpageList newList = new MainpageList(this, mainpageListItems);
        setListAdapter(newList);

    }






    class Connect extends AsyncTask<Void,Void,String> {

        //request data from server
        @Override
        protected String doInBackground(Void... params) {
            final String url = "http://192.168.0.103:3000/api/v1/main";
            //final String url = "https://voicewave.org/api/v1/main";

            DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
            HttpGet localHttpGet = new HttpGet(url);
            ResponseHandler<String> handler = new BasicResponseHandler();
            String response = "";
            try {
                response = localDefaultHttpClient.execute(localHttpGet, handler);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result){
            jsonStr = result;
        }

    }















}
