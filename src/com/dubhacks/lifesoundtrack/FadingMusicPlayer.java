package com.dubhacks.lifesoundtrack;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

public class FadingMusicPlayer {
	
	public MediaPlayer mediaPlayer;
	float volume = 1;
	boolean isFadingIn;
	
	public FadingMusicPlayer(Context ctx, int resid) {
		mediaPlayer = MediaPlayer.create(ctx, resid);
		mediaPlayer.setLooping(true);
	}
	
	public void start() {
		mediaPlayer.start();
	}
	
	public void pause() {
		mediaPlayer.pause();
	}
	
	public void fadeOut() {
	    final Handler h = new Handler();
	    h.postDelayed(new Runnable()
	    {

	        @Override
	        public void run()
	        {
	      	  if(isFadingIn)
	      		  return;
	      	  
	            Log.d("volume fading out: ", " " + volume);
	            volume *= .9;
	            mediaPlayer.setVolume(volume,volume);
	            if(volume <= .2) {
	            	mediaPlayer.setVolume(0, 0);
	            	volume = 0;
	            	pause();
	            	return;
	            }
	            h.postDelayed(this, 500);
	        }
	        
	    }, 500); // 1 second delay (takes millis)
	}
	
	public void fadeIn() {
		 isFadingIn = true;
		 volume = .1f;
		 mediaPlayer.start();
	    final Handler h = new Handler();
	    h.postDelayed(new Runnable()
	    {

	        @Override
	        public void run()
	        {
	      	  	if(volume  <.001) 
	      	  		volume = .1f;
	            Log.d("volume fading in: ", " " + volume);
	            volume *= 1.18;
	            mediaPlayer.setVolume(volume,volume);
	            if(volume >= .98) {
	            	mediaPlayer.setVolume(1, 1);
	            	volume = 1;
	            	isFadingIn = false;
	            	return;
	            }
	            h.postDelayed(this, 500);
	        }
	        
	    }, 500); // 1 second delay (takes millis)
	}

}

