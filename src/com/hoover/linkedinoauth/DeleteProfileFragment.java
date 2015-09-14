package com.hoover.linkedinoauth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class DeleteProfileFragment extends Fragment {
	String userId;
	public DeleteProfileFragment(){}
	public static final String TAG = DeleteProfileFragment.class.getSimpleName();
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_delete_profile, container, false);
        SharedPreferences preferences = this.getActivity().getSharedPreferences("user_info", 0);
        SharedPreferences.Editor editor=preferences.edit();
        editor.clear();
		editor.commit();
		String l = preferences.getString("userCompany", null);
	preferences = this.getActivity().getSharedPreferences("user_info", 0);
	String m=preferences.getString("userCompany", null);
	System.out.println("");
        Intent myIntent = new Intent(DeleteProfileFragment.this.getActivity(), DeleteProfileService.class);
        myIntent.putExtra("userId", this.userId);
        getActivity().startService(myIntent);
        
        return rootView;
    }
	/*public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		 Intent myIntent = new Intent(DeleteProfileFragment.this.getActivity(), Login.class);
	     startActivity(myIntent);
	}*/

	public static Fragment newInstance(String userId) {
		DeleteProfileFragment delfrag=new DeleteProfileFragment();
		delfrag.userId=userId;
		// TODO Auto-generated method stub
		return delfrag;
	}
}
