package com.xylon.thetweetzone.models;

import java.io.Serializable;
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

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;

@Table(name = "Tweet")
public class Tweet extends Model implements Serializable {
	
	private static String TAG = Tweet.class.getSimpleName();
	@Column(name = "body")
	public String body;
	
	@Column(name = "tid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	public long tid;
	
	@Column(name = "createdAt")
	public String createdAt;
	
	@Column(name = "user",onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
	public User user;
	
	@Column(name="createdAtEpoch")
	public long createdAtEpoch;
	
	@Column(name="url") 
	public String url;
	
	@Column(name="displayUrl") 
	public String displayUrl;
	
	@Column(name="fav") 
	public boolean favorited;

	// do not persist to database
	public int retweetCount;

	@Column(name="retweeted")
	public boolean retweeted;

	// do not persist to database
	@Column(name="favCount")
	public int favCount;
	
	@Column(name="inReplyToUserId")
	public String inReplyToUserId;
	
	@Column(name="mediaUrl")
	public String mediaUrl;
	
	public String getMediaUrl() {
		return mediaUrl;
	}

	// Make sure to have a default constructor for every ActiveAndroid model
    public Tweet(){
        super();
     }
    
	public static Tweet fromJSON(JSONObject jsonObject) {
		Tweet tweet = new Tweet();
		try {
			tweet.body = jsonObject.getString("text");
			tweet.tid = jsonObject.getLong("id");
			tweet.createdAt = jsonObject.getString("created_at");
			tweet.user =User.fromJSON(jsonObject.getJSONObject("user"));
			tweet.createdAtEpoch = convertTimeToEpoch(tweet.createdAt);
			tweet.favorited = jsonObject.getBoolean("favorited");
			tweet.retweeted = jsonObject.getBoolean("retweeted");
			tweet.retweetCount = jsonObject.getInt("retweet_count");
			tweet.favCount = jsonObject.getInt("favorite_count");
			tweet.inReplyToUserId = jsonObject.getString("in_reply_to_user_id");

			JSONObject jsonObj = jsonObject.getJSONObject("entities");
			if (jsonObj != null) {
				JSONArray jsonArr = jsonObj.getJSONArray("urls");
				if (jsonArr != null && jsonArr.length() > 0) {
					tweet.url = jsonArr.getJSONObject(0).getString("url");
					tweet.displayUrl = jsonArr.getJSONObject(0).getString(
							"display_url");	
				}
				if ( jsonObj.has("media")) {
					JSONArray jsonArr2 = jsonObj.getJSONArray("media");
					if (jsonArr2 != null && jsonArr2.length() > 0) {
						tweet.mediaUrl = jsonArr2.getJSONObject(0).getString("media_url");
						tweet.displayUrl = jsonArr2.getJSONObject(0).getString(
								"display_url");
					}
				}
			}
			if (jsonObject.has("retweeted_status")) {
				JSONObject json = jsonObject.getJSONObject("retweeted_status");
				if (json != null) {
					tweet.retweetCount = json.getInt("retweet_count");
					tweet.favCount = json.getInt("favorite_count");
				}
			}
			
		} catch (JSONException e) {
			Log.d(TAG, e.toString());
			return null;
		}
		// Extract values from the json to populate member variables
		return tweet;
		
	}

	public int getFavCount() {
		return favCount;
	}

	public int getRetweetCount() {
		return retweetCount;
	}

	public boolean isRetweeted() {
		return retweeted;
	}

	public String getInReplyToUserId() {
		return inReplyToUserId;
	}

	public String getUrl() {
		return url;
	}

	public String getDisplayUrl() {
		return displayUrl;
	}

	public boolean isFavorited() {
		return favorited;
	}

	public String getBody() {
		return body;
	}

	public long getTid() {
		return tid;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public User getUser() {
		return user;
	}
	
	public static long convertTimeToEpoch(String time) {
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
	
	public String isReTweet() {
		String body = this.getBody();
		String screenName = null;
		if (body.startsWith("RT")) {
			int start = body.indexOf("@");
			int end = body.indexOf(":");
			if ((start != -1) && (end != -1))
				screenName = body.substring(start + 1, end);
		}
		return screenName;
	}
	
	@Override
	public String toString() {
		return getBody() + "-" + getTid() + "-" + getUrl() + "-" + getDisplayUrl() + "-" + getTid() ;
	}

}
