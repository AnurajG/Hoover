package com.hoover.linkedinoauth;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoover.util.CardFlipListener;
import com.hoover.util.HoovChapter;
//import android.support.v7.widget.RecyclerView;

public class HomeViewAdapter extends RecyclerView
.Adapter<HomeViewAdapter
.DataObjectHolder> {
	private static String LOG_TAG = "MyRecyclerViewAdapter";
	public ArrayList<HoovChapter> mDataset;
	private static MyClickListener myClickListener;
	private final Context mcontext;
	private final String currentuserId;
	private Interpolator mInterpolator=new LinearInterpolator();
	//private final HoovChapter pHoov;
	private LayoutInflater inflater = null;
	public boolean reload=false;

	public static class DataObjectHolder extends RecyclerView.ViewHolder{
		//ViewFlipper flipper;
		TextView fText;
		TextView fDate;
		TextView fUpLabel;
		TextView fCommenLabel;
		Button fUpButton;
		Button fDetails;
		Animator anim;
		CardView card;



		TextView fCommentCount;
		Button fCommentButton;
		private final Context context;
		//private final HoovChapter parentHoov;

		//TextView dateTime;

		public DataObjectHolder(View itemView,final ArrayList<HoovChapter> mDataset1,final String currentUserId) {
			super(itemView);
			//flipper=(ViewFlipper)itemView.findViewById(R.id.viewFlipper);
			//flipper.setDisplayedChild(0);
			fText = (TextView) itemView.findViewById(R.id.hoov_text);
			card = (CardView) itemView.findViewById(R.id.card_view);
			fDate = (TextView) itemView.findViewById(R.id.hoov_date);
			fUpLabel = (TextView) itemView.findViewById(R.id.hoov_up_count);
			//downlabel = (TextView) itemView.findViewById(R.id.hoov_down_count);
			fCommenLabel=(TextView)itemView.findViewById(R.id.hoov_comment_count);
			fUpButton=(Button)itemView.findViewById(R.id.hoov_up_button);
			fDetails=(Button)itemView.findViewById(R.id.detail);
			//h_down_button=(Button)itemView.findViewById(R.id.hoov_down_button);

			fCommentButton=(Button)itemView.findViewById(R.id.hoov_comment_button);

			context = itemView.getContext();
			//parentHoov=pHoov;

			//dateTime = (TextView) itemView.findViewById(R.id.textView2);
			Log.i(LOG_TAG, "Adding Listener");
			//itemView.setOnClickListener(this);


			View.OnClickListener delHandler = new View.OnClickListener() {
				public void onClick(View v) {
					int position = getLayoutPosition();
					String hoovid=mDataset1.get(position).mongoHoovId;
					((HoovDetailsActivity)context).deleteComment(position);
				}
			};

			View.OnClickListener detailsHandler = new View.OnClickListener() {
				public void onClick(View v) {


					ObjectAnimator animation = ObjectAnimator.ofFloat(card, "rotationY", 360.0f, 180.0f);
					int position = (Integer) v.getTag();
					HoovChapter selectedHoov = mDataset1.get(position);

					animation.addListener(new CardFlipListener(v, card,selectedHoov,context));
					animation.setDuration(250);
					//animation.setRepeatCount(ObjectAnimator.INFINITE);
					animation.setInterpolator(new AccelerateInterpolator());
					animation.start();
					//activity.overridePendingTransition(R.anim.from_middle,R.anim.to_middle);
				}
			};


			View.OnClickListener upHandler = new View.OnClickListener() {
				public void onClick(View v) {
					//DataObjectHolder holder=(DataObjectHolder) v.getTag();
					int position = (Integer) v.getTag();

					//int currDownCount=Integer.parseInt((String)downlabel.getText());
					String curr_up_value=(String)fUpLabel.getText();
					int final_up_value=Integer.parseInt(curr_up_value);

					//String curr_down_value=(String)downlabel.getText();
					//int final_down_value=Integer.parseInt(curr_down_value);
					if(fUpButton.getTag().equals(1)){
						//If like button is green
						final_up_value=Integer.parseInt(curr_up_value) - 1;
						fUpButton.setBackground(context.getResources().getDrawable(R.drawable.up));
						fUpButton.setTag(0);
					}else{
						//If like button is not green 
						final_up_value=Integer.parseInt(curr_up_value) + 1;
						fUpButton.setTag(1);
						fUpButton.setBackground(context.getResources().getDrawable(R.drawable.greenup));
						/*if(h_down_button.getTag().equals(1)){
							//if dislike button is red
							final_down_value= final_down_value- 1;
							downlabel.setText(String.valueOf(final_down_value));
							h_down_button.setBackground(context.getResources().getDrawable(R.drawable.down));
							h_down_button.setTag(0);;
						}*/
					}

					fUpLabel.setText(String.valueOf(final_up_value));
					//int finalDownCount=final_down_value;


					Intent intent = new Intent(context, SaveLikeDislikeService.class);
					intent.putExtra(SaveLikeDislikeService.userId,mDataset1.get(position).hoovUserId);
					System.out.println(mDataset1.get(position).hoovUserId);
					intent.putExtra(SaveLikeDislikeService.hoovId,mDataset1.get(position).mongoHoovId);
					intent.putExtra(SaveLikeDislikeService.insertUp,true);
					/*if(finalDownCount<currDownCount)
						intent.putExtra(SaveLikeDislikeService.deleteDown,true);*/
					context.startService(intent);

				}
			};


			View.OnClickListener commentHandler = new View.OnClickListener() {
				public void onClick(View v) {
					HoovChapter selectedHoov = new HoovChapter();
					RelativeLayout rl=(RelativeLayout)v.getParent();
					final int position = (Integer) v.getTag();
					selectedHoov = mDataset1.get(position);
					Intent myIntent = new Intent(context, HoovDetailsActivity.class);
					myIntent.putExtra("chapter", selectedHoov);
					context.startActivity(myIntent);
				}
			};
			fCommentButton.setOnClickListener(commentHandler);
			fUpButton.setOnClickListener(upHandler);
			fDetails.setOnClickListener(detailsHandler);
			//h_down_button.setOnClickListener(downHandler);


		}
	}

	public void setOnItemClickListener(MyClickListener myClickListener) {
		System.out.println("clicked2==================");

		this.myClickListener = myClickListener;
	}

	public HomeViewAdapter(ArrayList<HoovChapter> myDataset, Context mContext, String id) {
		mDataset = myDataset;
		mcontext=mContext;
		currentuserId=id;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//pHoov=parentHoov;
	}

	@Override
	public DataObjectHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		/*View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.hoov_list, parent, false);

		DataObjectHolder dataObjectHolder = new DataObjectHolder(view,mDataset,currentuserId);
		return dataObjectHolder;*/
		View itemView = inflater.
				inflate(R.layout.activity_cardview, parent, false);
		DataObjectHolder viewHolder=new DataObjectHolder(itemView,mDataset,currentuserId);
		itemView.setTag(viewHolder);
		return viewHolder ;
	}
	//New methods added for SwingFlingAdapterView
	public View getView(int position, View convertView, ViewGroup parent){
		if (convertView == null) {
			DataObjectHolder dh=onCreateViewHolder(parent, getItemViewType(position));
			convertView = dh.itemView;

		} else {
			onViewRecycled(getViewHolder(convertView));
		}
		//flipperTemp = (ViewFlipper) convertView.findViewById(R.id.viewFlipper);
		onBindViewHolder(getViewHolder(convertView), position);
		return convertView;
	}
	private DataObjectHolder getViewHolder(View view) {
		return (DataObjectHolder) view.getTag();
	}

	public HoovChapter getItem(int position) {
		return mDataset == null ? null : mDataset.get(position);
	}
	public void registerAdapterDataObserver (AdapterDataObserver observer){
		super.registerAdapterDataObserver(observer);
	}
	public void unregisterAdapterDataObserver (AdapterDataObserver observer){
		super.unregisterAdapterDataObserver(observer);
	}
	//=====================================
	@Override
	public void onBindViewHolder(DataObjectHolder holder, int position) {
		if(holder!=null){
			//holder.flipper.setDisplayedChild(0);
			holder.fText.setText(mDataset.get(position).hoovText);
			//holder.anim= new Animator[] { ObjectAnimator.ofFloat(view, "alpha", mFrom, 1f) };
			holder.fDate.setText(mDataset.get(position).hoovDate);
			holder.fUpLabel.setText(""+mDataset.get(position).hoov_up_ids.size());
			//holder.downlabel.setText(""+mDataset.get(position).hoov_down_ids.size());
			holder.fCommenLabel.setText(""+mDataset.get(position).commentHoovIds.size());
			holder.fUpButton.setTag(0);
			holder.fDetails.setTag(position);
			//holder.h_down_button.setTag(0);
			if(mDataset.get(position).hoov_up_ids.contains(mDataset.get(position).hoovUserId)){
				holder.fUpButton.setBackground(mcontext.getResources().getDrawable(R.drawable.greenup));
				holder.fUpButton.setTag(1);
			}/*else if(mDataset.get(position).hoov_down_ids.contains(mDataset.get(position).hoovUserId)){
			holder.h_down_button.setBackground(context.getResources().getDrawable(R.drawable.reddown));
			holder.h_down_button.setTag(1);
		}*/



			//holder.flipper.setFlipInterval(1000);
			//holder.flipper.startFlipping();
		}
		else{
			System.out.println("NULllllllll");
		}
	}

	public void addItem(HoovChapter dataObj, int index) {
		mDataset.add(index, dataObj);
		notifyItemInserted(index);
	}

	public void deleteItem(int index) {
		mDataset.remove(index);
		notifyItemRemoved(index);
	}


	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	public interface MyClickListener {
		public void onItemClick(int position, View v);
	}
}