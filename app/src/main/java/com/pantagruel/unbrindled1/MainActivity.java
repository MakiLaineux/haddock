package com.pantagruel.unbrindled1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private Vector<Statement> mStatementsStock;
    private ImageButton mButton;
    private TextView mTextView, mTextStock;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.txt_statement);
        mTextStock = findViewById(R.id.txt_stock);
        mButton = findViewById(R.id.button_statement);

        loadStatements();

        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                actionClick();
            }
        });
    }

    private void actionClick() {

        // icon animation
        // Animation de l'icone synchro
        float deg = mButton.getRotation() + 360F;
        mButton.animate().rotation(deg).setDuration(1000).setInterpolator(new AccelerateDecelerateInterpolator());
        // random statement selection
        if (mStatementsStock.size()==0)
            return;
        Random r = new Random();
        int i = r.nextInt(mStatementsStock.size());
        Statement s = mStatementsStock.get(i);

        // display and remove from stock
        mTextView.setText(s.getText());
        mStatementsStock.remove(i);
        mTextStock.setText(""+mStatementsStock.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            case R.id.action_list:
                intent = new Intent(this, ManageListActivity.class);
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivityForResult(intent, App.REQUEST_LIST);
                return true;
            case R.id.action_profile:
                intent = new Intent(this, ProfileActivity.class);
                intent.putExtra(App.EXTRA_ACTIVITY, App.FROM_MAIN_ACTIVITY);
                startActivityForResult(intent, App.REQUEST_PROFILE);
                return true;
            case R.id.action_favori:
                App.toggleScope();
                if (App.scope == App.SCOPE_FAVORITES){
                    mMenu.findItem(R.id.action_favori).setIcon(R.drawable.ic_action_favorite_empty);
                } else {
                    mMenu.findItem(R.id.action_favori).setIcon(R.drawable.ic_action_favorite_full);
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
            case App.REQUEST_PROFILE:
            case App.REQUEST_LIST:
                loadStatements();
                mTextView.setText("");
                break;
            default:
                break;
        }
    }

    private void loadStatements() {
        mStatementsStock = App.mBaseLocale.getStatementVector();
        mTextView.setText("");
        if (mStatementsStock != null){
            mTextStock.setText(""+mStatementsStock.size());
        }
    }
}
