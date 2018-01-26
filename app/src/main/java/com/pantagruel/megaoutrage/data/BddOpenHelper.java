package com.pantagruel.megaoutrage.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pantagruel.megaoutrage.App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by MAKI LAINEUX on 16/01/2018.
 */

public class BddOpenHelper extends SQLiteOpenHelper {

    private final String TAG = this.getClass().getSimpleName();

    // string array of columns.
    private static final String[] COLUMNS = { App.STATEMENT_COL_ID, App.STATEMENT_COL_TEXT, App.STATEMENT_COL_PROFILE };
    private Context mContext;

    // Build the SQL query that creates the table.
    private static final String STATEMENT_LIST_TABLE_CREATE =
            "CREATE TABLE " + App.TABLE_STATEMENT + " (" +
                    App.STATEMENT_COL_ID + " INTEGER PRIMARY KEY, " +
                    App.STATEMENT_COL_TEXT + " TEXT, " +
                    App.STATEMENT_COL_PROFILE + " TEXT, " +
                    App.STATEMENT_COL_STATUS + " INTEGER );";

    public BddOpenHelper(Context context) {
        super(context, App.DATABASE_NAME, null, App.DATABASE_VERSION);
        mContext = context;

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STATEMENT_LIST_TABLE_CREATE);
        fillDatabaseWithData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "BDD Upgrade");
        db.execSQL("DROP TABLE IF EXISTS " + App.TABLE_STATEMENT);
        onCreate(db);
    }

    // Initial loading from Assets, only on database creation
    private boolean fillDatabaseWithData(SQLiteDatabase db) {
        String ligne; 		//ligne lue dans le fichier
        boolean bOK = false;
        ContentValues values = new ContentValues();

        try {
            BufferedReader buf = new BufferedReader(new InputStreamReader(mContext.getAssets().open("fill.txt"), "ISO-8859-1"));
            while ((ligne = buf.readLine()) != null) {
                if (ligne.length() <= Profile.NB_CHECKBOX) continue;
                String strProfile = ligne.substring(0,13);
                String strText = ligne.substring(13);
                Statement statement = new Statement(App.NOID, strText, strProfile, App.STATUS_NORMAL);
                values.put(App.STATEMENT_COL_TEXT, statement.getText());
                values.put(App.STATEMENT_COL_PROFILE, statement.getTextProfile());
                values.put(App.STATEMENT_COL_STATUS, App.STATUS_NORMAL);
                db.insert(App.TABLE_STATEMENT, null, values);
            }
            bOK=true;
            buf.close();

        } catch (ArrayIndexOutOfBoundsException e) {
        } catch (IOException e) {
        } catch (java.lang.NullPointerException e) {
        } catch (java.lang.RuntimeException e) {
        }
        return bOK;
        }

}
