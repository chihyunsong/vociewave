package org.voicewave.voicewave;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by chihyun on 2015-07-29.
 */
public class LoginControl {

    static  final  String PREF_USER_NAME = "userid";
    static final String PREF_USER_KEY = "token";
    private static Context ctx;
    private static String key;
    private static int userid;

    static SharedPreferences getSharedPreferences(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUser(Context ctx, String key, int userid){
        LoginControl.ctx = ctx;
        LoginControl.key = key;
        LoginControl.userid = userid;
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(PREF_USER_NAME, userid);
        editor.putString(PREF_USER_KEY, key);
        editor.commit();
    }
    public static int getPrefUserName(Context ctx){
        return getSharedPreferences(ctx).getInt(PREF_USER_NAME, 0);
    }
    public static String getPrefUserKey(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_USER_KEY, "");
    }
    public static void logout(Context ctx){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(PREF_USER_NAME,-1);
        editor.putString(PREF_USER_KEY, "");
        editor.commit();
    }



}

