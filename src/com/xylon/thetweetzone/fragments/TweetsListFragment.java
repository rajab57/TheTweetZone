package com.xylon.thetweetzone.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.adapters.TweetArrayAdapter;
import com.xylon.thetweetzone.helpers.EndlessScrollListener;
import com.xylon.thetweetzone.listeners.CustomRefreshListener;
import com.xylon.thetweetzone.listeners.CustomScrollListener;
import com.xylon.thetweetzone.models.Tweet;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TweetsListFragment extends Fragment {

	private static String TAG = TweetsListFragment.class.getSimpleName();
	ArrayList<Tweet> tweets;
	TweetArrayAdapter aTweets;
	protected PullToRefreshListView lvTweets;
	private boolean isRefreshing = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(getActivity(), tweets);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout
		Log.d(TAG, "OnCreateView");
		View v = inflater.inflate(R.layout.fragment_tweetslist, container,
				false);
		lvTweets = (PullToRefreshListView) v.findViewById(R.id.lvTweets);
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
				Log.d(TAG, "onScroll");
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your
				// AdapterView
				long maxId = -1;
				int sinceId = -1;

				if (aTweets.isEmpty()) {
					sinceId = 1;
				} else {
					if (!isRefreshing) {
						maxId = tweets.get(aTweets.getCount() - 1).getTid();

					}
				}
				performActionOnScroll(sinceId, maxId+1);

			}
		});

		// ** REFRESH
		// Set a listener to be invoked when the list should be refreshed.
		lvTweets.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				Log.d(TAG, "onRefresh");
				// Your code to refresh the list contents
				// Make sure you call listView.onRefreshComplete()
				// once the loading is done. This can be done from here or any
				// place such as when the network request has completed
				// successfully.
				if (!tweets.isEmpty()) {
					// long sinceId = tweets.get(0).getUid();
					performActionOnRefresh(tweets.get(0).getTid(), -1); // fetch
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

				// TODO There should be a callback to the parent Activity
				// which calls this intent

				// Intent i = new Intent(,
				// TweetActivity.class);
				// ++position; // picks the previous item, probably starts at 0
				// instead of 1
				// i.putExtra("position", position);
				// Tweet item = (Tweet) parent.getItemAtPosition(position);
				// i.putExtra("tweet", item);
				// startActivityForResult(i, REQUEST_CODE);

			}

		});
	}

	public void performActionOnScroll(long sinceId, long maxId) {
		Log.d(TAG, "PerfomActionOnScroll");

	}

	public void performActionOnRefresh(long sinceId, long maxId) {

	}

}
