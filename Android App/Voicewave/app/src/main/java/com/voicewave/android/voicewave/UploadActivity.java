package com.voicewave.android.voicewave;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UploadActivity extends ListActivity {


    private String path;
    private String sourcePath;
    private TextView strPath;
    private List values = new ArrayList();

    private Button test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Use the current directory as title
        sourcePath=Environment.getExternalStorageDirectory().getAbsolutePath();
        path = sourcePath;

        test = (Button) findViewById(R.id.tester);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadActivity.this, RecorderActivity.class);

                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });



        if (getIntent().hasExtra("path")) {
            Log.i("------------------","extra path= "+getIntent().getStringExtra("path"));
            path = getIntent().getStringExtra("path");
            //name = getIntent().getStringExtra("name");

            strPath = (TextView) findViewById(R.id.pathfinder);
            strPath.setText("내 디바이스" + path.replace(sourcePath, ""));


            values = checkForMusic(path, values);
            Collections.sort(values);


        }else {
            strPath = (TextView) findViewById(R.id.pathfinder);
            strPath.setText("내 디바이스" + path.replace(sourcePath, ""));
            //setTitle(path);


            // Read all files sorted into the values-array
            values = checkForMusicDir(path, values);
            Collections.sort(values);
        }

        List cleanValues = new ArrayList();
        for(Object temp : values){
            String filename= temp.toString();
            filename = filename.substring(filename.lastIndexOf("/")+1,filename.length());
            cleanValues.add(filename);
        }



        // Put the data into the list
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, cleanValues);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String filename = (String) getListAdapter().getItem(position);

        for (Object temp : values){
            String path = temp.toString();
            if (filename.equals(path.substring(path.lastIndexOf("/")+1,path.length()))){
                if (new File(path).isDirectory()) {
                    Intent intent = new Intent(this, UploadActivity.class);
                    intent.putExtra("path", path);

                    startActivity(intent);
                } else {
                    Intent intent = new Intent(UploadActivity.this, PlayerActivity.class);
                    intent.putExtra("musicTitle", path);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }
    }

    private List checkForMusicDir(String path, List values){
        File dir = new File(path);
        if (!dir.canRead()) {
            setTitle(getTitle() + " (inaccessible)");
        }

        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                //exclude hidden files
                if (!file.startsWith(".")) {
                    if (file.matches(".*\\.(mp3|m4a|wav|mid|ogg|wma)")) {
                        if (!values.contains(path)) {
                            values.add(path);
                        }
                    } else {
                        if (new File(path + File.separator + file).isDirectory()) {
                            values = checkForMusicDir(path + File.separator + file, values);
                        }
                    }
                }
            }
        }
        return values;
    }

    private List checkForMusic(String path, List values){
        File dir = new File(path);
        if (!dir.canRead()) {
            setTitle(getTitle() + " (inaccessible)");
        }

        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                //exclude hidden files
                if (!file.startsWith(".")) {
                    if (file.matches(".*\\.(mp3|m4a|wav|mid|ogg|wma)")) {
                        if (!values.contains(path)) {
                            values.add(path+File.separator+file);
                        }
                    }
                }
            }
        }
        return values;
    }


    public void goBack(View v){
        finish();
    }
}