package com.xylon.thetweetzone.fragments;

import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;

import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.adapters.TweetArrayAdapter;
import com.xylon.thetweetzone.helpers.EndlessScrollListener;
import com.xylon.thetweetzone.models.Tweet;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TweetsListFragment extends Fragment {

	private static String TAG = TweetsListFragment.class.getSimpleName();
	ArrayList<Tweet> tweets;
	TweetArrayAdapter aTweets;
	protected PullToRefreshListView lvTweets;
	boolean isRefreshing = false;
	ProgressBar pb;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(getActivity(), tweets);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout
		View v = inflater.inflate(R.layout.fragment_tweetslist, container,
				false);
		lvTweets = (PullToRefreshListView) v.findViewById(R.id.lvTweets);
		pb = (ProgressBar)v.findViewById(R.id.pbTweetList);
		// populateTimeline(1, -1); // OnScroll called onCreate
		lvTweets.setAdapter(aTweets);

		return v;
	}

	public void addAll(ArrayList<Tweet> tweets) {

		aTweets.addAll(tweets);
	}

	protected void setupListeners() {

		// ** ONSCROLL
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your
				// AdapterView
				long maxId = -1;
				int sinceId = -1;

				if (aTweets.isEmpty()) {
					sinceId = 1;
				} else {
					if (!isRefreshing) {
						maxId = tweets.get(aTweets.getCount() - 1).getTid()-1;

					}
				}
				if (sinceId == 1 )  { tweets.clear(); aTweets.clear(); }
				performActionOnScroll(sinceId, maxId);

			}
		});

		// ** REFRESH
		// Set a listener to be invoked when the list should be refreshed.
		lvTweets.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Your code to refresh the list contents
				// Make sure you call listView.onRefreshComplete()
				// once the loading is done. This can be done from here or any
				// place such as when the network request has completed
				// successfully.
				if (!tweets.isEmpty()) {
					performActionOnRefresh(tweets.get(0).getTid()+1, -1); // fetch
																		// fresh
																		// tweets
																		// from
																		// the
																		// Twitter
																		// server
					isRefreshing = true;
				}
			}
		});

		// ** ONITEMCLICK
		lvTweets.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long rowId) {

				Tweet item = (Tweet) parent.getItemAtPosition(++position);
				performActionOnItemClick(item, position); //// picks the previous item, probably starts at 0

			}

		});
	}
	
	public void performActionOnItemClick(Tweet item,int position) {
		Log.d(TAG, "Implement your own method performActionOnItemClick");
	}

	public void performActionOnScroll(long sinceId, long maxId) {
		Log.d(TAG, "Implement your own method performActionOnScroll");

	}

	public void performActionOnRefresh(long sinceId, long maxId) {
		Log.d(TAG, "Implement your own method performActionOnRefresh");
	}
	
	// Should be called manually when an async task has started
	// Ensure that it is run on the UI thread
	public void showProgressBar() {
		pb.setVisibility(ProgressBar.VISIBLE);
	}
	
	public void hideProgressBar() {
		// run a background job and once complete
		pb.setVisibility(ProgressBar.GONE);
	}
	public void makeToast(String msg) {
		Log.d(TAG,"Implement TOAST");
	}
	
	
	protected void handleListenerResults(ArrayList<Tweet> ts) {
		hideProgressBar(); // 1
		makeToast("Fetched tweets from Twitter server");
//		for (Tweet tweet : ts ){
//			Log.d(TAG,String.valueOf(tweet.getTid()));
//		}
		if (!isRefreshing || aTweets.isEmpty())
			addAll(ts);
		else {
			Collections.reverse(ts);
			long start = tweets.get(0).getTid();
			for (Tweet tweet : ts) {
				if (tweet.getTid() == start) {
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
	
	protected void handleListenerFailure() {
		lvTweets.onRefreshComplete();
		lvTweets.setSelection(0);
		hideProgressBar(); // 1
	}
	
}
