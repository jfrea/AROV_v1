package com.jwetherell.augmented_reality.data;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.jwetherell.augmented_reality.R;
import com.jwetherell.augmented_reality.ui.IconMarker;
import com.jwetherell.augmented_reality.ui.Marker;

/**
 * This class extends NetworkDataSource to fetch data from a Csumb Campus Json file. 
 */

public class GooglePlacesDataSource extends NetworkDataSource{
	
	private static final String URL = "http://hosting.otterlabs.org/classes/hernandezjuana/capstone/csumb.json";

	private static Bitmap icon = null;


	@Override
	public String createRequestURL(double lat, double lon, double alt, float radius, String locale) {
		try {
			return URL;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public List<Marker> parse(String URL) {
		if (URL == null) throw new NullPointerException();

		InputStream stream = null;
		stream = getHttpGETInputStream(URL);
		if (stream == null) throw new NullPointerException();

		String string = null;
		string = getHttpInputString(stream);
		if (string == null) throw new NullPointerException();

		JSONObject json = null;
		try {
			json = new JSONObject(string);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (json == null) throw new NullPointerException();

		return parse(json);
	}

	@Override
	public List<Marker> parse(JSONObject root) {
		if (root == null) throw new NullPointerException();

		JSONObject jo = null;
		JSONArray dataArray = null;
		List<Marker> markers = new ArrayList<Marker>();

		try {
			if (root.has("csumb")) dataArray = root.getJSONArray("csumb");
			if (dataArray == null) return markers;
			Log.d(getClass().getName(), dataArray.toString());
			int top = Math.min(MAX, dataArray.length());
			for (int i = 0; i < top; i++) {
				jo = dataArray.getJSONObject(i);
				Marker ma = processJSONObject(jo);
				if (ma != null) markers.add(ma);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return markers;
	}

	private Marker processJSONObject(JSONObject jo) {
		if (jo == null) throw new NullPointerException();

		if (!jo.has("geometry")) throw new NullPointerException();

		Marker ma = null;
		try {
			Double lat = null, lon = null;

			if (!jo.isNull("geometry")) {
				JSONObject geo = jo.getJSONObject("geometry");
				JSONObject coordinates = geo.getJSONObject("location");
				lat = Double.parseDouble(coordinates.getString("lat"));
				lon = Double.parseDouble(coordinates.getString("lng"));
			}
			if (lat != null) {
				String user = jo.getString("name");	
				
			//Create customized icon dynamically from url
			try {
				URL url = new URL(jo.getString("icon"));
				icon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			} 
			catch (Exception e) {
				e.printStackTrace();}

			ma = new IconMarker(user + ": " + jo.getString("name"), lat, lon, 0, Color.RED, icon, "test");
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ma;
	}
}
