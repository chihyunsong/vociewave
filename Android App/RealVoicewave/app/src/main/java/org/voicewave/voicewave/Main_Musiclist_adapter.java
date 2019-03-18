package org.voicewave.voicewave;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Roy on 7/26/2015.
 */
public class Main_Musiclist_adapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<Main_Musiclist_Item> musicList;
    private static LayoutInflater inflater = null;
    private String imagePath;
    private int cur_record;
    private String token;
    private int user_id;
    private VoiceWaveApplication mApp;

    public Main_Musiclist_adapter(Context context, ArrayList<Main_Musiclist_Item> musicList, BtnClick clicked) {
        //super(context, R.layout.row_musiclist, musicList);
        this.context = context;
        this.musicList = musicList;
        this.clicked = clicked;
        token = LoginControl.getPrefUserKey(context);
        user_id = LoginControl.getPrefUserName(context);

        //inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /***
     * @Override public View getView(int position, View convertView, ViewGroup parent) {
     * View vi = convertView;
     * if(vi == null) {
     * vi = inflater.inflate(R.layout.row_musiclist, null);
     * }
     * TextView title = (TextView) vi.findViewById(R.id.musicTitle);
     * TextView username = (TextView) vi.findViewById(R.id.userName);
     * ImageView profileImg = (ImageView)vi.findViewById(R.id.profilePic);
     * title.setText(musicList.get(position).getTitle());
     * username.setText(musicList.get(position).getUsername());
     * <p/>
     * Object profilePic = musicList.get(position).getprofilePath();
     * <p/>
     * <p/>
     * profileImg.setImageResource(R.drawable.defaultprofile);
     * <p/>
     * <p/>
     * <p/>
     * return vi;
     * }
     */

    @Override
    public int getGroupCount() {
        return musicList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<Main_ExpandList_Item> detail = musicList.get(groupPosition).getItems();
        return detail.size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return musicList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<Main_ExpandList_Item> detail = musicList.get(groupPosition).getItems();
        return detail.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Main_Musiclist_Item item = (Main_Musiclist_Item) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.row_musiclist, null);

        }
        TextView title = (TextView) convertView.findViewById(R.id.musicTitle);
        TextView username = (TextView) convertView.findViewById(R.id.userName);
        title.setText(item.getTitle());
        username.setText(item.getUsername());
        ImageView profileImg = (ImageView) convertView.findViewById(R.id.profilePic);

        Bitmap profilePic = (Bitmap) item.getprofilePath();

        if (item.getprofilePath() == null) {
            profileImg.setImageResource(android.R.color.transparent);
        } else {
            profileImg.setImageBitmap(profilePic);
        }


        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Main_ExpandList_Item item = (Main_ExpandList_Item) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expand_musiclist, null);
        }

        final ImageButton openRecordBtn = (ImageButton) convertView.findViewById(R.id.detailBtn);
        openRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main_Musiclist_Item temp = (Main_Musiclist_Item) getGroup(groupPosition);
                if (clicked != null) {
                    clicked.onBtnClick(temp.getRecord_id());

                }

            }
        });
        ImageButton likeBtn = (ImageButton) convertView.findViewById(R.id.likeBtn);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main_Musiclist_Item temp = (Main_Musiclist_Item) getGroup(groupPosition);
                postLike(temp.getRecord_id());
            }
        });


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private BtnClick clicked = null;

    public interface BtnClick {
        public abstract void onBtnClick(int record_id);
    }

    public void postLike(int record_id) {
        cur_record = record_id;
        new incLike().execute();
    }

    class incLike extends AsyncTask<Void, Void, String> {
        private HttpURLConnection urlConnection;

        //request data from server
        @Override
        protected String doInBackground(Void... params) {
            // got this from http://blog.naver.com/javaking75/140196018323

            urlConnection = null;
            String url = "https://voicewave.org";
            String str = "";
            InputStream content;

            String record_id = String.valueOf(cur_record);
            try {
                String addUrl = "/api/v1/increment_like?user_id=" + String.valueOf(user_id) + "&token=" + token + "&record_id=" + record_id;

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
                JSONObject a = new JSONObject(result);
                String msg = a.getString("message");
                if (msg.startsWith("error")){
                    Toast.makeText(context, R.string.like_fail, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context, R.string.like_success, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }


    }
}

