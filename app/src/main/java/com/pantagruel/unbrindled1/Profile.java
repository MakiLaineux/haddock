package com.pantagruel.unbrindled1;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by MAKI LAINEUX on 17/01/2018.
 */

public class Profile {

    // must match the number of boolean members of the class
    public static final int NB_CHECKBOX = 13;

    // order of the flags : nature flags, then style flags, then criteria flags
    public static final int NB_CHECKBOX_NATURE = 3;
    public static final int NB_CHECKBOX_STYLE = 3;
    public static final int NB_CHECKBOX_CRITERIA = 7;
    public static final String CHECKBOXNAME = "checkbox";

    private boolean[] flag = new boolean[NB_CHECKBOX];

    public Profile(){
    }

    public Profile(boolean[] boolArray){
        this.flag = boolArray;
    }

    public boolean isChecked(int i){return flag[i];}
    public void setChecked(int i, boolean b){flag[i]=b;}

    public void feedFromString (String s){
        if (s.length() != NB_CHECKBOX) {
            Log.e(App.TAG, "feedFromString : String length does not match Profile length : Z" + s + "Z" );
            return;           
        }
        else
            Log.d(App.TAG, "feedFromString : Profile : Z" + s + "Z");
        for (int i =0 ; i < NB_CHECKBOX ; i++)
            this.flag[i] = (s.charAt(i) == 'X');
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
            buffer.append(flag[i] ? 'X' : ' ');

        Log.d(App.TAG, "toString : Text : Z" + buffer.toString() + "Z");
        
        return(buffer.toString());
    }

    // build profile from user preferences
    public void feedFromPreferences(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(App.context);
        for (int i = 0; i<Profile.NB_CHECKBOX ; i++) {
            String prefName = App.PROFILE_BOOL + Integer.toString(i);
            flag[i] = sharedPref.getBoolean(prefName, true);
        }
    }


    public boolean matches(Profile p){
        // returns true if natures match, styles also match, and criterias also match

        int j=0;

        // at least one nature must match
        boolean match = false;
        for (int i =0 ; i < NB_CHECKBOX_NATURE ; i++) {
            if (flag[j] && p.flag[j]) match = true;
            j++;
        }
        if (!match) return false;


        // at least one style must match
        match = false;
        for (int i =0 ; i < NB_CHECKBOX_STYLE ; i++) {
            if (flag[j] && p.flag[j]) match = true;
            j++;
        }
        if (!match) return false;

        // at least one criteria must match
        match = false;
        for (int i =0 ; i < NB_CHECKBOX_CRITERIA ; i++) {
            if (flag[j] && p.flag[j]) match = true;
            j++;
        }
        return match;
    }
}

