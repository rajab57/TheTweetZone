package com.xylon.thetweetzone.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateUtils;
import android.util.Log;

public class Tweet {
	private String body;
	private long uid;
	private String createdAt;
	private User user;
	private transient long createdAtEpoch;
	
	public static Tweet fromJSON(JSONObject jsonObject) {
		Tweet tweet = new Tweet();
		try {
			tweet.body = jsonObject.getString("text");
			tweet.uid = jsonObject.getLong("id");
			tweet.createdAt = jsonObject.getString("created_at");
			tweet.user =User.fromJSON(jsonObject.getJSONObject("user"));
			tweet.createdAtEpoch = converteTimeToEpoch(tweet.createdAt);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		// Extract values from the json to populate member variables
		return tweet;
		
	}

	public String getBody() {
		return body;
	}

	public long getUid() {
		return uid;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public User getUser() {
		return user;
	}
	
	public static long converteTimeToEpoch(String time) {
		SimpleDateFormat sdf  = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
		Date date;
		try {
			date = sdf.parse(time);			
		} catch (ParseException e) {
			Log.d("ERROR", "Time in incorrect format");
			return 0;		
		}
		return date.getTime();
		
		
	}
	
	public String getCreatedAtFromNow() {
		String timeago = "";
		Date currentDate = new Date();
		long currentDateEpoch = currentDate.getTime();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		cal.add(Calendar.HOUR_OF_DAY, 0);
		cal.add(Calendar.MINUTE, 0);
		cal.add(Calendar.SECOND,0);
		long beg7daysago = cal.getTimeInMillis();
        //cal.setTimeZone(TimeZone.getTimeZone("GMT")); // TODO hardcoded time zone
		if (createdAtEpoch != 0) {
			CharSequence charSeq = DateUtils.getRelativeTimeSpanString(
					createdAtEpoch, currentDateEpoch, 0);
					final StringBuilder sb = new StringBuilder(charSeq.length());
					sb.append(charSeq);
					timeago = sb.toString();
		}
		String returnStr;
		if (createdAtEpoch > beg7daysago) {
			 // not less than 7 days old
			if (timeago.equalsIgnoreCase("yesterday"))
				return "1d";
			int pos = timeago.indexOf("hours ago");
			if (pos != -1)
				return (timeago.substring(0, pos - 1) + "h");
			pos = timeago.indexOf("minutes ago");
			if (pos != -1)
				return (timeago.substring(0, pos - 1) + "m");
			pos = timeago.indexOf("days ago");
			if (pos != -1)
				return timeago.substring(0, pos - 1) + "d";
		} else {
			// older than 7 days
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
			return sdf.format(new Date(createdAtEpoch));
			
		}
			
		return timeago;
	}

	public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		for ( int i = 0; i < jsonArray.length(); ++i ) {
			JSONObject tweetJson = null;
			try {
				tweetJson = jsonArray.getJSONObject(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			Tweet tweet = Tweet.fromJSON(tweetJson);
			if (tweet != null )
				tweets.add(tweet);
		}
		return tweets;
	}
	
	@Override
	public String toString() {
		return getBody() + "-" + getUser().getScreenName();
	}

}
