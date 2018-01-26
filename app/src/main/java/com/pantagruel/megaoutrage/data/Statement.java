package com.pantagruel.megaoutrage.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.pantagruel.megaoutrage.App;

public class Statement implements Parcelable{
	private static String TAG = Statement.class.getSimpleName();
	private int id;
	private String text;
	private Profile profile;
	private int status;

	public Statement() {
		this.id = App.NOID;
		this.text = App.NOTEXT;
		this.status = App.STATUS_NONE;
		this.profile = new Profile();
	}

	public Statement(int id, String text, String profile, int status) {
		this.id = id;
		this.text = text;
		this.profile = new Profile();
		this.setProfileFromString(profile);
		this.status = status;
	}

	private Statement(Parcel in) {
		this.id = in.readInt();
		this.text = in.readString();
		this.profile = new Profile();
		this.setProfileFromString(in.readString());
		this.status = in.readInt();
	}

	public int getId() {return id;}
	public String getText() {
		return text;
	}
	public String getTextProfile() {return profile.toString();}
	public Profile getProfile() {return profile;}
	public int getStatus() {
		return status;
	}

	public void setId(int i) {id = i;}
	public void setText(String t) {text = t;}
	public void setProfileFromString(String s) {profile.feedFromString(s);}
	public void setStatus(int s) {status=s;}
	public boolean matchesProfile(Profile p) {
		return p.matches(this.profile);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(text);
		dest.writeString(profile.toString());
		dest.writeInt(status);
	}
	public static final Parcelable.Creator<Statement> CREATOR = new Parcelable.Creator<Statement>() {
		public Statement createFromParcel(Parcel in) {
			return new Statement(in);
		}

		public Statement[] newArray(int size) {
			return new Statement[size];
		}
	};
}
