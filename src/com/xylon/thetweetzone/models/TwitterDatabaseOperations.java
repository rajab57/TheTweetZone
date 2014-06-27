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
	
	
	public static List<Tweet> getTweetsNewerThan(long sinceId, int count) {
		Long start = (long) sinceId;
		return new Select()
		.from(Tweet.class)
		.where("tid> ?" , start)
		.orderBy("tid DESC")
		.limit(count)
		.execute();	
	}
	
	public static List<Tweet> getTweetsOlderThan(long maxId, int count) {
		return new Select()
		.from(Tweet.class)
		.where("tid< ?" , maxId)
		.orderBy("tid DESC")
		.limit(count)
		.execute();	
	}
	
	public static User getUser(long id) {
		return  new Select()
		.from(User.class)
		.where("uid= ? ", id)
		.orderBy("uid DESC")
		.executeSingle();
	}
	
	public static User getUserWithScreenName(String sName) {
		return  new Select()
		.from(User.class)
		.where("screenName= ?", sName)
		.executeSingle();
	}
}
