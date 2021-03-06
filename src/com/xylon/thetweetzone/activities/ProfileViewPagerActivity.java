package com.xylon.thetweetzone.activities;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.TwitterClientApp;
import com.xylon.thetweetzone.adapters.ProfileViewPagerAdapter;
import com.xylon.thetweetzone.adapters.TweetArrayAdapter.ReplyToTweetListener;
import com.xylon.thetweetzone.fragments.ComposeTweetDialogFragment;
import com.xylon.thetweetzone.fragments.NoScrollUserTimelineFragment;
import com.xylon.thetweetzone.models.User;

public class ProfileViewPagerActivity extends FragmentActivity implements
		OnClickListener, ReplyToTweetListener {

	private static String TAG = ProfileViewPagerActivity.class.getSimpleName();
	private User accountInfo;
	private TextView tvTweetsCnt;
	private TextView tvFollowingCnt;
	private TextView tvFollowersCnt;
	private TextView tvFollowing;
	private TextView tvFollowers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getViews();
		Intent intent = getIntent();
		String screenName = intent.getStringExtra("screenName");
		if (screenName.equals("authorizedUser"))
			loadProfileInfo();
		else
			loadUserProfileInfo(screenName);
		// loadPage(); Of course you cannot call here, have to call once Profile is loaded. dah!!

	}

	private void getViews() {

		tvTweetsCnt = (TextView) findViewById(R.id.tvTweetsCnt);
		tvFollowingCnt = (TextView) findViewById(R.id.tvFollowingCnt);
		tvFollowersCnt = (TextView) findViewById(R.id.tvFollowersCnt);
		getResources().getColor(R.color.twitterGray);
		tvFollowing = (TextView) findViewById(R.id.tvFollowing);
		tvFollowers = (TextView) findViewById(R.id.tvFollowers);
		tvFollowing.setOnClickListener(this);
		tvFollowers.setOnClickListener(this);

	}

	private void loadProfileInfo() {
		TwitterClientApp.getRestClient().getUserAccountInformation(
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, JSONObject json) {

						accountInfo = User.fromJSON(json);
						setProfileInfo();
						loadPage();

					}
					@Override
					public void onSuccess(JSONObject json) {
						accountInfo = User.fromJSON(json);
						setProfileInfo();
						loadPage();
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
						loadPage();

					}
					
					@Override
					public void onSuccess(JSONObject json) {
						accountInfo = User.fromJSON(json);
						setProfileInfo();
						loadPage();
					}

					@Override
					public void onFailure(Throwable e, String s) {
						Log.d("debug", "Posting Tweet to Timeline failed");
						Log.d("debug", s.toString());

					}
					
					@Override
					public void onFailure(Throwable e, JSONObject json) {
						Log.d(TAG, json.toString());
					}
				});

	}

	public void loadPage() {
		ProfileViewPagerAdapter adapter = new ProfileViewPagerAdapter(
				accountInfo);

		//ViewPager vp = null;
		// TODO How to pass other data to Adapter ????
		final ViewPager vp = (ViewPager) findViewById(R.id.vpProfile);
		//vp.setUser(accountInfo);
		vp .setAdapter(adapter);

		if (accountInfo.getBannerUrl() != null
				&& !accountInfo.getBannerUrl().equals("")) {
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.loadImage(accountInfo.getBannerUrl(),
					new ImageLoadingListener() {

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							Log.d(TAG, "on loading complete");
							// super.onLoadingComplete(imageUri, view,
							// loadedImage);
							//vp.setBackground(new BitmapDrawable(this, loadedImage));
							vp.setBackgroundDrawable(new BitmapDrawable(
									loadedImage));

						}
					});
		} else {
			vp.setBackgroundColor(getResources().getColor(R.color.twitterProfileGray));
		}
		vp.setCurrentItem(0);

	}

	private void setProfileInfo() {
		if (accountInfo != null) {
			// getActionBar().setTitle("@" + accountInfo.getScreenName());
			getActionBar().setTitle("Profile");

			// TODO hardcoded grey. How to fetch from Resources and add to Html
			String text = "<font color='black'><b>"
					+ accountInfo.getTweetsCount()
					+ "<b></font><br><font color='#7D7D7D'>TWEETS</font>";
			tvTweetsCnt.setText(Html.fromHtml(text),
					TextView.BufferType.SPANNABLE);
			text = "<font color='black'><b>" + accountInfo.getFollowingCount()
					+ "<b></font><br><font color='#7D7D7D'>FOLLOWING</font>";
			tvFollowingCnt.setText(Html.fromHtml(text),
					TextView.BufferType.SPANNABLE);
			text = "<font color='black'><b>" + accountInfo.getFollowersCount()
					+ "<b></font><br><font color='#7D7D7D'>FOLLOWERS</font>";
			tvFollowersCnt.setText(Html.fromHtml(text),
					TextView.BufferType.SPANNABLE);
			// Begin the transaction
			NoScrollUserTimelineFragment fragment = new NoScrollUserTimelineFragment();
			Bundle args = new Bundle();
	        args.putString("screen_name", accountInfo.getScreenName());
	        fragment.setArguments(args);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			// Replace the container with the new fragment
			ft.replace(R.id.fragUsers, fragment);
			// or ft.add(R.id.your_placeholder, new FooFragment());
			// Execute the changes specified
			ft.commit();

		}
	}

	@Override
	public void onClick(View v) {
		if (accountInfo != null) {
			if (v.getId() == R.id.tvFollowing) {
				Intent intent = new Intent(this, UsersListActivity.class);
				intent.putExtra("user_name", accountInfo.getName());
				intent.putExtra("key", "Following");
				startActivity(intent);
			}
			if (v.getId() == R.id.tvFollowers) {
				Intent intent = new Intent(this, UsersListActivity.class);
				intent.putExtra("user_name", accountInfo.getName());
				intent.putExtra("key", "Followers");
				startActivity(intent);
			}
		}

	}
	
	// TODO How to avoid duplication!!
	@Override
	public void onReplyToTweet(String replyUserName, long statusId) {
		ComposeTweetDialogFragment dialogFragment = new ComposeTweetDialogFragment();
		Bundle args = new Bundle();
		args.putSerializable("user", accountInfo);
		args.putLong("status_id", statusId);
		args.putString("action", "reply");
		args.putString("reply_user", replyUserName);
		dialogFragment.setArguments(args);
		dialogFragment.show(getSupportFragmentManager(), "replyTweet");
	}

	
	

}
