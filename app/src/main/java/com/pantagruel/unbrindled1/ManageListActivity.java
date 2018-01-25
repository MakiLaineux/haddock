package com.pantagruel.unbrindled1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ManageListActivity extends AppCompatActivity {

    private static final String TAG = ManageListActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private StatementAdapter mAdapter;
    private Menu mMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_list);

        // Create recycler view.
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Create an mAdapter and supply the data to be displayed.
        mAdapter = new StatementAdapter();
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
                Context context = App.context;
                Intent intent = new Intent(context, EditStatementActivity.class);
                intent.putExtra(App.EXTRA_REQUEST, App.REQUEST_ADD);
                startActivityForResult(intent, App.REQUEST_ADD);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Statement statement;

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode){
            case App.REQUEST_PROFILE:
                break;
            case App.REQUEST_ADD:
                statement = new Statement(
                        data.getIntExtra(App.EXTRA_ID, App.NOID),
                        data.getStringExtra(App.EXTRA_TEXT),
                        data.getStringExtra(App.EXTRA_PROFILE),
                        data.getIntExtra(App.EXTRA_STATUS, App.STATUS_NONE)
                );
                if (TextUtils.isEmpty(statement.getText())) {return;}
                mAdapter.insertStatement(statement);
                break;
            case App.REQUEST_EDIT:
                statement = new Statement(
                        data.getIntExtra(App.EXTRA_ID, App.NOID),
                        data.getStringExtra(App.EXTRA_TEXT),
                        data.getStringExtra(App.EXTRA_PROFILE),
                        data.getIntExtra(App.EXTRA_STATUS, App.STATUS_NONE)
                );
                if (TextUtils.isEmpty(statement.getText())) {return;}
                mAdapter.updateStatement(statement);
                break;
            default:
                break;
        }
        mAdapter.loadData();
        mAdapter.notifyDataSetChanged();
    }

    // RecyclerView events : short click : Edit item
    public void shortClick(View v, int pos) {
        Context context = App.context;
        Intent intent = new Intent(context, EditStatementActivity.class);

        Statement s = mAdapter.getStatementFromPosition(pos);

        intent.putExtra(App.EXTRA_ID, s.getId());
        intent.putExtra(App.EXTRA_TEXT, s.getText());
        intent.putExtra(App.EXTRA_PROFILE, s.getTextProfile());
        intent.putExtra(App.EXTRA_STATUS, s.getStatus());
        intent.putExtra(App.EXTRA_REQUEST, App.REQUEST_EDIT);

        // Start an empty edit activity.
        startActivityForResult(intent, App.REQUEST_EDIT);
    }

    // RecyclerView events : long click : Toggle mark
    public void longClick(View v, int pos) {
        Statement s = mAdapter.getStatementFromPosition(pos);
        mAdapter.toggleFavori(s);
        mAdapter.loadData();
        mAdapter.notifyDataSetChanged(); // MAJ de l'affichage
    }

    // RecyclerView events : Swipe : item suppression
    public void actionDelete(RecyclerView.ViewHolder viewHolder){
        int pos = viewHolder.getAdapterPosition();
        Statement s = mAdapter.getStatementFromPosition(pos);
        mAdapter.removeStatement(s);
        mAdapter.loadData();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manage_list, menu);
        if (App.scope == App.SCOPE_FAVORITES){
            menu.findItem(R.id.action_favori).setIcon(R.drawable.ic_action_favorite_empty);
        } else {
            menu.findItem(R.id.action_favori).setIcon(R.drawable.ic_action_favorite_full);
        }
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_profile:
                intent = new Intent(this, ProfileActivity.class);
                intent.putExtra(App.EXTRA_ACTIVITY, App.FROM_LIST_ACTIVITY);
                startActivityForResult(intent, App.REQUEST_PROFILE);
                return true;
            case R.id.action_favori:
                App.toggleScope();
                if (App.scope == App.SCOPE_FAVORITES){
                    mMenu.findItem(R.id.action_favori).setIcon(R.drawable.ic_action_favorite_empty);
                } else {
                    mMenu.findItem(R.id.action_favori).setIcon(R.drawable.ic_action_favorite_full);
                }
                mAdapter.loadData();
                mAdapter.notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}