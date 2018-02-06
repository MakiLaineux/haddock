package com.pantagruel.megaoutrage.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pantagruel.megaoutrage.App;
import com.pantagruel.megaoutrage.R;
import com.pantagruel.megaoutrage.data.Statement;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = App.TAG + MainActivity.class.getSimpleName();
    private final int REQUEST_LIST = 1;
    private final int REQUEST_PROFILE = 2;
    private ArrayList<Statement> mStatementsStock;
    private ImageButton mButton;
    private TextView mTextView, mTextStock;
    private Menu mMenu;
    private String mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.txt_statement);
        mTextStock = findViewById(R.id.txt_stock);
        mButton = findViewById(R.id.button_statement);

        if (savedInstanceState != null) {
            mTextView.setText(savedInstanceState.getString("currentText"));
            mStatementsStock = savedInstanceState.getParcelableArrayList("currentList");
            mTextStock.setText(String.valueOf(mStatementsStock.size()));
        } else {
            loadStatements();
        }
        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                actionClick();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (App.sScope == App.SCOPE_FAVORITES){
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
            case R.id.action_list:
                intent = new Intent(this, ManageListActivity.class);
                startActivityForResult(intent, REQUEST_LIST);
                return true;
            case R.id.action_profile:
                intent = new Intent(this, ProfileActivity.class);
                startActivityForResult(intent, REQUEST_PROFILE);
                return true;
            case R.id.action_favori:
                App.toggleScope();
                if (App.sScope == App.SCOPE_FAVORITES){
                    mMenu.findItem(R.id.action_favori).setIcon(R.drawable.ic_action_favorite_empty_white);
                } else {
                    mMenu.findItem(R.id.action_favori).setIcon(R.drawable.ic_action_favorite_full_white);
                }
                loadStatements();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_PROFILE:
            case REQUEST_LIST:
                loadStatements();
                mTextView.setText("");
                break;
            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentText", mText);
        outState.putParcelableArrayList("currentList", mStatementsStock);
    }


    private void actionClick() {
        // random statement selection
        if (mStatementsStock.size()==0) {
            loadStatements();
            if (mStatementsStock.size()==0) return;
        }
        Random r = new Random();
        int i = r.nextInt(mStatementsStock.size());
        Statement s = mStatementsStock.get(i);

        mText = s.getText();
        mTextView.setText("");

        // icon animation
        // Animation de l'icone synchro
        float deg = mButton.getRotation() + 360F;
        mButton.animate().rotation(deg).setDuration(1000).setInterpolator(new LinearInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mTextView.setText(mText);
                    };
                });;

        // display and remove from stock
        //mTextView.setText(s.getText());
        mStatementsStock.remove(i);
        mTextStock.setText(String.valueOf(mStatementsStock.size()));
    }

    private void loadStatements() {
        mStatementsStock = App.sBaseLocale.getStatementList(App.sCurrentProfile, App.sScope);
        mTextView.setText("");
        if (mStatementsStock != null){
            mTextStock.setText(String.valueOf(mStatementsStock.size()));
        }
    }
}
