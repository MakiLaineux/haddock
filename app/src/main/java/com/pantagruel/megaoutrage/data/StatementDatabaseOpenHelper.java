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
 * Database Open Helper
 */
public class StatementDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = App.TAG + StatementDatabaseOpenHelper.class.getSimpleName();

    // string array of columns.
    private static final String[] COLUMNS = { StatementDatabase.STATEMENT_COL_ID, StatementDatabase.STATEMENT_COL_TEXT, StatementDatabase.STATEMENT_COL_PROFILE };
    private Context mContext;

    // Build the SQL query that creates the table.
    private static final String STATEMENT_LIST_TABLE_CREATE =
            "CREATE TABLE " + StatementDatabase.TABLE_STATEMENT + " (" +
                    StatementDatabase.STATEMENT_COL_ID + " INTEGER PRIMARY KEY, " +
                    StatementDatabase.STATEMENT_COL_TEXT + " TEXT, " +
                    StatementDatabase.STATEMENT_COL_PROFILE + " TEXT, " +
                    StatementDatabase.STATEMENT_COL_STATUS + " INTEGER );";

    public StatementDatabaseOpenHelper(Context context) {
        super(context, StatementDatabase.DATABASE_NAME, null, StatementDatabase.DATABASE_VERSION);
        mContext = context;

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STATEMENT_LIST_TABLE_CREATE);
        // on database creation : init content
        fillDatabaseWithData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "BDD Upgrade");
        db.execSQL("DROP TABLE IF EXISTS " + StatementDatabase.TABLE_STATEMENT);
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
                Statement statement = new Statement(App.NOID, strText, strProfile, Statement.STATUS_MARKED);
                values.put(StatementDatabase.STATEMENT_COL_TEXT, statement.getText());
                values.put(StatementDatabase.STATEMENT_COL_PROFILE, statement.getTextProfile());
                values.put(StatementDatabase.STATEMENT_COL_STATUS, Statement.STATUS_MARKED);
                db.insert(StatementDatabase.TABLE_STATEMENT, null, values);
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
