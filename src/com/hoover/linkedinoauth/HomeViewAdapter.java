package com.hoover.linkedinoauth;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Observable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoover.linkedinoauth.MyRecyclerAdapter.HoovViewHolder;
import com.hoover.util.HoovChapter;
import com.parse.GetCallback;
//import android.support.v7.widget.RecyclerView;

public class HomeViewAdapter extends RecyclerView
.Adapter<HomeViewAdapter
.DataObjectHolder> {
	private static String LOG_TAG = "MyRecyclerViewAdapter";
	public ArrayList<HoovChapter> mDataset;
	private static MyClickListener myClickListener;
	private final Context mcontext;
	private final String currentuserId;
	//private final HoovChapter pHoov;
	private LayoutInflater inflater = null;
	
	public static class DataObjectHolder extends RecyclerView.ViewHolder
	implements View
	.OnClickListener {
		TextView text;
		TextView date;
		TextView uplabel;
		//TextView downlabel;
		TextView commentlabel;
		Button h_up_button;
		//Button h_down_button;
		TextView h_comment_count;
		Button h_comment_button;
		private final Context context;
		//private final HoovChapter parentHoov;

		//TextView dateTime;

		public DataObjectHolder(View itemView,final ArrayList<HoovChapter> mDataset1,final String currentUserId) {
			super(itemView);
			text = (TextView) itemView.findViewById(R.id.hoov_text);
			date = (TextView) itemView.findViewById(R.id.hoov_date);
			uplabel = (TextView) itemView.findViewById(R.id.hoov_up_count);
			//downlabel = (TextView) itemView.findViewById(R.id.hoov_down_count);
			commentlabel=(TextView)itemView.findViewById(R.id.hoov_comment_count);
			h_up_button=(Button)itemView.findViewById(R.id.hoov_up_button);
			//h_down_button=(Button)itemView.findViewById(R.id.hoov_down_button);

			h_comment_button=(Button)itemView.findViewById(R.id.hoov_comment_button);


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

					//int currDownCount=Integer.parseInt((String)downlabel.getText());
					String curr_up_value=(String)uplabel.getText();
					int final_up_value=Integer.parseInt(curr_up_value);

					//String curr_down_value=(String)downlabel.getText();
					//int final_down_value=Integer.parseInt(curr_down_value);
					if(h_up_button.getTag().equals(1)){
						//If like button is green
						final_up_value=Integer.parseInt(curr_up_value) - 1;
						h_up_button.setBackground(context.getResources().getDrawable(R.drawable.up));
						h_up_button.setTag(0);
					}else{
						//If like button is not green 
						final_up_value=Integer.parseInt(curr_up_value) + 1;
						h_up_button.setTag(1);
						h_up_button.setBackground(context.getResources().getDrawable(R.drawable.greenup));
						/*if(h_down_button.getTag().equals(1)){
							//if dislike button is red
							final_down_value= final_down_value- 1;
							downlabel.setText(String.valueOf(final_down_value));
							h_down_button.setBackground(context.getResources().getDrawable(R.drawable.down));
							h_down_button.setTag(0);;
						}*/
					}

					uplabel.setText(String.valueOf(final_up_value));
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

			/*View.OnClickListener downHandler = new View.OnClickListener() {
				public void onClick(View v) {

					int position = getPosition();
					int currUpCount=Integer.parseInt((String)uplabel.getText());

					String curr_down_value=(String)downlabel.getText();
					int final_down_value=Integer.parseInt(curr_down_value);
					String curr_up_value=(String)uplabel.getText();
					int final_up_value=Integer.parseInt(curr_up_value);


					if(h_down_button.getTag().equals(1)){
						//If dislike button is red
						final_down_value=Integer.parseInt(curr_down_value) - 1;
						h_down_button.setBackground(context.getResources().getDrawable(R.drawable.down));
						h_down_button.setTag(0);
					}else{
						final_down_value=Integer.parseInt(curr_down_value) + 1;
						h_down_button.setBackground(context.getResources().getDrawable(R.drawable.reddown));
						h_down_button.setTag(1);
						//If like button is green
						if(h_up_button.getTag().equals(1)){
							final_up_value=final_up_value - 1;
							uplabel.setText(String.valueOf(final_up_value));
							h_up_button.setBackground(context.getResources().getDrawable(R.drawable.up));
							h_up_button.setTag(0);
						}	
					}
					downlabel.setText(String.valueOf(final_down_value));

					int finalUpCount=final_up_value;
					Intent intent = new Intent(context, SaveLikeDislikeService.class);
					intent.putExtra(SaveLikeDislikeService.userId,mDataset1.get(position).hoovUserId);
					intent.putExtra(SaveLikeDislikeService.hoovId,mDataset1.get(position).mongoHoovId);
					intent.putExtra(SaveLikeDislikeService.insertDown,true);
					if(finalUpCount<currUpCount)
						intent.putExtra(SaveLikeDislikeService.deleteUp,true);
					context.startService(intent);	
				}
			};*/
			View.OnClickListener commentHandler = new View.OnClickListener() {
				public void onClick(View v) {
					HoovChapter selectedHoov = new HoovChapter();
					RelativeLayout rl=(RelativeLayout)v.getParent();
					final int position = getPosition();
					selectedHoov = mDataset1.get(position);
					Intent myIntent = new Intent(context, HoovDetailsActivity.class);
					myIntent.putExtra("chapter", selectedHoov);
					context.startActivity(myIntent);
				}
			};
			h_comment_button.setOnClickListener(commentHandler);
			h_up_button.setOnClickListener(upHandler);
			//h_down_button.setOnClickListener(downHandler);


		}

		@Override
		public void onClick(View v) {
			System.out.println("clicked1======================");
			myClickListener.onItemClick(getPosition(), v);
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
			holder.text.setText(mDataset.get(position).hoovText);
			holder.date.setText(mDataset.get(position).hoovDate);
			holder.uplabel.setText(""+mDataset.get(position).hoov_up_ids.size());
			//holder.downlabel.setText(""+mDataset.get(position).hoov_down_ids.size());
			holder.commentlabel.setText(""+mDataset.get(position).commentHoovIds.size());
			holder.h_up_button.setTag(0);
			//holder.h_down_button.setTag(0);
			if(mDataset.get(position).hoov_up_ids.contains(mDataset.get(position).hoovUserId)){
				holder.h_up_button.setBackground(mcontext.getResources().getDrawable(R.drawable.greenup));
				holder.h_up_button.setTag(1);
			}/*else if(mDataset.get(position).hoov_down_ids.contains(mDataset.get(position).hoovUserId)){
			holder.h_down_button.setBackground(context.getResources().getDrawable(R.drawable.reddown));
			holder.h_down_button.setTag(1);
		}*/
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