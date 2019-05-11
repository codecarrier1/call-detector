package com.example.detectcall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Helper class to detect incoming and outgoing calls.
 * @author Moskvichev Andrey V.
 *
 */
public class CallHelper {

	/**
	 * Broadcast receiver to detect the outgoing calls.
	 */
	public class OutgoingReceiver extends BroadcastReceiver {
		private static final String TAG = "Phone";

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			try {

				if(arg1.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
					String number = arg1.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
					Toast.makeText(arg0,number,Toast.LENGTH_LONG).show();
				}

				if(arg1.getAction().equals("android.intent.action.PHONE_STATE")){

					String state = arg1.getStringExtra(TelephonyManager.EXTRA_STATE);

					if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
						Log.d(TAG, TAG+"Inside Extra state off hook");
						String number = arg1.getStringExtra(arg1.EXTRA_PHONE_NUMBER);
						//Toast.makeText(arg0,number,Toast.LENGTH_LONG).show();
						Log.d(TAG, "outgoing number : " + number);
					}

					else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
						Log.d(TAG, TAG+"Inside EXTRA_STATE_RINGING");
						String number = arg1.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
						Toast.makeText(arg0,number,Toast.LENGTH_LONG).show();
						Log.d(TAG, TAG+"incoming number : " + number);
					}
					else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
						Log.d(TAG, TAG+"Inside EXTRA_STATE_IDLE");
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private Context ctx;
	private TelephonyManager tm;

	private OutgoingReceiver outgoingReceiver;

	public CallHelper(Context ctx) {
		this.ctx = ctx;
		
		outgoingReceiver = new OutgoingReceiver();
	}
	
	/**
	 * Start calls detection.
	 */
	public void start() {
		tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		ctx.registerReceiver(outgoingReceiver, intentFilter);
	}
	
	/**
	 * Stop calls detection.
	 */
	public void stop() {
		ctx.unregisterReceiver(outgoingReceiver);
	}

}
