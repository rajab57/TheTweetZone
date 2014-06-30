package com.xylon.thetweetzone.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.TwitterClientApp;
import com.xylon.thetweetzone.adapters.UsersArrayAdapter;
import com.xylon.thetweetzone.api.ApiConstants.UserType;
import com.xylon.thetweetzone.api.TwitterClient;
import com.xylon.thetweetzone.helpers.NetworkingUtils;
import com.xylon.thetweetzone.models.User;

public class UserListFragment extends Fragment {
	private static String TAG = UserListFragment.class.getSimpleName();
	ArrayList<User> users;
	UsersArrayAdapter userAdapter;
	protected ListView lvUsers;
	TwitterClient client;
	String userName;
	String key; // followers or following

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userName = getArguments().getString("user_name", "");	
		key = getArguments().getString("key");
		users = new ArrayList<User>();
		userAdapter = new UsersArrayAdapter(getActivity(), users);
		client = TwitterClientApp.getRestClient();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout
		View v = inflater.inflate(R.layout.fragment_userslist, container,
				false);
		lvUsers = (ListView) v.findViewById(R.id.lvUsers);
		lvUsers.setAdapter(userAdapter);
		UserType userType = null;
		if ( key.equals("Followers"))
			userType = UserType.FOLLOWERS;
		else if (key.equals("Following"))
			userType = UserType.FOLLOWING;
		
		showUsersByType(userName, "", userType);
		return v;
	}
	
	public void addAll(ArrayList<User> users) {
		userAdapter.addAll(users);
	}
	
	public void showUsersByType(String userId, String screenName, UserType userType) {
		if (NetworkingUtils.isNetworkAvailable(getActivity())) {
			client.showUsersByType(userId, screenName, userType , new JsonHttpResponseHandler() {
				
				@Override
				public void onSuccess(int successCode, JSONArray jsonArray) {
					handleResult(jsonArray);
				}
				
				@Override
				public void onSuccess(int arg0, JSONObject arg1) {
					JSONArray json;
					try {
						json = arg1.getJSONArray("users");
						handleResult(json);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				@Override
				public void onSuccess(JSONObject arg0) {
					JSONArray json;
					try {
						json = arg0.getJSONArray("users");
						handleResult(json);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				@Override
				public void onSuccess(JSONArray jsonArray) {
					handleResult(jsonArray);
				}
				
				@Override
				public void onFailure(Throwable arg0, JSONObject arg1) {

					Log.d(TAG, arg1.toString());
					Log.d(TAG,arg0.toString());
				}
			});
		}
	}
	
	private void handleResult(JSONArray jsonArray) {
		userAdapter.clear();
		ArrayList<User>usrs = User.fromJSONArray(jsonArray);
		userAdapter.addAll(usrs);
	}


}
