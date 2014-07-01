package com.xylon.thetweetzone.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.fragments.UserListFragment;

public class UsersListActivity extends FragmentActivity {

	private static String TAG = UsersListActivity.class.getSimpleName();

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userslist);
		
		String userName = getIntent().getStringExtra("user_name");
		String key = getIntent().getStringExtra("key");
		UserListFragment usersFragment = new UserListFragment();
		Bundle args = new Bundle();
        args.putString("user_name", userName);
        args.putString("key",key);
        getActionBar().setTitle(key);
        usersFragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flUsersList, usersFragment);
        ft.commit();
	}
	

}
