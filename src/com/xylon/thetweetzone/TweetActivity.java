package com.xylon.thetweetzone;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xylon.thetweetzone.helpers.CommonUtils;
import com.xylon.thetweetzone.models.Tweet;

public class TweetActivity extends Activity {

	private TwitterClient client;
	private Button btnTweet;
	private EditText etTweetText;
	private TextView tvTextCount;
	private Tweet tweet;
	private int twitterGrey;
	private int twitterBlue;
	private int twitterRed;
	private static int MAX_COUNT = 140;
	private String initialStr;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet);
		
		tweet = (Tweet) getIntent().getSerializableExtra("tweet");
		twitterGrey = getResources().getColor(R.color.twitterGray);
		twitterBlue = getResources().getColor(R.color.twitterBlue);
		twitterRed = getResources().getColor(R.color.twitterRed);
		// get the views and populate
		addViews();

		// Add listeners
		addListeners();
		
		client = TwitterClientApp.getRestClient();
	}
	
	private void addListeners() {
		addTextWatchToTweet();
		OnTweet();
		etTweetText.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	etTweetText.setText("@" + tweet.getUser().getScreenName());
				etTweetText.setTextColor(getResources().getColor(android.R.color.black));
				etTweetText.setSelection(etTweetText.getText().length());
	        }

	    });
		
	}
	private void addViews() {
		TextView tvName = (TextView) findViewById(R.id.tvName);
		ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
		TextView tvBody = (TextView) findViewById(R.id.tvBody);
		TextView tvScreenName = (TextView) findViewById(R.id.tvScreenName);
		TextView tvCreatedAt = (TextView) findViewById(R.id.tvCreatedAt);
		TextView tvRetweetCount = (TextView)findViewById(R.id.tvRetweetCount);
		TextView tvFavCount = (TextView)findViewById(R.id.tvFavCount);
		tvTextCount = (TextView) findViewById(R.id.tvTextCount);
		etTweetText = (EditText) findViewById(R.id.etTweetText);
		btnTweet = (Button) findViewById(R.id.btnTweet);
		WebView wvImage = (WebView)findViewById(R.id.wvImage);
		
		// populate view with tweet data
		ivProfileImage.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(),
				ivProfileImage);
		tvName.setText(tweet.getUser().getName());
		tvScreenName.setText("@" + tweet.getUser().getScreenName());
		tvBody.setText(tweet.getBody());
		Linkify.addLinks(tvBody,Linkify.WEB_URLS);  // Make links active
		tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
		tvFavCount.setText(String.valueOf(tweet.getFavCount()) );
		//Tue Aug 28 19:59:34 +0000 2012
		String strTime = CommonUtils.convertDateFormat(tweet.getCreatedAt(),"EEE MMM d HH:mm:ss Z yyyy","h:mm a" );
		String strDate = CommonUtils.convertDateFormat(tweet.getCreatedAt(),"EEE MMM d HH:mm:ss Z yyyy","dd MMM yy" );
		tvCreatedAt.setText(strTime + " ¥ " + strDate);
		initialStr = "Reply to " + tweet.getUser().getScreenName();
		etTweetText.setText(initialStr);
		etTweetText.setTextColor(twitterGrey);
		disableButton();
		// Embed images
		if (tweet.getUrl() != null && tweet.getUrl().length() > 0) {
			wvImage.setVisibility(View.VISIBLE);
			wvImage.getSettings().setJavaScriptEnabled(true);
			wvImage.loadUrl(tweet.getUrl());
			wvImage.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return false;
				}
			});
		} else {
			wvImage.setVisibility(View.GONE);
		}
	}

	public void addTextWatchToTweet() {
		etTweetText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
				int numChar = s.length();
				int leftCount = MAX_COUNT - numChar;
				customizeTextCountAppearance(numChar, leftCount);
				if (numChar > 0 && numChar <= MAX_COUNT && !btnTweet.isEnabled()) {
					enableButton();
				}
				else if (numChar <= 0 && numChar > MAX_COUNT && btnTweet.isEnabled()) {
					disableButton();
				}
				
			}
			
		});
	}
	
	public void customizeTextCountAppearance(int textcount, int leftCount) {
		tvTextCount.setText(String.valueOf(leftCount) );
		if ( textcount > MAX_COUNT ) {
			tvTextCount.setTextColor(twitterRed);
		} else
			tvTextCount.setTextColor(twitterGrey);
		
	}
	
	private void enableButton() {
		btnTweet.setEnabled(true);
		btnTweet.setTextColor(twitterBlue);
	}
	
	private void disableButton() {
		btnTweet.setEnabled(false);
		btnTweet.setTextColor(twitterGrey);
	}
	
	public void OnTweet() {
		btnTweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				replyTo(etTweetText.getText().toString(), tweet.getUser().getScreenName());
				customizeTextCountAppearance(0, 140);
				etTweetText.setText("");
				disableButton();
				
			}
		});
	}
	
	
	public void replyTo(String tweet, String replyId) {
		client.postStatusUpdateInReply(tweet, replyId ,new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				Log.d("DEBUG", "Successfully posted to Twitter");
				
			}

			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				Log.d("DEBUG", "Successfully posted to Twitter 2");
			}

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", "Posting Tweet to Timeline failed");
				Log.d("debug", s.toString());

			}
		});
	}

}
