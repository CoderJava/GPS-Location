package com.tugas.gpslocation;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;

public class TimerService extends Service {
	
	private CountDownTimer countDownTimer;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		countDownTimer = new CountDownTimer(30000, 1000) {
			@Override
			public void onFinish() {
				Intent intentBroadcastTimer = new Intent();
				intentBroadcastTimer.setAction("broadcast_timer");
				intentBroadcastTimer.putExtra("countdown", 0L);
				sendBroadcast(intentBroadcastTimer);
				new Handler().postDelayed(new Runnable() {
					public void run() {
						countDownTimer.start();
					}					
				}, 1000);								
			}

			@Override
			public void onTick(long millis) {
				Intent intentBroadcastTimer = new Intent();
				intentBroadcastTimer.setAction("broadcast_timer");
				intentBroadcastTimer.putExtra("countdown", millis);
				sendBroadcast(intentBroadcastTimer);
			}		
		};
		countDownTimer.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		countDownTimer.cancel();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	
}
