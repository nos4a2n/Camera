package com.naveed.camera.adapter;

import com.naveed.camera.R;
import com.naveed.camera.helper.Utils;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FullScreenImageAdapter extends PagerAdapter {

	private Activity _activity;
	private ArrayList<String> _imagePaths, pictureData;
	private LayoutInflater inflater;
	Cursor cursor;

	// constructor
	public FullScreenImageAdapter(Activity activity,
			ArrayList<String> imagePaths, Cursor c) {
		this._activity = activity;
		this._imagePaths = imagePaths;
		this.cursor = c;

	}

	@Override
	public int getCount() {
		return this._imagePaths.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imgDisplay;
		Button btnClose;
		TextView tvLatLng, tvPlace, tvDescription, tvDate;

		inflater = (LayoutInflater) _activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image,
				container, false);

		imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
		btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
		tvLatLng = (TextView) viewLayout.findViewById(R.id.tvLatLng);
		tvPlace = (TextView) viewLayout.findViewById(R.id.tvPlace);
		tvDescription = (TextView) viewLayout.findViewById(R.id.tvDescription);
		tvDate = (TextView) viewLayout.findViewById(R.id.tvDate);

		cursor.moveToPosition(position);

		String latLng = "Coordinates = "
				+ cursor.getString(cursor.getColumnIndex("P_lat")) + ", "
				+ cursor.getString(cursor.getColumnIndex("P_lng"));
		
		String place = "Taken at: " + cursor.getString(cursor.getColumnIndex("P_place"));
		String description = "Description: " + cursor.getString(cursor.getColumnIndex("P_description"));
		String date = "Time taken: " + cursor.getString(cursor.getColumnIndex("P_date"));
		
		tvPlace.setText(place);
		tvLatLng.setText(latLng);
		tvDescription.setText(description);
		tvDate.setText(date);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position),
				options);
		imgDisplay.setImageBitmap(bitmap);

		// close button click event
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				_activity.finish();
			}
		});

		((ViewPager) container).addView(viewLayout);

		return viewLayout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);

	}
}