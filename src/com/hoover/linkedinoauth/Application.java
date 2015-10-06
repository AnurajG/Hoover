package com.hoover.linkedinoauth;

import java.util.ArrayList;

import com.parse.Parse;
import com.parse.ParseInstallation;

public final class Application extends android.app.Application {
	private ArrayList<Integer> randArray;
	@Override
	public void onCreate() {
		super.onCreate();
		randArray=new ArrayList<Integer>();
		for (int i=1; i<=100; i++) {
			randArray.add(i);
		}
		//FontsOverride.setDefaultFont(this, "NORMAL", "fonts/ChaletComprimeCologneSixty.ttf");
		Parse.initialize(this, "5QnLXvhEOlJovLNbSZrOfm67mtMbzeXOm6OwGp73", "ssC405N4t8vWYNoL7yPe6yzfNZ5eXcbbyImAQKXB");
		ParseInstallation.getCurrentInstallation().saveInBackground();
	}
	public ArrayList<Integer> getRandArray() {
		return randArray;
	}
	public void setRandArray(ArrayList<Integer> randArray) {
		this.randArray = randArray;
	}
}