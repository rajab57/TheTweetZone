package com.xylon.thetweetzone.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

}
