package com.digiflare.ces2013;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsActivity extends FragmentActivity implements ActionBar.TabListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current tab position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	public static final String EXTRA_SHOW = "extra_show";
	
	ActionBar mActionBar;
	TextView mTextView;
	JSONObject mShow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		
		mTextView = (TextView) findViewById(R.id.details_text);

		// Set up the action bar to show tabs.
		mActionBar = getActionBar();
		mActionBar.setIcon(R.drawable.none);
		mActionBar.setDisplayHomeAsUpEnabled(true);		

		// For each of the sections in the app, add a tab to the action bar.
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mActionBar.addTab(mActionBar.newTab().setText(R.string.title_section_featured).setTabListener(this));
		mActionBar.addTab(mActionBar.newTab().setText(R.string.title_section_movies).setTabListener(this));
		mActionBar.addTab(mActionBar.newTab().setText(R.string.title_section_tv).setTabListener(this));
		
		// Get show info from intent
		Bundle extras = getIntent().getExtras();
		if(null != extras) {
			try {
				String json = extras.getString(EXTRA_SHOW);
				mShow = new JSONObject(json);
				mTextView.setText(mShow.getJSONObject("title").getString("short"));
				
			} catch(JSONException e) {
				System.out.println("========== DetailsActivity JSONException! ==========");
				e.printStackTrace();
				System.out.println("========== End JSONException! ==========");
			}
		}
		
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}
}
