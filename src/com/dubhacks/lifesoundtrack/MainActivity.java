package com.dubhacks.lifesoundtrack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	public static final String SESSION_ID = "com.dubhacks.lifesoundtrack.sessionID";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		String s = "DUBHACKS BITCHES";
		setText(s);
		
	}
	
	public void makeRequest(String email, String password) {
		RequestQueue queue = Volley.newRequestQueue(this);
		String userID = "ee61231b-36b6-4be8-8731-e6bb9a88eb4b";
		String secret = "ca3359f0-e242-4474-b246-8e42a330c7f2";
		String encodedPass = password;
		String encodedEmail = email;
		try {
			encodedEmail = URLEncoder.encode(email, "UTF-8");
			encodedPass = URLEncoder.encode(password, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
		}
		String minutes = "60";
		String url ="https://api.moj.io:443/v1/Login/"+userID+"?secretKey="+secret+"&userOrEmail="+encodedEmail+"&password="+encodedPass+"&minutes={minutes}?minutes="+minutes+"";
		
	// Request a string response from the provided URL.
		StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {
			
		    public void onResponse(String response) {
		        // Display the first 500 characters of the response string.
		   	 String id = null;
		   	 try {
					JSONObject json = new JSONObject(response);
			   	id = json.getString("_id");
				} catch (JSONException e) {
					e.printStackTrace();
				}
		   	 // navigate to another screen
		        setText("id is: "+ id);
		        Intent intent = new Intent(MainActivity.this, MainScreenActivity.class);
		        intent.putExtra(SESSION_ID, id);
		        startActivity(intent);
		        
		    }
		    
		}, new Response.ErrorListener() {
		    @Override
		    public void onErrorResponse(VolleyError error) {
		        setText("That didn't work!");
		    }
		});
		// Add the request to the RequestQueue.
		queue.add(stringRequest);
	}
	
	public void setText(String s) {
		 TextView v = (TextView) findViewById(R.id.text);
        v.setText(s);
	}
	
	public void login(View button) {
		EditText userName = (EditText) findViewById(R.id.userName);
		EditText password = (EditText) findViewById(R.id.password);
		String userNameText = userName.getText().toString();
		String passwordText = password.getText().toString();
		//makes the request and logs in with the response
		makeRequest(userNameText, passwordText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
