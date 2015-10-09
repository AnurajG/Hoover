package com.hoover.linkedinoauth;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyRecyclerAdapter extends  RecyclerView
.Adapter<MyRecyclerAdapter
.HoovViewHolder> {
	private List<String> hoovList;
	public MyRecyclerAdapter(List<String> hList) {
        this.hoovList = hList;
	}
	public static class HoovViewHolder extends RecyclerView.ViewHolder {
	     protected TextView vhoovtext;
	    public HoovViewHolder(View v) {
	          super(v);
	          vhoovtext = (TextView) v.findViewById(R.id.hoov_text);
	      }
	 }

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return hoovList.size();
	}

	@Override
	public void onBindViewHolder(HoovViewHolder arg0, int arg1) {
		// TODO Auto-generated method stub
		String s=hoovList.get(arg1);
		arg0.vhoovtext.setText(s);
		
	}

	@Override
	public HoovViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		View itemView = LayoutInflater.
                from(arg0.getContext()).
                inflate(R.layout.activity_cardview, arg0, false);

    return new HoovViewHolder(itemView);
		
	}

}
