package com.pantagruel.megaoutrage.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.pantagruel.megaoutrage.App;

import java.util.ArrayList;

/*
 * Custom loader performing in a separate thread all database operations (query, insert, update, delete)
 *
 * The operation type must be passed as a parameter of the constructor
 *
 * Work is done in a separate thread by the loadInBackground method. Whatever the operation, after all
 * database updates are performed, loadInBackground queries the database to obtain un updated  ArrayList of Statements,
 * which is returned to the calling Activity.
 * */

public class StatementArrayListLoader extends AsyncTaskLoader<ArrayList<Statement>> {

    // Operation codes constants

    // Only query the database to obtain a new ArrayList of Statements
    public static final int LOADER_REFRESH = 1;

    // Add a Statement
    public static final int LOADER_ADD = 2;

    // Update an existing Statement (all Statement members but the status)
    public static final int LOADER_UPDATE = 3;

    // Update an existing Statement : inverse its status
    public static final int LOADER_TOGGLE = 4;

    // Delete an existing Statement
    public static final int LOADER_DELETE = 5;


    private static final String TAG = App.TAG + StatementArrayListLoader.class.getSimpleName();

    private int mOperation; // keeps the opeation code
    private Statement mStatement; // if needed, the statement to be inserted/deleted/updated


    // Constructor
    public StatementArrayListLoader(Context context, int operation, Statement statement){
        super(context);
        mOperation = operation;   // must match one of the operation code constants
        mStatement = statement;   // may be null if operation code is REFRESH
    }

    /**
     * Called on a worker thread to perform the actual load and to return
     * the result of the load operation.
     */
    @Override
    public ArrayList<Statement> loadInBackground() {

        // Perform long running database operations, depending of operation code stored in the constructor
        switch (mOperation){
            case LOADER_REFRESH:
                Log.d(TAG, "Loader : Refresh");
                break;
            case LOADER_ADD:
                Log.d(TAG, "Loader : Add");
                App.sBaseLocale.insertOneStatement(mStatement);
                break;
            case LOADER_DELETE:
                Log.d(TAG, "Loader : Delete");
                App.sBaseLocale.removeOneStatement(mStatement);
                break;
            case LOADER_UPDATE:
                Log.d(TAG, "Loader : Update");
                App.sBaseLocale.updateOneStatement(mStatement);
                break;
            case LOADER_TOGGLE:
                Log.d(TAG, "Loader : Toggle");
                App.sBaseLocale.toggleStatutFavori(mStatement);
                break;
            default:
                Log.wtf(TAG, "loader : illegal operation");
                break;
        }

        // in all cases, query the database and return the refreshed array of results
        ArrayList<Statement> arr = App.sBaseLocale.getStatementList(App.sCurrentProfile, App.sScope);

        return arr;
    }
}
