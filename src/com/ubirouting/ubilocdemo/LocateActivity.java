package com.ubirouting.ubilocdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ubirouting.naturelib.Position;
import com.ubirouting.naturelib.ShituLocationListener;
import com.ubirouting.naturelib.ShituLocationManager;
import com.ubirouting.naturelib.ShituLocationParameters;
import com.ubirouting.naturelib.ShituStore;

public class LocateActivity extends Activity implements ShituLocationListener {

	ShituLocationManager mLocationManager;

	private static final String TAG = "LocateActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ShituStore store = Constant.sStorePass;
		Toast.makeText(this, store.toString(), Toast.LENGTH_LONG).show();

		ShituLocationParameters para = new ShituLocationParameters(store, (short) 1, ShituLocationManager.LOC_IBEACON_ONLY);
		mLocationManager = ShituLocationManager.newManager(this, para);
		mLocationManager.setOnLocationListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocationManager.onResume();
		mLocationManager.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationManager.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocationManager.onDestroy();
	}

	@Override
	public void onException(String arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetAngle(int arg0) {
		Log.d(TAG, "angle is " + arg0);
	}

	@Override
	public void onGetLocation(Position arg0) {
		Log.d(TAG, "position is " + arg0);
	}

	@Override
	public void onGetStatus(int arg0) {
		Log.d(TAG, "status is " + arg0);

	}

	@Override
	public void onSwitchFloor(int arg0) {
		Log.d(TAG, "switch to floor " + arg0);

	}
}
