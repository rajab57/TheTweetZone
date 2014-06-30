package com.xylon.thetweetzone.fragments;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xylon.thetweetzone.TwitterClientApp;
import com.xylon.thetweetzone.activities.TweetActivity;
import com.xylon.thetweetzone.api.TwitterClient;
import com.xylon.thetweetzone.helpers.NetworkingUtils;
import com.xylon.thetweetzone.models.Tweet;

public class SearchFragment extends TweetsListFragment {
	
	private static String TAG = SearchFragment.class.getSimpleName();
	private TwitterClient client;
	private String query ="";
	private static int REQUEST_CODE = 22;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		client = TwitterClientApp.getRestClient();

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Read query from Shared Preferences
		SharedPreferences prefs = getActivity().getSharedPreferences("query", Context.MODE_PRIVATE);
		query = prefs.getString("query","");
		if (!aTweets.isEmpty()) aTweets.clear();
		View v = super.onCreateView(inflater, container, savedInstanceState);
		setupListeners();
		return v;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(TAG, " Attaching to Activity");
	}
	
	
	@Override
	public void performActionOnScroll(long sinceId, long maxId) {
		searchTweets(query, sinceId, maxId);
	}
	
	@Override
	public void performActionOnRefresh(long sinceId, long maxId) {
		searchTweets(query, sinceId, maxId);
	}
	
	@Override
	public void performActionOnItemClick(Tweet item, int position) {
		Intent i = new Intent(getActivity(), TweetActivity.class);
		i.putExtra("position", position);
		i.putExtra("tweet", item);
		startActivityForResult(i, REQUEST_CODE);
	}
	
	/**
	 * Returns a collection of the most recent Tweets and retweets posted by the
	 * authenticating user and the users they follow. The home timeline is
	 * central to how most users interact with the Twitter service.
	 * 
	 * @param sinceId
	 * @param maxId
	 */
	public void searchTweets(String query, long sinceId, long maxId) {
		
		if (NetworkingUtils.isNetworkAvailable(getActivity())) {
			showProgressBar(); // 1
			if (sinceId == 1 )  { tweets.clear(); aTweets.clear(); }
			Log.d(TAG, "Calling twitter for search " + query);
			client.searchTweets(query,sinceId, maxId,
					new JsonHttpResponseHandler() {
				
						@Override
						public void onSuccess(int successCode, JSONObject jsonObj) {
							hideProgressBar(); // 1
							JSONArray json;
							try {
								json = jsonObj.getJSONArray("statuses");
								ArrayList<Tweet> ts = Tweet.fromJSONArray(json);
								handleListenerResults(ts);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}							
						}

						@Override
						public void onFailure(Throwable e, String s) {
							Log.d("debug", e.toString());
							Log.d("debug", s.toString());
							handleListenerFailure();
						}
					});
		} else {
			makeToast("Network not available");
		}
	}
}
