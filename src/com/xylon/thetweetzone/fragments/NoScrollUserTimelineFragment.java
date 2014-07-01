package com.xylon.thetweetzone.fragments;

import java.util.ArrayList;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xylon.thetweetzone.TwitterClientApp;
import com.xylon.thetweetzone.api.TwitterClient;
import com.xylon.thetweetzone.helpers.NetworkingUtils;
import com.xylon.thetweetzone.models.Tweet;

public class NoScrollUserTimelineFragment extends TweetsListFragment {
	
	private TwitterClient client;
	private static String TAG = NoScrollUserTimelineFragment.class.getSimpleName();

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		client = TwitterClientApp.getRestClient();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		//setupListeners();
		populateTimelineFromTwitter(1,-1);
		return v;
	}
	
	@Override
	public void performActionOnScroll(long sinceId, long maxId) {
		
	}
	
	@Override
	public void performActionOnRefresh(long sinceId, long maxId) {
		
	}


	/**
	 * Returns a collection of the most recent Tweets and retweets posted from
	 *  The user timeline
	 * 
	 * @param sinceId
	 * @param maxId
	 */
	public void populateTimelineFromTwitter(long sinceId, long maxId) {
		
		if (NetworkingUtils.isNetworkAvailable(getActivity())) {
			showProgressBar(); // 1
			if (sinceId == 1 )  { tweets.clear(); aTweets.clear(); }
			client.getUserTimeline(sinceId, maxId,
					new JsonHttpResponseHandler() {

						@Override
						public void onSuccess(JSONArray json) {
							handleResults(json);
						}
						
						@Override
						public void onSuccess(int successCode, JSONArray json) {
							handleResults(json);
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
	
	public void handleResults(JSONArray json) {
		hideProgressBar(); // 1
		ArrayList<Tweet> ts = Tweet.fromJSONArray(json);
		// Send in only 3 results
		ArrayList<Tweet> ts3 = new ArrayList<Tweet>();
		for (int i = 0; i < 3; i++) {
			ts3.add(ts.get(i));
		}
		handleListenerResults(ts3);
		
	}

}
