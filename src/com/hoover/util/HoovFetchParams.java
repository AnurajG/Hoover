package com.hoover.util;

public class HoovFetchParams {
	public String city;
	public String comapny;
	public String parentId;
	public enum eOrder{NEW,TOP};
	public static enum eType{HOME,FOLLOW};
	public eOrder order; 
	public eType type; 
}
