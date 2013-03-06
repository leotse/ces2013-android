package com.digiflare.ces2013.data;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

public class ImageService {

	// constants
	private static final String BASE_URL = "http://ces2013.herokuapp.com/resource/android";
	private static final String LOGO_URL = BASE_URL + "/logo";
	private static final String BACKGROUND_URL = BASE_URL + "/background";
	
	private static final String[] ALLOWED_CONTENT_TYPES = new String[] { "image/png", "image/jpeg" };
	private static final String BAKCGROUND_CACHE_KEY = "background-cache";
	private static final String LOGO_CACHE_KEY = "logo-cache";

	
	// fields
	private AsyncHttpClient client = new AsyncHttpClient();
	private HashMap<String, BitmapDrawable> cache = new HashMap<String, BitmapDrawable>();
	private Context context = null;
	
	
	// singleton implementation
	private static ImageService instance;
	public static ImageService getInstance(Context c) {
		if(null == instance) {
			instance = new ImageService();
			instance.setContext(c);
		}
		return instance;
	}
	
	
	// sets the context of this service
	public void setContext(Context c) {
		context = c;
	}
	
	
	// downloads and caches the background image
	public void getBackground(final ImageDownloadedHandler handler) {
		getImage(BACKGROUND_URL, BAKCGROUND_CACHE_KEY, handler);
	}
	
	// downloads and caches the logo image
	public void getLogo(final ImageDownloadedHandler handler) {
		getImage(LOGO_URL, LOGO_CACHE_KEY, handler);
	}
	
	// download and caches an image if it is not already downlaoded
	// return cached copy otherwise
	public void getImage(String url, final String cacheKey, final ImageDownloadedHandler handler) {
		
		// return cached copy if available
		BitmapDrawable cached = cache.get(cacheKey);
		if(null != cached) {
			
			handler.onSuccess(cached);
			
		} else {
		
			// or download it from server!
			client.get(url, new BinaryHttpResponseHandler(ALLOWED_CONTENT_TYPES) {
				
				@Override
				public void onSuccess(byte[] image) {
					Log.i("ImageService.getBackground()", "done downloading!");
					
					// create the drawable
					Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
					BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
					
					// cache it in memroy and make callback!
					cache.put(cacheKey, drawable);
					handler.onSuccess(drawable);
				}
				
				@Override
				public void handleFailureMessage(Throwable e, byte[] body) {
					Log.e("ImageService.getBackground()", e.getLocalizedMessage());
					handler.onError(e);
				}
			});
		}
		
	}
}