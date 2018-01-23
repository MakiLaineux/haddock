package com.pantagruel.unbrindled1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
        String where = Globals.STATEMENT_COL_ID+ " = \"" + idBdd + "\"";
        try {
            if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
            if (c != null) c.close();
            c = mBdd.query(Globals.TABLE_STATEMENT, new String[]{
                Globals.STATEMENT_COL_ID,
                Globals.STATEMENT_COL_TEXT,
                Globals.STATEMENT_COL_PROFILE,
                Globals.STATEMENT_COL_STATUS,
                },
                where, null, null, null, null);
        } catch (Exception e) {
            Log.e(Globals.TAG, "GetOneStatement EXCEPTION! " + e);
            return null;
        }
        Statement s = new Statement(
                idBdd,
                c.getString(Globals.STATEMENT_NUM_COL_TEXT),
                c.getString(Globals.STATEMENT_NUM_COL_PROFILE),
                c.getInt(Globals.STATEMENT_NUM_COL_STATUS)
        );
        return s;
    }

    // Get a cursor with all statements matching given status and profile
    public Cursor getAllStatement(int paramStatut, Profile paramProfile){
        String where;
        switch (paramStatut) {
            case Globals.STATUS_ALL:
                where = null;
                break;
            case Globals.STATUS_MARKED:
                where = Globals.STATEMENT_COL_STATUS + " = " + Integer.toString(Globals.STATUS_MARKED);
                break;
            default: where = null;break;
        }
        try {
            if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
            if (c != null) c.close();
            c = mBdd.query(Globals.TABLE_STATEMENT, new String[]{
                        Globals.STATEMENT_COL_ID,
                        Globals.STATEMENT_COL_TEXT,
                        Globals.STATEMENT_COL_PROFILE,
                        Globals.STATEMENT_COL_STATUS,
                },
                where, null, null, null, Globals.STATEMENT_COL_TEXT+" ASC");
        } catch (Exception e) {
            Log.e(Globals.TAG, "GetAllStatements EXCEPTION! " + e);
            return null;
        }
        return c;
    }

    public int removeOneStatement(Statement statement) {
        int deleted = 0;
        try {
            if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
            deleted = mBdd.delete(Globals.TABLE_STATEMENT,
                    Globals.STATEMENT_COL_ID + " = ? ", new String[]{String.valueOf(statement.getId())});
        } catch (Exception e) {
            Log.e (Globals.TAG, "DELETE EXCEPTION! " + e.getMessage());
        }
        return deleted;
    }

    public int updateOneStatement(Statement statement){
        int mNumberOfRowsUpdated = -1;
        try {
            if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
            ContentValues values = new ContentValues();
            values.put(Globals.STATEMENT_COL_TEXT, statement.getText());
            values.put(Globals.STATEMENT_COL_PROFILE, statement.getTextProfile());
            values.put(Globals.STATEMENT_COL_STATUS, statement.getStatus());
            mNumberOfRowsUpdated = mBdd.update(Globals.TABLE_STATEMENT,
                    values,
                    Globals.STATEMENT_COL_ID + " = ?",
                    new String[]{String.valueOf(statement.getId())});
            Log.d(Globals.TAG, "number of rows uopdated : "+mNumberOfRowsUpdated);
        } catch (Exception e) {
            Log.e(Globals.TAG, "UPDATE EXCEPTION! " + e.getMessage());
        }
        return mNumberOfRowsUpdated;
    }

    public long insertOneStatement(Statement statement){
        long newId = 0;
        ContentValues values = new ContentValues();
        values.put(Globals.STATEMENT_COL_TEXT, statement.getText());
        values.put(Globals.STATEMENT_COL_PROFILE, statement.getTextProfile());
        values.put(Globals.STATEMENT_COL_STATUS, Globals.STATUS_NORMAL);
        try {
            if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
            newId = mBdd.insert(Globals.TABLE_STATEMENT, null, values);
        } catch (Exception e) {
            Log.e(Globals.TAG, "INSERT EXCEPTION! " + e.getMessage());
        }
        Log.d(Globals.TAG, "insert newid = "+newId);
        return newId;
    }

    public long count() {
        if (mBdd == null) {mBdd = mOpenHelper.getWritableDatabase();}
        return DatabaseUtils.queryNumEntries(mBdd, Globals.TABLE_STATEMENT);
    }

    public int toggleStatutFavori(Statement statement) {
        int newStatus;
        switch (statement.getStatus()) {
            case Globals.STATUS_NORMAL :
            case Globals.STATUS_NONE :
                newStatus = Globals.STATUS_MARKED; break;
            case Globals.STATUS_MARKED :
                newStatus = Globals.STATUS_NORMAL; break;
            default:
                newStatus = statement.getStatus(); break;
        }
        statement.setStatus(newStatus);

        // update Database
        return updateOneStatement(statement);
    }
}
