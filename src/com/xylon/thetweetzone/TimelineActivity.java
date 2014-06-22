package com.xylon.thetweetzone;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xylon.thetweetzone.helpers.EndlessScrollListener;
import com.xylon.thetweetzone.models.Tweet;

public class TimelineActivity extends Activity {
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private TweetArrayAdapter  aTweets;
	private ListView lvTweets;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterClientApp.getRestClient();
		lvTweets = (ListView) findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(this, tweets);
		populateTimeline(1,-1);
		lvTweets.setAdapter(aTweets);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
	    @Override
	    public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
	    	long maxId = tweets.get(aTweets.getCount()-1).getUid();
	        populateTimeline(-1,maxId); 
              
	    }
        });
    }
	
	public void populateTimeline(int sinceId, long maxId) {
		client.getHomeTimeline(sinceId, maxId, new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(JSONArray json) {
				aTweets.addAll(Tweet.fromJSONArray(json));
			}
			
			@Override
			public void onFailure(Throwable e , String s) {
				Log.d("debug", e.toString());
				Log.d("debug", s.toString());
				
			}
		});
	}
}
