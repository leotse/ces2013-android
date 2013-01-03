package com.digiflare.ces2013.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.digiflare.ces2013.R;
import com.loopj.android.image.SmartImageView;

public class CarouselItem extends FrameLayout {

	public static final String TITLE = "title";
	public static final String URL = "url";
	
	Context mContext;
	View mContainer;
	TextView mTitle;
	SmartImageView mImage;
	
	public CarouselItem(Context context, AttributeSet attributes) {
		super(context, attributes);
	}
	
	public CarouselItem(Context context, String title, String imageUrl) {
		super(context);
		mContext = context;
		
		// create view
		mContainer = View.inflate(context, R.layout.fragment_carousel_item, null);
		mTitle = (TextView) mContainer.findViewById(R.id.carousel_item_title);
		mImage = (SmartImageView) mContainer.findViewById(R.id.carousel_item_image);
		populate(title, imageUrl);
		this.addView(mContainer);
	}
	
	
	// method to set data for this carousel item
	private void populate(String title, String imageUrl) {
		mTitle.setText(title);
		mImage.setImageUrl(imageUrl);
	}
}