package com.pantagruel.megaoutrage.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

import com.pantagruel.megaoutrage.App;
import com.pantagruel.megaoutrage.data.Profile;
import com.pantagruel.megaoutrage.R;

public class ProfileActivity extends AppCompatActivity {

    private int mCallingActivity;
    public CheckBox[] mProfileCheckBox = new CheckBox[Profile.NB_CHECKBOX];
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // identity of the calling activity should be passed in extra.
        mCallingActivity = getIntent().getExtras().getInt(App.EXTRA_ACTIVITY, App.FROM_MAIN_ACTIVITY);

        // Init checkboxes from boolean profile values stored in shared preferences
        int boxId;
        String boxName;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(App.context);
        for (int i = 0; i<Profile.NB_CHECKBOX ; i++) {
            // find checkbox
            boxName = Profile.CHECKBOXNAME + Integer.toString(i);
            boxId = getResources().getIdentifier(boxName, "id", getPackageName());
            mProfileCheckBox[i] = findViewById(boxId);
            // set with boolean profile value
            String prefName = App.PROFILE_BOOL + Integer.toString(i);
            mProfileCheckBox[i].setChecked(sharedPref.getBoolean(prefName, true));
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public Intent getParentActivityIntent() {
        // Retrieve identity of the calling activity
        Intent i = null;
        switch (mCallingActivity) {
            case App.FROM_LIST_ACTIVITY:
                i = new Intent(this, ManageListActivity.class);
                break;
            case App.FROM_MAIN_ACTIVITY:
            default:
                i = new Intent(this, MainActivity.class);
                break;
        }
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return i;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_okprofile:
                boolean[] boolArray = new boolean[Profile.NB_CHECKBOX];

                //saving the profile booleans in shared preferences
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(App.context);
                SharedPreferences.Editor editor = sharedPref.edit();

                for (int i = 0; i<Profile.NB_CHECKBOX ; i++) {
                    String prefName = App.PROFILE_BOOL + Integer.toString(i);
                    boolArray[i] = mProfileCheckBox[i].isChecked();
                    editor.putBoolean(prefName, boolArray[i]);
                }
                editor.apply();
                App.setCurrentProfile(new Profile(boolArray));
                setResult(RESULT_OK);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
