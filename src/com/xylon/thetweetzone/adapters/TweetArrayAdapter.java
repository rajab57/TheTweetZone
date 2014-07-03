package com.xylon.thetweetzone.adapters;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.activities.ProfileViewPagerActivity;
import com.xylon.thetweetzone.fragments.ComposeTweetDialogFragment;
import com.xylon.thetweetzone.fragments.ReTweetDialogFragment;
import com.xylon.thetweetzone.fragments.ComposeTweetDialogFragment.PostToTimelineListener;
import com.xylon.thetweetzone.helpers.TweetActions;
import com.xylon.thetweetzone.models.Tweet;
import com.xylon.thetweetzone.models.TwitterDatabaseOperations;
import com.xylon.thetweetzone.models.User;

public class TweetArrayAdapter extends ArrayAdapter<Tweet>{
	
	private static String TAG = TweetArrayAdapter.class.getSimpleName();
	private Context mContext;
	
	 /** Declaring the interface, to invoke a callback function in the implementing activity class */
	ReplyToTweetListener replyToTweetListener;

   /** An interface to be implemented in the hosting activity for "OK" button click listener */
   public interface ReplyToTweetListener {
       public void onReplyToTweet(String replyUserName, long statusId);
   }
   
	public TweetArrayAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);
		mContext = context;
		replyToTweetListener = (ReplyToTweetListener)mContext;
	}
	
	// View lookup cache
    private static class ViewHolder {
    	ImageView ivProfileImage;
    	TextView tvUserName ;
    	TextView tvOtherText;
    	TextView tvBody;
    	TextView tvTimeAgo;
    	TextView tvReTweetName;
    	TextView tvRetweetCount;
    	TextView tvFavCount;
    	ImageButton ibFav;
    	ImageButton ibRetweet;
    	ImageView ivTweetPhoto;
    	ImageButton ibReply;
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
			viewHolder.tvRetweetCount = (TextView)convertView.findViewById(R.id.tvRetweetCount);
			viewHolder.tvFavCount = (TextView)convertView.findViewById(R.id.tvFavCount);	
			viewHolder.ibFav = (ImageButton)convertView.findViewById(R.id.ibFav);
			viewHolder.ibRetweet = (ImageButton)convertView.findViewById(R.id.ibRetweet);
			viewHolder.ivTweetPhoto = (ImageView)convertView.findViewById(R.id.ivTweetPhoto);
			viewHolder.ibReply = (ImageButton)convertView.findViewById(R.id.ibReply);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		

		ImageLoader imageLoader = ImageLoader.getInstance();
		//populate view with tweet data
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), viewHolder.ivProfileImage);
		
		if(tweet.getMediaUrl() != null && !(tweet.getMediaUrl().equals(""))) {
			imageLoader.displayImage(tweet.getMediaUrl(),viewHolder.ivTweetPhoto);
		} else {
			viewHolder.ivTweetPhoto.setVisibility(View.GONE);
		}
		
		// FAV
		if(tweet.getFavCount()> 0)
			viewHolder.tvFavCount.setText(Integer.toString(tweet.getFavCount() ));
		if(tweet.getRetweetCount() > 0)
			viewHolder.tvRetweetCount.setText(Integer.toString(tweet.getRetweetCount()));
		//RETWEET
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
		
		// TIP: Set TAG on the image to get the screenName on click of image later
		viewHolder.ivProfileImage.setTag (tweet.getUser().getScreenName());
		viewHolder.ibFav.setTag(tweet);
		viewHolder.ibRetweet.setTag(tweet.getTid());
		viewHolder.ibReply.setTag(tweet);
		viewHolder.ivProfileImage
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String screenName = (String) v.getTag();
						Intent i = new Intent(v.getContext(),
								ProfileViewPagerActivity.class);
						// screenName is passed to the activity
						i.putExtra("screenName", screenName);
						v.getContext().startActivity(i);

					}

				});
		
		// Fav onclick
		final TextView favcnt = viewHolder.tvFavCount;
		final ImageButton favimg = viewHolder.ibFav;
		final TextView retweetcount = viewHolder.tvRetweetCount;
		final ImageButton retweetImg = viewHolder.ibRetweet;
		viewHolder.ibFav.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TweetActions.favTweet(v.getContext(), (Tweet) v.getTag(), favcnt, favimg);
				
			}
		});
		
		// Retweet onClick
		viewHolder.ibRetweet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				// Open Dialog ( based on the current state of the Tweet isRetweeted or not ?)
				// TODO Should this be at this level ??? Fix the bad implementation
				// TODO Merge with Detail Tweet screen as well. Reuse needed here.
				if (mContext instanceof FragmentActivity) {
					long tweetId = (Long)v.getTag();
					FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();
			        ReTweetDialogFragment retweetDialog = new ReTweetDialogFragment(tweetId,retweetImg, retweetcount);
			        retweetDialog.show(fm, "fragment_edit_name");
				}
			}
		});
		
		viewHolder.ibReply.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG,"reply button clicked");

				Tweet tweet = (Tweet) v.getTag();
				replyToTweetListener.onReplyToTweet(tweet.getUser().getScreenName(), tweet.getTid());
			}			
		});
		
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
