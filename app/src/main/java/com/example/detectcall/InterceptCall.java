package com.example.detectcall;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.LogRecord;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InterceptCall extends BroadcastReceiver {

    private static final String TAG = "Phone";

    private static final String CHANNEL_ID = "Notification";
    private static final String CHANNEL_NAME = "Notification";
    private static final String CHANNEL_DESCRIPTION = "Notification";

    String myResponse;
    int mIsResponse = 0;

    private void displayNotification (Context context,String title, String content) {

        Toast.makeText(context,content,Toast.LENGTH_LONG).show();

        if(title.equals("Response")) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle(title)
                            .setContentText(content)
                            .setPriority(NotificationCompat.PRIORITY_MAX);

            NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(context);
            mNotificationManager.notify(2, mBuilder.build());
        }
        else {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle(title)
                            .setContentText(content)
                            .setPriority(NotificationCompat.PRIORITY_MAX);

            NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(context);
            mNotificationManager.notify(1, mBuilder.build());
        }

    }

    private void parse(String response) {
        myResponse = response;
        Log.i("response",myResponse);
    }

    public void makeRequest() {

        OkHttpClient client = new OkHttpClient();

        String url = "https://reqres.in/api/users?page=2";

        final Request request = new Request.Builder().url(url).build();

        mIsResponse = 0;

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Response res = response;
                if(response.isSuccessful()) {

                    myResponse = response.body().string();
                    mIsResponse = 1;
                }
            }
        });
    }

    public class Android_Contact {
        public String android_contact_name = "";
        public String android_contact_phone = "";
        public int android_contact_ID = 0;
    }

    @Override
    public void onReceive(final Context arg0, Intent arg1) {

        try {
            if(arg1.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                String number = arg1.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                //Toast.makeText(arg0,"Outcoming:"+number,Toast.LENGTH_LONG).show();

                displayNotification(arg0,"Outgoing call:", number);
                makeRequest();
                final Handler handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {

                        if(mIsResponse == 1) {
                            displayNotification(arg0,"Response",myResponse);
                            mIsResponse = 0;
                            return;
                        }
                        handler.postDelayed(this, 1000);
                    }
                };

                handler.postDelayed(r, 1000);
                //displayNotification(arg0,"Response",myResponse);
            }

            if(arg1.getAction().equals("android.intent.action.PHONE_STATE")){

                String state = arg1.getStringExtra(TelephonyManager.EXTRA_STATE);

                if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                    Log.d(TAG, TAG+"Inside Extra state off hook");
                    String number = arg1.getStringExtra(arg1.EXTRA_PHONE_NUMBER);
                    Log.d(TAG, "outgoing number : " + number);
                }

                else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                    Log.d(TAG, TAG+"Inside EXTRA_STATE_RINGING");
                    String number = arg1.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    //Toast.makeText(arg0,"Incoming:"+number,Toast.LENGTH_LONG).show();
                    Log.d(TAG, TAG+"incoming number : " + number);
                    displayNotification(arg0,"Incoming call:", number);
                    makeRequest();

                    final Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {

                            if(mIsResponse == 1) {
                                displayNotification(arg0,"Response",myResponse);
                                mIsResponse = 0;
                                return;
                            }
                            handler.postDelayed(this, 1000);
                        }
                    };

                    handler.postDelayed(r, 1000);

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
