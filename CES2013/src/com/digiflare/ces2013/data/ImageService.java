package com.digiflare.ces2013.data;

import android.util.Log;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

public class ImageService {

	// constants
	private static final String BASE_URL = "http://ces2013.herokuapp.com/resource/android";
	private static final String LOGO_URL = BASE_URL + "/logo";
	private static final String BACKGROUND_URL = BASE_URL + "/background";

	
	// fields
	private AsyncHttpClient client = new AsyncHttpClient();
	
	
	// singleton implementation
	private static ImageService instance;
	public static ImageService getInstance() {
		if(null == instance) {
			instance = new ImageService();
		}
		return instance;
	}
	
	
	// downloads and caches the background image
	public void getBackground() {
		
		String[] allowedContentTypes = new String[] { "image/png", "image/jpeg" };
		client.get(BACKGROUND_URL, new BinaryHttpResponseHandler(allowedContentTypes) {
			
			@Override
			public void onSuccess(byte[] image) {
				Log.i("ImageService", "done downloading!");
			}
			
			@Override
			public void handleFailureMessage(Throwable e, byte[] body) {
				Log.e("ImageService.getBackground()", e.getLocalizedMessage());
			}
		});
	}
	
	// downloads and caches the logo image
	public void getLogo() {
	}
}
