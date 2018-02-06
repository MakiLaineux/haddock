package com.pantagruel.megaoutrage.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.pantagruel.megaoutrage.App;
import com.pantagruel.megaoutrage.activities.ManageListActivity;

import java.util.ArrayList;

/**
 * Created by MAKI LAINEUX on 05/02/2018.
 */

public class StatementArrayListLoader extends AsyncTaskLoader<ArrayList<Statement>> {

    private static final String TAG = App.TAG + StatementArrayListLoader.class.getSimpleName();
    private int mScope;
    private Profile mProfile;

    public StatementArrayListLoader(Context context, Profile profile, int scope){
        super(context);
        Log.d(TAG, "Loader : Constructor, Profile:"+profile.toString()+" Scope : "+scope);
        mProfile = profile;
        mScope = scope;
    }

    /**
     * Called on a worker thread to perform the actual load and to return
     * the result of the load operation.
     */
    @Override
    public ArrayList<Statement> loadInBackground() {
        Log.d(TAG, "Loader : loadInBackground début");
        ArrayList<Statement> arr = App.sBaseLocale.getStatementList(mProfile, mScope);
        Log.d(TAG, "Loader : loadInBackground fin, ArrayList size ="+arr.size());
        return arr;
    }


}
