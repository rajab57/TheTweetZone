package com.xylon.thetweetzone;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xylon.thetweetzone.ComposeTweetDialogFragment.PostToTimelineListener;
import com.xylon.thetweetzone.helpers.EndlessScrollListener;
import com.xylon.thetweetzone.helpers.NetworkingUtils;
import com.xylon.thetweetzone.models.Tweet;
import com.xylon.thetweetzone.models.User;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends Activity implements PostToTimelineListener{
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private TweetArrayAdapter aTweets;
	private PullToRefreshListView lvTweets;
	private boolean isRefreshing = true;
	User accountInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterClientApp.getRestClient();
		lvTweets = (PullToRefreshListView) findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(this, tweets);
		populateTimeline(1, -1);
		lvTweets.setAdapter(aTweets);
		getUserAccountInfo();
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your
				// AdapterView
				long maxId = -1;
				int sinceId = -1;
				if(aTweets.isEmpty()) {
					sinceId = 1;
				} else {
					maxId = tweets.get(aTweets.getCount() - 1).getUid();
				}
				populateTimeline(sinceId, maxId);

			}
		});

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
					//long sinceId = tweets.get(0).getUid();
					populateTimeline(1,-1);
					isRefreshing = true;
				}
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.item_options, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		if (id == R.id.action_compose) {
			DialogFragment dialogFragment = new ComposeTweetDialogFragment();
			Bundle args = new Bundle();
		    args.putSerializable("user", accountInfo);
		    dialogFragment.setArguments(args);
			dialogFragment.show(getFragmentManager(), "composeTweet");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

	/**
	 * Returns a collection of the most recent Tweets and retweets posted by the authenticating user and 
	 * the users they follow. 
	 * The home timeline is central to how most users interact with the Twitter service.
	 * @param sinceId
	 * @param maxId
	 */
	public void populateTimeline(int sinceId, long maxId) {

		if (NetworkingUtils.isNetworkAvailable(this)) {
			client.getHomeTimeline(sinceId, maxId,
					new JsonHttpResponseHandler() {

						@Override
						public void onSuccess(JSONArray json) {
							aTweets.addAll(Tweet.fromJSONArray(json));
							System.out.println("In populateTimeline");
							if (isRefreshing == true) {
								lvTweets.onRefreshComplete();
								isRefreshing = false;
							}
						}

						@Override
						public void onFailure(Throwable e, String s) {
							Log.d("debug", e.toString());
							Log.d("debug", s.toString());
							lvTweets.onRefreshComplete();

						}
					});
		} else {
			// No network - fetch from the DB
		}
	}

	/**
	 * Updates the authenticating user's current status, also known as tweeting
	 * 
	 * @param s
	 * status message to be tweeted upto 140 chars
	 */
	@Override
	public void onPostToTimeline(String s) {
		client.postStatusUpdate(s, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				Log.d("DEBUG", "Successfully posted to Twitter");
				populateTimeline(1, -1);
			}
			
			@Override
			 public void onSuccess(int statusCode,  JSONObject response) {
				Log.d("DEBUG", "Successfully posted to Twitter 2");
				tweets.clear();
				populateTimeline(1, -1);  // there is delay in updates
				Tweet tweet = Tweet.fromJSON(response);
				tweets.add(tweet);
				aTweets.notifyDataSetChanged();
				
			}

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", "Posting Tweet to Timeline failed");
				Log.d("debug", s.toString());

			}
		});
	}
	
	public void getUserAccountInfo() {
		client.getUserAccountInformation( new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				accountInfo = User.fromJSON(json);
			}

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", "Posting Tweet to Timeline failed");
				Log.d("debug", s.toString());

			}
		});
	}

}
