package com.xylon.thetweetzone.adapters;

import com.xylon.thetweetzone.fragments.HomeTimelineFragment;
import com.xylon.thetweetzone.fragments.MentionsTimelineFragement;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TimelineViewPagerAdapter extends FragmentPagerAdapter {
private static int NUM_ITEMS = 2;

    public TimelineViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
        case 0: // Fragment # 0 - This will show FirstFragment
            return new HomeTimelineFragment();
        case 1: // Fragment # 0 - This will show FirstFragment different title
            return new MentionsTimelineFragement();
        default:
            return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        if ( position == 0)
        	return "HOME";
        else if ( position == 1)
        	return "MENTIONS";
        return null;
    }

}