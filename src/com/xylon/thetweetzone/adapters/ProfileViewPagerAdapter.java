package com.xylon.thetweetzone.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.models.User;

public class ProfileViewPagerAdapter extends PagerAdapter {
	
	User mUser;
	public ProfileViewPagerAdapter(User accountInfo) {
		mUser = accountInfo;
	}

	public int getCount() {
		return 2;
	}

	public Object instantiateItem(View collection, int position) {

		LayoutInflater inflater = (LayoutInflater) collection.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		int resId = 0;
		View view = null;
		switch (position) {
		case 0:
			resId = showProfile();
			view = inflater.inflate(resId, null);
			ImageView ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
			TextView tvUserName = (TextView) view.findViewById(R.id.tvUserName);
			TextView tvScreenName = (TextView) view.findViewById(R.id.tvScreenName);
			ivProfileImage.setImageResource(android.R.color.transparent);
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(mUser.getProfileImageUrl(),
					ivProfileImage);
			tvUserName.setText(mUser.getName());
			tvScreenName.setText("@" + mUser.getScreenName());
			break;
		case 1:
			resId = showTagline();
			view = inflater.inflate(resId, null);
			TextView tvTagline = (TextView)view.findViewById(R.id.tvTagline);
			tvTagline.setText(mUser.getDescription());
			break;
		}

		((ViewPager) collection).addView(view, 0);
		return view;
	}

	public int showTagline() {
		int resId;
		resId = R.layout.tagline_view;
		return resId;
	}

	public int showProfile() {
		int resId;
		resId = R.layout.profile_view;
		return resId;
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}
}