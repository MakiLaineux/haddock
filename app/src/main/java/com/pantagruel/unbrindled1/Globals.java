package com.pantagruel.unbrindled1;

import android.app.Application;
import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;

/**
 * Created by MAKI LAINEUX on 18/03/2016.
 */
public class Globals extends Application {

    public static final String TAG = "STATEMENTS";  //pour debug
    public static Globals context;

    public static final String EXTRA_ID = "com.pantagruel.unbrindled.ID";
    public static final String EXTRA_TEXT = "com.pantagruel.unbrindled.TEXT";
    public static final String EXTRA_PROFILE = "com.pantagruel.unbrindled.PROFILE";
    public static final String EXTRA_STATUS = "com.pantagruel.unbrindled.STATUS";

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

    // Named values
    public static final int NOID = -99;
    public static final String NOTEXT = "NO TEXT";

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}