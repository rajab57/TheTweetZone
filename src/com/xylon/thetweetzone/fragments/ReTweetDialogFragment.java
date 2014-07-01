package com.xylon.thetweetzone.fragments;

import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.TwitterClientApp;
import com.xylon.thetweetzone.api.TwitterClient;
import com.xylon.thetweetzone.helpers.NetworkingUtils;

public class ReTweetDialogFragment extends DialogFragment {

	private static String TAG = ReTweetDialogFragment.class.getSimpleName();
	private long tweetId;
	private ImageButton ivRetweet;
	private TextView tvRetweetCnt;
	private Button bCancel;
	private Button bQuote;
	private Button bRetweet;

	public ReTweetDialogFragment(long id, ImageButton img, TextView cnt) {
		// Empty constructor required for DialogFragment
		tweetId = id;
		ivRetweet = img;
		tvRetweetCnt = cnt;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_retweet, container);
		Dialog dialog = getDialog();
		dialog.setTitle("Retweet");
		dialog.show();
		getViews(view);
		setupListeners();
		return view;
	}

	public void getViews(View dialog) {
		bCancel = (Button) dialog.findViewById(R.id.btnCancel);
		bQuote = (Button) dialog.findViewById(R.id.btnQuote);
		bRetweet = (Button) dialog.findViewById(R.id.btnRetweet);
	}

	public void setupListeners() {
		bCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		bQuote.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		bRetweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TwitterClient client = TwitterClientApp.getRestClient();
				if (NetworkingUtils.isNetworkAvailable(getActivity())) {
					// showProgressBar(); // 1
					client.reTweet(tweetId, new JsonHttpResponseHandler() {

						public void onSuccess(JSONObject arg0) {
							handleResult();
						}

						@Override
						public void onSuccess(int arg0, JSONObject arg1) {
							handleResult();
						}
					});
				}
			}
		});

	}

	public void handleResult() {
		String cntStr = tvRetweetCnt.getText().toString();
		int prevCount = 0;
		if (cntStr != null && !cntStr.equals(""))
			prevCount = Integer.parseInt(cntStr);
		tvRetweetCnt.setText(String.valueOf(++prevCount));
		ivRetweet.setImageResource(R.drawable.retweet_sel);
		dismiss();
	}
}
