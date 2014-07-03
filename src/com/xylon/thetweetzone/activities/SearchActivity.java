package com.xylon.thetweetzone.activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.xylon.thetweetzone.R;
import com.xylon.thetweetzone.api.TwitterClient;
import com.xylon.thetweetzone.fragments.SearchFragment;
import com.xylon.thetweetzone.listeners.FragmentTabListener;

public class SearchActivity extends FragmentActivity implements OnQueryTextListener {

	private static String TAG = SearchActivity.class.getSimpleName();

	TwitterClient client;
	private SearchView searchView;
	private SharedPreferences prefs;  // shared preferences 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// MUST request the feature before setting content view
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_search);
		getActionBar().setDisplayShowTitleEnabled(false);
		
		// Get from SearchAction Bar
		Intent intent = getIntent();
	    String query = intent.getStringExtra("query");
		
	    // Write to SharedPreferences
	    writeQueryToSharedPref(query);
		
		setupTabs();
	}

	public interface SearchQueryListener {
	    void search();
	}
	
	private void writeQueryToSharedPref(String queryStr) {
		prefs = getSharedPreferences("query", Context.MODE_PRIVATE); 
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("query",queryStr );  //or you can use putInt, putBoolean ... 
		editor.commit();
	}
	private void setupTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab tab1 = actionBar
				.newTab()
				.setText("Top Tweets")
				.setTag("TopTweetsFragment")
				.setTabListener(
						new FragmentTabListener<SearchFragment>(
								R.id.flContainer, this, "first",
								SearchFragment.class));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		Tab tab2 = actionBar
				.newTab()
				.setText("All Tweets")
				.setTag("AllTweetsFragment")
				.setTabListener(
						new FragmentTabListener<SearchFragment>(
								R.id.flContainer, this, "second",
								SearchFragment.class));

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
					Toast.makeText(SearchActivity.this, msg,
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
		
		return super.onOptionsItemSelected(item);
	}
	
	public void onProfileView(MenuItem item) {
		Intent i = new Intent(this, ProfileViewPagerActivity.class);
		startActivity(i);
	}

	public boolean onQueryTextSubmit(String queryStr) {
		ActionBar actionBar = getActionBar();
		Tab selTab = actionBar.getSelectedTab();
		Log.d(TAG, "Writing to shared pref" + queryStr);
		writeQueryToSharedPref(queryStr);
		actionBar.selectTab(selTab);
		
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}

}
