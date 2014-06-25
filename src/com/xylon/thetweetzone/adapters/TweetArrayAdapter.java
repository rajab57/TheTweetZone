package com.xylon.thetweetzone.adapters;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.TwitterClient;
import com.xylon.thetweetzone.TwitterClientApp;
import com.xylon.thetweetzone.models.Tweet;
import com.xylon.thetweetzone.models.TwitterDatabaseOperations;
import com.xylon.thetweetzone.models.User;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {
	
	private User currentUser;

	public TweetArrayAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);
	}
	
	// View lookup cache
    private static class ViewHolder {
    	ImageView ivProfileImage;
    	TextView tvUserName ;
    	TextView tvOtherText;
    	TextView tvBody;
    	TextView tvTimeAgo;
    	TextView tvReTweetName;
    	
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Tweet tweet = getItem(position);
		ViewHolder viewHolder; // view lookup cache stored in tag
		if (convertView == null ) {
			viewHolder = new ViewHolder();
			LayoutInflater inflator = LayoutInflater.from(getContext());
			convertView = inflator.inflate(R.layout.tweet_item, parent, false);
			viewHolder.ivProfileImage = (ImageView)convertView.findViewById(R.id.ivProfile);
			viewHolder.tvUserName = (TextView)convertView.findViewById(R.id.tvUserName);
			viewHolder.tvOtherText = (TextView)convertView.findViewById(R.id.tvOtherText);
			viewHolder.tvBody = (TextView)convertView.findViewById(R.id.tvBody);
			viewHolder.tvTimeAgo = (TextView)convertView.findViewById(R.id.tvTimeAgo);
			viewHolder.tvReTweetName = (TextView)convertView.findViewById(R.id.tvReTweetName);
			viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		

		ImageLoader imageLoader = ImageLoader.getInstance();
		//populate view with tweet data
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), viewHolder.ivProfileImage);
		
		User user = isReTweet(tweet);
		if ( user == null  ) {
			viewHolder.tvUserName.setText(tweet.getUser().getName());
			viewHolder.tvOtherText.setText("@" + tweet.getUser().getScreenName());
			viewHolder.tvBody.setText(tweet.getBody());
			viewHolder.tvReTweetName.setText("");
		}
		else {
			viewHolder.tvUserName.setText(user.getName());
			viewHolder.tvOtherText.setText("@" + user.getScreenName());
			viewHolder.tvReTweetName.setText(tweet.getUser().getName() + " retweeted");
			viewHolder.tvBody.setText(removeExtrasFromBody(tweet));
		}
		Linkify.addLinks(viewHolder.tvBody,Linkify.WEB_URLS);  // Make links active
		viewHolder.tvTimeAgo.setText(tweet.getCreatedAtFromNow());
		return convertView;
	}
	
	public User isReTweet(Tweet tweet) {
		User user = null;
		String body = tweet.getBody();
		if ( body.startsWith("RT")) {
			int start = body.indexOf("@");
			int end = body.indexOf(":");
			String screenName ="";
			if ( (start != -1) && (end != -1) ) {
				 screenName = body.substring(start+1, end);
				user = TwitterDatabaseOperations.getUserWithScreenName(screenName);
			}
			
		}
		return user;
	}
	
	public String removeExtrasFromBody(Tweet tweet) {
		String body = tweet.getBody();
		if ( body.startsWith("RT")) {
			int start = body.indexOf(":");
			return body.substring(start+1);
		} else
			return tweet.getBody();
	}	
	
}
