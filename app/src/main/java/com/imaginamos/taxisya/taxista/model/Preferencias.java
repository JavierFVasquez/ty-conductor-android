package com.imaginamos.taxisya.taxista.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by developer_celuvans on 11/04/17.
 */

public class Preferencias {
    private SharedPreferences sp;
    private SharedPreferences.Editor ed;

    public Preferencias(Context context) {
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
        // this.sharedPreferences = getPreferences(MODE_PRIVATE);
        this.ed = sp.edit();
    }

    public String LeePreferencias(String key) {
        return sp.getString(key, "");
    }

    public void GrabaPreferencias(String key, String value) {
        ed.putString(key, value);
        ed.commit();
    }

    public String getRootActivity() {
        String nameActivity = LeePreferencias("rootActivity");
        Log.v("ROOT_ACTIVITY", "get rootActivity " + nameActivity);
        return nameActivity;
    }

    public void setRootActivity(String nameActivity) {
        GrabaPreferencias("rootActivity", nameActivity);
        Log.v("ROOT_ACTIVITY", "set rootActivity " + nameActivity);
    }
}
