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
import com.xylon.thetweetzone.models.Message;

public class MessageArrayAdapter extends ArrayAdapter<Message> {

	public MessageArrayAdapter(Context context, ArrayList<Message> msgs) {
		super(context, 0, msgs);

	}

	private static class ViewHolder {
		ImageView ivProfile;
		TextView tvUserName;
		TextView tvScreenName;
		TextView tvDescription;
		TextView tvCreatedAt;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("position " + position);
		Message msg = getItem(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			convertView = inflator.inflate(R.layout.message_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);
			viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
			viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
			viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.tvBody);
			viewHolder.tvCreatedAt = (TextView) convertView.findViewById(R.id.tvTimeAgo);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ImageLoader imageLoader = ImageLoader.getInstance();
		//populate view with tweet data
		imageLoader.displayImage(msg.getProfileImageUrl(), viewHolder.ivProfile);
		
		viewHolder.tvUserName.setText(msg.getName());
		viewHolder.tvScreenName.setText("@" + msg.getScreenName());
		viewHolder.tvDescription.setText(msg.getDescription());
		viewHolder.tvCreatedAt.setText(msg.getCreatedAt());
		return convertView;
	}
}
