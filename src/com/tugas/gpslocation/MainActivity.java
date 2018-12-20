package com.tugas.gpslocation;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.tugas.gpslocation.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.maps.*;

public class MainActivity extends Activity implements OnClickListener {

	private Button buttonStartLocationUpdate;
	private Button buttonStopLocationUpdate;
	private TextView textViewTimer;
	private TextView textViewLocation;
	private TextView textViewAddress;

	private LocationListener locationListener;
	private LocationManager locationManager;
	
	private BroadcastReceiver timerBroadcastReceiver;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initViews();
		initListeners();
		initLocationListener();
		initLocationManager();
		initTimerBroadcastReceiver();
		registerTimerBroadcast();
	}
	
	private void initTimerBroadcastReceiver() {
		timerBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent data) {
				if (data.getExtras() != null) {
					long millis = data.getLongExtra("countdown", -1L);
					int second = (int) (millis / 1000);
					textViewTimer.setText("Timer: " + second);
					if (second == 1) {
						Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (lastLocation == null) {
							showToastMessage("Location not found");
						} else {
							NumberFormat numberFormat = NumberFormat.getInstance();
							numberFormat.setMaximumFractionDigits(5);
							String strLatitude = numberFormat.format(lastLocation.getLatitude());
							String strLongitude = numberFormat.format(lastLocation.getLongitude());
							String strLocation = "Lat/Lng: " + strLatitude + "/" + strLongitude;
							textViewLocation.setText(strLocation);
							
							Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
							try {
								List<Address> addresses = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
								if (addresses.size() > 0) {									
									String address = addresses.get(0).getAddressLine(0);
									String city = addresses.get(0).getAddressLine(1);
									String country = addresses.get(0).getAddressLine(2);
									textViewAddress.setText("Address: " + address + ", " + city + ", " + country);
									showToastMessage("Location available");
								} else {
									showToastMessage("Failed to get address value");
								}						
							} catch (IOException e) {
								e.printStackTrace();
								showToastMessage("Failed to get address value with message: " + e.getMessage());
							}						
							
						}
					}
				}
			}
		};
	}
	
	private void registerTimerBroadcast() {
		try {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction("broadcast_timer");
			registerReceiver(timerBroadcastReceiver, intentFilter);
		} catch (Exception e) {
			e.printStackTrace();
			showToastMessage("Error with message: " + e.getMessage());
		}
	}

	@Override
	protected void onPause() {
		stopLocation();
		super.onPause();
	}

	private void initViews() {
    	buttonStartLocationUpdate = (Button) findViewById(R.id.button_start_location_update);
    	buttonStopLocationUpdate = (Button) findViewById(R.id.button_stop_location_update);
    	textViewTimer = (TextView) findViewById(R.id.text_view_timer);
    	textViewLocation = (TextView) findViewById(R.id.text_view_location);
    	textViewAddress = (TextView) findViewById(R.id.text_view_address);
    }
	
	private void initListeners() {
		buttonStartLocationUpdate.setOnClickListener(this);
		buttonStopLocationUpdate.setOnClickListener(this);
	}
	
	private void initLocationManager() {
		if (locationManager == null) {
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		}
	}
	
	private void initLocationListener() {
		if (locationListener == null) {
			locationListener = new LocationListener() {
				public void onLocationChanged(Location location) {
					showToastMessage("Location Changed");	
				}

				public void onProviderDisabled(String provider) {
					/* Nothing to do in here */			
				}

				public void onProviderEnabled(String provider) {
					/* Nothing to do in here */					
				}

				public void onStatusChanged(String provider, int status, Bundle extras) {
					/* Nothing to do in here */					
				}
			};
		}
	}

	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_start_location_update:
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
				Intent intentTimerService = new Intent(this, TimerService.class);
				startService(intentTimerService);
				textViewTimer.setText("Timer: 30");
				showToastMessage("Loccation Service Running");
				break;
			case R.id.button_stop_location_update:
				stopLocation();
				break;
		}
	}
	
	private void stopLocation() {
		if (!textViewTimer.getText().equals("Timer: -")) {
			locationManager.removeUpdates(locationListener);
			Intent intentTimerService = new Intent(this, TimerService.class);
			stopService(intentTimerService);
			textViewTimer.setText("Timer: -");
			textViewLocation.setText("Lat/Lng: -");
			textViewAddress.setText("Address: -");
			showToastMessage("Location Service Stopped");
		}		
	}
	
	private void showToastMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG)
			.show();
	}
	
}