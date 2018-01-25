package com.pantagruel.unbrindled1;

import android.app.Application;

/**
 * Created by MAKI LAINEUX on 18/03/2016.
 */
public class App extends Application {

    public static final String TAG = "STATEMENTS";  //pour debug

    public static final String EXTRA_ID = "com.pantagruel.unbrindled.ID";
    public static final String EXTRA_TEXT = "com.pantagruel.unbrindled.TEXT";
    public static final String EXTRA_PROFILE = "com.pantagruel.unbrindled.PROFILE";
    public static final String EXTRA_STATUS = "com.pantagruel.unbrindled.STATUS";
    public static final String EXTRA_ACTIVITY = "com.pantagruel.unbrindled.ACTIVITY";
    public static final String EXTRA_REQUEST = "com.pantagruel.unbrindled.REQUEST";

    // Database
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "statements";
    public static final String TABLE_STATEMENT = "table_statements";

    // Database Columns
    public static final int STATEMENT_NUM_COL_ID = 0;
    public static final int STATEMENT_NUM_COL_TEXT = 1;
    public static final int STATEMENT_NUM_COL_PROFILE = 2;
    public static final int STATEMENT_NUM_COL_STATUS = 3;
    public static final String STATEMENT_COL_ID = "_id";
    public static final String STATEMENT_COL_TEXT = "text";
    public static final String STATEMENT_COL_PROFILE = "profile";
    public static final String STATEMENT_COL_STATUS = "status";

    // Status conditions
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_MARKED = 1;
    public static final int STATUS_ALL = 2;
    public static final int STATUS_NONE = 3;

    // Favorites conditions
    public static final int SCOPE_ALL = 0;
    public static final int SCOPE_FAVORITES = 1;
    public static final int SCOPE_DELETED = 2;

    // Named values
    public static final int NOID = -99;
    public static final int REQUEST_NONE = 0;
    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;
    public static final int REQUEST_PROFILE = 3;
    public static final int REQUEST_LIST = 4;
    public static final int FROM_MAIN_ACTIVITY = 0;
    public static final int FROM_LIST_ACTIVITY = 1;

    public static final String NOTEXT = "NO TEXT";
    public static final String PROFILE_BOOL = "ProfileBool";

    public static App context;
    public static Profile currentProfile = new Profile();
    public static int scope = 0;

    public static BddLocale mBaseLocale = null;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        //read profile preferences
        currentProfile.feedFromPreferences();

        //open database
        if (mBaseLocale == null) {
            mBaseLocale = new BddLocale(this);
            mBaseLocale.open();
        }
    }

    static public void setCurrentProfile (Profile p){currentProfile=p;}
    static public void setScope(boolean b){scope = b ? SCOPE_FAVORITES : SCOPE_ALL;}
    static public void toggleScope(){scope = scope == SCOPE_FAVORITES ? SCOPE_ALL : SCOPE_FAVORITES;}
}