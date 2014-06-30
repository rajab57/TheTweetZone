package com.xylon.thetweetzone.fragments;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.Intent;
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

public class UserTimelineFragment extends TweetsListFragment {
	
	private TwitterClient client;
	private static int REQUEST_CODE = 23;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		client = TwitterClientApp.getRestClient();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		setupListeners();
		return v;
	}
	
	@Override
	public void performActionOnScroll(long sinceId, long maxId) {
		populateTimelineFromTwitter(sinceId, maxId);
	}
	
	@Override
	public void performActionOnRefresh(long sinceId, long maxId) {
		populateTimelineFromTwitter(sinceId, maxId);
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
	public void populateTimelineFromTwitter(long sinceId, long maxId) {
		
		if (NetworkingUtils.isNetworkAvailable(getActivity())) {
			showProgressBar(); // 1
			if (sinceId == 1 )  { tweets.clear(); aTweets.clear(); }
			client.getUserTimeline(sinceId, maxId,
					new JsonHttpResponseHandler() {

						@Override
						public void onSuccess(JSONArray json) {
							hideProgressBar(); // 1
							ArrayList<Tweet> ts = Tweet.fromJSONArray(json);
							// Send in only 3 results
							
							handleListenerResults(ts);
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
