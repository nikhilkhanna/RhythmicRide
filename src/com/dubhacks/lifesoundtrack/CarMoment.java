package com.dubhacks.lifesoundtrack;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class CarMoment {
	public double speed;
	public double latitude;
	public double longitude;
	public String time;
	public boolean hasSpeedLimit = false;
	public double speedLimit;
	
	public CarMoment(double speed, double latitude, double longitude, String time) {
		this.speed = speed;
		this.latitude = latitude;
		this.longitude = longitude;
		this.time = time;
	}
	
	public void getSpeedLimit(Context ctx) {
		RequestQueue queue = Volley.newRequestQueue(ctx);
		String url ="http://route.st.nlp.nokia.com/routing/6.2/getlinkinfo.json?waypoint="+latitude+","+longitude+"&linkAttributes=dynamicSpeedInfo";

		// Request a string response from the provided URL.
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
		    new Response.Listener<String>() {
		    @Override
		    public void onResponse(String response) {
		        // Display the first 500 characters of the response string.
		        getJSON(response);
		        //TODO react to response 
		    }
		}, new Response.ErrorListener() {
		    public void onErrorResponse(VolleyError error) {
		        return;
		    }
		});
		// Add the request to the RequestQueue.
		queue.add(stringRequest);
	}
	
	public void getJSON(String response) {
		JSONObject jObject = null;
		double baseSpeed = 0;
		try {
			jObject = new JSONObject(response);
			JSONArray jArray = jObject.getJSONObject("Response").getJSONArray("Link");
			baseSpeed = jArray.getJSONObject(0).getJSONObject("DynamicSpeedInfo").getDouble("BaseSpeed");
			baseSpeed = 3.6 * baseSpeed;
			Log.v("limit: ", ""+baseSpeed);
			hasSpeedLimit = true;
			speedLimit = baseSpeed;
		}
		catch(Exception e) {
			hasSpeedLimit = true;
			speedLimit = 80;
		}
	}
	
}