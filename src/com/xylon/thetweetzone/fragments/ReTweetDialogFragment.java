package com.xylon.thetweetzone.fragments;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.TwitterClientApp;
import com.xylon.thetweetzone.api.TwitterClient;
import com.xylon.thetweetzone.helpers.NetworkingUtils;

public class ReTweetDialogFragment extends DialogFragment {

	private static String TAG = ReTweetDialogFragment.class.getSimpleName();
	private long tweetId;

	public ReTweetDialogFragment( long id) {
		// Empty constructor required for DialogFragment
		tweetId = id;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_retweet, container);

		getDialog().setTitle("Retweet");
		//tweetId = getArguments().getInt("tweet_id");

		return view;
	}

	public void onCancel(View v) {
		this.dismiss();
	}

	public void onQuote(View v) {
		this.dismiss();
	}

	public void onRetweet(View v) {
		// Need Tweet ID
		// TODO This should happen in the parent Activity ????
		TwitterClient client = TwitterClientApp.getRestClient();
		if (NetworkingUtils.isNetworkAvailable(getActivity())) {
			// showProgressBar(); // 1
			client.reTweet(tweetId, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int arg0, JSONObject arg1) {
					Log.d(TAG, "Succesfully retweeted");
				}
			});
		}
	}
}
