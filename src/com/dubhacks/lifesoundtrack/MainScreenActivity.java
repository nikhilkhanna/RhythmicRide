package com.dubhacks.lifesoundtrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import android.widget.TextView;

public class MainScreenActivity extends ActionBarActivity {

	public String sessionID;
	
	MediaPlayer mMediaPlayer;
	float volume = 1.0f;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		
		sessionID = getIntent().getStringExtra(MainActivity.SESSION_ID);
		TextView text = (TextView) findViewById(R.id.testText);
		text.setText(sessionID);
		
		mMediaPlayer = MediaPlayer.create(this, R.raw.blackskinhead);
		mMediaPlayer.start();
		fadeOut();

		
	}
	
	void fadeOut() {
	    final Handler h = new Handler();
	    h.postDelayed(new Runnable()
	    {

	        @Override
	        public void run()
	        {
	            Log.d("volume", " " + volume);
	            volume *= .85;
	            mMediaPlayer.setVolume(volume,volume);
	            if(volume <= .04) {
	            	mMediaPlayer.setVolume(0, 0);
	            	return;
	            }
	            h.postDelayed(this, 500);
	        }
	        
	    }, 500); // 1 second delay (takes millis)
	}
	
	void fadeIn() {
	    final Handler h = new Handler();
	    h.postDelayed(new Runnable()
	    {

	        @Override
	        public void run()
	        {
	            Log.d("volume", " " + volume);
	            volume *= .85;
	            mMediaPlayer.setVolume(volume,volume);
	            if(volume <= .04) {
	            	mMediaPlayer.setVolume(0, 0);
	            	return;
	            }
	            h.postDelayed(this, 500);
	        }
	        
	    }, 500); // 1 second delay (takes millis)
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
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
