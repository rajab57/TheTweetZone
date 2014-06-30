package com.xylon.thetweetzone.helpers;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.TwitterClientApp;
import com.xylon.thetweetzone.api.TwitterClient;
import com.xylon.thetweetzone.models.Tweet;

public class TweetActions {
	
	private static String TAG = TweetActions.class.getSimpleName();
	
	public static void favTweet(Context context, final Tweet tweet, final TextView favCountView, final ImageButton favImage) {
		TwitterClient client = TwitterClientApp.getRestClient();
		if (NetworkingUtils.isNetworkAvailable(context)) {
			client.favoriteTweet(tweet.getTid(), new JsonHttpResponseHandler() {
				
				@Override
				public void onSuccess(int arg0, JSONObject arg1) {
					int prevCount = tweet.getFavCount();
					favCountView.setText(String.valueOf(++prevCount));
					favImage.setImageResource(R.drawable.fav_sel);
				}
				
				@Override
				public void onFailure(Throwable arg0) {
					Log.d(TAG, "Favoriting Tweet failed");
				}			
			});
		}
	}
	
	public static void unFavTweet(Activity activity, final Tweet tweet, final TextView favCountView, final ImageButton favImage) {
		TwitterClient client = TwitterClientApp.getRestClient();
		if (NetworkingUtils.isNetworkAvailable(activity)) {
			client.unFavoriteTweet(tweet.getTid(), new JsonHttpResponseHandler() {
				
				@Override
				public void onSuccess(int arg0, JSONObject arg1) {
					int prevCount = tweet.getFavCount();
					favCountView.setText(String.valueOf(--prevCount));
					favImage.setImageResource(R.drawable.fav);
				}
				
				@Override
				public void onFailure(Throwable arg0) {
					Log.d(TAG, "Favoriting Tweet failed");
				}			
			});
		}
	}

}
