package com.pantagruel.unbrindled1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int STATEMENT_EDIT = 1;
    public static final int STATEMENT_ADD = 2;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private StatementAdapter mAdapter;
    private int mScopeAffichage = Globals.STATUS_ALL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create recycler view.
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Create an mAdapter and supply the data to be displayed.
        mAdapter = new StatementAdapter(mScopeAffichage);
        // Connect the mAdapter with the recycler view.
        mRecyclerView.setAdapter(mAdapter);

        // RecyclerView : Click management
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

        // Swipe management
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;  //not used
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        actionDelete(viewHolder);
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);




        // Add a floating action click handler for creating new entries.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start empty edit activity.
                Context context = Globals.context;
                Intent intent = new Intent(context, EditStatementActivity.class);
                startActivityForResult(intent, STATEMENT_ADD);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.ko_return,
                    Toast.LENGTH_LONG).show();
            return;
        }
        Statement statement = new Statement(
                data.getIntExtra(Globals.EXTRA_ID, Globals.NOID),
                data.getStringExtra(Globals.EXTRA_TEXT),
                data.getStringExtra(Globals.EXTRA_PROFILE),
                data.getIntExtra(Globals.EXTRA_STATUS, Globals.STATUS_NONE)
        );
        if (TextUtils.isEmpty(statement.getText())) {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
            return;
        }

        switch (requestCode){
            case STATEMENT_ADD:
                mAdapter.insertStatement(statement);
                break;
            case STATEMENT_EDIT:
                mAdapter.updateStatement(statement);
                break;
            default:
                break;
        }
        mAdapter.loadData(mScopeAffichage);
        mAdapter.notifyDataSetChanged();
    }


    // RecyclerView events : short click : Edit item
    public void shortClick(View v, int pos) {
        Context context = Globals.context;
        Intent intent = new Intent(context, EditStatementActivity.class);

        Statement s = mAdapter.getStatementFromPosition(pos);

        intent.putExtra(Globals.EXTRA_ID, s.getId());
        intent.putExtra(Globals.EXTRA_TEXT, s.getText());
        intent.putExtra(Globals.EXTRA_PROFILE, s.getTextProfile());
        intent.putExtra(Globals.EXTRA_STATUS, s.getStatus());

        // Start an empty edit activity.
        startActivityForResult(intent, STATEMENT_EDIT);
    }

    // RecyclerView events : long click : Toggle mark
    public void longClick(View v, int pos) {
        Statement s = mAdapter.getStatementFromPosition(pos);
        mAdapter.toggleFavori(s);
        mAdapter.loadData(mScopeAffichage);
        mAdapter.notifyDataSetChanged(); // MAJ de l'affichage
    }

    // RecyclerView events : Swipe : item suppression
    public void actionDelete(RecyclerView.ViewHolder viewHolder){
        int pos = viewHolder.getAdapterPosition();
        Statement s = mAdapter.getStatementFromPosition(pos);
        mAdapter.removeStatement(s);
        mAdapter.loadData(mScopeAffichage);
        mAdapter.notifyDataSetChanged();
    }

}