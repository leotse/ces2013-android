package com.digiflare.ces2013;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.digiflare.ces2013.data.APIClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.image.SmartImageView;

public class DetailsActivity extends FragmentActivity {

	public static final String EXTRA_SHOW = "extra_show";
	
	JSONObject mShow;
	JSONArray mRelatedShows;
	
	ActionBar mActionBar;
	LinearLayout mContent;
	SmartImageView mImageView;
	TextView mTitle;
	TextView mSubtitle;
	TextView mSynopsis;
	ProgressBar mProgress;
	LayoutInflater mLayoutInflater;
	Activity mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		
		// ui elements
		mContent = (LinearLayout) findViewById(R.id.details_content);
		mImageView = (SmartImageView) findViewById(R.id.details_image);
		mTitle = (TextView) findViewById(R.id.details_title);
		mSubtitle = (TextView) findViewById(R.id.details_subtitle);
		mSynopsis = (TextView) findViewById(R.id.details_synopsis);
		mProgress = (ProgressBar) findViewById(R.id.details_related_progress);
		mLayoutInflater = getLayoutInflater();
		mContext = this;
		
		// event listers
		mImageView.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View image) {
				
				// play a dummy video
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			    Uri data = Uri.parse("http://wpc.7710.edgecastcdn.net/007710/mps/Digiflare/200/31/The_Avengers_Trailer_480p.mp4");
			    intent.setDataAndType(data, "video/mp4");
			    startActivity(intent);
			}
		});

		// Set up the action bar to show tabs.
		mActionBar = getActionBar();
		mActionBar.setIcon(R.drawable.none);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(false);
		
		// Get show info from intent
		Bundle extras = getIntent().getExtras();
		if(null != extras) {
			try {
				// deserilize show json
				String json = extras.getString(EXTRA_SHOW);
				mShow = new JSONObject(json);
				
				// get fields from json 
				String imageUrl = APIClient.getImage(mShow, 0, 800);
				String title = mShow.getJSONObject("title").getString("short");
				int releaseYear = mShow.getInt("releaseYear");
				double rating = mShow.getDouble("averageRating");
				String synopsis = mShow.getString("synopsis");
				
				// populate ui!
				mImageView.setImageUrl(imageUrl);
				mTitle.setText(title);
				mSubtitle.setText(releaseYear + "  PG  1h 34m");
				mSynopsis.setText(synopsis);
				
				// also start loading related titles
				String showId = mShow.getString("_id");
				mProgress.setVisibility(View.VISIBLE);
				APIClient.getRelated(showId, new JsonHttpResponseHandler() {
					
					@Override
					public void onSuccess(JSONObject json) {
						try {
							mRelatedShows = json.getJSONArray("response");
							mProgress.setVisibility(View.GONE);
						
							// render the related shows
							// process response into something we can work with
							JSONObject feature;
							View view = null;
							SmartImageView image;
							int imageId, iterations;
							
							iterations = ((int)(mRelatedShows.length() / 3)) * 3;
							for(int i = 0; i < iterations; i++) { 
								feature = mRelatedShows.getJSONObject(i);
								switch(i % 3) {
									case 1:
										imageId = R.id.featured_row_thumbnail2;
										break;
									case 2:
										imageId = R.id.featured_row_thumbnail3;
										break;
									default:
										view = mLayoutInflater.inflate(R.layout.featured_row, null);
										mContent.addView(view);
										imageId = R.id.featured_row_thumbnail1;
										break;
								}
								
								image = (SmartImageView) view.findViewById(imageId);
								image.setTag(i);
								image.setImageUrl(APIClient.getImage(feature, 0, 200));
								image.setOnClickListener(new View.OnClickListener(){
									@Override
									public void onClick(View image) {
										try {
											int position = (Integer) image.getTag();
											JSONObject feature = mRelatedShows.getJSONObject(position);
											
											// show details view
											Intent intent = new Intent(mContext, DetailsActivity.class);
											intent.putExtra(DetailsActivity.EXTRA_SHOW, feature.toString());
											startActivity(intent);
											
										} catch(JSONException e){
											System.out.println("========== Something weird happen on image click ==========");
										} catch(ClassCastException e) {
											System.out.println("========== Something weird happen on image click ==========");
										}
									}
								});
							}
						} catch(JSONException e) {
							Log.e("DetailsActivity.onCreate", e.getLocalizedMessage());
						}
					}
					
					@Override
					public void handleFailureMessage(Throwable e, String body) {
						Log.e("DetailsActivity.onCreate()", e.getLocalizedMessage());
						mProgress.setVisibility(View.GONE);
					}
				});
				
			} catch(JSONException e) {
				Log.e("DetailsActivity.onCreate()", e.getLocalizedMessage());
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_details, menu);
		// return true;
		return false;
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
}
