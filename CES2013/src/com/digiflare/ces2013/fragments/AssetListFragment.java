package com.digiflare.ces2013.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.digiflare.ces2013.DetailsActivity;
import com.digiflare.ces2013.R;
import com.digiflare.ces2013.data.APIClient;
import com.digiflare.ces2013.ui.CarouselItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.image.SmartImageView;

public class AssetListFragment extends Fragment {
	
	public static final String URL = "url";

	Activity mContext;
	LayoutInflater mLayoutInflater;
	View mContainer;
	GridView mGridView;
	LinearLayout mContent;
	ViewPager mViewPager;
	ProgressBar mProgress;
	JSONArray features;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(null == mContainer) {
			mLayoutInflater = inflater;
			mContainer = (View) inflater.inflate(R.layout.fragment_featured, null);
			mContent = (LinearLayout) mContainer.findViewById(R.id.content_container);
			mViewPager = (ViewPager) mContainer.findViewById(R.id.carousel_view_pager);
			mGridView = (GridView) mContainer.findViewById(R.id.featured_list);
			mProgress = (ProgressBar) mContainer.findViewById(R.id.progress);
			populate();
		}
		return mContainer;
	}
	
	@Override 
	public void onDestroyView() {
		super.onDestroyView();
		((ViewManager)mContainer.getParent()).removeView(mContainer);
	}

	// populates the list
	public void populate() {
		
		// load data first if it is not loaded
		mProgress.setVisibility(View.VISIBLE);
		String url = getArguments().getString(URL);
		APIClient.get(url, new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(JSONObject response) {
				try {
					features = response.getJSONArray("response");
					
					// bind carousel data
					CarouselAdapter carouselAdapter = new CarouselAdapter(features);
					mViewPager.setAdapter(carouselAdapter);
					
					
					// process response into something we can work with
					JSONObject feature;
					View view = null;
					SmartImageView image;
					int imageId, iterations;
					
					iterations = ((int)(features.length() / 3)) * 3;
					for(int i = 0; i < iterations; i++) { 
						feature = features.getJSONObject(i);
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
						image.setImageUrl(getThumbnailImage(feature, 150, 200));
						image.setOnClickListener(new View.OnClickListener(){
							@Override
							public void onClick(View image) {
								try {
									int position = (Integer) image.getTag();
									JSONObject feature = features.getJSONObject(position);
									
									// show details view
									Intent intent = new Intent(getActivity(), DetailsActivity.class);
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
					
					// and finally hide the indicator
					mProgress.setVisibility(View.GONE);
					mContent.setVisibility(View.VISIBLE);
					
				} catch (JSONException e) {
					System.out.println("========== JSON Error! ==========");
					e.printStackTrace();
					System.out.println("========== End JSON Error! ==========");
				}
			}
			
			@Override
			public void handleFailureMessage(Throwable e, String body) {
				// network error handling here
				System.out.println("========== Network Error! ==========");
				System.out.println(body);
				System.out.println("========== End Network Error! ==========");
			}
		});
	}

	// helper method to get the proper thumbnail from a featured show
	private String getThumbnailImage(JSONObject feature, int minRes, int maxRes) throws JSONException {
		JSONArray boxArts = feature.getJSONArray("boxArt");
		
		// find the box art!
		JSONObject boxArt;
		int width;
		for(int i = 0; i < boxArts.length(); i++) {
			boxArt = boxArts.getJSONObject(i);
			width = boxArt.getInt("width");
			if(width >= minRes && width <= maxRes) {
				return boxArt.getString("url");
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @author leotse
	 * A special adapter to show the carousel
	 *
	 */
	
	private class CarouselAdapter extends PagerAdapter {
		private JSONArray mData;

		public CarouselAdapter(JSONArray data) {
			super();
			mData = data;
		}

		@Override
		public Object instantiateItem (ViewGroup container, int position)
		{
			if(position < mData.length()) {
				try {
					JSONObject dataItem = mData.getJSONObject(position);
					String title = dataItem.getJSONObject("title").getString("short");
					String imageUrl = getCarouselImage(dataItem, 600, 800);
					CarouselItem item = new CarouselItem(getActivity(), title, imageUrl);
				
					// remove view from previous parent
					if(null != item.getParent()) {
						((ViewManager)item.getParent()).removeView(item);
					}
					
					// show the carousel item
					container.addView(item);
					return item;
					
				} catch(JSONException e) {
					System.out.println("========= Carousel JSON Error! ==========");
					e.printStackTrace();
					System.out.println("========= End JSON Error! ==========");
				}
			}
			return null;
		}
		
		@Override
		public void destroyItem (ViewGroup container, int position, Object object) {
			CarouselItem view = (CarouselItem) object;
			container.removeView(view);
		}

		@Override
		public int getCount() {
			return mData.length();
		}
		
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		
		// helpers
		private String getCarouselImage(JSONObject item, int minRes, int maxRes) throws JSONException {
			JSONArray boxarts = item.getJSONArray("boxArt");
			JSONObject boxart;
			int width;
			String url;
			for(int i = 0; i < boxarts.length(); i++) {
				boxart = (JSONObject) boxarts.get(i);
				width = boxart.getInt("width");
				url = boxart.getString("url");
				if(width >= minRes && width <= maxRes) {
					return url;
				}
			}
			return null;
		}
	}
}
