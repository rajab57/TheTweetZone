package com.xylon.thetweetzone.activities;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.TwitterClient;
import com.xylon.thetweetzone.TwitterClientApp;
import com.xylon.thetweetzone.R.id;
import com.xylon.thetweetzone.R.layout;
import com.xylon.thetweetzone.R.menu;
import com.xylon.thetweetzone.adapters.TweetArrayAdapter;
import com.xylon.thetweetzone.fragments.ComposeTweetDialogFragment;
import com.xylon.thetweetzone.fragments.ComposeTweetDialogFragment.PostToTimelineListener;
import com.xylon.thetweetzone.helpers.EndlessScrollListener;
import com.xylon.thetweetzone.helpers.NetworkingUtils;
import com.xylon.thetweetzone.helpers.TwitterFetch;
import com.xylon.thetweetzone.models.Tweet;
import com.xylon.thetweetzone.models.TwitterDatabaseOperations;
import com.xylon.thetweetzone.models.User;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends Activity implements
		PostToTimelineListener, TwitterFetch {
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private TweetArrayAdapter aTweets;
	private PullToRefreshListView lvTweets;
	private boolean isRefreshing = false;
	private boolean onScroll = false;
	private User accountInfo;
	private static int REQUEST_CODE = 20;
		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// MUST request the feature before setting content view
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_timeline);
		getActionBar().setDisplayShowTitleEnabled(false);
		client = TwitterClientApp.getRestClient();
		lvTweets = (PullToRefreshListView) findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(this, tweets);
		System.out.println("onCreate call");
		//populateTimeline(1, -1); // OnScroll called onCreate
		lvTweets.setAdapter(aTweets);
		getUserAccountInfo();
		setupListeners();
	}

	private void setupListeners() {
		
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
						maxId = tweets.get(aTweets.getCount() - 1).getTid();

					}
				}
				System.out.println("OnScroll call");
				populateTimeline(sinceId, maxId);

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
				System.out.println("OnRefresh call");
				if (!tweets.isEmpty()) {
					// long sinceId = tweets.get(0).getUid();
					System.out.println("On refreshing");
					populateTimelineFromTwitter(tweets.get(0).getTid(), -1);  // fetch fresh tweets from the Twitter server
					isRefreshing = true;
				}
			}
		});

		// ** ONITEMCLICK
		lvTweets.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long rowId) {
				Intent i = new Intent(TimelineActivity.this,
						TweetActivity.class);
				++position;  // picks the previous item, probably starts at 0 instead of 1
				i.putExtra("position", position);
				Tweet item = (Tweet) parent.getItemAtPosition(position);
				i.putExtra("tweet", item);
				startActivityForResult(i, REQUEST_CODE);

			}

		});
	}

	// Should be called manually when an async task has started
	// Ensure that it is run on the UI thread
	public void showProgressBar() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					setProgressBarIndeterminateVisibility(true);
					return;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Should be called when an async task has finished
	public void hideProgressBar() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					setProgressBarIndeterminateVisibility(false);
					return;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void makeToast(final String msg) {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(TimelineActivity.this, msg, Toast.LENGTH_SHORT).show();
					return;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
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
	 * Returns a collection of the most recent Tweets and retweets posted by the
	 * authenticating user and the users they follow. The home timeline is
	 * central to how most users interact with the Twitter service.
	 * 
	 * @param sinceId
	 * @param maxId
	 */
	public void populateTimelineFromTwitter(long sinceId, long maxId) {
		
		long mSinceId;

		System.out.println("Twitter call for " + sinceId + ":" + maxId + ": " + tweets.size()) ;
		if (NetworkingUtils.isNetworkAvailable(this)) {
			showProgressBar(); // 1
			mSinceId = sinceId;
			if (sinceId == 1 )  { tweets.clear(); aTweets.clear(); }
			client.getHomeTimeline(sinceId, maxId,
					new JsonHttpResponseHandler() {

						@Override
						public void onSuccess(JSONArray json) {
							hideProgressBar(); // 1
							ArrayList<Tweet> ts = Tweet.fromJSONArray(json);
							makeToast("Fetched tweets from Twitter server");
							PostTweetsToDBTask dbTask = new PostTweetsToDBTask();
							dbTask.execute(tweets);
							for (Tweet tweet : ts) {
								isReTweet(tweet);
							}
							
							if ( !isRefreshing || aTweets.isEmpty())
								aTweets.addAll(ts);
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
								System.out.println("Refreshing is done");
								lvTweets.onRefreshComplete();
								lvTweets.setSelection(0);
								isRefreshing = false;
							}
						}

						@Override
						public void onFailure(Throwable e, String s) {
							Log.d("debug", e.toString());
							Log.d("debug", s.toString());
							lvTweets.onRefreshComplete();
							hideProgressBar(); // 1

						}
					});
		} else {
			Toast.makeText(getApplicationContext(), "Network not available",
					Toast.LENGTH_SHORT).show();
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
				System.out.println("db call for " + sinceId + ":" + maxId  +":" + tweets.get(0).getTid() + ": " + tweets.size());
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
				System.out.println("from db Tweets size " + tweets.size());
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

	public void getUserAccountInfo() {
		client.getUserAccountInformation(new JsonHttpResponseHandler() {
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

	public void isReTweet(Tweet tweet) {
		String body = tweet.getBody();
		if (body.startsWith("RT")) {
			int start = body.indexOf("@");
			int end = body.indexOf(":");
			String screenName = "";
			if ((start != -1) && (end != -1))
				screenName = body.substring(start + 1, end);
			User user = TwitterDatabaseOperations
					.getUserWithScreenName(screenName);
			if (user == null) {
				getUserFromScreenName(screenName);
			}

		}
	}
	
	
	public void getUserFromScreenName(final String sName) {
		TwitterClient client = TwitterClientApp.getRestClient();
		client.searchUserByScreenName(sName, new JsonHttpResponseHandler() {
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

	@Override
	public void fetchTwitterCallback(long sinceId, long maxId) {
		populateTimelineFromTwitter(sinceId,maxId);
		
	}

}
