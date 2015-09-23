package com.hoover.linkedinoauth;

import com.parse.Parse;
import com.parse.ParseInstallation;

public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //FontsOverride.setDefaultFont(this, "NORMAL", "fonts/ChaletComprimeCologneSixty.ttf");
        Parse.initialize(this, "5QnLXvhEOlJovLNbSZrOfm67mtMbzeXOm6OwGp73", "ssC405N4t8vWYNoL7yPe6yzfNZ5eXcbbyImAQKXB");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        
    }
}