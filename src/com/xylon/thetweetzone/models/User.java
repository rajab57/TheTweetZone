package com.xylon.thetweetzone.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table( name = "Users")
public class User extends Model implements Serializable {
	@Column(name = "name")
	private String name;
	
	@Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid = -1;
	
	@Column(name="screenName")
	private String screenName;
	
	@Column(name="profileImageUrl")
	private String profileImageUrl;
	
	@Column(name="followersCount")
	private int followersCount;
	
	@Column(name="tweetsCount")
	private int tweetsCount;
	
	@Column(name="followingCount")
	private int followingCount;
	
	@Column(name="decription")
	private String description;
	
	@Column(name="backgroundImageUrl") 
	private String backgroundIamgeUrl;
	
	@Column(name="bannerUrl")
	private String bannerUrl;

	public User() {
		super();
	}
	public static User fromJSON(JSONObject jsonObject) {
		
		User u = new User();
		try {
			u.name = jsonObject.getString("name");
			u.uid = jsonObject.getLong("id");
			u.screenName = jsonObject.getString("screen_name");
			u.profileImageUrl = jsonObject.getString("profile_image_url");
			u.followersCount = jsonObject.getInt("followers_count");
			u.tweetsCount = jsonObject.getInt("statuses_count");
			u.followingCount = jsonObject.getInt("friends_count");
			u.description = jsonObject.getString("description");
			u.backgroundIamgeUrl = jsonObject.getString("profile_background_image_url");
			if(jsonObject.has("profile_banner_url"))
				u.bannerUrl = jsonObject.getString("profile_banner_url");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return u;
	}

	public int getTweetsCount() {
		return tweetsCount;
	}
	public int getFollowingCount() {
		return followingCount;
	}
	public String getName() {
		return name;
	}

	public long getUid() {
		return uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	
	public String getBannerUrl() {
		return bannerUrl;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name + ":");
		sb.append(uid +":");
		sb.append(screenName +":");
		sb.append(profileImageUrl + ":");
		if (backgroundIamgeUrl != null)
			sb.append(backgroundIamgeUrl + ":");
		return sb.toString();
		
	}
	public int getFollowersCount() {
		return followersCount;
	}
	public String getDescription() {
		return description;
	}
	
	public String getBackgroundIamgeUrl() {
		return backgroundIamgeUrl;
	}
	
	public static ArrayList<User> fromJSONArray(JSONArray jsonArray) {
		ArrayList<User> users = new ArrayList<User>();
		for ( int i = 0; i < jsonArray.length(); ++i ) {
			JSONObject userJson = null;
			try {
				userJson = jsonArray.getJSONObject(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			User user = User.fromJSON(userJson);
			if (user != null )
				users.add(user);
		}
		return users;
	}
}
