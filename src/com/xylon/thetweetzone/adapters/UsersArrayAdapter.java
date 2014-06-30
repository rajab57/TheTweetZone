package com.xylon.thetweetzone.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.models.User;

public class UsersArrayAdapter extends ArrayAdapter<User> {

	public UsersArrayAdapter(Context context, ArrayList<User> users) {
		super(context, 0, users);

	}

	private static class ViewHolder {
		ImageView ivProfile;
		TextView tvUserName;
		TextView tvScreenName;
		TextView tvDescription;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		User user = getItem(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			convertView = inflator.inflate(R.layout.user_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);
			viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
			viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
			viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.tvBody);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ImageLoader imageLoader = ImageLoader.getInstance();
		//populate view with user data
		imageLoader.displayImage(user.getProfileImageUrl(), viewHolder.ivProfile);
		
		viewHolder.tvUserName.setText(user.getName());
		viewHolder.tvScreenName.setText("@" + user.getScreenName());
		viewHolder.tvDescription.setText(user.getDescription());
		return convertView;
	}
}
