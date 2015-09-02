package com.hoover.linkedinoauth;

import com.hoover.util.FontsOverride;

public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "NORMAL", "fonts/ChaletComprimeCologneSixty.ttf");
        
    }
}