package com.dubhacks.lifesoundtrack;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

public class FadingMusicPlayer {
	
	public MediaPlayer mediaPlayer;
	float volume = 1;
	
	public FadingMusicPlayer(Context ctx, int resid) {
		mediaPlayer = MediaPlayer.create(ctx, resid);
		mediaPlayer.setLooping(true);
	}
	
	public void start() {
		mediaPlayer.start();
	}
	
	public void fadeOut() {
	    final Handler h = new Handler();
	    h.postDelayed(new Runnable()
	    {

	        @Override
	        public void run()
	        {
	            Log.d("volume", " " + volume);
	            volume *= .85;
	            mediaPlayer.setVolume(volume,volume);
	            if(volume <= .04) {
	            	mediaPlayer.setVolume(0, 0);
	            	volume = 0;
	            	return;
	            }
	            h.postDelayed(this, 500);
	        }
	        
	    }, 500); // 1 second delay (takes millis)
	}
	
	public void fadeIn() {
		 volume = 0;
	    final Handler h = new Handler();
	    h.postDelayed(new Runnable()
	    {

	        @Override
	        public void run()
	        {
	            Log.d("volume", " " + volume);
	            volume *= 1.13;
	            mediaPlayer.setVolume(volume,volume);
	            if(volume >= .98) {
	            	mediaPlayer.setVolume(1, 1);
	            	volume = 1;
	            	return;
	            }
	            h.postDelayed(this, 500);
	        }
	        
	    }, 500); // 1 second delay (takes millis)
	}

}

