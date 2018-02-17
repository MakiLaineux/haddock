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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pantagruel.megaoutrage.App;
import com.pantagruel.megaoutrage.R;
import com.pantagruel.megaoutrage.data.Statement;

import java.util.ArrayList;
import java.util.Random;

// Main activity
public class MainActivity extends AppCompatActivity {
    private static final String TAG = App.TAG + MainActivity.class.getSimpleName();

    // request codes used to call startActivityForResult, checked in onActivityResult
    private final int REQUEST_LIST = 1;
    private final int REQUEST_PROFILE = 2;

    // The statements to display
    private ArrayList<Statement> mStatementsStock;

    // UI elements
    private ImageButton mButton;
    private TextView mTextView, mTextStock;
    private LinearLayout mLinearLayoutDisplay;
    private Menu mMenu;

    // Current text displayed
    private String mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.txt_statement);
        mTextStock = findViewById(R.id.txt_stock);
        mButton = findViewById(R.id.button_statement);
        mLinearLayoutDisplay = findViewById(R.id.img_haddock);

        // if necessary, reload parameters saved in onSaveInstanceState
        if (savedInstanceState != null) {
            /* activate to start activity with current text, deactivate if display is animated
            mTextView.setText(savedInstanceState.getString("currentText"));
            */
            mTextView.setText("");
            mStatementsStock = savedInstanceState.getParcelableArrayList("currentList");
            mTextStock.setText(String.valueOf(mStatementsStock.size()));
        } else {
            loadStatements();
        }
        mLinearLayoutDisplay.setBackgroundResource(R.drawable.captain2);
        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                actionClick();
            }
        });
    }

    // Menu creation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                // pass the calling activity to permit correct up navigation
                intent.putExtra(ProfileActivity.CALLING_ACTIVITY, ProfileActivity.CALLING_ACTIVITY_MAIN);
                startActivityForResult(intent, REQUEST_PROFILE);
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

        // display one animation that will triger a second
        launchFirstAnimation();

        // display and remove from stock
        //mTextView.setText(s.getText());
        mStatementsStock.remove(i);

        mTextStock.setText(String.valueOf(mStatementsStock.size()));
    }

    private void launchFirstAnimation(){
        // icon animation
        float deg = mButton.getRotation() + 360F;
        mButton.animate().rotation(deg).setDuration(1000).setInterpolator(new LinearInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLinearLayoutDisplay.setBackgroundResource(R.drawable.captain1);
                        mTextView.setText(mText);
                        launchSecondAnimation();
                    };
                });;
    }

    private void launchSecondAnimation(){
        float deg = mButton.getRotation() + 360F;
        mButton.animate().rotation(deg).setDuration(1000).setInterpolator(new LinearInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLinearLayoutDisplay.setBackgroundResource(R.drawable.captain2);
                        mTextView.setText("");
                    };
                });;
    }

     // this method could be modified to run in a separate thread
    private void loadStatements() {
        // load only favorites
        mStatementsStock = App.sBaseLocale.getStatementList(App.sCurrentProfile, Statement.SCOPE_FAVORITES);
        mTextView.setText("");
        if (mStatementsStock != null){
            mTextStock.setText(String.valueOf(mStatementsStock.size()));
        }
    }
}
