package com.hoover.util;

import java.util.ArrayList;

public class Hoov {

	public String id;
	public String company;
	public String city;
	public String hoov;
	public String parentId;
	public ArrayList<String> hoovUpIds;
	public ArrayList<String> hoovDownIds;
	public ArrayList<String> commentHoovIds;
	public ArrayList<String> followerUserIds;
	public ArrayList<String> abuserUserIds;
	public Integer status;//1-submitted not checked 2-checked and to be moderated 3-contains not allowed text 0-fine 
	
}
