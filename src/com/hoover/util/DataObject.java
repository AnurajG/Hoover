package com.hoover.util;


public class DataObject {
    private String hoovText;
    private String hoovId;
    private String userId;
 
    public DataObject (String text1, String text2, String text3){
        hoovText = text1;
        hoovId = text2;
        userId =text3;
    }
 
    public String gethoovText() {
        return hoovText;
    }
 
    public void sethoovText(String mText1) {
        this.hoovText = mText1;
    }
 
    public String gethoovId() {
        return hoovId;
    }
 
    public void sethoovId(String mText2) {
        this.hoovId = mText2;
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}