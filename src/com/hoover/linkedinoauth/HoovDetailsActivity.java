package com.hoover.linkedinoauth;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.hoover.util.DataObject;

public class HoovDetailsActivity extends Activity{
	private TextView hoovText;
	private TextView hoovDate;

	private String mongoHoovId;
	Button rehoov_in;
	private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hoov_detail);
		mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
		
		mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

		hoovText = (TextView) findViewById(R.id.hoovtextView);
		hoovDate= (TextView) findViewById(R.id.hoovdateView2);
		Intent intent = getIntent();

		hoovText.setText(intent.getStringExtra("text"));
		hoovText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
			    getResources().getDimension(R.dimen.hoov_detail_text));
		hoovDate.setText(intent.getStringExtra("date"));

		mongoHoovId=intent.getStringExtra("mongodbHoovId");

		rehoov_in=(Button) findViewById(R.id.rehoov);
		rehoov_in.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(HoovDetailsActivity.this,HoovActivity.class);
				myIntent.putExtra("mongodbParentId",mongoHoovId);
				startActivity(myIntent); 

			} 

		});

	}
	
	 @Override
	    protected void onResume() {
	        super.onResume();
	        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
	                .MyClickListener() {
	            @Override
	            public void onItemClick(int position, View v) {
	                Log.i("card view", " Clicked on Item " + position);
	            }
	        });
	    }
	 
	 
	 
	 private ArrayList<DataObject> getDataSet() {
	        ArrayList results = new ArrayList<DataObject>();
	        for (int index = 0; index < 20; index++) {
	            DataObject obj = new DataObject("Some Primary Text " + index,
	                    "Secondary " + index);
	            results.add(index, obj);
	        }
	        return results;
	    }
}
