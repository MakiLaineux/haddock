package com.pantagruel.unbrindled1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Vector;

/**
 * Created by MAKI LAINEUX on 21/01/2018.
 */

public class BddLocale {

    private SQLiteDatabase mBdd;
    private BddOpenHelper mOpenHelper;

    //working cursor
    private Cursor c;

    public BddLocale(Context context){

        mOpenHelper = new BddOpenHelper(context);
    }

    public void open(){

        mBdd = mOpenHelper.getWritableDatabase();
    }

    // Get one statement from its bdd key
    public Statement getOneStatementFromIdBdd(int idBdd){
        String where = App.STATEMENT_COL_ID+ " = \"" + idBdd + "\"";
        try {
            if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
            if (c != null) c.close();
            c = mBdd.query(App.TABLE_STATEMENT, new String[]{
                App.STATEMENT_COL_ID,
                App.STATEMENT_COL_TEXT,
                App.STATEMENT_COL_PROFILE,
                App.STATEMENT_COL_STATUS,
                },
                where, null, null, null, null);
        } catch (Exception e) {
            Log.e(App.TAG, "GetOneStatement EXCEPTION! " + e);
            return null;
        }
        Statement s = new Statement(
                idBdd,
                c.getString(App.STATEMENT_NUM_COL_TEXT),
                c.getString(App.STATEMENT_NUM_COL_PROFILE),
                c.getInt(App.STATEMENT_NUM_COL_STATUS)
        );
        return s;
    }

    // Get a vector with all statements matching given status and profile
    public Vector<Statement> getStatementVector(){
        Statement s;
        Vector<Statement> v = new Vector<Statement>();

        // First get a Cursor with records matching the status
        String where = null;
        if (App.scope == App.STATUS_MARKED)
            where = App.STATEMENT_COL_STATUS + " = " + Integer.toString(App.STATUS_MARKED);
        try {
            if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
            if (c != null) c.close();
            c = mBdd.query(App.TABLE_STATEMENT, new String[]{
                            App.STATEMENT_COL_ID,
                            App.STATEMENT_COL_TEXT,
                            App.STATEMENT_COL_PROFILE,
                            App.STATEMENT_COL_STATUS,
                    },
                    where, null, null, null, App.STATEMENT_COL_TEXT+" ASC");
        } catch (Exception e) {
            Log.e(App.TAG, "GetAllStatements EXCEPTION! " + e);
            return null;
        }
        if (c == null) return null;

        // then fill the vector with items matching the profile
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            s = new Statement(
                    c.getInt(App.STATEMENT_NUM_COL_ID),
                    c.getString(App.STATEMENT_NUM_COL_TEXT),
                    c.getString(App.STATEMENT_NUM_COL_PROFILE),
                    c.getInt(App.STATEMENT_NUM_COL_STATUS)
            );
            if (s.matchesProfile(App.currentProfile))
                v.add(s);
            c.moveToNext();
        }
        return v;
    }


        // Get a cursor with all statements matching given status and profile
    public Cursor getAllStatement(int paramStatut, Profile paramProfile){
        // get a Cursor with records matching the status, then remove items not matching the profile

        String where = null;
        if (paramStatut == App.STATUS_MARKED)
            where = App.STATEMENT_COL_STATUS + " = " + Integer.toString(App.STATUS_MARKED);

        try {
            if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
            if (c != null) c.close();
            c = mBdd.query(App.TABLE_STATEMENT, new String[]{
                        App.STATEMENT_COL_ID,
                        App.STATEMENT_COL_TEXT,
                        App.STATEMENT_COL_PROFILE,
                        App.STATEMENT_COL_STATUS,
                },
                where, null, null, null, App.STATEMENT_COL_TEXT+" ASC");
        } catch (Exception e) {
            Log.e(App.TAG, "GetAllStatements EXCEPTION! " + e);
            return null;
        }
        if (c == null) return null;

        return c;
    }

    public int removeOneStatement(Statement statement) {
        int deleted = 0;
        try {
            if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
            deleted = mBdd.delete(App.TABLE_STATEMENT,
                    App.STATEMENT_COL_ID + " = ? ", new String[]{String.valueOf(statement.getId())});
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
            values.put(App.STATEMENT_COL_TEXT, statement.getText());
            values.put(App.STATEMENT_COL_PROFILE, statement.getTextProfile());
            values.put(App.STATEMENT_COL_STATUS, statement.getStatus());
            mNumberOfRowsUpdated = mBdd.update(App.TABLE_STATEMENT,
                    values,
                    App.STATEMENT_COL_ID + " = ?",
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
        values.put(App.STATEMENT_COL_TEXT, statement.getText());
        values.put(App.STATEMENT_COL_PROFILE, statement.getTextProfile());
        values.put(App.STATEMENT_COL_STATUS, App.STATUS_NORMAL);
        try {
            if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
            newId = mBdd.insert(App.TABLE_STATEMENT, null, values);
        } catch (Exception e) {
            Log.e(App.TAG, "INSERT EXCEPTION! " + e.getMessage());
        }
        Log.d(App.TAG, "insert newid = "+newId);
        return newId;
    }

    public long count() {
        if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
        return DatabaseUtils.queryNumEntries(mBdd, App.TABLE_STATEMENT);
    }

    public int toggleStatutFavori(Statement statement) {
        int newStatus;
        switch (statement.getStatus()) {
            case App.STATUS_NORMAL :
            case App.STATUS_NONE :
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
