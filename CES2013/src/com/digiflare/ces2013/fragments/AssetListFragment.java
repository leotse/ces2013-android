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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

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
	ListView mListView;
	ProgressBar mProgress;
	JSONArray features;
	ShowAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(null == mContainer) {
			mContainer = (View) inflater.inflate(R.layout.fragment_featured, null); 
			mListView = (ListView) mContainer.findViewById(R.id.featured_list);
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
					
					// process response into something we can work with
					List<Map<String, String>> data = new ArrayList<Map<String, String>>();
					JSONObject feature;
					Map<String, String> map;
					for(int i = 0; i < features.length(); i++) {
						feature = features.getJSONObject(i);
						map = new HashMap<String, String>();
						map.put(TITLE, feature.getJSONObject("title").getString("short"));
						map.put(THUMBNAIL, getThumbnailUrl(feature));
						data.add(map);
					}
					
					// adapter field mappings
					String[] from = new String[] { TITLE, THUMBNAIL };
					int[] to = new int[] { R.id.featured_row_title, R.id.featured_row_thumbnail };

					// create adapter!
					adapter = new ShowAdapter(mContext, data, R.layout.featured_row, from, to);
					mListView.setAdapter(adapter);
					
					// and finally hide the indicator
					mProgress.setVisibility(View.GONE);
					
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
	private String getThumbnailUrl(JSONObject feature) throws JSONException {
		JSONArray boxArts = feature.getJSONArray("boxArt");
		
		// find the box art!
		JSONObject boxArt;
		int width;
		for(int i = 0; i < boxArts.length(); i++) {
			boxArt = boxArts.getJSONObject(i);
			width = boxArt.getInt("width");
			if(width >= 88) {
				return boxArt.getString("url");
			}
		}
		return null;
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
		public void setViewImage(ImageView v, String value) {
			try {
				SmartImageView image = (SmartImageView) v;
				image.setImageUrl(value);
			} catch(ClassCastException e) {
				System.out.println("Please use SmartViewImage in ShowAdapter layout");
				throw e;
			}
		}
    }
}
