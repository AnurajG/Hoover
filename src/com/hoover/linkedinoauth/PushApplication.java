package com.hoover.linkedinoauth;


import android.app.Application;

public class PushApplication extends Application {

	/*private final String VARIANT_ID       = "da36ac4a-721e-4951-b9ee-5d65631f3526";
	private final String SECRET           = "0459ac47-697f-48b9-94a0-655fdf140d54";
	private final String GCM_SENDER_ID    = "274446651998";
	private final String UNIFIED_PUSH_URL = "https://push-hooverest.rhcloud.com/ag-push/";
	private final String TAG="PUSH";

	@Override
	public void onCreate() {
		super.onCreate();

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
				Log.i(TAG, "Registration Succeeded!");
			}

			@Override
			public void onFailure(Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		});

	}*/
}
