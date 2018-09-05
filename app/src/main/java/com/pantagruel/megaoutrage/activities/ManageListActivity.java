package com.pantagruel.megaoutrage.activities;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pantagruel.megaoutrage.App;
import com.pantagruel.megaoutrage.R;
import com.pantagruel.megaoutrage.data.StatementArrayListLoader;
import com.pantagruel.megaoutrage.util.RecyclerItemClickListener;
import com.pantagruel.megaoutrage.data.Statement;
import com.pantagruel.megaoutrage.adapters.StatementAdapter;

import java.util.ArrayList;


/**
 * RecyclerView Activity
 *  This activity implements a Loader for all changes (refresh display, insert, update, delete)
 *
 */
public class ManageListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Statement>> {

    private static final String TAG = App.TAG + ManageListActivity.class.getSimpleName();

    // Menu choices
    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;
    public static final int REQUEST_PROFILE = 3;

    private RecyclerView mRecyclerView;  // the RecyclerView
    private LinearLayoutManager mLayoutManager;  // LayoutManager to handle the rv
    private StatementAdapter mAdapter; // Adapter needed for the recyclerbiew
    private Statement mCurrentStatement; // current handled statement
    private Menu mMenu; // this field is used to dynamically change menu icons

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_list);

        // Create the recycler view.
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);

        // the rv must be given a LayoutManager object (not used otherwise in this code)
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Create an mAdapter, the data set to display will be supplied later asynchronously with a Loader
        mAdapter = new StatementAdapter(this);

        // Connect the mAdapter with the recycler view.
        mRecyclerView.setAdapter(mAdapter);

        // if necessary, retrieve the current statement saved in onSaveInstanceState
        // this is necessary if configuration has change during an EDIT operation
        if (savedInstanceState != null) {
            mCurrentStatement = savedInstanceState.getParcelable("currentStatement");
        } else {
            mCurrentStatement = null;
        }

        /* Init a Loader (via a LoaderManager instance)to build the data to display in the rv
        This Loader implementation checks the Loader id to select the operation to perform (display refresh only,
         various db update followed by a display refresh, etc. See possible values in the Loader class
         note : for some unknown reason, it seems that forceLoad has to be called */
        getLoaderManager().initLoader(StatementArrayListLoader.LOADER_REFRESH, null, this).forceLoad();

        // RecyclerView : Add short and long click management using an utility class
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        shortClick(view, position);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        longClick(view, position);
                    }
                })
        );

        // RecyclerView : for gesture management (swipe in this case), use the ItemTouchHelper class
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;  // not used
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        actionDelete(viewHolder);
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        // Floating action button to create a new element in the db and refresh the rv
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageListActivity.this, EditStatementActivity.class);
                // pass the nature of the request (REQUEST_ADD)
                intent.putExtra(EditStatementActivity.EXTRA_REQUEST, EditStatementActivity.REQUEST_ADD);
                // Start an empty edit activity.
                startActivityForResult(intent, EditStatementActivity.REQUEST_ADD);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Menu icon for favorites depends on the current user preferences
        getMenuInflater().inflate(R.menu.menu_manage_list, menu);
        if (App.sScope == Statement.SCOPE_FAVORITES){
            menu.findItem(R.id.action_favori).setIcon(R.drawable.ic_action_favorite_empty_white);
        } else {
            menu.findItem(R.id.action_favori).setIcon(R.drawable.ic_action_favorite_full_white);
        }
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
            // launch an activity to update the user preferences for profile:
            case R.id.action_profile:
                intent = new Intent(ManageListActivity.this, ProfileActivity.class);
                // pass the calling activity to permit correct up navigation
                intent.putExtra(ProfileActivity.CALLING_ACTIVITY, ProfileActivity.CALLING_ACTIVITY_LIST);
                startActivityForResult(intent, REQUEST_PROFILE);
                break;
            // Change the user preferences for scope, change the scope menu icon, and  refresh display:
            case R.id.action_favori:
                App.toggleScope();
                if (App.sScope == Statement.SCOPE_FAVORITES){
                    mMenu.findItem(R.id.action_favori).setIcon(R.drawable.ic_action_favorite_empty_white);
                } else {
                    mMenu.findItem(R.id.action_favori).setIcon(R.drawable.ic_action_favorite_full_white);
                }
                // Log.d(TAG, "Activity : restartLoader - favori mode changed");
                getLoaderManager().restartLoader(StatementArrayListLoader.LOADER_REFRESH, null, this).forceLoad();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Statement statement;
        Bundle bundle = new Bundle();

        super.onActivityResult(requestCode, resultCode, data);

        // perform action only if result_code is RESULT_OK
        if (resultCode != RESULT_OK) return;

        switch (requestCode){
            // The profile has been changed, refresh display
            case REQUEST_PROFILE:
                getLoaderManager().restartLoader(StatementArrayListLoader.LOADER_REFRESH, null, this).forceLoad();
                break;
            // new record to add, passed in an extra : call the loader which will add the record in db an refresh display
            case REQUEST_ADD:
                statement = data.getParcelableExtra(EditStatementActivity.EXTRA_STATEMENT);
                // action only if the record is ok
                if (!isValidAdd(statement)) return;
                // pass the record to the Loader in a bundle
                bundle.putParcelable("statement", statement);
                // call the loader
                getLoaderManager().restartLoader(StatementArrayListLoader.LOADER_ADD, bundle, this).forceLoad();
                // keep the handled statement, used later to scroll the rv to this position
                mCurrentStatement = statement;
                break;
            // existing record changed, passed in an extra : call the loader which will update the db an refresh display
            case REQUEST_EDIT:
                statement = data.getParcelableExtra(EditStatementActivity.EXTRA_STATEMENT);
                // action only if the item is ok
                if (!isValidUpdate(statement)) return;
                // pass the record to the Loader in a bundle
                bundle.putParcelable("statement", statement);
                // call the loader
                getLoaderManager().restartLoader(StatementArrayListLoader.LOADER_UPDATE, bundle, this).forceLoad();
                // keep the handled statement, used later to scroll the rv to this position
                mCurrentStatement = statement;
                break;
            default: // impossible case
                Log.wtf(TAG, "onActivityResult : mysterious request code");
                break;
        }
    }

    // Save current statement (if exists)
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentStatement != null)
            outState.putParcelable("currentStatement", mCurrentStatement);
    }



    // Check if updated entry is valid : at least one letter, text not already used by another entry
    private boolean isValidUpdate(Statement statement) {
        if (!statement.getText().matches(".*[a-zA-Z]+.*")) {
            Toast.makeText(this, R.string.message_statement_empty, Toast.LENGTH_LONG).show();
            return false;
        }
        // if old text differs from new text, new text must not be already used
        if ((!mCurrentStatement.getText().equals(statement.getText())) &&
                (App.sBaseLocale.statementTextAlreadyExist(statement.getText()))) {
            Toast.makeText(this, R.string.message_statement_already_exists, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    // Check if new entry is valid : at least one letter, text not already existing
    private boolean isValidAdd(Statement statement) {
        // statement must not be empty
        if (!statement.getText().matches(".*[a-zA-Z]+.*")) {
            Toast.makeText(this, R.string.message_statement_empty, Toast.LENGTH_LONG).show();
            return false;
        }
        // the text must not be already used
        if (App.sBaseLocale.statementTextAlreadyExist(statement.getText())) {
            Toast.makeText(this, R.string.message_statement_already_exists, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    // RecyclerView events : short click : Edit item in another activity
    public void shortClick(View v, int pos) {
        Intent intent = new Intent(ManageListActivity.this, EditStatementActivity.class);

        // get the record to edit from its position in the rv
        mCurrentStatement = mAdapter.getStatementFromPosition(pos);

        // pass in an extra the nature of the request (REQUEST_EDIT) and the parcelable record to edit
        intent.putExtra(EditStatementActivity.EXTRA_REQUEST, EditStatementActivity.REQUEST_EDIT);
        intent.putExtra(EditStatementActivity.EXTRA_STATEMENT, mCurrentStatement);

        // Start the edit activity.
        startActivityForResult(intent, EditStatementActivity.REQUEST_EDIT);
    }

    // RecyclerView events : long click : Toggle favorite status of the item and refresh display
    public void longClick(View v, int pos) {
        // get the record to edit from its position in the rv
        Statement s = mAdapter.getStatementFromPosition(pos);

        // Call the loader which will perform the bdd update end refresh display
        Bundle bundle = new Bundle();
        bundle.putParcelable("statement", s);
        getLoaderManager().restartLoader(StatementArrayListLoader.LOADER_TOGGLE, bundle, this).forceLoad();
        // keep the handled statement, used later to scroll the rv to this position
        mCurrentStatement = s;
    }

    // RecyclerView events : Swipe : item suppression
    public void actionDelete(RecyclerView.ViewHolder viewHolder){
        // get the record to edit from its position in the rv
        int pos = viewHolder.getAdapterPosition();
        Statement s = mAdapter.getStatementFromPosition(pos);

        // Call the loader which will perform the bdd update end refresh display
        Bundle bundle = new Bundle();
        bundle.putParcelable("statement", s);
        getLoaderManager().restartLoader(StatementArrayListLoader.LOADER_DELETE, bundle, this).forceLoad();
    }

    /*
     * Instantiate a new custom Loader for the requested operation.
     * - id : id of the loader, set to a constant representing the operation to perform, see constants in the Loader class.
     * A separate Loader instance is created for each operation type
     * - Bundle : the data to pass to the Loader. This is null for display refresh
     * and contains the affected Statement when a bdd updated is requested
     */
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Statement s = null;
        Log.d(TAG, "Activity : callback onCreateLoader, id = "+id);
        if (args != null) s = args.getParcelable("statement");
        return new StatementArrayListLoader(this, id, s);
    }

    /*
     * Automatically called when a previously created loader has finished its load.
     * loader : The id of the Loader that has finished.
     * data : The data generated by the Loader, ie the new ArrayList to display
     */
    @Override
    public void onLoadFinished(Loader<ArrayList<Statement>> loader, ArrayList<Statement> data) {
        // The Loader returns a new dataset, set the Adapter data set with this data
        mAdapter.setStatementList(data);

        switch (loader.getId()) {
            // if a statement has been added or updated, scroll the rv to its position
            case StatementArrayListLoader.LOADER_ADD:
            case StatementArrayListLoader.LOADER_UPDATE:
                if (mCurrentStatement == null) { // impossible case
                    Log.wtf(TAG, "no current statement");
                }
                // Find the (maybe new) position in the RecyclerView, use the (maybe new) text
                int pos = mAdapter.getPosFromStatementUsingText(mCurrentStatement);
                // Scroll the rv to position the item on top of the screen
                mLayoutManager.scrollToPositionWithOffset(pos, 40);
                break;

            // otherwise, do nothing
            case StatementArrayListLoader.LOADER_TOGGLE:
            case StatementArrayListLoader.LOADER_DELETE:
            case StatementArrayListLoader.LOADER_REFRESH:
            default:
                break;
        }

        // notify the adapter to trigger a redisplay
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader loader) {
        Log.d(TAG, "Activity : callback onLoaderReset");
        mAdapter.setStatementList(null);
        mAdapter.notifyDataSetChanged();
    }
}