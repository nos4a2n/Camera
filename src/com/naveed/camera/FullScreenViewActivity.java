package com.naveed.camera;

import java.util.ArrayList;

import com.naveed.camera.adapter.FullScreenImageAdapter;
import com.naveed.camera.helper.Utils;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class FullScreenViewActivity extends Activity{

	private Utils utils;
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;
	 ArrayList<String> pathHolder = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_view);
		
		SQLiteDatabase db = openOrCreateDatabase("PictureDB", 0,
						null);
		        Cursor c = db.rawQuery("SELECT * FROM data4", null);
				c.moveToFirst();
				while(!c.isAfterLast()){
					String path = c.getString(c.getColumnIndex("P_path"));
					pathHolder.add(path);
					c.moveToNext();
				}
				db.close();

		viewPager = (ViewPager) findViewById(R.id.pager);

		utils = new Utils(getApplicationContext());

		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);

		adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,
				utils.getFilePaths(pathHolder));

		viewPager.setAdapter(adapter);

		// displaying selected image first
		viewPager.setCurrentItem(position);
	}
}
