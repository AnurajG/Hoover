package com.hoover.linkedinoauth;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hoover.linkedinoauth.HoovDetailsActivity.DeleteCommentAsyncTask;
import com.hoover.util.HoovChapter;
//import android.support.v7.widget.RecyclerView;

public class HoovDetailsViewAdapter extends RecyclerView
.Adapter<HoovDetailsViewAdapter
.DataObjectHolder> {
	private static String LOG_TAG = "MyRecyclerViewAdapter";
	private ArrayList<HoovChapter> mDataset;
	private static MyClickListener myClickListener;
	private final Context context;
	private final String currentuserId;
	//private final HoovChapter pHoov;

	public static class DataObjectHolder extends RecyclerView.ViewHolder
	implements View
	.OnClickListener {
		TextView label;
		TextView uplabel;
		TextView downlabel;
		Button h_up_button;
		Button h_down_button;
		Button del;
		private final Context context;
		//private final HoovChapter parentHoov;

		//TextView dateTime;

		public DataObjectHolder(View itemView,final ArrayList<HoovChapter> mDataset1,final String currentUserId) {
			super(itemView);
			label = (TextView) itemView.findViewById(R.id.comment_hoov);
			uplabel = (TextView) itemView.findViewById(R.id.comment_up_count);
			downlabel = (TextView) itemView.findViewById(R.id.comment_down_count);
			h_up_button=(Button)itemView.findViewById(R.id.comment_up_button);
			h_down_button=(Button)itemView.findViewById(R.id.comment_down_button);
			del=(Button)itemView.findViewById(R.id.delete_comment_button);

			context = itemView.getContext();
			//parentHoov=pHoov;

			//dateTime = (TextView) itemView.findViewById(R.id.textView2);
			Log.i(LOG_TAG, "Adding Listener");
			itemView.setOnClickListener(this);


			View.OnClickListener delHandler = new View.OnClickListener() {
				public void onClick(View v) {
					int position = getPosition();
					String hoovid=mDataset1.get(position).mongoHoovId;
					((HoovDetailsActivity)context).deleteComment(position);
				}
			};
			View.OnClickListener upHandler = new View.OnClickListener() {
				public void onClick(View v) {
					//DataObjectHolder holder=(DataObjectHolder) v.getTag();
					int position = getPosition();

					int currDownCount=Integer.parseInt((String)downlabel.getText());
					String curr_up_value=(String)uplabel.getText();
					int final_up_value=Integer.parseInt(curr_up_value) + 1;
					uplabel.setText(String.valueOf(final_up_value));

					String curr_down_value=(String)downlabel.getText();
					int final_down_value=Integer.parseInt(curr_down_value);
					if(!h_down_button.isEnabled()){
						final_down_value= final_down_value- 1;
						downlabel.setText(String.valueOf(final_down_value));
						h_down_button.setEnabled(true);
						h_down_button.setBackground(context.getResources().getDrawable(R.drawable.down));
					}
					h_up_button.setBackground(context.getResources().getDrawable(R.drawable.greenup));
					h_up_button.setEnabled(false);
					int finalDownCount=final_down_value;


					Intent intent = new Intent(context, SaveLikeDislikeService.class);
					intent.putExtra(SaveLikeDislikeService.userId,mDataset1.get(position).hoovUserId);
					System.out.println(mDataset1.get(position).hoovUserId);
					intent.putExtra(SaveLikeDislikeService.hoovId,mDataset1.get(position).mongoHoovId);
					intent.putExtra(SaveLikeDislikeService.insertUp,true);
					if(finalDownCount<currDownCount)
						intent.putExtra(SaveLikeDislikeService.deleteDown,true);
					context.startService(intent);

				}
			};

			View.OnClickListener downHandler = new View.OnClickListener() {
				public void onClick(View v) {

					int position = getPosition();
					int currUpCount=Integer.parseInt((String)uplabel.getText());

					String curr_down_value=(String)downlabel.getText();
					int final_down_value=Integer.parseInt(curr_down_value) + 1;
					downlabel.setText(String.valueOf(final_down_value));
					String curr_up_value=(String)uplabel.getText();
					int final_up_value=Integer.parseInt(curr_up_value);
					if(!h_up_button.isEnabled()){
						final_up_value=final_up_value - 1;
						uplabel.setText(String.valueOf(final_up_value));
						h_up_button.setEnabled(true);
						h_up_button.setBackground(context.getResources().getDrawable(R.drawable.up));
					}
					h_down_button.setBackground(context.getResources().getDrawable(R.drawable.reddown));
					h_down_button.setEnabled(false);

					int finalUpCount=final_up_value;
					Intent intent = new Intent(context, SaveLikeDislikeService.class);
					intent.putExtra(SaveLikeDislikeService.userId,mDataset1.get(position).hoovUserId);
					intent.putExtra(SaveLikeDislikeService.hoovId,mDataset1.get(position).mongoHoovId);
					intent.putExtra(SaveLikeDislikeService.insertDown,true);
					if(finalUpCount<currUpCount)
						intent.putExtra(SaveLikeDislikeService.deleteUp,true);
					context.startService(intent);	
				}
			};



			h_up_button.setOnClickListener(upHandler);
			h_down_button.setOnClickListener(downHandler);
			del.setOnClickListener(delHandler);

		}

		@Override
		public void onClick(View v) {
			myClickListener.onItemClick(getPosition(), v);
		}
	}

	public void setOnItemClickListener(MyClickListener myClickListener) {
		this.myClickListener = myClickListener;
	}

	public HoovDetailsViewAdapter(ArrayList<HoovChapter> myDataset, Context mContext, String id) {
		mDataset = myDataset;
		context=mContext;
		currentuserId=id;
		//pHoov=parentHoov;
	}

	@Override
	public DataObjectHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.comment_list, parent, false);

		DataObjectHolder dataObjectHolder = new DataObjectHolder(view,mDataset,currentuserId);
		return dataObjectHolder;
	}

	@Override
	public void onBindViewHolder(DataObjectHolder holder, int position) {
		holder.label.setText(mDataset.get(position).hoovText);
		holder.uplabel.setText(""+mDataset.get(position).hoov_up_ids.size());
		holder.downlabel.setText(""+mDataset.get(position).hoov_down_ids.size());
		if(!mDataset.get(position).hoovUserId.equals(currentuserId)){
			holder.del.setVisibility(View.GONE);
		}
		if(mDataset.get(position).hoov_up_ids.contains(mDataset.get(position).hoovUserId)){
			holder.h_up_button.setBackground(context.getResources().getDrawable(R.drawable.greenup));
			holder.h_up_button.setEnabled(false);
		}else if(mDataset.get(position).hoov_down_ids.contains(mDataset.get(position).hoovUserId)){
			holder.h_down_button.setBackground(context.getResources().getDrawable(R.drawable.reddown));
			holder.h_down_button.setEnabled(false);	
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