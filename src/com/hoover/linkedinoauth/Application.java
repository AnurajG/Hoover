package com.hoover.linkedinoauth;

import java.net.URI;

import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.RegistrarManager;
import org.jboss.aerogear.android.unifiedpush.gcm.AeroGearGCMPushConfiguration;

import android.util.Log;

import com.parse.Parse;
import com.parse.ParseInstallation;

public final class Application extends android.app.Application {
	private final String VARIANT_ID       = "da36ac4a-721e-4951-b9ee-5d65631f3526";
	private final String SECRET           = "0459ac47-697f-48b9-94a0-655fdf140d54";
	private final String GCM_SENDER_ID    = "274446651998";
	private final String UNIFIED_PUSH_URL = "https://push-hooverest.rhcloud.com/ag-push/";
	@Override
	public void onCreate() {
		super.onCreate();
		//FontsOverride.setDefaultFont(this, "NORMAL", "fonts/ChaletComprimeCologneSixty.ttf");
		/*Parse.initialize(this, "5QnLXvhEOlJovLNbSZrOfm67mtMbzeXOm6OwGp73", "ssC405N4t8vWYNoL7yPe6yzfNZ5eXcbbyImAQKXB");
		ParseInstallation.getCurrentInstallation().saveInBackground();*/
		RegistrarManager.config("register", AeroGearGCMPushConfiguration.class)
		.setPushServerURI(URI.create(UNIFIED_PUSH_URL))
		.setSenderIds(GCM_SENDER_ID)
		.setVariantID(VARIANT_ID)
		.setSecret(SECRET)
		.asRegistrar();

		PushRegistrar registrar = RegistrarManager.getRegistrar("register");
		registrar.register(getApplicationContext(), new Callback<Void>() {
			@Override
			public void onSuccess(Void data) {
				System.out.println("success");
			}

			@Override
			public void onFailure(Exception e) {
				System.out.println("failure");
			}
		});

	}
}