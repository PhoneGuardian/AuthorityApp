package it.polimi.guardian.authorityapp;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/*
Copyright 2015 StolicJovanovicSkarica

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Demo";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i = 0; i < 5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification("Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        String[] data = msg.split("&");
        Event ev = new Event();
        ev.setLat(Integer.parseInt(data[0]));
        ev.setLng(Integer.parseInt(data[1]));
        ev.setDescription(data[2]);
        ev.setEvent_time(data[3]);
        ev.setLocation_acc(Float.parseFloat(data[4]));
        ev.setType_of_event(data[5]);
        ev.setUser_phone(data[6]);
        ev.setAnonymous(Integer.parseInt(data[7]));
        ev.setAddress(data[8]);

        Context ctx = getApplicationContext();
        Intent clickActivity = new Intent(ctx,MapActivity.class);
        clickActivity.putExtra("lat",ev.getLat());
        clickActivity.putExtra("lng",ev.getLng());
        clickActivity.putExtra("eventDescription",ev);

        NotificationCompat.Builder mBuilder = null;
        switch (data[5]) {
            case "F":
                mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.flame_gray)
                                .setContentTitle("Alert!")
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(data[2]))
                                .setContentText(data[2]);
                break;
            case "P":
                mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.police_badge_gray)
                                .setContentTitle("Alert!")
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(data[2]))
                                .setContentText(data[2]);
                break;
            case "E":
                mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ambulance_gray)
                                .setContentTitle("Alert!")
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(data[2]))
                                .setContentText(data[2]);
                break;
        }


        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}