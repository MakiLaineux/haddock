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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.pantagruel.megaoutrage.App;
import com.pantagruel.megaoutrage.data.Profile;
import com.pantagruel.megaoutrage.R;
import com.pantagruel.megaoutrage.data.Statement;


/**
 * Activity to edit an existing or create a new word.
 */
public class EditStatementActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.pantagruel.unbrindled.ID";
    public static final String EXTRA_TEXT = "com.pantagruel.unbrindled.TEXT";
    public static final String EXTRA_PROFILE = "com.pantagruel.unbrindled.PROFILE";
    public static final String EXTRA_STATUS = "com.pantagruel.unbrindled.STATUS";
    public static final String EXTRA_REQUEST = "com.pantagruel.unbrindled.REQUEST";
    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;

    private final String TAG = this.getClass().getSimpleName();
    private EditText mEditTextView;
    private CheckBox[] mProfileCheckBox = new CheckBox[Profile.NB_CHECKBOX];
    private int mStatementStatus = App.STATUS_NORMAL;
    private int mId = App.NOID;
    private int mRequestCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_statement);

        mEditTextView = findViewById(R.id.edit_text);
        int boxId;
        String boxName;
        for (int i = 0; i<Profile.NB_CHECKBOX ; i++) {
            boxName = Profile.CHECKBOXNAME + Integer.toString(i);
            boxId = getResources().getIdentifier(boxName, "id", getPackageName());
            mProfileCheckBox[i] = findViewById(boxId);
        }

        // Get data sent from calling activity.
        Bundle extras = getIntent().getExtras();
        if (extras == null){
            finish();
        } else {
            mRequestCode = extras.getInt(EXTRA_REQUEST, 0);
        }

        // Init display
        switch (mRequestCode){
            case REQUEST_ADD:
                mEditTextView.setText("");
                for (int i=0; i<Profile.NB_CHECKBOX ; i++) mProfileCheckBox[i].setChecked(false);
                break;
            case REQUEST_EDIT:
                mId = extras.getInt(EXTRA_ID, App.NOID);
                Statement statement = new Statement(
                        mId,
                        extras.getString(EXTRA_TEXT),
                        extras.getString(EXTRA_PROFILE),
                        extras.getInt(EXTRA_STATUS, App.STATUS_NORMAL)
                );
                mStatementStatus = statement.getStatus(); // remember whether an edited statement is marked
                mEditTextView.setText(statement.getText());
                for (int i=0; i<Profile.NB_CHECKBOX ; i++) {
                    mProfileCheckBox[i].setChecked(statement.getProfile().isChecked(i));
                }
                break;
            default:
                finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_statement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_valid_statement:
                returnReply();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *  Click handler for the Save button.
     *  Creates a new intent for the reply, adds the reply message to it as an extra,
     *  sets the intent result, and closes the activity.
     */
    public void returnReply() {
        int boxId;
        String boxName;

        // Find the edited statement
        String text = ((EditText) findViewById(R.id.edit_text)).getText().toString();

        // Build a Profile object with the checkboxes inputs
        Profile p = new Profile();
        for (int i=0; i<Profile.NB_CHECKBOX ; i++) {
            boxName = Profile.CHECKBOXNAME + Integer.toString(i);
            boxId = getResources().getIdentifier(boxName, "id", getPackageName());
            mProfileCheckBox[i] = findViewById(boxId);
            p.setChecked(i, mProfileCheckBox[i].isChecked());
        }

        // Reply
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_ID, mId);
        replyIntent.putExtra(EXTRA_TEXT, text);
        replyIntent.putExtra(EXTRA_PROFILE, p.toString());
        replyIntent.putExtra(EXTRA_STATUS, mStatementStatus);
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}

