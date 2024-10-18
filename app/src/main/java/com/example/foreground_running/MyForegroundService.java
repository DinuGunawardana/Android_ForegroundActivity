package com.example.foreground_running;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyForegroundService extends Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Start a new thread to simulate background work
        new Thread(() -> {
            while (true) {
                Log.e("Service", "Service is running...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Create notification for the foreground service and display it on the lock screen
        createNotificationChannel();

        // Build the notification with lock screen visibility (pinned notification)
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service Running")
                .setContentText("Your service is running in the background")
                .setSmallIcon(R.drawable.ic_notifications)
                .setPriority(NotificationCompat.PRIORITY_HIGH)  // High priority to pin it on the lock screen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)  // Ensure visibility on the lock screen
                .setOngoing(true)  // Pinned notification, cannot be swiped away
                .build();

        // Start the service with the pinned notification
        startForeground(1, notification);

        return START_STICKY;  // Keep the service running
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Create a notification channel for Android O (API 26) and above
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_HIGH  // High importance for lock screen notifications
            );
            serviceChannel.setDescription("This is the foreground service channel");
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);  // Ensure visibility on lock screen

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
