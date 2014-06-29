package com.xylon.thetweetzone.api;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
 
    
    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, ApiConstants.REST_URL, ApiConstants.REST_CONSUMER_KEY, ApiConstants.REST_CONSUMER_SECRET, ApiConstants.REST_CALLBACK_URL);
    }
    
    /**
     * Get the tweets in authenticating user's timeline.
     * If sinceId or maxId < 0 , ignore the parameter
     * @param sinceId
     * @param maxId
     * @param handler
     */
    public void getHomeTimeline(long sinceId, long maxId, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl(ApiConstants.HOME_TIMELINE_URL);
    	RequestParams params = new RequestParams();
    	if ( sinceId > 0)
    		params.put("since_id", Long.toString(sinceId)); // more recent than specified id
    	if ( maxId > 0)
    		params.put("max_id", Long.toString(maxId)); // older than specified id
    	
    	//params.put("since_id", "1");
    	client.get(apiUrl,params, handler);
    }
    
    /**
     * Post status update in your timeline
     * @param status
     * @param handler
     */
    public void postStatusUpdate(String status, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl(ApiConstants.STATUS_UPDATE_URL);
    	RequestParams params = new RequestParams();
    	params.put("status", status);
    	client.post(apiUrl,params, handler);
    	
    }
    
    /**
     * Returns a collection of relevant Tweets matching a specified query.
     * @param query ( required parameter - search string )
     * @param sinceId (specify -1 if not used, if given returns tweets newer than)
     * @param maxId ( specify -1 if not used, if given returns tweets older than)
     * @param handler
     */
    public void searchTweets(String query, long sinceId, long maxId, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl(ApiConstants.SEARCH_URL);
    	RequestParams params = new RequestParams();
    	if ( sinceId > 0)
    		params.put("since_id", Long.toString(sinceId)); // more recent than specified id
    	if ( maxId > 0)
    		params.put("max_id", Long.toString(maxId)); // older than specified id
    	
    	params.put("q", query);
    	client.get(apiUrl,params, handler);
    }
    
    /**
     * Post status update to a user
     * @param status
     * @param replyId
     * @param handler
     */
    public void postStatusUpdateInReply(String status, String replyId, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl(ApiConstants.STATUS_UPDATE_URL);
    	RequestParams params = new RequestParams();
    	params.put("status", status);
    	params.put("in_reply_to_status_id" , replyId);
    	client.post(apiUrl,params, handler);
    	
    }
    
    /**
     * Get the authenticating user's account information
     * @param handler
     */
    public void getUserAccountInformation(AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl(ApiConstants.VERIFY_CREDENTIALS_URL);
    	client.get(apiUrl, null, handler);
    }
    
    /**
     * Get User profile given the screen name of the user
     * @param sName
     * @param handler
     */
    public void getUserProfileFromScreenName(String sName, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl(ApiConstants.SHOW_USERS_URL);
    	RequestParams params = new RequestParams();
    	params.put("screen_name", sName);
    	params.put("include_entities","false");
    	client.get(apiUrl,params,handler);
    }
    
    /**
     * Returns the 20 most recent mentions (tweets containing a users's @screen_name) for the authenticating user.
     * this api can return only 800 tweets
     */
    public void getMentionTimeline(long sinceId, long maxId, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl(ApiConstants.MENTIONS_TIMELINE_URL);
      	RequestParams params = new RequestParams();
    	if ( sinceId > 0)
    		params.put("since_id", Long.toString(sinceId)); // more recent than specified id
    	if ( maxId > 0)
    		params.put("max_id", Long.toString(maxId)); // older than specified id
    	
    	client.get(apiUrl,params, handler);
    	
    }
    
    /**
     * Returns a collection of the most recent Tweets posted by the user indicated by the screen_name or user_id parameters.
     * @param sinceId
     * @param maxId
     * @param handler
     */
    public void getUserTimeline(long sinceId, long maxId, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl(ApiConstants.USER_TIEMLINE_URL);
      	RequestParams params = new RequestParams();
    	if ( sinceId > 0)
    		params.put("since_id", Long.toString(sinceId)); // more recent than specified id
    	if ( maxId > 0)
    		params.put("max_id", Long.toString(maxId)); // older than specified id
    	
    	client.get(apiUrl,params, handler);
    	
    }
    
    /**
     * Returns a cursored collection of user IDs for every user following the specified user.
     * can spcify either userid or screename and search with either of them.
     * @param userId
     * @param screenName
     */
    public void getMyFollowers(String userId, String screenName, AsyncHttpResponseHandler handler ) {
       	String apiUrl = getApiUrl(ApiConstants.FOLLOWERS_URL);
    	RequestParams params = new RequestParams();
    	if ( screenName == null || screenName.equals(""))
    		params.put("user_id",userId);
    	else
    		params.put("screen_name", screenName);
    	client.get(apiUrl,params,handler);
    	
    }
    
    /**
     * Favorites the status specified in the ID parameter as the authenticating user. 
     * Returns the favorite status when successful.
     * A 200 OK response from this method will indicate whether the intended action was successful or not.
     * @param tweetId
     * @param handler
     */
	public void favoriteTweet(long tweetId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl(ApiConstants.FAV_URL);
		RequestParams params = new RequestParams();
		if (tweetId > 0) {
			params.put("id", Long.toString(tweetId));
		}
		client.post(apiUrl, params, handler);
	}

	/**
	 * Un-favorites the status specified in the ID parameter as the authenticating user. 
	 * Returns the un-favorited status in the requested format when successful.
	 * @param tweetId
	 * @param handler
	 */
	public void unFavoriteTweet(long tweetId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl(ApiConstants.UNFAV_URL);
		RequestParams params = new RequestParams();
		if (tweetId > 0) {
			params.put("id", Long.toString(tweetId));
		}
		client.post(apiUrl, params, handler);
	}
 
}