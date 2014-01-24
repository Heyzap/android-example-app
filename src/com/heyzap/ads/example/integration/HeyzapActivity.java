package com.heyzap.ads.example.integration;

import com.heyzap.ads.example.integration.util.SystemUiHider;
import com.heyzap.sdk.ads.HeyzapAds;
import com.heyzap.sdk.ads.IncentivizedAd;
import com.heyzap.sdk.ads.InterstitialAd;
import com.heyzap.sdk.ads.VideoAd;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

/*** HEYZAP ADS INTEGRATION
 * 
 * This activity demonstrates how to use various
 * features of the Heyzap Ads SDK.
 *
 *
 * IMPORTANT: Before you look at the rest of this code, look at AndroidManifest.xml for the required additions.
 *
 */


public class HeyzapActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*** START HERE
		 * 
		 * You must "start" Heyzap at the beginning of your app's lifecycle.
		 */
		
		HeyzapAds.start(this);
		
		setupCallbacks(); //Go here to see how to setup callbacks for Heyzap ads
		
		/* After you are done, continue creating your activity. */
		setContentView(R.layout.activity_heyzap);
		setupInterface();

	}
	
	/*** INTERSTITIALS
	 * 
	 * Interstitials are fullscreen ads composed of a static or a video creative. They will display over a game in another activity.
	 * 
	 */
	
	public void onInterstitialFetch(View view) {
		
		/* FETCH AN AD (REQUIRED)
		 * Before you can show an ad, you must fetch one from the ad server. Doing this as far in advance of showing the ad (e.g. at the beginning of a level)
		 * is highly recommended. IMPORTANT: If an ad has not been fetched beforehand, an ad cannot be shown.
		 * 
		 * When an ad has been successfully fetched, a 
		 * 
		 */
		InterstitialAd.fetch();
	}
	
	public void onInterstitialDisplay(View view) {
		
		/* CHECK IF AD IS AVAILABLE (RECOMMENDED)
		 * */
		
		if (InterstitialAd.isAvailable()) {
			
			/* DISPLAY AN AD (HIGHLY RECOMMENDED)
			 * Note: you must pass an Activity into this method.
			 * */
			
			InterstitialAd.display(this);
		} else {
			//Uh oh, an ad is not available yet.
			
			Toast.makeText(this, "An interstitial ad is not available yet.", Toast.LENGTH_SHORT).show();
		}
	}
	
	/*** VIDEOS
	 * 
	 * Videos are fullscreen ads, but unlike interstitials they only show a video.
	 *  
	 **/
	
	public void onVideoFetch(View view) {
		/* This works exactly the same as fetching an interstitial. */
		VideoAd.fetch();
	}
	
	public void onVideoDisplay(View view) {
		
		if (VideoAd.isAvailable()) {
			VideoAd.display(this);
		}
	}
	
	/*** REWARDED VIDEO VIEW
	 * 
	 * Rewarded Video Views (Incentivized Ads) show your users a video and when they complete the ad, are given a reward (such as points or virtual currency).
	 * 
	 * Performance of these ads is better because users want to get the reward.
	 * 
	 * */
	
	public void onRewardedViewFetch(View view) {
		/* This works exactly the same as fetching an interstitial. */
		IncentivizedAd.fetch();
	}
	
	public void onRewardedViewDisplay(View view) {
		if (IncentivizedAd.isAvailable()) {
			IncentivizedAd.display(this);
		}
	}
	
	/*** CALLBACKS
	 * 
	 * If you would like to be notified when various things happen during the lifecycle of an ad, callbacks are what you need.
	 * 
	 */
	
	protected void setupCallbacks() {
		
		/* STATUS CALLBACKS
		 * OnStatusListener callbacks apply to the general lifecycle of an ad.
		 */
		HeyzapAds.setOnStatusListener(new HeyzapAds.OnStatusListener() {

			@Override
			public void onAvailable(String tag) {
				/* An ad you fetched is now available.
				 * */
				
				HeyzapActivity.this.doSomethingWithTheInterface(true);
			}
			
			@Override
			public void onShow(String tag) {
				/* An ad has been successfully shown to the user.
				 * */
				
				HeyzapActivity.this.doSomethingWithTheInterface(false);
			}

			@Override
			public void onClick(String tag) {
				/* Somebody clicked on an ad. Note: when an ad is clicked on, the user will temporarily leave your app to
				 * go to the play store. 
				 * */
			}
			
			@Override
			public void onHide(String tag) {
				/* The user has hidden the ad and has been returned to your app.
				 * */
			}
			
			@Override
			public void onFailedToFetch(String tag) {
				/* Uh oh! The fetch did not work. This is usually for one of three reasons:
				 * a) your internet connection is not working
				 * b) your app has not been added to Heyzap's database yet (contact sales or add your game from the dashboard).
				 * c) the ad server could not find a good enough ad
				 * */
				
				Toast.makeText(HeyzapActivity.this, "No ad was able to be fetched.", Toast.LENGTH_SHORT).show();
				HeyzapActivity.this.doSomethingWithTheInterface(false);
			}

			@Override
			public void onFailedToShow(String tag) {
				/* Oops! You attempted to show an ad when none had been successfully fetched yet.
				 * */
				
				Toast.makeText(HeyzapActivity.this, "An ad could not be shown because none were available.", Toast.LENGTH_SHORT).show();
				HeyzapActivity.this.doSomethingWithTheInterface(false);
			}
		});
		
		/*
		 * INCENTIVIZED RESULT CALLBACKS
		 * 
		 */
		
		HeyzapAds.setOnIncentiveResultListener(new HeyzapAds.OnIncentiveResultListener() {
			
			@Override
			public void onComplete() {
				/*
				 * Great! The user has completed the ad. Give them a nice reward for their hard work. This callback is called right after onHide above.
				 */
				
				HeyzapActivity.this.giveAReward();
			}
			
			@Override
			public void onIncomplete() {
				/*
				 * Darn! The user did not complete the ad. Don't give them anything.
				 */
				
				HeyzapActivity.this.doNothing();
			}
			

		});
	}
	
	/* Some not useful code below. */
	
	private void giveAReward() {
		HeyzapActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				HeyzapActivity.this.completedRadioButton.setChecked(true);
			}
		});
	}
	
	private void doNothing() {
		HeyzapActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				HeyzapActivity.this.completedRadioButton.setChecked(false);
			}
		});
	}
	
	private void doSomethingWithTheInterface(final Boolean notify) {
		
		HeyzapActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (InterstitialAd.isAvailable() && notify) {
					Toast.makeText(HeyzapActivity.this, "Interstitial ad is now available. Try showing one.", Toast.LENGTH_SHORT).show();
				}
				
				HeyzapActivity.this.interstitialRadioButton.setChecked(InterstitialAd.isAvailable());
				
				if (VideoAd.isAvailable() && notify) {
					Toast.makeText(HeyzapActivity.this, "Video ad is now available. Try showing one.", Toast.LENGTH_SHORT).show();
				}
				
				HeyzapActivity.this.videoRadioButton.setChecked(VideoAd.isAvailable());
				
				if (IncentivizedAd.isAvailable() && notify) {
					Toast.makeText(HeyzapActivity.this, "Rewarded video view ad is now available. Try showing one.", Toast.LENGTH_SHORT).show();
				}
				
				HeyzapActivity.this.incentivizedRadioButton.setChecked(VideoAd.isAvailable());
			}
		});
		
	}
	
	protected RadioButton interstitialRadioButton;
	protected RadioButton videoRadioButton;
	protected RadioButton incentivizedRadioButton;
	protected RadioButton completedRadioButton;
	private void setupInterface() {
		this.interstitialRadioButton = (RadioButton)findViewById(R.id.interstitial_radio);
		this.videoRadioButton = (RadioButton)findViewById(R.id.video_radio);
		this.incentivizedRadioButton = (RadioButton)findViewById(R.id.incentivized_radio);
		this.completedRadioButton = (RadioButton)findViewById(R.id.completed_radio);
	}
}
