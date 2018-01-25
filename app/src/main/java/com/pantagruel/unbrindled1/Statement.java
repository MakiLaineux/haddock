package com.pantagruel.unbrindled1;

public class Statement {
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
}
