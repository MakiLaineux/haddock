package com.pantagruel.megaoutrage;

import android.app.Application;

import com.pantagruel.megaoutrage.data.Statement;
import com.pantagruel.megaoutrage.data.StatementDatabase;
import com.pantagruel.megaoutrage.data.Profile;

/**
 * Created by MAKI LAINEUX on 18/03/2016.
 */
public class App extends Application {

    public static final String TAG = "STATEMENTS";  //pour debug

    // Named values
    public static final int NOID = -99;

    public static Profile sCurrentProfile = new Profile();
    public static int sScope = 0;
    public static StatementDatabase sBaseLocale = null;

    @Override
    public void onCreate() {
        super.onCreate();

        //read profile preferences
        sCurrentProfile.feedFromPreferences(this);

        //open database
        if (sBaseLocale == null) {
            sBaseLocale = new StatementDatabase(this);
            sBaseLocale.open();
        }
    }

    static public void setCurrentProfile(Profile p){
        sCurrentProfile =p;}
    static public void setScope(boolean b){
        sScope = b ? Statement.SCOPE_FAVORITES : Statement.SCOPE_ALL;
    }
    static public void toggleScope(){
        sScope = (sScope == Statement.SCOPE_FAVORITES) ? Statement.SCOPE_ALL : Statement.SCOPE_FAVORITES;
    }
}