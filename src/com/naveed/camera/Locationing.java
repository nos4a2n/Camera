package com.naveed.camera;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Locationing extends ListActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener {

	EditText currentLoc, descriptionBox;
	ImageView mImageView;
	Button save;

	// Database values
	private String path = "";
	private String latitude = "";
	private String longitude = "";
	private String selectedPlace = "";
	private String description = "";

	LocationClient locationClient;

	ArrayList<String> places = new ArrayList<String>();
	ArrayList<String> tempPlaces = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);

		locationClient = new LocationClient(this, this, this);
		locationClient.connect();

		currentLoc = (EditText) findViewById(R.id.selectedLoc);
		mImageView = (ImageView) findViewById(R.id.imageView1);
		descriptionBox = (EditText) findViewById(R.id.description);
		save = (Button) findViewById(R.id.save);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			path = extras.getString("imagePath");
		}

		setPic();

		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				selectedPlace = currentLoc.getText().toString();
				description = descriptionBox.getText().toString();
				storePictureData();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Locationing.this);
				builder.setMessage("Picture has been saved!");
				builder.setCancelable(false);
				builder.setNeutralButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Locationing.this.finish();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}

	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

		/* Associate the Bitmap to the ImageView */
		mImageView.setImageBitmap(bitmap);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		currentLoc.setText(l.getItemAtPosition(position).toString());
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		//String msg = "Location: " + location.getLatitude() + ", "
			//	+ location.getLongitude();
		Toast.makeText(this, "Still looking for places", Toast.LENGTH_SHORT).show();

		if (locationClient.getLastLocation() != null) {
			Location lastLoc = locationClient.getLastLocation();

			double lat = lastLoc.getLatitude();
			double lng = lastLoc.getLongitude();

			DecimalFormat df = new DecimalFormat("###.######");
			latitude = df.format(lat).toString();
			longitude = df.format(lng).toString();

			String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/"
					+ "json?location="
					+ lat
					+ ","
					+ lng
					+ "&radius=2000&sensor=true"
					+ "&key=AIzaSyAcyn2epzlgrS4DWvVhMYSukgWkE5WSrDw";// API
																		// KEY
			new GetPlaces().execute(placesSearchStr);

			for (int i = 0; i < tempPlaces.size(); i++) {
				places.add(tempPlaces.remove(i));
			}
			if (!places.isEmpty()) {
				locationClient.disconnect();
			}

			setListAdapter(new ArrayAdapter<String>(Locationing.this,
					android.R.layout.simple_list_item_1, places));
		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Looking for nearby places", Toast.LENGTH_SHORT).show();
		LocationRequest request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setInterval(0);
		request.setFastestInterval(0);
		// request.setExpirationDuration(5000);
		locationClient.requestLocationUpdates(request, this);

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	private void storePictureData() {

		SQLiteDatabase db = openOrCreateDatabase("PictureDB", MODE_PRIVATE,
				null);
		db.execSQL("CREATE TABLE IF NOT EXISTS data4 (P_lat TEXT, P_lng TEXT, P_place TEXT, P_description TEXT, P_path TEXT);");
		db.execSQL("INSERT INTO data4 VALUES ('" + latitude + "','"
				+ longitude + "','" + selectedPlace + "','" + description
				+ "','" + path + "');");
		db.close();
	}

	private class GetPlaces extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... placesURL) {
			// fetch places

			// build result as string
			StringBuilder placesBuilder = new StringBuilder();
			// process search parameter string(s)
			for (String placeSearchURL : placesURL) {
				HttpClient placesClient = new DefaultHttpClient();
				try {
					// try to fetch the data

					// HTTP Get receives URL string
					HttpGet placesGet = new HttpGet(placeSearchURL);
					// execute GET with Client - return response
					HttpResponse placesResponse = placesClient
							.execute(placesGet);
					// check response status
					StatusLine placeSearchStatus = placesResponse
							.getStatusLine();
					// only carry on if response is OK
					if (placeSearchStatus.getStatusCode() == 200) {
						// get response entity
						HttpEntity placesEntity = placesResponse.getEntity();
						// get input stream setup
						InputStream placesContent = placesEntity.getContent();
						// create reader
						InputStreamReader placesInput = new InputStreamReader(
								placesContent);
						// use buffered reader to process
						BufferedReader placesReader = new BufferedReader(
								placesInput);
						// read a line at a time, append to string builder
						String lineIn;
						while ((lineIn = placesReader.readLine()) != null) {
							placesBuilder.append(lineIn);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return placesBuilder.toString();
		}

		// process data retrieved from doInBackground
		protected void onPostExecute(String result) {

			try {
				// parse JSON

				// create JSONObject, pass stinrg returned from doInBackground
				JSONObject resultObject = new JSONObject(result);
				// get "results" array
				JSONArray placesArray = resultObject.getJSONArray("results");

				// loop through places
				for (int p = 0; p < placesArray.length(); p++) {
					// parse each place
					// if any values are missing we won't show the marker
					boolean missingValue = false;

					String placeName = "";

					try {
						// attempt to retrieve place data values
						missingValue = false;
						// get place at this index
						JSONObject placeObject = placesArray.getJSONObject(p);

						// name
						placeName = placeObject.getString("name");
					} catch (JSONException jse) {
						Log.d("PLACES", "missing value");
						missingValue = true;
						jse.printStackTrace();
					}

					tempPlaces.add(placeName);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
