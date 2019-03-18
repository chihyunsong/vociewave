package com.voicewave.android.voicewave;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;



public class LoginActivity extends FragmentActivity {

    private String email,password,jsonStr , nickname,passwordConfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


    }



    public void loginPopup(View view) {
       final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.popup_login);
        dialog.setTitle("로그인");

        Button loginBtn = (Button)dialog.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailET = (EditText)dialog.findViewById(R.id.emailEditText);
                EditText passwordET = (EditText)dialog.findViewById(R.id.passwordEditText);
                TextView informT = (TextView)dialog.findViewById(R.id.informText);

                email = emailET.getText().toString();
                password = passwordET.getText().toString();

                if(email.equals("")){
                    informT.setText("보이스웨이브 아이디를 입력해주세요.");
                }else if(password.equals("")) {
                    informT.setText("비밀번호를 입력하세요.");
                }else {

                    AsyncTask task = new LoginNetworking().execute();

                    try {
                        jsonStr = (String) task.get();
                        task.cancel(true);
                        JSONObject object = new JSONObject(jsonStr);
                        if (object.getString("message").equals("Authentication success")) {
                            JSONObject dataObj = object.getJSONObject("data");
                            int userid = dataObj.getInt("user_id");
                            String key = dataObj.getString("key");
                            LoginControl.setUser(LoginActivity.this,key,userid);
                            //go back to mainpage + cannot come back to this page- flagged
                            Intent mainIntent = new Intent(LoginActivity.this, MainPageActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(mainIntent);
                        } else {
                            informT.setText("로그인 정보를 확인해주세요.");
                        }


                        Log.i("testing result ", object.getString("message"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        });
        dialog.show();

    }

    class LoginNetworking extends AsyncTask<Void,Void,String>{
        private final String url = "http://192.168.0.103:3000/api/v1/sessions";
        @Override
        protected String doInBackground(Void... params) {
            String resultSet ="";

            DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
            HttpPost localHttpPost = new HttpPost(url);
            MultipartEntity localMultipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            try {
                localMultipartEntity.addPart("email",new StringBody(email));
                localMultipartEntity.addPart("password", new StringBody(password));

                localHttpPost.setEntity(localMultipartEntity);
                HttpResponse response = localDefaultHttpClient.execute(localHttpPost);
                Log.i("testing connection ", "responsecode"+response.getStatusLine().getStatusCode());

                resultSet = EntityUtils.toString(response.getEntity());
                if (resultSet != null) {
                    Log.i("RESPONSE :::: ", resultSet);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultSet;

        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

        }
    }

    public void signinPopup(View view){
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.popup_signin);
        dialog.setTitle("회원가입");

        Button signinBtn = (Button)dialog.findViewById(R.id.signinBtn);
        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailET = (EditText)dialog.findViewById(R.id.emailEditText);
                EditText nicknameET = (EditText)dialog.findViewById(R.id.nicknameEditText);
                EditText passwordET = (EditText)dialog.findViewById(R.id.passwordEditText);
                EditText passwordConfirmEt = (EditText)dialog.findViewById(R.id.passwordConfirmEditText);
                TextView informEmail = (TextView)dialog.findViewById(R.id.informEmail);
                TextView informNickname = (TextView)dialog.findViewById(R.id.informNickname);
                TextView informPassword = (TextView)dialog.findViewById(R.id.informPassword);
                TextView informPasswordConfirm = (TextView)dialog.findViewById(R.id.informPasswordConfirm);

                email = emailET.getText().toString();
                nickname = nicknameET.getText().toString();
                password = passwordET.getText().toString();
                passwordConfirm = passwordConfirmEt.getText().toString();

                boolean condition = true;

                if(email.equals("")) {
                    informEmail.setText("올바른 이메일을 입력해주세요.");
                    condition = false;
                    Log.i("condition test1", Boolean.toString(condition));
                }else{informEmail.setText("");}

                if(nickname.equals("")|| nickname.length()<2){
                    informNickname.setText("닉네임은 2글자 이상이어야 합니다.");
                    condition = false;
                    Log.i("condition test2", Boolean.toString(condition));
                }else{informNickname.setText("");}

                if(password.equals("")|| password.length()<8) {
                    Log.i("password length", String.valueOf(password.length()));
                    informPassword.setText("비밀번호는 8글자 이상이어야 합니다.");
                    condition = false;
                    Log.i("condition test3", Boolean.toString(condition));
                }else{informPassword.setText("");}

                if(!password.equals(passwordConfirm)){
                    informPasswordConfirm.setText("비밀번호가 동일하지 않습니다.");
                    condition = false;
                    Log.i("condition test4", Boolean.toString(condition));
                }else{informPasswordConfirm.setText("");}

                Log.i("condition test", Boolean.toString(condition));
                if (condition){
                    Log.i("asyncTask test","entering asynctask");
                    AsyncTask task = new SigninNetworking().execute();

                    try {
                        jsonStr = (String) task.get();
                        task.cancel(true);
                        JSONObject object = new JSONObject(jsonStr);
                        if (object.getString("message").equals("Authentication success")) {
                            JSONObject dataObj = object.getJSONObject("data");
                            int userid = dataObj.getInt("user_id");
                            String key = dataObj.getString("key");
                            LoginControl.setUser(LoginActivity.this,key,userid);

                            //go back to mainpage + cannot come back to this page- flagged
                            Intent mainIntent = new Intent(LoginActivity.this, MainPageActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(mainIntent);

                        } else {

                        }


                        Log.i("testing result ", object.getString("message"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        });
        dialog.show();

    }


    class SigninNetworking extends AsyncTask<Void,Void,String>{
        private final String url = "http://192.168.0.103:3000/api/v1/users";
        @Override
        protected String doInBackground(Void... params) {
            String resultSet ="";

            DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
            HttpPost localHttpPost = new HttpPost(url);
            MultipartEntity localMultipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            try {
                localMultipartEntity.addPart("email",new StringBody(email));
                localMultipartEntity.addPart("nick_name", new StringBody(nickname));
                localMultipartEntity.addPart("password", new StringBody(password));
                localMultipartEntity.addPart("password_confirmation", new StringBody(passwordConfirm));

                localHttpPost.setEntity(localMultipartEntity);
                HttpResponse response = localDefaultHttpClient.execute(localHttpPost);
                Log.i("testing connection ", "responsecode"+response.getStatusLine().getStatusCode());

                resultSet = EntityUtils.toString(response.getEntity());
                if (resultSet != null) {
                    Log.i("RESPONSE :::: ", resultSet);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultSet;

        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

        }
    }

}
