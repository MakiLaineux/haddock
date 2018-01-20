package com.pantagruel.unbrindled1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by MAKI LAINEUX on 16/01/2018.
 */

public class WordListOpenHelper extends SQLiteOpenHelper {

    // Column names
    private static final String TAG = WordListOpenHelper.class.getSimpleName();
    // string array of columns.
    private static final String[] COLUMNS = { Globals.STATEMENT_COL_ID, Globals.STATEMENT_COL_TEXT, Globals.STATEMENT_COL_PROFILE };

    // Build the SQL query that creates the table.
    private static final String WORD_LIST_TABLE_CREATE =
            "CREATE TABLE " + Globals.TABLE_STATEMENT + " (" +
                    Globals.STATEMENT_COL_ID + " INTEGER PRIMARY KEY, " +
                    Globals.STATEMENT_COL_TEXT + " TEXT, " +
                    Globals.STATEMENT_COL_PROFILE + " TEXT );";

    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    public WordListOpenHelper(Context context) {
        super(context, Globals.DATABASE_NAME, null, Globals.DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WORD_LIST_TABLE_CREATE);
        fillDatabaseWithData(db);
    }

    private boolean fillDatabaseWithData(SQLiteDatabase db) {
        String ligne; 		//ligne lue dans le fichier
        boolean bOK = false;
        ContentValues values = new ContentValues();

        try {
            BufferedReader buf = new BufferedReader(new InputStreamReader(Globals.context.getAssets().open("fill.txt"), "ISO-8859-1"));
            while ((ligne = buf.readLine()) != null) {
                if (ligne.length() <= Profile.NB_CHECKBOX) continue;
                String strProfile = ligne.substring(0,13);
                String strText = ligne.substring(13);
                Statement statement = new Statement(strText, strProfile);
                values.put(Globals.STATEMENT_COL_TEXT, statement.getText());
                values.put(Globals.STATEMENT_COL_PROFILE, statement.getTextProfile());
                db.insert(Globals.TABLE_STATEMENT, null, values);
            }
            bOK=true;
            buf.close();

        } catch (ArrayIndexOutOfBoundsException e) {
            Toast.makeText(Globals.context, "Problème dans le format du fichier Assets", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(Globals.context, "IOException fillDatabaseWithData", Toast.LENGTH_SHORT).show();
        } catch (java.lang.NullPointerException e) {
            Toast.makeText(Globals.context, "NullPointer dans fillDatabaseWithData", Toast.LENGTH_SHORT).show();
        } catch (java.lang.RuntimeException e) {
            Toast.makeText(Globals.context, "RunTime Exception dans FillDatabaseWithData", Toast.LENGTH_SHORT).show();
        }
        return bOK;
        }

    public Statement query(int position) {
        String query = "SELECT  * FROM " + Globals.TABLE_STATEMENT +
                " ORDER BY " + Globals.STATEMENT_COL_TEXT + " ASC " +
                "LIMIT " + position + ",1";
        Cursor cursor = null;
        Statement entry = new Statement();
        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.rawQuery(query, null);
            cursor.moveToFirst();
            entry.setId(cursor.getInt(cursor.getColumnIndex(Globals.STATEMENT_COL_ID)));
            entry.setText(cursor.getString(cursor.getColumnIndex(Globals.STATEMENT_COL_TEXT)));
            entry.setProfileFromString(cursor.getString(cursor.getColumnIndex(Globals.STATEMENT_COL_PROFILE)));

        } catch (Exception e) {
            Log.e(TAG, "EXCEPTION! " + e);
        } finally {
            cursor.close();
            return entry;
        }
    }

    public long insert(Statement statement){
        long newId = 0;
        ContentValues values = new ContentValues();
        values.put(Globals.STATEMENT_COL_TEXT, statement.getText());
        values.put(Globals.STATEMENT_COL_PROFILE, statement.getTextProfile());
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            newId = mWritableDB.insert(Globals.TABLE_STATEMENT, null, values);
        } catch (Exception e) {
            Log.e(TAG, "INSERT EXCEPTION! " + e.getMessage());
        }
        return newId;
    }
    public int update(int id, Statement statement){
        int mNumberOfRowsUpdated = -1;
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            ContentValues values = new ContentValues();
            values.put(Globals.STATEMENT_COL_TEXT, statement.getText());
            values.put(Globals.STATEMENT_COL_PROFILE, statement.getTextProfile());
            mNumberOfRowsUpdated = mWritableDB.update(Globals.TABLE_STATEMENT,
                    values,
                    Globals.STATEMENT_COL_ID + " = ?",
                    new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.e(TAG, "UPDATE EXCEPTION! " + e.getMessage());
        }
        return mNumberOfRowsUpdated;
    }

    public int delete(int id) {
        int deleted = 0;
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            deleted = mWritableDB.delete(Globals.TABLE_STATEMENT,
                    Globals.STATEMENT_COL_ID + " = ? ", new String[]{String.valueOf(id)});

        } catch (Exception e) {
            Log.e (TAG, "DELETE EXCEPTION! " + e.getMessage());
        }
        return deleted;
    }

    public long count(){
        if (mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(mReadableDB, Globals.TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(WordListOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + Globals.TABLE_STATEMENT);
        onCreate(db);
    }
}
