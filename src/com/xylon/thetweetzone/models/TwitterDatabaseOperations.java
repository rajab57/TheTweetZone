package com.xylon.thetweetzone.models;

import java.util.ArrayList;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

public class TwitterDatabaseOperations {
	
	public static void batchInsertTweets(ArrayList<Tweet> tweets) {
		ActiveAndroid.beginTransaction();
		try {
		        for (int i = 0; i < tweets.size(); i++) {
		        	System.out.println("USERINFO " + tweets.get(i).getUser().toString());
		        	System.out.println("TWEET " + tweets.get(i).toString());
		            
		           tweets.get(i).getUser().save();
		           tweets.get(i).save();
		        }
		        ActiveAndroid.setTransactionSuccessful();
		}
		finally {
		        ActiveAndroid.endTransaction();
		}
	}
	
	public static void insertTweet(Tweet tweet) {
		tweet.save();
	}
	
	public static List<Tweet> getAllTweets() {
	    return new Select()
	        .from(Tweet.class)
	        .execute();
	}
	
	public static List<Tweet> getTweetsNewerThan(int sinceId, int count) {
		return new Select()
		.from(Tweet.class)
		.where("uid>=" , sinceId)
		.limit(count)
		.execute();	
	}
	
	public static List<Tweet> getTweetsOlderThan(long maxId, int count) {
		return new Select()
		.from(Tweet.class)
		.where("uid<=" , maxId)
		.limit(count)
		.execute();	
	}
}
