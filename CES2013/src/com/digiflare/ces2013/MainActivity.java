package com.digiflare.ces2013;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Menu;

import com.digiflare.ces2013.data.APIClient;
import com.digiflare.ces2013.fragments.AssetListFragment;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ActionBar mActionBar;
	AssetListFragment mFeatured, mMovies, mShows, mCurrent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// set up fragments
		Bundle args;

		mFeatured = new AssetListFragment();
		args = new Bundle();
		args.putString(AssetListFragment.URL, APIClient.FEATURED_URL);
		mFeatured.setArguments(args);

		mMovies = new AssetListFragment();
		args = new Bundle();
		args.putString(AssetListFragment.URL, APIClient.MOVIES_URL);
		mMovies.setArguments(args);

		mShows = new AssetListFragment();
		args = new Bundle();
		args.putString(AssetListFragment.URL, APIClient.SHOWS_URL);
		mShows.setArguments(args);

		// Set up the action bar.
		mActionBar = getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mActionBar.addTab(mActionBar.newTab().setText(R.string.title_section_featured).setTabListener(this));
		mActionBar.addTab(mActionBar.newTab().setText(R.string.title_section_movies).setTabListener(this));
		mActionBar.addTab(mActionBar.newTab().setText(R.string.title_section_tv).setTabListener(this));
		
		mActionBar.setIcon(R.drawable.none);
		mActionBar.setDisplayShowTitleEnabled(false);
		
		// debug code
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		System.out.println(size);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		// return true;
		return false;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		int position = tab.getPosition();

		// create fragment for the given tab position
		switch (position) {
		case 0:
			mCurrent = mFeatured;
			break;
		case 1:
			mCurrent = mMovies;
			break;
		case 2:
			mCurrent = mShows;
			break;
		default:
			mCurrent = null;
			break;
		}

		// update tab content
		getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.main_container, mCurrent)
			.commit();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
}