package org.voicewave.voicewave;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;


public class AccountActivity extends Activity implements SelectGoogleAccountDialogFragment.SelectAccountDialogProtocol {
    private AccountManager mAccountManager;
    private ProgressDialog progress;
    public Context mContext;
    public Activity mActivity;
    private  LoginButton facebooklogin;
    private CallbackManager callbackManager;
    protected static final String TAG = "VoiceWave";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        VoiceWaveApplication mApp = (VoiceWaveApplication) getApplication();

        facebooklogin = (LoginButton) findViewById(R.id.facebook_login_button);
        callbackManager = CallbackManager.Factory.create();
        facebooklogin.setReadPermissions("email");
        facebooklogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                requestAPIKeyFromOAuthToken(loginResult.getAccessToken().getToken(),"facebook");
            }

            @Override
            public void onCancel() {
                // App code
            }


            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        mContext = this;
        mActivity = this;


        findViewById(R.id.google_login_button).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        selectAccount();
                    }
                });/*
        findViewById(R.id.facebook_login_button).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        facebook_signin();
                    }
                });
*/

        findViewById(R.id.login_button).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        final Dialog popupWindow = new Dialog(view.getContext());
                        popupWindow.setContentView(R.layout.activity_signin);
                        popupWindow.setTitle("로그인");
                        popupWindow.show();

                        popupWindow.findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                signin(popupWindow);
                            }
                        });


                        popupWindow.setCanceledOnTouchOutside(true);
                    }
                });


        findViewById(R.id.signup_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog popupWindow = new Dialog(view.getContext());
                        popupWindow.setContentView(R.layout.activity_signup);
                        popupWindow.setTitle("아이디 만들기");
                        popupWindow.show();

                        popupWindow.findViewById(R.id.btn_signup_signup).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                signup(popupWindow);
                            }
                        });
                        popupWindow.setCanceledOnTouchOutside(true);


                    }
                });
    }








    private void facebook_signin(){/**
    //NotifierHelper.displayToast(mContext, "TODO: onClick_facebookLogin", NotifierHelper.SHORT_TOAST);

        // https://developers.facebook.com/docs/reference/dialogs/oauth/

        webService = new WebService();

        String authRequestRedirect = AppContext.FB_APP_OAUTH_BASEURL+AppContext.FB_APP_OAUTH_URL
                + "?client_id="+AppContext.FB_APP_ID
                + "&response_type=token"
                + "&display=touch"
                + "&scope=" + TextUtils.join(",", AppContext.FB_PERMISSIONS)
                + "&redirect_uri="+AppContext.FB_APP_CALLBACK_OAUTHCALLBACK
                ;
        CookieSyncManager.createInstance(this);
        new FacebookOAuthDialog(mContext, authRequestRedirect
                , new GenericDialogListener() {
            public void onComplete(Bundle values) {
                // https://YOUR_REGISTERED_REDIRECT_URI/?code=CODE
                // onComplete->Bundle[{expires_in=0, access_token=<ACCESS_TOKEN>}]
/*
if user ALLOWs your app -> Bundle[{expires_in=0, access_token=<ACCESS_TOKEN>}]
if user doesNOT ALLOW -> Bundle[{error=access_denied, error_description=The+user+denied+your+request}]
 */
                // ensure any cookies set by the dialog are saved
        /**
                CookieSyncManager.getInstance().sync();

                String tokenResponse = "";
                try{

                    tokenResponse = values.toString();
                    JSONObject tokenJson = new JSONObject(tokenResponse.substring(7,tokenResponse.length()-1));
                    String token = tokenJson.getString("access_token");
                    requestAPIKeyFromOAuthToken(token,"facebook");



                }
                catch (Exception ex1){
                    tokenResponse = null;
                }
            }
            public void onError(String e) {
            }
            public void onCancel() {

            }
        }).show();*/

    }













    private void signup(Dialog view){
        //startActivity(new Intent(this,Signup.class));

        EditText emailET = (EditText)view.findViewById(R.id.edittext_signup_email);
        EditText password1ET = (EditText)view.findViewById(R.id.edittext_signup_password);
        EditText password2ET = (EditText)view.findViewById(R.id.editText_signup_re_password);
        EditText nicknameET = (EditText)view.findViewById(R.id.edittext_signup_nickname);
        String email = emailET.getText().toString();
        String password1 = password1ET.getText().toString();
        String password2 = password2ET.getText().toString();
        String nickname = nicknameET.getText().toString();

        if(!isValidEmail(email)){
            Toast.makeText(getApplicationContext(), R.string.no_email, Toast.LENGTH_SHORT).show();
        }
        else if(password1.length()<8){
            Toast.makeText(getApplicationContext(), R.string.password_length, Toast.LENGTH_SHORT).show();
        }
        else if(!password1.equals(password2)){
            Toast.makeText(getApplicationContext(), R.string.password_confirmation, Toast.LENGTH_SHORT).show();
        }
        else if(nickname.length()<2){
            Toast.makeText(getApplicationContext(), R.string.nickname_length, Toast.LENGTH_SHORT).show();
        }
        else{
            requestAPIKeyFromSignup(email, nickname, password1 ,password2);
        }

    }
    private static final Pattern EMAIL_ADDRESS  = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
    private void requestAPIKeyFromSignup(String email,String nickname, String password1, String password2) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams("email", email);
        params.put("nick_name",nickname);
        params.put("password",password1);
        params.put("password_confirmation",password2);
        progress = ProgressDialog.show(this, "Signup","Signup....", true);

        client.post("https://voicewave.org/api/v1/users", params, new AsyncHttpResponseHandler() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes, StandardCharsets.UTF_8);
                Toast.makeText(getApplicationContext(),R.string.signup_sucess,
                        Toast.LENGTH_LONG).show();
                progress.dismiss();
                finish();
            }
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                String str = new String(bytes, StandardCharsets.UTF_8);
                progress.dismiss();
                try {
                    JSONObject jsonObj = new JSONObject(str);

                    JSONObject err_msg = jsonObj.getJSONObject("message");
                    if(err_msg.has("nick_name")) {
                        Toast.makeText(getApplicationContext(), R.string.nickname_duplicate, Toast.LENGTH_LONG).show();
                    }
                    else if(err_msg.has("email")){
                        Toast.makeText(getApplicationContext(),R.string.email_duplicate, Toast.LENGTH_LONG).show();
                    }
                }
                catch(Exception e){
                }
                throwable.printStackTrace();
            }
        });
    }











    private void signin(Dialog popupWindow){


        EditText emailET = (EditText)popupWindow.findViewById(R.id.edit_text_email);
        EditText passwordET = (EditText)popupWindow.findViewById(R.id.edit_text_password);


        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        if(email.equals("")){
            Toast.makeText(getApplicationContext(), R.string.no_email, Toast.LENGTH_SHORT).show();
        }else if(password.equals("")) {
            Toast.makeText(getApplicationContext(), R.string.no_password, Toast.LENGTH_SHORT).show();
        }else {
            requestAPIKeyFromLogin(email,password);
        }

    }

    private void selectAccount() {
        mAccountManager = AccountManager.get(AccountActivity.this);
        Account[] accounts = mAccountManager.getAccountsByType("com.google");

        if (accounts.length == 0) {
            Toast.makeText(getApplicationContext(), R.string.no_account_found_message, Toast.LENGTH_SHORT).show();
        } else if (accounts.length == 1) {
            getTokenForAccount(accounts[0]);
        } else {
            String[] accountNames = new String[accounts.length];

            for (int i = 0; i < accounts.length; i++) {
                accountNames[i] = accounts[i].name;
            }

            DialogFragment newFragment = SelectGoogleAccountDialogFragment.newInstance(R.string.select_account_dialog_title, accountNames);
            newFragment.show(getFragmentManager(), "dialog");
        }
    }

    public void gotAccount(int index) {
        Account[] accounts = mAccountManager.getAccountsByType("com.google");
        getTokenForAccount(accounts[index]);
    }

    @Override
    public void onBackPressed() {
        // Exit gracefully by starting OS Home Activity
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    private void getTokenForAccount(Account account) {
        Bundle options = new Bundle();

        mAccountManager.getAuthToken(
                account,
                "oauth2:https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email",
                options,
                this,
                new OnTokenAcquired(),
                null
        );
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {

            Bundle bundle = null;
            try {
                bundle = result.getResult();

                String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                Intent launch = (Intent) result.getResult().get(AccountManager.KEY_INTENT);

                if (launch != null) {
                    startActivityForResult(launch, 0);
                    return;
                }

                requestAPIKeyFromOAuthToken(token,"google");
            } catch (OperationCanceledException e) {
                e.printStackTrace();
            } catch (AuthenticatorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    private void requestAPIKeyFromLogin(String email, String password) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams("email", email);
        params.put("password",password);
        progress = ProgressDialog.show(this, "Login","Login....", true);

        client.post("https://voicewave.org/api/v1/sessions", params, new AsyncHttpResponseHandler() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes, StandardCharsets.UTF_8);
                try {
                    JSONObject jsonObj = new JSONObject(str);
                    JSONObject dataObj = jsonObj.getJSONObject("data");
                    int userid = dataObj.getInt("user_id");
                    String key = dataObj.getString("key");

                    LoginControl.setUser(AccountActivity.this, key, userid);
                    Toast.makeText(getApplicationContext(),R.string.login_success,
                            Toast.LENGTH_LONG).show();
                    progress.dismiss();
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.dismiss();
                }
            }
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(),R.string.check_password, Toast.LENGTH_LONG).show();
                throwable.printStackTrace();
            }
        });
    }

    private void requestAPIKeyFromOAuthToken(String token, String type) {
        final String types = type;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams("token", token);
        params.put("type",type);
        progress = ProgressDialog.show(this, "Login","Login....", true);

        client.post("https://voicewave.org/api/v1/sessions", params, new AsyncHttpResponseHandler() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String str = new String(bytes, StandardCharsets.UTF_8);
                try {
                    JSONObject jsonObj = new JSONObject(str);
                    JSONObject dataObj = jsonObj.getJSONObject("data");
                    int userid = dataObj.getInt("user_id");
                    String key = dataObj.getString("key");
                    LoginControl.setUser(AccountActivity.this, key, userid);
                    if (types.equals("google")) {
                        Toast.makeText(getApplicationContext(),R.string.google_login_success,
                                Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),R.string.facebook_login_success,
                                Toast.LENGTH_LONG).show();

                    }

                    progress.dismiss();
                    LoginManager.getInstance().logOut();

                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.dismiss();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                progress.dismiss();
                Toast.makeText(getApplicationContext(), R.string.connection_error_message, Toast.LENGTH_LONG).show();
                throwable.printStackTrace();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode,  resultCode,  data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}