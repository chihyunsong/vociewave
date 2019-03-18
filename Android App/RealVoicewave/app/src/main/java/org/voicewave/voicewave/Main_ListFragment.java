package org.voicewave.voicewave;

import android.app.Activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Roy on 7/26/2015.
 */
public class Main_ListFragment extends Fragment{


    private String url = "https://voicewave.org/";
    private Main_Musiclist_adapter adapter;
    private ExpandableListView lv;
    private View current;
    private String jsonStr;
    private ArrayList<Main_Musiclist_Item> musicList;
    private int currentPos ;
    private Spinner genre;
    private TextView popular, recent;
    private String optionValue;
    private String option;
    private String category;
    private ProgressBar loading;

    private String profilePath;

    //new test variable
    private Main_Musiclist_adapter ExpAdapter;
    private int lastExpandedPosition = -1;


    private int receivedID;


    public int getPosition(){
        return currentPos;
    }

    public String getPlayerTitle(int position){
        String title = musicList.get(position).getTitle();
        return title;
    }
    public int getClickedItemID(int position){
        return  position;
    }
    public String getMusicPath(int position){
        String path = musicList.get(position).getMusicPath();
        return path;
    }
    public int getRecordId(int position){
        return musicList.get(position).getRecord_id();
    }

    private ArrayList<Main_Musiclist_Item> createList(String jsonStr) throws JSONException {
        ArrayList<Main_Musiclist_Item> musicList = new ArrayList<Main_Musiclist_Item>();
        JSONObject object = new JSONObject(jsonStr);
        JSONArray array = object.getJSONArray("data");



        for(int i = 0; i<array.length(); i++){
            int id = array.getJSONObject(i).getInt("id");
            String title = array.getJSONObject(i).getString("title");
            String userNickname = array.getJSONObject(i).getString("user_nick_name");
            String musicPath = array.getJSONObject(i).getString("path");
            profilePath = array.getJSONObject(i).getString("user_profile_path");
            //``````````
            ArrayList<Main_ExpandList_Item> ch_list = new ArrayList<Main_ExpandList_Item>();
            Main_ExpandList_Item newExp = new Main_ExpandList_Item();
            ch_list.add(newExp);
            //`````
            musicList.add(new Main_Musiclist_Item(title,userNickname,musicPath,null, id, ch_list));



/***            AsyncTask newTask = new GetImage().execute();
            try {
                Object profilePic = newTask.get();
                musicList.add(new Main_Musiclist_Item(title,userNickname,musicPath,profilePic));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
*/
        }






        return musicList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        category = "all";
        option = "recent";
        optionValue = "true";
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        current = inflater.inflate(R.layout.fragment_main_list,container,false);
        lv= (ExpandableListView)current.findViewById(R.id.listView);
        genre = (Spinner)current.findViewById(R.id.genre);
        loading = (ProgressBar)current.findViewById(R.id.loading);
        loading.setVisibility(View.GONE);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
               getActivity(), R.array.genre,android.R.layout.simple_spinner_dropdown_item);
        genre.setAdapter(adapter);


        popular = (TextView)current.findViewById(R.id.popularity);
        recent = (TextView)current.findViewById(R.id.recency);
        recent.setTextColor(Color.parseColor("#FFF44336"));



        return current;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        new Connect().execute();
        lv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

