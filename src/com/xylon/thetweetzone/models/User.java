package com.xylon.thetweetzone.models;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table( name = "Users")
public class User extends Model implements Serializable {
	@Column(name = "name")
	public String name;
	
	@Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	public long uid = -1;
	
	@Column(name="screenName")
	public String screenName;
	
	@Column(name="profileImageUrl")
	public String profileImageUrl;
	
	@Column(name="followersCount")
	public int followersCount;

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
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return u;
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
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name + ":");
		sb.append(uid +":");
		sb.append(screenName +":");
		sb.append(profileImageUrl + ":");
		return sb.toString();
		
	}
	
//	 // Used to return items from another table based on the foreign key
//    public List<Tweet> tweets() {
//        return getMany(Tweet.class, "User");
//    }

}
