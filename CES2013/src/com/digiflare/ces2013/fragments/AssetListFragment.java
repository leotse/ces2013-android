package com.digiflare.ces2013.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.digiflare.ces2013.R;
import com.digiflare.ces2013.data.APIClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.image.SmartImageView;

public class AssetListFragment extends Fragment {
	
	public static final String URL = "url";
	private static final String TITLE = "title";
	private static final String THUMBNAIL = "thumbnail";

	Activity mContext;
	View mContainer;
	GridView mGridView;
	View mContent;
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
		mContainer = (View) inflater.inflate(R.layout.fragment_featured, null);
		mContent = (View) mContainer.findViewById(R.id.content_container);
		mViewPager = (ViewPager) mContainer.findViewById(R.id.carousel_view_pager);
		mGridView = (GridView) mContainer.findViewById(R.id.featured_list);
		mProgress = (ProgressBar) mContainer.findViewById(R.id.progress);
		populate();
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
					List<Map<String, String>> data = new ArrayList<Map<String, String>>();
					JSONObject feature;
					Map<String, String> map;
					for(int i = 0; i < features.length(); i++) {
						feature = features.getJSONObject(i);
						map = new HashMap<String, String>();
						map.put(TITLE, feature.getJSONObject("title").getString("short"));
						map.put(THUMBNAIL, getThumbnailImage(feature, 150, 200));
						data.add(map);
					}
					
					// adapter field mappings
					String[] from = new String[] { TITLE, THUMBNAIL };
					int[] to = new int[] { R.id.featured_row_title, R.id.featured_row_thumbnail };

					// create adapter!
					ShowAdapter listAdapter = new ShowAdapter(mContext, data, R.layout.featured_row, from, to);
					mGridView.setAdapter(listAdapter);
					
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
	
	public class CarouselAdapter extends PagerAdapter {
		
		private Context mContext;
		private JSONArray mData;
		private List<CarouselItem> mItems;

		public CarouselAdapter(JSONArray data) {
			super();
			mContext = getActivity();
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
					CarouselItem item = new CarouselItem(mContext, title, imageUrl);
				
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
	
    /**
     * 
     * @author leotse
     * A special adapter to use show a movie or tv show
     * Assumes row layout uses loopj image view
     *
     */
    private class ShowAdapter extends SimpleAdapter {
		public ShowAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
		}
		
		@Override
		public void setViewText(TextView v, String text) {
		}
		
		@Override
		public void setViewImage(ImageView v, String value) {
			try {
				SmartImageView image = (SmartImageView) v;
				image.setImageDrawable(null);
				image.setImageUrl(value);
			} catch(ClassCastException e) {
				System.out.println("Please use SmartViewImage in ShowAdapter layout");
				throw e;
			}
		}
    }
}
