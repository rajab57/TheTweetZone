package com.xylon.thetweetzone.models;

import java.util.ArrayList;

import android.content.ClipData.Item;

import com.activeandroid.ActiveAndroid;

public class TwitterDatabaseOperations {
	
	private static void batchInsert(ArrayList<Tweet> tweets) {
		ActiveAndroid.beginTransaction();
		try {
		        for (int i = 0; i < tweets.size(); i++) {
		            tweets.get(i).save();
		        }
		        ActiveAndroid.setTransactionSuccessful();
		}
		finally {
		        ActiveAndroid.endTransaction();
		}
	}

}
