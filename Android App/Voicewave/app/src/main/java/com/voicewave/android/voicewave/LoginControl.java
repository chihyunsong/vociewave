package com.voicewave.android.voicewave;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Roy on 7/22/2015.
 */
public class LoginControl {

    static  final  String PREF_USER_NAME = "username";
    static final String PREF_USER_KEY = "";
    static SharedPreferences getSharedPreferences(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUser(Context ctx, String key, int userid){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, key);
        editor.putInt(PREF_USER_KEY, userid);
        editor.commit();
    }
    public static String getPrefUserName(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_USER_NAME,"");
    }
    public static int getPrefUserKey(Context ctx){
        return getSharedPreferences(ctx).getInt(PREF_USER_KEY,0);
    }


}
