package com.hoover.util;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.hoover.linkedinoauth.HoovDetailsActivity;

public class CardFlipListener implements AnimatorListener{

	private View view;
	private View card;
	private HoovChapter selectedHoov;
	private Context context;


	public CardFlipListener(View v, View card,HoovChapter hc, Context c){
		this.view=v;
		this.card=card;
		this.selectedHoov=hc;
		this.context=c;
	}
	@Override
	public void onAnimationStart(Animator animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationRepeat(Animator animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationEnd(Animator animation) {
		Intent myIntent = new Intent(context, HoovDetailsActivity.class);
		myIntent.putExtra("chapter", selectedHoov);
		Activity activity = (Activity) context;

		activity.startActivity(myIntent);
		activity.overridePendingTransition(0, 0);

		ObjectAnimator animation2 = ObjectAnimator.ofFloat(card, "rotationY", 180.0f, 360.0f);
		animation2.setDuration(1);
		animation2.setInterpolator(new AccelerateDecelerateInterpolator());
		animation2.start();
	}

	@Override
	public void onAnimationCancel(Animator animation) {
		// TODO Auto-generated method stub

	}


}
