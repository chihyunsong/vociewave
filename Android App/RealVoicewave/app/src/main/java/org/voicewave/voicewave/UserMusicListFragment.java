package org.voicewave.voicewave;

import android.app.Activity;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

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
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Roy on 7/26/2015.
 */
public class UserMusicListFragment extends android.support.v4.app.Fragment {

    private String url = "https://voicewave.org/";
    private ExpandableListView lv;
    private View current;
    private ArrayList<Main_Musiclist_Item> musicList;
    private int lastExpandedPosition;

    private int user_id;
    private String profilePath;
    private int uid;
    private String token;
    private Bundle args;
    private Main_Musiclist_adapter ExpAdapter;
    private CustomOnClickListener customListener;


    private String pic;
    private int receivedID;

    public String getPlayerTitle(int position) {
        String title = musicList.get(position).getTitle();
        return title;
    }

    public int getRecord(int position){
        return musicList.get(position).getRecord_id();
    }

    public String getMusicPath(int position) {
        String path = musicList.get(position).getMusicPath();
        return path;
    }

    private ArrayList<Main_Musiclist_Item> createList(String jsonStr) throws JSONException {
        ArrayList<Main_Musiclist_Item> musicList = new ArrayList<Main_Musiclist_Item>();
        JSONObject object = new JSONObject(jsonStr);
        JSONArray array = object.getJSONArray("data");
        if (pic.equals("")) {
            for (int i = 0; i < array.length(); i++) {
                int id = array.getJSONObject(i).getInt("id");
                String title = array.getJSONObject(i).getString("title");
                String userNickname = array.getJSONObject(i).getString("user_nick_name");
                String musicPath = array.getJSONObject(i).getString("path");
                profilePath = array.getJSONObject(i).getString("user_profile_path");

                ArrayList<Main_ExpandList_Item> ch_list = new ArrayList<Main_ExpandList_Item>();
                Main_ExpandList_Item newExp = new Main_ExpandList_Item();
                ch_list.add(newExp);
                //`````
                musicList.add(new Main_Musiclist_Item(title,userNickname,musicPath,null,id, ch_list));
            }

        } else {
            AsyncTask newTask = new GetImage().execute();
            try {
                Object profilePic = newTask.get();
                for (int i = 0; i < array.length(); i++) {
                    int id = array.getJSONObject(i).getInt("id");
                    String title = array.getJSONObject(i).getString("title");
                    String userNickname = array.getJSONObject(i).getString("user_nick_name");
                    String musicPath = array.getJSONObject(i).getString("path");
                    profilePath = array.getJSONObject(i).getString("user_profile_path");


                    ArrayList<Main_ExpandList_Item> ch_list = new ArrayList<Main_ExpandList_Item>();
                    Main_ExpandList_Item newExp = new Main_ExpandList_Item();
                    ch_list.add(newExp);
                    //`````
                    musicList.add(new Main_Musiclist_Item(title,userNickname,musicPath,profilePic, id, ch_list));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }


        return musicList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = LoginControl.getPrefUserName(getActivity());
        token = LoginControl.getPrefUserKey(getActivity());
        args = getArguments();
        user_id = args.getInt("user_id");
        pic = args.getString("pic");
        lastExpandedPosition = -1;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        current = inflater.inflate(R.layout.fragment_user_music_list_pragment, container, false);
        lv = (ExpandableListView) current.findViewById(R.id.userMusicListView);
        new Connect().execute();


        return current;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        lv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                customListener.onClicked(getPlayerTitle(groupPosition), getMusicPath(groupPosition), getRecord(groupPosition));
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    lv.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
        lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ImageButton testbtn = (ImageButton) v.findViewById(R.id.likeBtn);
                testbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                return true;
            }
        });


    }

    //custom listener for comunication between activity and this fragment -http://here4you.tistory.com/51


    public interface CustomOnClickListener {
        public void onClicked(String title, String musicPath, int record_id);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        customListener =(CustomOnClickListener)activity;
        openRecordpage = (openRecord)activity;
    }
    private openRecord openRecordpage;
    public interface openRecord{
        public void onClickRecordOpen(int recordid);

    }

    class Connect extends AsyncTask<Void, Void, String> {

        //request data from server
        @Override
        protected String doInBackground(Void... params) {
            // got this from http://blog.naver.com/javaking75/140196018323
            BufferedReader bufreader = null;
            HttpURLConnection urlConnection = null;

            StringBuffer page = new StringBuffer();
            try {
                String addUrl = "api/v1/users/" + String.valueOf(user_id) + "/?" + "type=voice_list&user_id=" +
                        String.valueOf(uid) + "&token=" + token;
                URL musicPathUrl = new URL(url + addUrl);
                urlConnection = (HttpsURLConnection) musicPathUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                String s = String.valueOf(urlConnection.getResponseCode());
                InputStream contentStream = urlConnection.getInputStream();
                bufreader = new BufferedReader(new InputStreamReader(contentStream, "UTF-8"));
                String line = null;
                while ((line = bufreader.readLine()) != null) {
                    page.append(line);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                musicList = createList(result);
                //lv.setAdapter(new Main_Musiclist_adapter(current.getContext(), musicList));
                //new test---------------------------------

                ExpAdapter = new Main_Musiclist_adapter(getActivity(),musicList, new Main_Musiclist_adapter.BtnClick() {
                    @Override
                    public void onBtnClick(int record_id) {
                        receivedID = record_id;
                        openRecordpage.onClickRecordOpen(receivedID);
                    }
                });
                lv.setAdapter(ExpAdapter);

                //end new test---------------------------------apter(current.getContext(), musicList));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }
    class GetImage extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {

                //InputStream is = (InputStream) new URL("https://voicewave.org/"+pic).getContent();
                //Drawable d = Drawable.createFromStream(is, "src name");

                Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL("https://voicewave.org/" + pic).getContent());
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
        @Override
        protected void onPostExecute(Bitmap result){
            super.onPostExecute(result);
        }
    }
}



