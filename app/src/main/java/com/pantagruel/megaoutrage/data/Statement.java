package com.pantagruel.megaoutrage.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.pantagruel.megaoutrage.App;
import com.pantagruel.megaoutrage.activities.ManageListActivity;

public class Statement implements Parcelable{
	private static final String TAG = App.TAG + Statement.class.getSimpleName();
	private int mId;
	private String mText;
	private Profile mProfile;
	private int mStatus;

	public Statement(int id, String text, String profile, int status) {
		this.mId = id;
		this.mText = text;
		this.mProfile = new Profile();
		this.setProfileFromString(profile);
		this.mStatus = status;
	}

	private Statement(Parcel in) {
		this.mId = in.readInt();
		this.mText = in.readString();
		this.mProfile = new Profile();
		this.setProfileFromString(in.readString());
		this.mStatus = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mId);
		dest.writeString(mText);
		dest.writeString(mProfile.toString());
		dest.writeInt(mStatus);
	}

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

	public void setProfileFromString(String s) {
		mProfile.feedFromString(s);}

	public void setStatus(int s) {
		mStatus =s;}

	public boolean matchesProfile(Profile p) {
		return p.matches(this.mProfile);
	}

}
