package org.voicewave.voicewave;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

public class UploadActivity extends AppCompatActivity {


    private UploadListFragment albumFragment;

    private FragmentManager fm = getSupportFragmentManager();

    //boolean indication for album is opened or not
    //at default its false
    private boolean dirOpened = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(R.string.app_voiceUpload);

        albumFragment = new UploadListFragment();


        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragmentContainer, albumFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_record) {
            Intent intent = new Intent(this, RecorderActivity.class);
            startActivity(intent);
            finish();
        }
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

    public void setDirOpened(){
        dirOpened =true;
    }

    @Override
    public void onBackPressed(){
        if (dirOpened){
            dirOpened = false;
            ((UploadListFragment) fm.findFragmentById(R.id.fragmentContainer)).onBackCalled();
        }else{
            finish();
        }

    }
}
