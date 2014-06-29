package com.xylon.thetweetzone.api;

public class ApiConstants {

	protected static final String REST_URL = "https://api.twitter.com/1.1/"; // Change this, base API URL
	protected static final String REST_CONSUMER_KEY = "V5JZ4naOdF9v5z2vbKNeMjB4V";       // Change this
	protected static final String REST_CONSUMER_SECRET = "oV1q2gaVfeMD9cVjToa4SeCEPD8yyf4rRfYDRyZCW5575RK4HD"; // Change this
	protected static final String REST_CALLBACK_URL = "oauth://cpbasictweets"; // Change this (here and in manifest)
	protected static String HOME_TIMELINE_URL = "statuses/home_timeline.json";
	protected static String STATUS_UPDATE_URL ="statuses/update.json";
	protected static String VERIFY_CREDENTIALS_URL ="account/verify_credentials.json";
	protected static String SHOW_USERS_URL ="users/show.json";
	protected static String MENTIONS_TIMELINE_URL = "statuses/mentions_timeline.json";
	protected static String USER_TIEMLINE_URL ="statuses/user_timeline.json";
	protected static String FOLLOWERS_URL = "followers/ids.json";
	protected static String FAV_URL = "favorites/create.json";
	protected static String UNFAV_URL ="favorites/destroy.json";
	protected static String SEARCH_URL="search/tweets.json";

}
