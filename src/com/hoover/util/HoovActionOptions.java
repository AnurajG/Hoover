package com.hoover.util;

public enum HoovActionOptions {
	FOLLOW("follow"),UNFOLLOW("Unfollow"),MARK_ABUSE("Mark Abuse"),DELETE("Delete");
	public String optionString;
	private HoovActionOptions(String s){
		this.optionString=s;
	}
	public String getoptionString(){
		return optionString;
	}
	
	

}
