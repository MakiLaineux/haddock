package com.pantagruel.unbrindled1;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

/**
 * Implements a RecyclerView that displays a list of words from a SQL database.
 * - Clicking the fab button opens a second activity to add a word to the database.
 * - Clicking the Edit button opens an activity to edit the current word in the database.
 * - Clicking the Delete button deletes the current word from the database.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int STATEMENT_EDIT = 1;
    public static final int STATEMENT_ADD = -1;

    private RecyclerView mRecyclerView;
    private DividerItemDecoration mDividerItemDecoration;
    private LinearLayoutManager mLayoutManager;
    private WordListAdapter mAdapter;
    private WordListOpenHelper mDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create database if not exists.
        mDB = new WordListOpenHelper(this);

        // Create recycler view.
        mRecyclerView = findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);

        // Create an mAdapter and supply the data to be displayed.
        mAdapter = new WordListAdapter(this, mDB);
        // Connect the mAdapter with the recycler view.
        mRecyclerView.setAdapter(mAdapter);

        // Add a floating action click handler for creating new entries.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start empty edit activity.
                Intent intent = new Intent(getBaseContext(), EditStatementActivity.class);
                startActivityForResult(intent, STATEMENT_EDIT);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STATEMENT_EDIT) {
            if (resultCode == RESULT_OK) {
                Statement statement = new Statement(
                        data.getStringExtra(Globals.EXTRA_TEXT),
                        data.getStringExtra(Globals.EXTRA_PROFILE)
                        );
                if (!TextUtils.isEmpty(statement.getText())) {
                    int id = data.getIntExtra(Globals.EXTRA_ID, -99);
                    if (id == STATEMENT_ADD) {
                        mDB.insert(statement);
                    } else if (id >= 0) {
                        mDB.update(id, statement);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.empty_not_saved,
                            Toast.LENGTH_LONG).show();
                }
            }

        }
    }
}