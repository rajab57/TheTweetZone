package com.xylon.thetweetzone.activities;

import org.json.JSONObject;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.TwitterClientApp;
import com.xylon.thetweetzone.adapters.ViewPagerAdapter;
import com.xylon.thetweetzone.api.TwitterClient;
import com.xylon.thetweetzone.fragments.ComposeTweetDialogFragment;
import com.xylon.thetweetzone.fragments.HomeTimelineFragment;
import com.xylon.thetweetzone.fragments.MentionsTimelineFragement;
import com.xylon.thetweetzone.models.User;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class TimelineViewPagerActivity extends FragmentActivity implements
		OnQueryTextListener {

	private static String TAG = TimelineViewPagerActivity.class.getSimpleName();
	private HomeTimelineFragment homeFragment;
	private MentionsTimelineFragement mentionsFragment;
	TwitterClient client;
	private User accountInfo;
	private static int REQUEST_CODE = 20;
	private SearchView searchView;
	FragmentPagerAdapter adapterViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vp_timeline);
		getActionBar().setDisplayShowTitleEnabled(false);
		client = TwitterClientApp.getRestClient();
		getUserAccountInfo();
		ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
		adapterViewPager = new ViewPagerAdapter(getSupportFragmentManager());
		vpPager.setAdapter(adapterViewPager);
		// Attach the page change listener inside the activity
		vpPager.setOnPageChangeListener(new OnPageChangeListener() {

		    // This method will be invoked when a new page becomes selected.
		    @Override
		    public void onPageSelected(int position) {
		        Toast.makeText(TimelineViewPagerActivity.this, 
		                    "Selected page position: " + position, Toast.LENGTH_SHORT).show();
		    }

		    // This method will be invoked when the current page is scrolled
		    @Override
		    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		        // Code goes here
		    }

		    // Called when the scroll state changes: 
		    // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
		    @Override
		    public void onPageScrollStateChanged(int state) {
		        // Code goes here
		    }
		});

	}
	
	

	// Should be called when an async task has finished
	public void hideProgressBar() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					setProgressBarIndeterminateVisibility(false);
					return;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void makeToast(final String msg) {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(TimelineViewPagerActivity.this, msg,
							Toast.LENGTH_SHORT).show();
					return;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.item_options, menu);
		searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setOnQueryTextListener(this);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.action_compose) {
			DialogFragment dialogFragment = new ComposeTweetDialogFragment();
			Bundle args = new Bundle();
			args.putSerializable("user", accountInfo);
			dialogFragment.setArguments(args);
			dialogFragment.show(getFragmentManager(), "composeTweet");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onProfileView(MenuItem item) {
		Log.d(TAG, "IN profile view");
		Intent i = new Intent(this, ProfileActivity.class);
		i.putExtra("screenName", "authorizedUser");
		startActivity(i);
	}

	public void onShowMessages(MenuItem item) {
		Log.d(TAG, "IN Message view");
		Intent i = new Intent(this, DirectMessagesActivity.class);
		startActivity(i);
	}

	public boolean onQueryTextSubmit(String queryStr) {
		Intent i = new Intent(this, SearchActivity.class);
		i.putExtra("query", queryStr);
		startActivity(i);
		return true;
	}

	public void getUserAccountInfo() {
		client.getUserAccountInformation(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				accountInfo = User.fromJSON(json);
			}

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", "Posting Tweet to Timeline failed");
				Log.d("debug", s.toString());

			}
		});
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}

}
