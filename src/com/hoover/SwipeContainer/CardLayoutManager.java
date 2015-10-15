package com.hoover.SwipeContainer;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutParams;

public class CardLayoutManager extends RecyclerView.LayoutManager{

	@Override
	public LayoutParams generateDefaultLayoutParams() {
		return new RecyclerView.LayoutParams(
				RecyclerView.LayoutParams.WRAP_CONTENT,
				RecyclerView.LayoutParams.WRAP_CONTENT);
	}
}
