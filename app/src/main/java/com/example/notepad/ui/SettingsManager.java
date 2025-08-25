package com.example.notepad.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

public class SettingsManager {
    private static final String PREF = "note_prefs";
    private static final String KEY_FONT_SIZE = "font_size_sp";
    private static final String KEY_FONT_FAMILY = "font_family";
    private static final String KEY_SORT_MODE = "sort_mode"; // "created" or "updated"

    public static int getFontSizeSp(Context c){
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getInt(KEY_FONT_SIZE, 16);
    }

    public static void setFontSizeSp(Context c, int sp){
        c.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putInt(KEY_FONT_SIZE, sp).apply();
    }

    public static String getFontFamily(Context c){
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getString(KEY_FONT_FAMILY, "iransansdn_fa_num");
    }

    public static void setFontFamily(Context c, String family){
        c.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(KEY_FONT_FAMILY, family).apply();
    }

    public static Typeface getTypeface(Context c){
        String fam = getFontFamily(c);
        int id = c.getResources().getIdentifier(fam, "font", c.getPackageName());
        Typeface tf = null;
        if(id != 0){
            tf = ResourcesCompat.getFont(c, id);
        }
        return tf != null ? tf : Typeface.DEFAULT;
    }

    public static String getSortMode(Context c){
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY_SORT_MODE, "updated");
    }

    public static void setSortMode(Context c, String mode){
        c.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(KEY_SORT_MODE, mode).apply();
    }
}
