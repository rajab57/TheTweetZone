package com.xylon.thetweetzone.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.TwitterClientApp;
import com.xylon.thetweetzone.adapters.MessageArrayAdapter;
import com.xylon.thetweetzone.api.TwitterClient;
import com.xylon.thetweetzone.helpers.NetworkingUtils;
import com.xylon.thetweetzone.models.Message;

public class DirectMessagesActivity extends Activity {
	
	private static String TAG = DirectMessagesActivity.class.getSimpleName();
	ArrayList<Message> messages;
	MessageArrayAdapter mAdapter;
	ListView lvMessages;
	private TwitterClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_direct_messages);
		messages = new ArrayList<Message>();
		mAdapter = new MessageArrayAdapter(this,messages);
		lvMessages = (ListView)findViewById(R.id.lvMsg);
		lvMessages.setAdapter(mAdapter);
		client = TwitterClientApp.getRestClient();
		showMessages(1, -1);
	}
	
	public void showMessages(long sinceId, long maxId) {
		if (NetworkingUtils.isNetworkAvailable(this)) {
			Log.d(TAG, "showmessages");
			client.getDirectMessages(sinceId, maxId, new JsonHttpResponseHandler() {
				
				@Override
				public void onSuccess(int successCode, JSONArray jsonArray) {
					Log.d(TAG, "here 1");
					mAdapter.clear();
					ArrayList<Message>msgs = Message.fromJSONArray(jsonArray);
					mAdapter.addAll(msgs);
				}
				
				@Override
				public void onSuccess(JSONArray jsonArray) {
					mAdapter.clear();
					ArrayList<Message>msgs = Message.fromJSONArray(jsonArray);
					Log.d(TAG, " num " + msgs.size());
					mAdapter.addAll(msgs);
				}
				
				@Override
				public void onSuccess(int arg0, JSONObject arg1) {
					Log.d(TAG, "here 3");
				}
				
				@Override
				public void onFailure(Throwable arg0, JSONObject arg1) {
					Log.d(TAG , "here 4");
					Log.d(TAG, arg1.toString());
					Log.d(TAG,arg0.toString());
				}
			});
		}
	}
	
}
