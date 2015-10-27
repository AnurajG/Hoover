package com.hoover.SwipeContainer;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.hoover.linkedinoauth.HomeViewAdapter;
import com.hoover.linkedinoauth.R;

public class SwipeFlingAdapterView extends BaseFlingAdapterView {
	private int MAX_VISIBLE = 4;
    private int MIN_ADAPTER_STACK = 0;
    private float ROTATION_DEGREES = 15.f;
  private HomeViewAdapter mAdapter;
    private int LAST_OBJECT_IN_STACK = 0;
    private onFlingListener mFlingListener;
    //private AdapterDataSetObserver  mDataSetObserver;
    private AdapterDataObserver mDataSetObserver ;
    private boolean mInLayout = false;
    private View mActiveCard = null;
    private OnItemClickListener mOnItemClickListener;
    private FlingCardListener flingCardListener;
    private PointF mLastTouchPoint;
   
    public SwipeFlingAdapterView(Context context) {
        this(context, null);
    }

    public SwipeFlingAdapterView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.SwipeFlingStyle);
    }

    public SwipeFlingAdapterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeFlingAdapterView, defStyle, 0);
        MAX_VISIBLE = a.getInt(R.styleable.SwipeFlingAdapterView_max_visible, MAX_VISIBLE);
        MIN_ADAPTER_STACK = a.getInt(R.styleable.SwipeFlingAdapterView_min_adapter_stack, MIN_ADAPTER_STACK);
        ROTATION_DEGREES = a.getFloat(R.styleable.SwipeFlingAdapterView_rotation_degrees, ROTATION_DEGREES);
        a.recycle();
    }


    /**
     * A shortcut method to set both the listeners and the adapter.
     *
     * @param context The activity context which extends onFlingListener, OnItemClickListener or both
     * @param mAdapter The adapter you have to set.
     */
    public void init(final Context context, HomeViewAdapter mAdapter) {
        if(context instanceof onFlingListener) {
            mFlingListener = (onFlingListener) context;
        }else{
            throw new RuntimeException("Activity does not implement SwipeFlingAdapterView.onFlingListener");
        }
        if(context instanceof OnItemClickListener){
            mOnItemClickListener = (OnItemClickListener) context;
        }
        setAdapter(mAdapter);
    }

 	public View getSelectedView() {
        return mActiveCard;
    }


    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //super.onLayout(changed, left, top, right, bottom);
        // if we don't have an adapter, we don't need to do anything
        if (mAdapter == null) {
            return;
        }

        mInLayout = true;
        final int adapterCount = mAdapter.getItemCount();

        if(adapterCount == 0) {
            removeAllViewsInLayout();
        }else {
            View topCard = getChildAt(LAST_OBJECT_IN_STACK);
            if(mActiveCard!=null && topCard!=null && topCard==mActiveCard) {
                if (this.flingCardListener.isTouching()) {
                    PointF lastPoint = this.flingCardListener.getLastPoint();
                    if (this.mLastTouchPoint == null || !this.mLastTouchPoint.equals(lastPoint)) {
                        this.mLastTouchPoint = lastPoint;
                        removeViewsInLayout(0, LAST_OBJECT_IN_STACK);
                        layoutChildren(1, adapterCount);
                    }
                }
            }else{
                // Reset the UI and set top view listener
                removeAllViewsInLayout();
                layoutChildren(0, adapterCount);
                setTopView();
            }
        }

        mInLayout = false;
        
        if(adapterCount <= MIN_ADAPTER_STACK) mFlingListener.onAdapterAboutToEmpty(adapterCount);
    }


    private void layoutChildren(int startingIndex, int adapterCount){
    	int i=0;
        while (startingIndex < Math.min(adapterCount, MAX_VISIBLE) ) {
            View newUnderChild = mAdapter.getView(startingIndex, null, this);
            if (newUnderChild.getVisibility() != GONE) {
            	if(mAdapter.reload)
            		animate(newUnderChild,startingIndex);
               makeAndAddView(newUnderChild,i++);
               LAST_OBJECT_IN_STACK = startingIndex;
            }
            startingIndex++;
        }
    }