                customListener.onClicked(getPlayerTitle(groupPosition), getMusicPath(groupPosition), getRecordId(groupPosition));
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    lv.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });








        // Listener for category selected
        category =genre.getSelectedItem().toString();
        if(category.equals(getResources().getString(R.string.all))){
            category = "all";
        }else if(category.equals(getResources().getString(R.string.song))){
            category = "song";
        }else if(category.equals(getResources().getString(R.string.compose))){
            category = "compose";
        }else if(category.equals(getResources().getString(R.string.instrumental))){
            category = "instrumental";
        }else if(category.equals(getResources().getString(R.string.voiceact))){
            category = "voiceact";
        }else if(category.equals(getResources().getString(R.string.humor))){
            category = "humor";
        }else {
            category = "etc";
        }


        genre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category =genre.getItemAtPosition(position).toString();
                if(category.equals(getResources().getString(R.string.all))){
                    category = "all";
                }else if(category.equals(getResources().getString(R.string.song))){
                    category = "1";
                }else if(category.equals(getResources().getString(R.string.compose))){
                    category = "2";
                }else if(category.equals(getResources().getString(R.string.instrumental))){
                    category = "3";
                }else if(category.equals(getResources().getString(R.string.voiceact))){
                    category = "4";
                }else if(category.equals(getResources().getString(R.string.humor))){
                    category = "5";
                }else {
                    category = "6";
                }

                new Connect().execute();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });




        //listeners for button pressed indication
        popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), popular);

                popup.getMenuInflater().inflate(R.menu.menu_popularity, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle() == getResources().getString(R.string.today)) {
                            optionValue = "day";
                            popular.setTextColor(Color.parseColor("#FFF44336"));
                            recent.setTextColor(Color.BLACK);
                        } else if (item.getTitle() == getResources().getString(R.string.week)) {
                            optionValue = "week";
                            popular.setTextColor(Color.parseColor("#FFF44336"));
                            recent.setTextColor(Color.BLACK);
                        } else {
                            optionValue = "all";
                            popular.setTextColor(Color.parseColor("#FFF44336"));
                            recent.setTextColor(Color.BLACK);
                        }
                        option = "popularity";
                        new Connect().execute();

                        return true;
                    }
                });
                popup.show();
            }
        });
        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recent.setTextColor(Color.parseColor("#FFF44336"));
                popular.setTextColor(Color.BLACK);
                option = "recent";
                optionValue = "true";
                new Connect().execute();

                lv.setAdapter(new Main_Musiclist_adapter(current.getContext(), musicList, new Main_Musiclist_adapter.BtnClick() {
                    @Override
                    public void onBtnClick(int record_id) {
                        receivedID = record_id;
                        openRecordpage.onClickRecordOpen(record_id);
                    }
                }));

            }
        });
    }






    //custom listener for comunication between activity and this fragment -http://here4you.tistory.com/51
    private CustomOnClickListener customListener;


    public interface CustomOnClickListener {
        public void onClicked(String title, String musicPath, int record_id);
    }

    private openRecord openRecordpage;

    public interface openRecord{
        public void onClickRecordOpen(int recordid);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        customListener =(CustomOnClickListener)activity;
        openRecordpage = (openRecord)activity;
    }



    class Connect extends AsyncTask<Void,Void,String> {

        @Override
        protected  void onPreExecute(){
            loading.setVisibility(View.VISIBLE);
        }

        //request data from server
        @Override
        protected String doInBackground(Void... params) {
            // got this from http://blog.naver.com/javaking75/140196018323
            BufferedReader bufreader=null;
            HttpURLConnection urlConnection = null;

            StringBuffer page=new StringBuffer();
            try {
                String addUrl = "api/v1/main/?category="+category+"&"+option+"="+ optionValue;
                URL musicPathUrl = new URL(url+addUrl);
                urlConnection =(HttpsURLConnection) musicPathUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                String s = String.valueOf(urlConnection.getResponseCode());
                InputStream contentStream =urlConnection.getInputStream();
                bufreader = new BufferedReader(new InputStreamReader(contentStream,"UTF-8"));
                String line = null;
                while((line = bufreader.readLine())!=null){
                    page.append(line);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    bufreader.close();
                    urlConnection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return page.toString();
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try {
                musicList = createList(result);
                //lv.setAdapter(new Main_Musiclist_adapter(current.getContext(), musicList));
                //new test---------------------------------

                ExpAdapter = new Main_Musiclist_adapter(getActivity(), musicList, new Main_Musiclist_adapter.BtnClick() {
                    @Override
                    public void onBtnClick(int record_id) {
                        receivedID = record_id;
                        openRecordpage.onClickRecordOpen(receivedID);
                    }
                });
                lv.setAdapter(ExpAdapter);
                loading.setVisibility(View.GONE);

                //end new test---------------------------------
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }


}
