package com.xylon.thetweetzone.fragments;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xylon.thetweetzone.TwitterClientApp;
import com.xylon.thetweetzone.api.TwitterClient;
import com.xylon.thetweetzone.helpers.EndlessScrollListener;
import com.xylon.thetweetzone.helpers.NetworkingUtils;
import com.xylon.thetweetzone.models.Tweet;

import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class UserTimelineFragment extends TweetsListFragment {
	
	private TwitterClient client;
	private boolean isRefreshing = false;
	private boolean onScroll = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		client = TwitterClientApp.getRestClient();
		
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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

	// Should be called manually when an async task has started
	// Ensure that it is run on the UI thread
	public void showProgressBar() {
		System.out.println("Implement Progress Bar");
	}
	
	public void hideProgressBar() {
		System.out.println("Implement Progress Bar");
	}
	public void makeToast(String msg) {
		System.out.println("Implement TOAST");
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
							makeToast("Fetched tweets from Twitter server");
							
							if ( !isRefreshing || aTweets.isEmpty())
								addAll(ts);
							else {
								Collections.reverse(ts);
								long start = tweets.get(0).getTid();
								for(Tweet tweet : ts) {
									if ( tweet.getTid() == start) {
										continue;
									}
									aTweets.insert(tweet, 0);
								}
							}
							if (isRefreshing == true) {
								isRefreshing = false;
								lvTweets.onRefreshComplete();
								lvTweets.setSelection(0);							
							}
						}

						@Override
						public void onFailure(Throwable e, String s) {
							Log.d("debug", e.toString());
							Log.d("debug", s.toString());
							lvTweets.onRefreshComplete();
							lvTweets.setSelection(0);
							hideProgressBar(); // 1

						}
					});
		} else {
			makeToast("Network not available");
		}
	}

}
