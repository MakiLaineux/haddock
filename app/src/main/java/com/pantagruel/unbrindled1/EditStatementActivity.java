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

package com.pantagruel.unbrindled1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;



/**
 * Activity to edit an existing or create a new word.
 */
public class EditStatementActivity extends AppCompatActivity {

    private static final String TAG = EditStatementActivity.class.getSimpleName();
    private static final int NO_ID = -99;
    private static final String NO_WORD = "";

    private EditText mEditTextView;
    public CheckBox[] mProfileCheckBox = new CheckBox[Profile.NB_CHECKBOX];

    int mSatementStatus = Globals.STATUS_NONE;
    int mId = NO_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word);

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

        // Init display
        mEditTextView.setText("");
        for (int i=0; i<Profile.NB_CHECKBOX ; i++) mProfileCheckBox[i].setChecked(false);

        // If we are passed content, fill it in for the user to edit.
        if (extras != null) {
            mId = extras.getInt(Globals.EXTRA_ID, NO_ID);
            Statement statement = new Statement(
                    mId,
                    extras.getString(Globals.EXTRA_TEXT, NO_WORD),
                    extras.getString(Globals.EXTRA_PROFILE, NO_WORD),
                    extras.getInt(Globals.EXTRA_STATUS, Globals.STATUS_NONE)
            );
            mSatementStatus = statement.getStatus(); // remember whether an edited statement is marked
            if (mId != NO_ID && statement.getText() != NO_WORD) {
                mEditTextView.setText(statement.getText());
                for (int i=0; i<Profile.NB_CHECKBOX ; i++) {
                    mProfileCheckBox[i].setChecked(statement.getProfile().isChecked(i));
                }
            }
        }
    }

    /**
     *  Click handler for the Save button.
     *  Creates a new intent for the reply, adds the reply message to it as an extra,
     *  sets the intent result, and closes the activity.
     */
    public void returnReply(View view) {
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
        replyIntent.putExtra(Globals.EXTRA_ID, mId);
        replyIntent.putExtra(Globals.EXTRA_TEXT, text);
        replyIntent.putExtra(Globals.EXTRA_PROFILE, p.toString());
        replyIntent.putExtra(Globals.EXTRA_STATUS, p.toString());
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}

