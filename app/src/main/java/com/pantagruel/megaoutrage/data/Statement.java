package com.pantagruel.megaoutrage.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.pantagruel.megaoutrage.App;
import com.pantagruel.megaoutrage.activities.ManageListActivity;
import com.pantagruel.megaoutrage.util.ProfileFormatException;

/**
 * A class representing a statement
 */
public class Statement implements Parcelable{

	// Status conditions
    public static final int STATUS_NORMAL = 0; // not marked as favorite
	public static final int STATUS_MARKED = 1; // marked as favorite
	// Favorites conditions
    public static final int SCOPE_ALL = 0; // search all statements
	public static final int SCOPE_FAVORITES = 1; // search only statements marked as favorites

	private static final String TAG = App.TAG + Statement.class.getSimpleName();


	/* Private fields include a text (literally the statement), a profile
	 which describes the allowed usages of the statement, and a status which for now describes
	 only whether the statement is marked as favorite or not
	  */
	private int mId;
	private String mText;
	private Profile mProfile;
	private int mStatus;

	// Constructor to use when creating a new Statement for database insertion (no existing id for now)
	public Statement() {
		this.mId = 0; // not used by the insert method
		this.mText = "";
		this.mProfile = new Profile(); // All usages allowed with this default constructor
		this.mStatus = STATUS_NORMAL;
	}

	// Constructor to use when Statement already exists in database
	public Statement(int id, String text, String profile, int status) {
		this.mId = id;
		this.mText = text;
		this.mProfile = new Profile();
		this.setProfileFromString(profile);
		this.mStatus = status;
	}

	// For Parcelable implementation
	private Statement(Parcel in) {
		this.mId = in.readInt();
		this.mText = in.readString();
		this.mProfile = new Profile();
		this.setProfileFromString(in.readString());
		this.mStatus = in.readInt();
	}

	// For Parcelable implementation
	@Override
	public int describeContents() {
		return 0;
	}

	// For Parcelable implementation
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mId);
		dest.writeString(mText);
		dest.writeString(mProfile.toString());
		dest.writeInt(mStatus);
	}

	// For Parcelable implementation
	public static final Parcelable.Creator<Statement> CREATOR = new Parcelable.Creator<Statement>() {
		public Statement createFromParcel(Parcel in) {
			return new Statement(in);
		}

		public Statement[] newArray(int size) {
			return new Statement[size];
		}
	};

	public int getId() {return mId;}

	public String getText() {
		return mText;
	}

	public String getTextProfile() {return mProfile.toString();}

	public Profile getProfile() {return mProfile;}

	public int getStatus() {
		return mStatus;
	}

	public void setId(int i) {
		mId = i;}

	public void setText(String t) {
		mText = t;}

	/**
	 * Set a Profile's content by updating all its possible usages.
	 * @param s a string of length NB_CHECKBOX whose chars represent each a usage.
	 *          The char is 'X' if the usage is allowed, ' ' otherwise.
	 *          If the length of the argument differs from NB_CHECKBOX, all the usage
	 *          are set to "allowed"
	 */
	public void setProfileFromString(String s) {
		try {
			mProfile.feedFromString(s);
		} catch (ProfileFormatException e){
			mProfile.clear();
		}
	}

	public void setProfile(Profile p) {mProfile = p;}

	public void setStatus(int s) {mStatus =s;}

	public boolean matchesProfile(Profile p) {
		return p.matches(this.mProfile);
	}

}
