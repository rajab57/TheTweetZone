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
	
	public static void insertUser(User user) {
		user.save();
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
	
	public static User getUser(long id) {
		return  new Select()
		.from(User.class)
		.where("uid= ? ", id)
		.executeSingle();
	}
	
	public static User getUserWithScreenName(String sName) {
		return  new Select()
		.from(User.class)
		.where("screenName= ?", sName)
		.executeSingle();
	}
}
