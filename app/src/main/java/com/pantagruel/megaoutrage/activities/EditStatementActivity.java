/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pantagruel.megaoutrage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

import com.pantagruel.megaoutrage.App;
import com.pantagruel.megaoutrage.data.Profile;
import com.pantagruel.megaoutrage.R;
import com.pantagruel.megaoutrage.data.Statement;


/**
 * Activity to edit an existing Statement or create a new one.
 */
public class EditStatementActivity extends AppCompatActivity {

    public static final String EXTRA_REQUEST = "com.pantagruel.unbrindled.REQUEST";
    public static final String EXTRA_STATEMENT = "com.pantagruel.unbrindled.STATEMENT";
    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;
    private static final String TAG = App.TAG + EditStatementActivity.class.getSimpleName();

    // UI fields
    private CheckBox[] mProfileCheckBox = new CheckBox[Profile.NB_CHECKBOX];

    // a Statement instance which will normally be returned to the calling activity
    private Statement mStatement;

    // a field to store the nature of the current request (ADD or EDIT)
    private int mRequestCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_statement);

        // Access the UI views
        EditText editText = findViewById(R.id.edit_text);

        /* For the profiling checkboxes, xml id names follow a common pattern
        which consists of appending to a string constant consecutive numbers (starting with 0) */
        int boxId;
        String boxName;
        for (int i = 0; i<Profile.NB_CHECKBOX ; i++) {
            boxName = Profile.CHECKBOXNAME + Integer.toString(i);
            boxId = getResources().getIdentifier(boxName, "id", getPackageName());
            mProfileCheckBox[i] = findViewById(boxId);
        }

        /* Get the data sent by the calling activity : a request code and,
        if this is an edition of an existing statement, the Statement to edit.
        Otherwise create an new Statement
         */
        Bundle extras = getIntent().getExtras();
        if (extras == null){
            Log.wtf(TAG, "extras is null");
            finish();
        }

        mRequestCode = extras.getInt(EXTRA_REQUEST, 0);
        if (mRequestCode == REQUEST_EDIT) {
            mStatement = extras.getParcelable(EXTRA_STATEMENT); // existing statement
        } else {
            mStatement = new Statement(); // empty statement
            mStatement.setStatus(App.sScope); // mark as favorite if it's the current user preference
        }

        // Init display
        editText.setText(mStatement.getText());
        for (int i=0; i<Profile.NB_CHECKBOX ; i++) {
            mProfileCheckBox[i].setChecked(mStatement.getProfile().isChecked(i));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_statement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_valid_statement:
                returnReply();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     *  Return the edited Statement in an extra, sets the intent result, and finish the activity.
     */
    private void returnReply() {

        // Access the UI views and update the Statement with user input
        // Text part :
        mStatement.setText(((EditText) findViewById(R.id.edit_text)).getText().toString());

        // Profile part : first build a Profile object with the checkboxes inputs
        Profile p = new Profile();
        for (int i=0; i<Profile.NB_CHECKBOX ; i++) {
            String boxName = Profile.CHECKBOXNAME + Integer.toString(i);
            int boxId = getResources().getIdentifier(boxName, "id", getPackageName());
            mProfileCheckBox[i] = findViewById(boxId);
            p.setChecked(i, mProfileCheckBox[i].isChecked());
        }
        mStatement.setProfile(p);

        // No need to update the status

        // Prepare the reply intent and finish
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_STATEMENT, mStatement);
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}

