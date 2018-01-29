package com.pantagruel.megaoutrage.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pantagruel.megaoutrage.App;

import java.util.ArrayList;

/**
 * Created by MAKI LAINEUX on 21/01/2018.
 */

public class StatementDatabase {

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
    private final String TAG = this.getClass().getSimpleName();
    private SQLiteDatabase mBdd;
    private StatementDatabaseOpenHelper mOpenHelper;
    private Cursor mCursor;

    public StatementDatabase(Context context){

        mOpenHelper = new StatementDatabaseOpenHelper(context);
    }

    public void open(){

        mBdd = mOpenHelper.getWritableDatabase();
    }

    // Get an arraylist with all statements matching current status and profile
    public ArrayList<Statement> getStatementList(){
        Statement s;
        ArrayList<Statement> v = new ArrayList<>();

        // First get a Cursor with records matching the status
        String where = null;
        if (App.sScope == App.STATUS_MARKED)
            where = STATEMENT_COL_STATUS + " = " + Integer.toString(App.STATUS_MARKED);
        try {
            if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
            if (mCursor != null) mCursor.close();
            mCursor = mBdd.query(TABLE_STATEMENT, new String[]{
                            STATEMENT_COL_ID,
                            STATEMENT_COL_TEXT,
                            STATEMENT_COL_PROFILE,
                            STATEMENT_COL_STATUS,
                    },
                    where, null, null, null, STATEMENT_COL_TEXT+" ASC");
        } catch (Exception e) {
            Log.e(App.TAG, "GetAllStatements EXCEPTION! " + e);
            return null;
        }
        if (mCursor == null) return null;

        // then fill the vector with items matching the profile
        mCursor.moveToFirst();
        for (int i = 0; i < mCursor.getCount(); i++) {
            s = new Statement(
                    mCursor.getInt(STATEMENT_NUM_COL_ID),
                    mCursor.getString(STATEMENT_NUM_COL_TEXT),
                    mCursor.getString(STATEMENT_NUM_COL_PROFILE),
                    mCursor.getInt(STATEMENT_NUM_COL_STATUS)
            );
            if (s.matchesProfile(App.sCurrentProfile))
                v.add(s);
            mCursor.moveToNext();
        }
        return v;
    }

    public int removeOneStatement(Statement statement) {
        int deleted = 0;
        try {
            if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
            deleted = mBdd.delete(TABLE_STATEMENT,
                    STATEMENT_COL_ID + " = ? ", new String[]{String.valueOf(statement.getId())});
        } catch (Exception e) {
            Log.e (App.TAG, "DELETE EXCEPTION! " + e.getMessage());
        }
        return deleted;
    }

    public int updateOneStatement(Statement statement){
        int mNumberOfRowsUpdated = -1;
        try {
            if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
            ContentValues values = new ContentValues();
            values.put(STATEMENT_COL_TEXT, statement.getText());
            values.put(STATEMENT_COL_PROFILE, statement.getTextProfile());
            values.put(STATEMENT_COL_STATUS, statement.getStatus());
            mNumberOfRowsUpdated = mBdd.update(TABLE_STATEMENT,
                    values,
                    STATEMENT_COL_ID + " = ?",
                    new String[]{String.valueOf(statement.getId())});
            Log.d(App.TAG, "number of rows uopdated : "+mNumberOfRowsUpdated);
        } catch (Exception e) {
            Log.e(App.TAG, "UPDATE EXCEPTION! " + e.getMessage());
        }
        return mNumberOfRowsUpdated;
    }

    public long insertOneStatement(Statement statement){
        long newId = 0;
        ContentValues values = new ContentValues();
        values.put(STATEMENT_COL_TEXT, statement.getText());
        values.put(STATEMENT_COL_PROFILE, statement.getTextProfile());
        values.put(STATEMENT_COL_STATUS, App.STATUS_NORMAL);
        try {
            if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
            newId = mBdd.insert(TABLE_STATEMENT, null, values);
        } catch (Exception e) {
            Log.e(App.TAG, "INSERT EXCEPTION! " + e.getMessage());
        }
        Log.d(App.TAG, "insert newid = "+newId);
        return newId;
    }

    public long count() {
        if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
        return DatabaseUtils.queryNumEntries(mBdd, TABLE_STATEMENT);
    }

    public int toggleStatutFavori(Statement statement) {
        int newStatus;
        switch (statement.getStatus()) {
            case App.STATUS_NORMAL :
                newStatus = App.STATUS_MARKED; break;
            case App.STATUS_MARKED :
                newStatus = App.STATUS_NORMAL; break;
            default:
                newStatus = statement.getStatus(); break;
        }
        statement.setStatus(newStatus);

        // update Database
        return updateOneStatement(statement);
    }
}
