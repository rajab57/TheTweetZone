package com.xylon.thetweetzone.activities;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.TwitterClientApp;
import com.xylon.thetweetzone.models.User;

public class ProfileActivity extends FragmentActivity {

	private User accountInfo;
	private ImageView ivProfileImage;
	private TextView tvUserName;
	private TextView tvScreenName;
	private TextView tvTweetsCnt;
	private TextView tvFollowingCnt;
	private TextView tvFollowersCnt;
	private int twitterGray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getViews();
		Intent intent = getIntent();
		String screenName = intent.getStringExtra("screenName");
		if(screenName.equals("authorizedUser"))
			loadProfileInfo();
		else
			loadUserProfileInfo(screenName);
	}

	

	private void getViews() {
		ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
		tvUserName = (TextView) findViewById(R.id.tvUserName);
		tvScreenName = (TextView) findViewById(R.id.tvScreenName);
		tvTweetsCnt = (TextView)findViewById(R.id.tvTweetsCnt);
		tvFollowingCnt = (TextView)findViewById(R.id.tvFollowingCnt);
		tvFollowersCnt = (TextView)findViewById(R.id.tvFollowersCnt);
		twitterGray = getResources().getColor(R.color.twitterGray);
		
	}

	private void loadProfileInfo() {
		TwitterClientApp.getRestClient().getUserAccountInformation(
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, JSONObject json) {
						accountInfo = User.fromJSON(json);
						setProfileInfo();

					}

					@Override
					public void onFailure(Throwable e, String s) {
						Log.d("debug", "Posting Tweet to Timeline failed");
						Log.d("debug", s.toString());

					}
				});

	}
	
	private void loadUserProfileInfo(String name) {
		TwitterClientApp.getRestClient().getUserProfileFromScreenName(name, 
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, JSONObject json) {
						accountInfo = User.fromJSON(json);
						setProfileInfo();

					}

					@Override
					public void onFailure(Throwable e, String s) {
						Log.d("debug", "Posting Tweet to Timeline failed");
						Log.d("debug", s.toString());

					}
				});
		
	}

	private void setProfileInfo() {
		if (accountInfo != null) {
			//getActionBar().setTitle("@" + accountInfo.getScreenName());
			getActionBar().setTitle("Profile");
			ivProfileImage.setImageResource(android.R.color.transparent);
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(accountInfo.getProfileImageUrl(),
					ivProfileImage);
			tvUserName.setText(accountInfo.getName());
			tvScreenName.setText("@" + accountInfo.getScreenName());
			// TODO hardcoded grey. How to fetch from Resources and add to Html
			String text = "<font color='black'><b>" + accountInfo.getTweetsCount() +  "<b></font><br><font color='#7D7D7D'>TWEETS</font>"; 
			tvTweetsCnt.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
			text = "<font color='black'><b>" + accountInfo.getFollowingCount() +  "<b></font><br><font color='#7D7D7D'>FOLLOWING</font>"; 
			tvFollowingCnt.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
			text = "<font color='black'><b>" + accountInfo.getFollowersCount() +  "<b></font><br><font color='#7D7D7D'>FOLLOWERS</font>"; 
			tvFollowersCnt.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
			
		}
	}
}