private void animate(View view, final int pos) {
    	view.animate().cancel();
    	view.setTranslationY(100.0f);//100
    	view.setAlpha(0);
    	view.animate().alpha(1.0f).translationY(0f).setDuration(2000).setStartDelay(pos * 100);
       
    }
   private void makeAndAddView(View child, int count) {
	   FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
        addViewInLayout(child, 0, lp, true);

        final boolean needToMeasure = child.isLayoutRequested();
        if (needToMeasure) {
            int childWidthSpec = getChildMeasureSpec(getWidthMeasureSpec(),
                    getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
                    lp.width);
            int childHeightSpec = getChildMeasureSpec(getHeightMeasureSpec(),
                    getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin,
                    lp.height);
            child.measure(childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }


        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();

        int gravity = lp.gravity;
        if (gravity == -1) {
            gravity = Gravity.TOP | Gravity.START;
        }


        int layoutDirection = getLayoutDirection();
        final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

        int childLeft;
        int childTop;
        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                childLeft = (getWidth() + getPaddingLeft() - getPaddingRight()  - w) / 2 +
                        lp.leftMargin - lp.rightMargin;
                childLeft+=(count*5);
                break;
            case Gravity.END:
                childLeft = getWidth() + getPaddingRight() - w - lp.rightMargin;
                break;
            case Gravity.START:
            default:
                childLeft = getPaddingLeft() + lp.leftMargin;
                break;
        }
        switch (verticalGravity) {
            case Gravity.CENTER_VERTICAL:
                childTop = (getHeight() + getPaddingTop() - getPaddingBottom()  - h) / 2 +
                        lp.topMargin - lp.bottomMargin;
                childTop+=(count*5);
                break;
            case Gravity.BOTTOM:
                childTop = getHeight() - getPaddingBottom() - h - lp.bottomMargin;
                break;
            case Gravity.TOP:
            default:
                childTop = getPaddingTop() + lp.topMargin;
                break;
        }

        child.layout(childLeft, childTop, childLeft + w, childTop + h);
    }




    /**
    *  Set the top view and add the fling listener
    */
    private void setTopView() {
        if(getChildCount()>0){

            mActiveCard = getChildAt(LAST_OBJECT_IN_STACK);
            if(mActiveCard!=null) {

                flingCardListener = new FlingCardListener(mActiveCard, mAdapter.getItem(0),
                        ROTATION_DEGREES, new FlingCardListener.FlingListener() {

                            @Override
                            public void onCardExited() {
                                mActiveCard = null;
                                mFlingListener.removeFirstObjectInAdapter();
                            }

                            @Override
                            public void leftExit(Object dataObject) {
                                mFlingListener.onLeftCardExit(dataObject);
                            }

                            @Override
                            public void rightExit(Object dataObject) {
                                mFlingListener.onRightCardExit(dataObject);
                            }

                            @Override
                            public void onClick(Object dataObject) {
                                if(mOnItemClickListener!=null)
                                    mOnItemClickListener.onItemClicked(0, dataObject);

                            }

                            @Override
                            public void onScroll(float scrollProgressPercent) {
                                mFlingListener.onScroll(scrollProgressPercent);
                            }
                        });

                mActiveCard.setOnTouchListener(flingCardListener);
            }
        }
    }

    public FlingCardListener getTopCardListener() throws NullPointerException{
        if(flingCardListener==null){
            throw new NullPointerException();
        }
        return flingCardListener;
    }

    public void setMaxVisible(int MAX_VISIBLE){
        this.MAX_VISIBLE = MAX_VISIBLE;
    }

    public void setMinStackInAdapter(int MIN_ADAPTER_STACK){
        this.MIN_ADAPTER_STACK = MIN_ADAPTER_STACK;
    }

    @Override
    public HomeViewAdapter getAdapter() {
        return mAdapter;
    }


    public void setAdapter(HomeViewAdapter adapter) {
    	super.setAdapter(adapter);
    	if (mAdapter != null && mDataSetObserver != null) {
    		mAdapter.unregisterAdapterDataObserver(mDataSetObserver);
    		mDataSetObserver = null;
    	}

    	mAdapter = adapter;

    	if (mAdapter != null  && mDataSetObserver == null) {
    		mDataSetObserver=new AdapterDataObserver() {
    			 @Override
    		        public void onChanged() {
    		        	 refreshDrawableState();
    		            requestLayout();
    		           
    		        }

    		       
    		};
    		mAdapter.registerAdapterDataObserver(mDataSetObserver);
    	}
    }

    public void setFlingListener(onFlingListener onFlingListener) {
        this.mFlingListener = onFlingListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }




    @Override
    public android.widget.FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameLayout.LayoutParams(getContext(), attrs);
    }


   private class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
        	 refreshDrawableState();
           // requestLayout();
           
        }

        @Override
        public void onInvalidated() {
        	 refreshDrawableState();
            //requestLayout();
            
        }

    }

    public interface OnItemClickListener {
        void onItemClicked(int itemPosition, Object dataObject);
    }

    public interface onFlingListener {
        void removeFirstObjectInAdapter();
        void onLeftCardExit(Object dataObject);
        void onRightCardExit(Object dataObject);
        void onAdapterAboutToEmpty(int itemsInAdapter);
        void onScroll(float scrollProgressPercent);
    }



}
