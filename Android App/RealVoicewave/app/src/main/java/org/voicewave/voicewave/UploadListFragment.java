package org.voicewave.voicewave;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UploadListFragment extends Fragment {
    //source Path of storage in phone
    private String sourcePath= Environment.getExternalStorageDirectory().getAbsolutePath();

    private ListView musicList;
    private View currentView;
    private List result = new ArrayList();
    private List onlyNames =  new ArrayList();
    private List allMusic =  new ArrayList();
    private List musicInDir = new ArrayList();
    private List musicFullPath = new ArrayList();

    private TextView album, track;
    private boolean albumClicked = true, trackClicked =false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        currentView = inflater.inflate(R.layout.fragment_upload_list, container, false);
        musicList = (ListView)currentView.findViewById(R.id.albumList);
        album = (TextView)currentView.findViewById(R.id.album);
        track = (TextView)currentView.findViewById(R.id.track);
        album.setTextColor(Color.parseColor("#FFF44336"));
        return currentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMusicDirs();
        String path = sourcePath;
        Collections.sort(result);
        onlyNames = showOnlyFileName(result);
        allMusic = getAllMusic(result);

        ArrayAdapter adapter = new ArrayAdapter(currentView.getContext(),android.R.layout.simple_list_item_2, android.R.id.text1,onlyNames);
        musicList.setAdapter(adapter);

        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                if (selectedItem.matches(".*\\.(mp3|m4a|wav|mid|ogg|wma)")) {
                    if (!trackClicked){
                        Intent intent = new Intent(getActivity(), UploadConfirmActivity.class);
                        intent.putExtra("path", musicInDir.get(position).toString());
                        startActivity(intent);
                        getActivity().finish();
                    }else{
                        Intent intent = new Intent(getActivity(), UploadConfirmActivity.class);
                        intent.putExtra("path", allMusic.get(position).toString());
                        startActivity(intent);
                        getActivity().finish();
                    }
                    // go to next intent here
                } else {
                    musicInDir = getMusicList((String) result.get(position));
                    ArrayAdapter adapter = new ArrayAdapter(currentView.getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, showOnlyFileName(musicInDir) );
                    musicList.setAdapter(adapter);
                    albumClicked = false;
                    //override function for on back activity
                    ((UploadActivity) getActivity()).setDirOpened();

                }
            }
        });

        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!albumClicked) {
                    album.setTextColor(Color.parseColor("#FFF44336"));
                    track.setTextColor(Color.BLACK);
                    ArrayAdapter adapter = new ArrayAdapter(currentView.getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, onlyNames);
                    musicList.setAdapter(adapter);
                    albumClicked = true;
                    trackClicked = false;
                }
            }
        });
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!trackClicked) {
                    track.setTextColor(Color.parseColor("#FFF44336"));
                    album.setTextColor(Color.BLACK);
                    ArrayAdapter adapter = new ArrayAdapter(currentView.getContext(), android.R.layout.simple_list_item_2, android.R.id.text1,  showOnlyFileName(allMusic));
                    musicList.setAdapter(adapter);
                    trackClicked = true;
                    albumClicked = false;
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getMusicDirs(){
        ContentResolver resolver = getActivity().getContentResolver();
        String[] folderColumn = {"distinct replace("+MediaStore.Audio.Media.DATA+","+ MediaStore.Audio.Media.DISPLAY_NAME+", '')",
        MediaStore.Audio.Media.ALBUM
        };


        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, folderColumn,
                null, null, null);

        if (cursor == null) {
            return;
        }

        while (cursor.moveToNext()){
            String filePath = cursor.getString(0);
            filePath = filePath.substring(filePath.indexOf('/', 1),
                    filePath.lastIndexOf('/'));

            result.add("/storage"+filePath);
        }
        cursor.close();
    }


    private List getMusicList (String path){
        List result =  new ArrayList();
        File dir = new File(path);
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {

                //exclude hidden files
                if (!file.startsWith(".")) {
                    if (file.matches(".*\\.(mp3|m4a|wav|mid|ogg|wma)")) {

                        if (!result.contains(file)) {
                            result.add(path +File.separator+ file);
                        }
                    }
                }
            }
        }
        return result;
    }

    private List getAllMusic(List storage){
        List all = new ArrayList();

        for (Object file : storage) {
            all.addAll(getMusicList((String) file));
        }

        return all;
    }

    private List showOnlyFileName(List list){
        //remove path description and get list that only consist of filename
        List fileNameList = new ArrayList();
        for(Object temp: list){
            String filename =temp.toString();
            filename = filename.substring(filename.lastIndexOf("/")+1,filename.length());
            fileNameList.add(filename);
        }
        return fileNameList;
    }

    public void onBackCalled () {
        ArrayAdapter adapter = new ArrayAdapter(currentView.getContext(),android.R.layout.simple_list_item_2, android.R.id.text1,onlyNames);
        musicList.setAdapter(adapter);
    }


}