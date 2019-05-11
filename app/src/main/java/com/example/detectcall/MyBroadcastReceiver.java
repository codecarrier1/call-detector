
package com.example.detectcall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(MyBroadcastReceiver.class.getSimpleName(),intent.toString());
        Log.d("aaaaa","sssssssssssss");
        Toast.makeText(context, "Outgoing call catched!", Toast.LENGTH_LONG).show();

    }
}