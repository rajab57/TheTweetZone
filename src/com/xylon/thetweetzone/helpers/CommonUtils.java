package com.xylon.thetweetzone.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


public class CommonUtils {
	
	//Tue Aug 28 19:59:34 +0000 2012
	public static String convertDateFormat (String dateStr, String fromFormatPattern, String toFormatPattern) {
		DateFormat fromFormat = new SimpleDateFormat(fromFormatPattern);
		DateFormat toFormat = new SimpleDateFormat(toFormatPattern);
		toFormat.setLenient(false);
		Date date;
		try {
			date = fromFormat.parse(dateStr);
			return toFormat.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.d("ERROR", "Date formatting Error");
		}
		return null;
	}
	
	public static Bitmap getBitmapFromURL(String imageUrl) {
	    try {
	        URL url = new URL(imageUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

}
