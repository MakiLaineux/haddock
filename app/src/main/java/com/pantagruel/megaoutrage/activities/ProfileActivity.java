package com.pantagruel.megaoutrage.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

import com.pantagruel.megaoutrage.App;
import com.pantagruel.megaoutrage.data.Profile;
import com.pantagruel.megaoutrage.R;

/**
 * Activity to update the current Profile
 */
public class ProfileActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    // to manage return to the calling activity
    public final static String CALLING_ACTIVITY = "calling_activity";
    public final static String CALLING_ACTIVITY_MAIN = "main";
    public final static String CALLING_ACTIVITY_LIST = "list";
    private String mUpActivity;

    // UI fields
    private CheckBox[] mProfileCheckBox = new CheckBox[Profile.NB_CHECKBOX];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Check extras to remember the calling activity (for the case of later Up navigation)
        Bundle extras = getIntent().getExtras();
        if (extras == null){
            Log.wtf(TAG, "extras is null");
            finish();
        }
        mUpActivity = extras.getString(CALLING_ACTIVITY, CALLING_ACTIVITY_MAIN);


        // The current user preferences for Profile are stored in SharedPreferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);

        /* Set the UI Views : For the profiling checkboxes, xml id names follow a common pattern
        which consists of appending to a string constant consecutive numbers (starting with 0) */
        int boxId;
        String boxName;
        for (int i = 0; i<Profile.NB_CHECKBOX ; i++) {
            // find checkbox
            boxName = Profile.CHECKBOXNAME + Integer.toString(i);
            boxId = getResources().getIdentifier(boxName, "id", getPackageName());
            mProfileCheckBox[i] = findViewById(boxId);
            // Init checkbox from boolean profile values stored in shared preferences
            String prefName = Profile.PROFILE_BOOL + Integer.toString(i);
            mProfileCheckBox[i].setChecked(sharedPref.getBoolean(prefName, true));
        }
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
            case R.id.action_valid_profile:
                boolean[] boolArray = new boolean[Profile.NB_CHECKBOX];

                //saving the profile booleans in shared preferences
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
                SharedPreferences.Editor editor = sharedPref.edit();

                for (int i = 0; i<Profile.NB_CHECKBOX ; i++) {
                    String prefName = Profile.PROFILE_BOOL + Integer.toString(i);
                    boolArray[i] = mProfileCheckBox[i].isChecked();
                    editor.putBoolean(prefName, boolArray[i]);
                }
                editor.apply();
                App.setCurrentProfile(new Profile(boolArray));
                setResult(RESULT_OK);
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // To ensure a return to the calling activity (overriding manifest parent activity)
    @Override
    public Intent getParentActivityIntent() {
        Intent i;
        if (mUpActivity.equals(CALLING_ACTIVITY_MAIN)) {
            i = new Intent(this, MainActivity.class);
        } else {
            i = new Intent(this, ManageListActivity.class);
        }
        // For reusing the previous Activity (i.e. bringing it to the top
        // without re-creating a new instance) set these flags:
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return i;
    }
}
