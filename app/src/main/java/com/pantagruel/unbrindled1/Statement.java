package com.pantagruel.unbrindled1;

public class Statement {
	private int id;
	private String text;
	private Profile profile;

	public Statement() {
		this.profile = new Profile();
	}


	public Statement(String text, String profile) {
		this.text = text;
		this.profile = new Profile();
		this.setProfileFromString(profile);
	}


	public int getId() {return id;}
	public String getText() {
		return text;
	}
	public String getTextProfile() {return profile.toString();}
	public Profile getProfile() {return profile;}

	public void setId(int i) {id = i;}
	public void setText(String t) {text = t;}
	public void setProfileFromString(String s) {profile.feedFromString(s);}
	public boolean matchesProfile(Profile p) {
		return p.matches(this.profile);
	}
}
