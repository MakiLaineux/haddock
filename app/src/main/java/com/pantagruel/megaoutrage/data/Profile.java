package com.pantagruel.megaoutrage.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.pantagruel.megaoutrage.App;

/**
 * Created by MAKI LAINEUX on 17/01/2018.
 */

public class Profile {

    public static final String PROFILE_BOOL = "ProfileBool";

    // must match the number of boolean members of the class
    public static final int NB_CHECKBOX = 13;
    public static final String CHECKBOXNAME = "checkbox";

    // order of the flags : nature flags, then style flags, then criteria flags
    private static final int NB_CHECKBOX_NATURE = 3;
    private static final int NB_CHECKBOX_STYLE = 3;
    private static final int NB_CHECKBOX_CRITERIA = 7;

    private final String TAG = this.getClass().getSimpleName();

    private boolean[] mFlag = new boolean[NB_CHECKBOX];

    public Profile(){}

    public Profile(boolean[] boolArray){
        this.mFlag = boolArray;
    }

    /**
     * Returns a string of fixed length. Each char represent a boolean member of the object.
     *
     * @return a string whose chars represent booleans, with true = 'X', false = ' '.
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(NB_CHECKBOX);
        for (int i =0 ; i < NB_CHECKBOX ; i++)
            buffer.append(mFlag[i] ? 'X' : ' ');

        Log.d(App.TAG, "toString : Text : Z" + buffer.toString() + "Z");

        return(buffer.toString());
    }

    public boolean isChecked(int i){return mFlag[i];}

    public void setChecked(int i, boolean b){mFlag[i]=b;}

    public void feedFromString (String s){
        if (s.length() != NB_CHECKBOX) {
            Log.e(App.TAG, "feedFromString : String length does not match Profile length : Z" + s + "Z" );
            return;           
        }
        else
            Log.d(App.TAG, "feedFromString : Profile : Z" + s + "Z");
        for (int i =0 ; i < NB_CHECKBOX ; i++)
            this.mFlag[i] = (s.charAt(i) == 'X');
    }

    // build profile from user preferences
    public void feedFromPreferences(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        for (int i = 0; i<Profile.NB_CHECKBOX ; i++) {
            String prefName = PROFILE_BOOL + Integer.toString(i);
            mFlag[i] = sharedPref.getBoolean(prefName, true);
        }
    }


    public boolean matches(Profile p){
        // returns true if natures match, styles also match, and criterias also match

        int j=0;

        // at least one nature must match
        boolean match = false;
        for (int i =0 ; i < NB_CHECKBOX_NATURE ; i++) {
            if (mFlag[j] && p.mFlag[j]) match = true;
            j++;
        }
        if (!match) return false;


        // at least one style must match
        match = false;
        for (int i =0 ; i < NB_CHECKBOX_STYLE ; i++) {
            if (mFlag[j] && p.mFlag[j]) match = true;
            j++;
        }
        if (!match) return false;

        // at least one criteria must match
        match = false;
        for (int i =0 ; i < NB_CHECKBOX_CRITERIA ; i++) {
            if (mFlag[j] && p.mFlag[j]) match = true;
            j++;
        }
        return match;
    }
}

