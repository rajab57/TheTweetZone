package com.xylon.thetweetzone.activities;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.TwitterClientApp;
import com.xylon.thetweetzone.api.TwitterClient;
import com.xylon.thetweetzone.fragments.ComposeTweetDialogFragment;
import com.xylon.thetweetzone.fragments.HomeTimelineFragment;
import com.xylon.thetweetzone.fragments.MentionsTimelineFragement;
import com.xylon.thetweetzone.fragments.SearchFragment;
import com.xylon.thetweetzone.listeners.FragmentTabListener;
import com.xylon.thetweetzone.models.User;

public class TimelineActivity extends FragmentActivity implements OnQueryTextListener {

	private static String TAG = TimelineActivity.class.getSimpleName();
	private HomeTimelineFragment homeFragment;
	private MentionsTimelineFragement mentionsFragment;
	TwitterClient client;
	private User accountInfo;
	private static int REQUEST_CODE = 20;
	private SearchView searchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		// MUST request the feature before setting content view
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_timeline);
		getActionBar().setDisplayShowTitleEnabled(false);
//		mentionsFragment = (MentionsTimelineFragement) getSupportFragmentManager()
//				.findFragmentById(R.id.fragTweets);
		client = TwitterClientApp.getRestClient();
		getUserAccountInfo();
		setupTabs();
	}

	private void setupTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab tab1 = actionBar
				.newTab()
				.setText("Home")
				.setIcon(R.drawable.ic_home)
				.setTag("HomeTimelineFragment")
				.setTabListener(
						new FragmentTabListener<HomeTimelineFragment>(
								R.id.flContainer, this, "first",
								HomeTimelineFragment.class));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		Tab tab2 = actionBar
				.newTab()
				.setText("mentions")
				.setIcon(R.drawable.ic_mentions)
				.setTag("MentionsTimelineFragment")
				.setTabListener(
						new FragmentTabListener<MentionsTimelineFragement>(
								R.id.flContainer, this, "second",
								MentionsTimelineFragement.class));

		actionBar.addTab(tab2);
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
					Toast.makeText(TimelineActivity.this, msg,
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
//		else if ( id == R.id.action_profile) {
//			onProfileView();
//			
//		}
		
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
		Intent i = new Intent(this,DirectMessagesActivity.class);
		startActivity(i);
	}

	public boolean onQueryTextSubmit(String queryStr) {
//		SearchFragment searchFragment = new SearchFragment();
//		Bundle args = new Bundle();
//        args.putString("query", queryStr);
//        searchFragment.setArguments(args);
//		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//		ft.replace(R.id.flContainer, searchFragment);
//		ft.commit();
		
		Intent i = new Intent(this,SearchActivity.class);
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
