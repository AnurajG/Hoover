package com.hoover.linkedinoauth;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hoover.util.HoovChapter;
//import android.support.v7.widget.RecyclerView;

public class NotifyListViewAdapter extends RecyclerView
.Adapter<NotifyListViewAdapter
.DataObjectHolder> {
	private static String LOG_TAG = "MyRecyclerViewAdapter";
	public ArrayList<HoovChapter> mDataset;
	private static MyClickListener myClickListener;
	private final String currentuserId;
	//private final HoovChapter pHoov;

	public static class DataObjectHolder extends RecyclerView.ViewHolder
	implements View
	.OnClickListener {
		TextView text;
		TextView date;
		//Button h_down_button;

		private final Context context;


		public DataObjectHolder(View itemView,final ArrayList<HoovChapter> mDataset1,final String currentUserId) {
			super(itemView);
			text = (TextView) itemView.findViewById(R.id.notify_text);
			date = (TextView) itemView.findViewById(R.id.notify_date);


			context = itemView.getContext();
			//parentHoov=pHoov;

			//dateTime = (TextView) itemView.findViewById(R.id.textView2);
			Log.i(LOG_TAG, "Adding Listener");
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			myClickListener.onItemClick(getPosition(), v);
		}
	}

	public void setOnItemClickListener(MyClickListener myClickListener) {
		this.myClickListener = myClickListener;
	}

	public NotifyListViewAdapter(ArrayList<HoovChapter> myDataset, String id) {
		mDataset = myDataset;
		currentuserId=id;
		//pHoov=parentHoov;
	}

	@Override
	public DataObjectHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.notify_list, parent, false);

		DataObjectHolder dataObjectHolder = new DataObjectHolder(view,mDataset,currentuserId);
		return dataObjectHolder;
	}

	@Override
	public void onBindViewHolder(DataObjectHolder holder, int position) {
		holder.text.setText(mDataset.get(position).hoovText);
		holder.date.setText(mDataset.get(position).hoovDate);

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