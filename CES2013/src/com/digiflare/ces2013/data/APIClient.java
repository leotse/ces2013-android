package com.digiflare.ces2013.data;

import java.util.Arrays;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class APIClient {

	private static final String BASE_URL = "http://ces2013.herokuapp.com/api";
	public static final String FEATURED_URL = BASE_URL + "/featured";
	public static final String MOVIES_URL = BASE_URL + "/movies?limit=50";
	public static final String SHOWS_URL = BASE_URL + "/tvshows?limit=50";
	public static final String RELATED_URL = BASE_URL + "/similar/%s";
	
	private static AsyncHttpClient client = new AsyncHttpClient();
	
	
	// API methods
	public static void get(String url, JsonHttpResponseHandler handler) {
		get(url, null, handler);
	}
	
	public static void getFeatured(JsonHttpResponseHandler handler) {
		get(FEATURED_URL, null, handler);
	}
	
	public static void getMovies(JsonHttpResponseHandler handler) {
		get(MOVIES_URL, null, handler);
	}
	
	public static void getShows(JsonHttpResponseHandler handler) {
		get(SHOWS_URL, null, handler);
	}
	
	public static void getRelated(String showId, JsonHttpResponseHandler handler) {
		String url = String.format(RELATED_URL, showId);
		get(url, null, handler);
	}
	
	// common network access method
	private static void get(String url, RequestParams params, JsonHttpResponseHandler handler) {
		client.get(url, params, handler);
	}
	
	
	// Some more helpers to process response
	public static String getImage(JSONObject item, int minRes, int maxRes) throws JSONException {
		JSONArray boxarts = item.getJSONArray("boxArt");
		
		// first put json objects in an array for sorting...
		JSONObject[] boxartsArray = new JSONObject[boxarts.length()];
		JSONObject boxart;
		for(int i = 0; i < boxarts.length(); i++) {
			boxart = (JSONObject) boxarts.get(i);
			boxartsArray[i] = boxart;
		}
		
		// sort the array!
		Arrays.sort(boxartsArray, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject a, JSONObject b) {
				try {
					int widtha = a.getInt("width");
					int widthb = b.getInt("width");
					
					if(widtha == widthb) return 0;
					return widtha < widthb ? -1 : 1;
					
				} catch(JSONException e) {
					Log.e("APIClient.getImage()", e.getLocalizedMessage());
				}
				return 0;
			}
		});		
		
		// return the best image that satisfies the requirement
		int width;
		String url;
		for(int i = boxartsArray.length - 1; i > 0; i--) {
			boxart = boxartsArray[i];
			width = boxart.getInt("width");
			url = boxart.getString("url");
			if(width >= minRes && width <= maxRes) {
				return url;
			}
		}
		return null;
	}
}