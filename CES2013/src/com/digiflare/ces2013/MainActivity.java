package com.digiflare.ces2013;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;

import com.digiflare.ces2013.data.APIClient;
import com.digiflare.ces2013.data.ImageDownloadedHandler;
import com.digiflare.ces2013.data.ImageService;
import com.digiflare.ces2013.fragments.AssetListFragment;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	View mRoot, mCustomBar;
	ActionBar mActionBar;
	AssetListFragment mFeatured, mMovies, mShows, mCurrent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mRoot = findViewById(R.id.main_container);

		// set up fragments
		Bundle args;

		mFeatured = new AssetListFragment();
		args = new Bundle();
		args.putString(AssetListFragment.URL, APIClient.FEATURED_URL);
		args.putString(AssetListFragment.TYPE, "Featured");
		mFeatured.setArguments(args);

		mMovies = new AssetListFragment();
		args = new Bundle();
		args.putString(AssetListFragment.URL, APIClient.MOVIES_URL);
		args.putString(AssetListFragment.TYPE, "Movies");
		mMovies.setArguments(args);

		mShows = new AssetListFragment();
		args = new Bundle();
		args.putString(AssetListFragment.URL, APIClient.SHOWS_URL);
		args.putString(AssetListFragment.TYPE, "Shows");
		mShows.setArguments(args);

		// Set up the action bar.
		mActionBar = getActionBar();
		
		mActionBar.setNavigationMode(ActionBar.DISPLAY_SHOW_CUSTOM);
	    mActionBar.setDisplayShowCustomEnabled(true);
	    mActionBar.setDisplayShowHomeEnabled(true);
	    mActionBar.setDisplayShowTitleEnabled(false);
	    
	    // custom view for action bar
		mCustomBar = getLayoutInflater().inflate(R.layout.title_bar, null);
		mActionBar.setCustomView(mCustomBar);
		
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mActionBar.addTab(mActionBar.newTab().setText(R.string.title_section_featured).setTabListener(this));
		mActionBar.addTab(mActionBar.newTab().setText(R.string.title_section_movies).setTabListener(this));
		mActionBar.addTab(mActionBar.newTab().setText(R.string.title_section_tv).setTabListener(this));
		
		//Hide the default app icon on the action bar
		View homeIcon = findViewById(android.R.id.home);
		((View) homeIcon.getParent()).setVisibility(View.GONE);
		
		// download dynamic background
		ImageService.getInstance(this).getBackground(new ImageDownloadedHandler() {
			
			@Override
			public void onSuccess(Drawable background) {
				mRoot.setBackgroundDrawable(background);
			}
		});
		ImageService.getInstance(this).getLogo(new ImageDownloadedHandler() {
			@Override
			public void onSuccess(Drawable logo) {
				mCustomBar.findViewById(R.id.title_bar_image);
				mCustomBar.setBackgroundDrawable(logo);
			}
		});
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