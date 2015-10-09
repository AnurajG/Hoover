package com.hoover.linkedinoauth;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RVdecoration extends RecyclerView.ItemDecoration{
	private final static int vertOverlap = -90;

	  @Override
	  public void getItemOffsets (Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

	    outRect.set(0, vertOverlap, 0, 0);

	  }

}
