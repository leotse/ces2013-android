package com.digiflare.ces2013.data;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class APIClient {

	private static final String BASE_URL = "http://ces2013.herokuapp.com/api";
	public static final String FEATURED_URL = BASE_URL + "/featured";
	public static final String MOVIES_URL = BASE_URL + "/movies?limit=50";
	public static final String SHOWS_URL = BASE_URL + "/tvshows?limit=50";
	
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
	
	// common network access method
	private static void get(String url, RequestParams params, JsonHttpResponseHandler handler) {
		client.get(url, params, handler);
	}	
}