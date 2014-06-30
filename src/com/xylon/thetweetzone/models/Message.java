package com.xylon.thetweetzone.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "messages")
public class Message extends Model {

	@Column(name = "name")
	private String name;

	@Column(name = "screenName")
	private String screenName;
	
	@Column(name="profileImageUrl")
	public String profileImageUrl;

	@Column(name = "description")
	private String description;
	
	@Column(name = "createdAt")
	public String createdAt;

	public Message() {
		super();
	}

	public String getName() {
		return name;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getDescription() {
		return description;
	}

	public String getCreatedAt() {
		return createdAt;
	}
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	
	public static Message fromJSON(JSONObject jsonObject) {
		Message msg = new Message();
		try {
			JSONObject msgJson = jsonObject.getJSONObject("sender");
			msg.name = msgJson.getString("name");
			msg.screenName = msgJson.getString("screen_name");
			msg.description = jsonObject.getString("text");
			msg.createdAt = jsonObject.getString("created_at");
			msg.profileImageUrl = msgJson.getString("profile_image_url");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return msg;
	}


	public static ArrayList<Message> fromJSONArray(JSONArray jsonArray) {
		ArrayList<Message> messages = new ArrayList<Message>();
		for (int i = 0; i < jsonArray.length(); ++i) {
			JSONObject msgJson = null;
			try {
				msgJson = jsonArray.getJSONObject(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			Message msg = Message.fromJSON(msgJson);
			if (msg != null)
				messages.add(msg);
		}
		return messages;
	}

}
