package com.dubhacks.lifesoundtrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dubhacks.lifesoundtrack.R.raw;

import android.support.v7.app.ActionBarActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainScreenActivity extends ActionBarActivity {

	public String sessionID;
	
	MediaPlayer mMediaPlayer;
	float volume = 1.0f;
	
	int numCurrentRows = 0;
	double lastSpeed = 0;
	JSONArray jArray;
	boolean playingMusic = false;
	ArrayList<Double> speeds;
	ArrayList<String> dates;
	
	ArrayList<CarMoment> moments;
	
	FadingMusicPlayer softPlayer;
	FadingMusicPlayer mediumPlayer;
	FadingMusicPlayer hardPlayer;
	
	FadingMusicPlayer currentPlayer;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		
		softPlayer = new FadingMusicPlayer(this, R.raw.letitbe);
		mediumPlayer = new FadingMusicPlayer(this, R.raw.sandstorm);
		hardPlayer = new FadingMusicPlayer(this, R.raw.blackskinhead);

		
		sessionID = getIntent().getStringExtra(MainActivity.SESSION_ID);
		TextView text = (TextView) findViewById(R.id.testText);
		Log.v(sessionID, sessionID);
		text.setText(sessionID);
		
		requestUpdate();
		
		final Handler h = new Handler();
	    h.postDelayed(new Runnable()
	    {

	        @Override
	        public void run()
	        {
	            // do stuff then
	            // can call h again after work!
	      	  	if(moments != null && !moments.isEmpty() && playingMusic) {
	      	  		switchPlayer();
	      	  	}
	      	  	requestUpdate();
	      	  	Log.v("rows", ""+numCurrentRows);
	      	  	h.postDelayed(this, 10000);
	        }
	    }, 100); 
		
	}
	
	public void requestUpdate() {
		RequestQueue queue = Volley.newRequestQueue(this);
		String url ="https://api.moj.io/v1/events?limit=50&desc=true";

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
		}) {
			@Override
		    public Map<String, String> getHeaders() throws AuthFailureError {
		        HashMap<String, String> params = new HashMap<String, String>();
		        params.put("MojioAPIToken", sessionID);
		        return params;
		    }
		};
		// Add the request to the RequestQueue.
		queue.add(stringRequest);
	}
	
	public void getJSON(String response) {
		JSONObject jObject = null;
		try {
			jObject = new JSONObject(response);
			int tempNumRows = jObject.getInt("TotalRows");
			Log.v("new", "new: "+tempNumRows);
			Log.v("old", "new: "+numCurrentRows);
			jArray = jObject.getJSONArray("Data");
			
			getAllSpeeds();
			setText(ArrayToString(speeds, dates));
			numCurrentRows = tempNumRows;
		}
		catch(Exception e) {}
	}
	
	//get the speeds from the jArray (might just average all the new speeds)
	public void getAllSpeeds() {
		ArrayList<Double> speedBuilder = new ArrayList<Double>();
		ArrayList<String> dateBuilder = new ArrayList<String>();
		ArrayList<Double> latitudeBuilder = new ArrayList<Double>();
		ArrayList<Double> longitudeBuilder = new ArrayList<Double>();
		for(int i = 0; i < jArray.length(); i++) {
			try{
				JSONObject oneObject = jArray.getJSONObject(i);
				try{
					double speed1 = oneObject.getDouble("Speed");
					String time = oneObject.getString("Time");
					speedBuilder.add(speed1);
					dateBuilder.add(time);
					JSONObject position = oneObject.getJSONObject("Location");
					latitudeBuilder.add(position.getDouble("Lat"));
					longitudeBuilder.add(position.getDouble("Lng"));
				}
				catch(JSONException j){
					//Null, don't add it to the array
				}
			}
			catch(Exception e){}
		}
		speeds = new ArrayList<Double>();
		dates = new ArrayList<String>();
		moments = new ArrayList<CarMoment>();
		for(int i = 0; i < speedBuilder.size(); i++) {
			if(speedBuilder.get(i) != null) {
				speeds.add(speedBuilder.get(i));
				dates.add(dateBuilder.get(i));
				moments.add(new CarMoment(speedBuilder.get(i), latitudeBuilder.get(i), longitudeBuilder.get(i), dateBuilder.get(i)));
			}
		}
  		moments.get(0).getSpeedLimit(MainScreenActivity.this);
	}
	
	public void startMusic(View v) {
		playingMusic = true;
		currentPlayer = intensityToPlayer();
		currentPlayer.start();
	}
	
	public void stopMusic(View v) {
		playingMusic = false;
	}
	
	public void switchPlayer() {
		FadingMusicPlayer tempPlayer = intensityToPlayer();
		tempPlayer.start();
		if(!currentPlayer.equals(tempPlayer)) {
			currentPlayer.fadeOut();
			tempPlayer.fadeIn();
			currentPlayer = tempPlayer;
		}
	}
	
	private FadingMusicPlayer intensityToPlayer() {
		double speed = moments.get(0).speed;
		double speedLimit = moments.get(0).speedLimit;
		Log.v("speed", ""+speed);
		Log.v("limit", ""+speedLimit);
		double intensity = speed/speedLimit;
		if(intensity <= .86)
			return hardPlayer;
		if(intensity >= 1.05)
			return softPlayer;
		else
			return mediumPlayer;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}
	
	public String ArrayToString(ArrayList<Double> speeds2, ArrayList<String> dates) {
		String stringRep = "";
		for(int i = 0; i < speeds2.size(); i++) {
			stringRep = stringRep +dates.get(i)+":"+ speeds2.get(i) + ", ";
		}
		return stringRep;
	}
	
	public void setText(String myText) {
 		TextView text = (TextView) findViewById(R.id.testText);
      text.setText(myText);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
}
