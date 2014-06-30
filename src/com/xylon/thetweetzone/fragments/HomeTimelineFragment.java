package com.xylon.thetweetzone.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xylon.thetweetzone.TwitterClientApp;
import com.xylon.thetweetzone.activities.TweetActivity;
import com.xylon.thetweetzone.api.TwitterClient;
import com.xylon.thetweetzone.fragments.ComposeTweetDialogFragment.PostToTimelineListener;
import com.xylon.thetweetzone.helpers.NetworkingUtils;
import com.xylon.thetweetzone.listeners.TwitterFetch;
import com.xylon.thetweetzone.models.Tweet;
import com.xylon.thetweetzone.models.TwitterDatabaseOperations;
import com.xylon.thetweetzone.models.User;

public class HomeTimelineFragment extends TweetsListFragment implements PostToTimelineListener ,TwitterFetch {
											
	
	private static String TAG = HomeTimelineFragment.class.getSimpleName();
	private TwitterClient client;
	private static int REQUEST_CODE =20;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		client = TwitterClientApp.getRestClient();
		Log.d (TAG, "OnCreate");
		//populateTimeline(1,-1);
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
		Log.d(TAG, "PerformActionOnScroll");
		populateTimeline(sinceId, maxId);
	}
	
	@Override
	public void performActionOnRefresh(long sinceId, long maxId) {
		// fetch fresh tweets from the Twitter server
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
			//showProgressBar(); // 1
			if (sinceId == 1 )  { tweets.clear(); aTweets.clear(); }
			client.getHomeTimeline(sinceId, maxId,
					new JsonHttpResponseHandler() {

						@Override
						public void onSuccess(JSONArray json) {
							//hideProgressBar(); // 1
							ArrayList<Tweet> ts = Tweet.fromJSONArray(json);
							//makeToast("Fetched tweets from Twitter server");
							PostTweetsToDBTask dbTask = new PostTweetsToDBTask();
							dbTask.execute(tweets);
							for (Tweet tweet : ts) {
								String screenName = tweet.isReTweet();
								if (screenName != null
										&& !screenName.equals("")) {
									User user = TwitterDatabaseOperations
											.getUserWithScreenName(screenName);
									if (user == null) {
										getUserFromScreenName(screenName);
									}
								}
							}
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


	private void populateTimeline(long sinceId, long maxId) {
		// check DB
		
		QueryTweetsFromDBTask dbTask = new QueryTweetsFromDBTask();
		dbTask.execute(sinceId, maxId, 20, this);
		// Check DB 
		// if not available, fetch from twitter if network avaialble ( done in postExecute of dbTask )	
	}
	private class PostTweetsToDBTask extends
			AsyncTask<ArrayList<Tweet>, Void, Void> {

		@Override
		protected Void doInBackground(ArrayList<Tweet>... params) {
			TwitterDatabaseOperations.batchInsertTweets(tweets);
			return null;
		}

		protected void onPostExecute() {
		}

	}

	/**
	 * Async Task to query the database for tweets
	 * 
	 * @param sinceId
	 *            int returns ids newer than
	 * @param maxId
	 *            Long returns ids older than
	 * @param count
	 *            Number of tweets to fetch from database
	 */
	private class QueryTweetsFromDBTask extends
			AsyncTask<Object, Void, ArrayList<Tweet>> {

		private TwitterFetch fetchTwitter;
		long sinceId;
	    long maxId;
		
		@Override
		protected ArrayList<Tweet> doInBackground(Object... params) {
			showProgressBar();// 3
			sinceId = (Long) params[0];
			maxId = (Long) params[1];
			int count = (Integer) params[2];
			fetchTwitter = (TwitterFetch) params[3];
			ArrayList<Tweet> ts = new ArrayList<Tweet>();
			if (sinceId == -1) {
				ts = (ArrayList<Tweet>) TwitterDatabaseOperations.getTweetsOlderThan(maxId, count);
			}
			else {
				ts = (ArrayList<Tweet>) TwitterDatabaseOperations.getTweetsNewerThan(sinceId, count);
			}
			return ts;
		}

		protected void onPostExecute(ArrayList<Tweet> result) {
			if ( result == null || result.isEmpty()) {
				makeToast("No requested data in the Database");
				// fetch from Twitter
				fetchTwitter.fetchTwitterCallback(sinceId, maxId);
			} else {
				makeToast("Data fetched from the Database ");
				aTweets.addAll(result);
			}
			hideProgressBar(); // 3
		}

	}

	/**
	 * Updates the authenticating user's current status, also known as tweeting
	 * 
	 * @param s
	 *            status message to be tweeted upto 140 chars
	 */
	@Override
	public void onPostToTimeline(String s) {
		showProgressBar(); // 4
		client.postStatusUpdate(s, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				populateTimeline(1, -1);
			}

			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				tweets.clear();
				// populateTimeline(1, -1); // there is delay in updates
				Tweet tweet = Tweet.fromJSON(response);
				aTweets.insert(tweet, 0);
				aTweets.notifyDataSetChanged();

			}

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", "Posting Tweet to Timeline failed");
				Log.d("debug", s.toString());

			}
		});
	}

	@Override
	public void fetchTwitterCallback(long sinceId, long maxId) {
		populateTimelineFromTwitter(sinceId,maxId);
		
	}
	
	public void getUserFromScreenName(final String sName) {
		TwitterClient client = TwitterClientApp.getRestClient();
		client.getUserProfileFromScreenName(sName, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				User user = User.fromJSON(json);
				// post it to database
				TwitterDatabaseOperations.insertUser(user);
			}
			@Override
			public void onSuccess(int statusCode, JSONObject json) {
				User user = User.fromJSON(json);
				// post it to database
				TwitterDatabaseOperations.insertUser(user);
			};			

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", "Posting Tweet to Timeline failed");
				Log.d("debug", s.toString());

			}
		});
	}

	
}
