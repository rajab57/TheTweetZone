package com.xylon.thetweetzone.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkingUtils {

	public static Boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null
				&& activeNetworkInfo.isConnectedOrConnecting();
	}

	public static int checkResponseStatus(JSONObject response, String TAG) {
		int statusCode = 0;
		if (!response.isNull("responseStatus")) {

			try {
				statusCode = response.getInt("responseStatus");
				switch (statusCode) {
				case 200:
					Log.d(TAG, "The HTTP request was successful !!"
							+ statusCode);
					break;
				case 204:
					Log.d(TAG, "The HTTP request received no response"
							+ statusCode);
					break;
				case 400:
					Log.d(TAG, "Bad HTTP Request " + statusCode);
					break;
				case 401:
					Log.d(TAG, "Unauthorized HTTP request " + statusCode);
					break;
				case 403:
					Log.d(TAG, "Forbidden Request " + statusCode);
					break;
				case 500:
					Log.d(TAG, "Internal Server Error " + statusCode);
					break;
				default:
					Log.d(TAG, "Status code returned " + statusCode);
					break;

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return statusCode;
	}
}
