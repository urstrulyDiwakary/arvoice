package com.arvoice.fcm;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.arvoice.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "my_channel";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM_TOKEN", "Refreshed token: " + token);
        sendTokenToServer(token);
    }

    private void sendTokenToServer(String token) {
        Log.d("TOKEN_UPLOAD", "Sending token to server: " + token);
        // TODO: Implement your API logic here
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d("FCM", "Data: " + remoteMessage.getData());

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            showNotification(title, message);
        }

        if (remoteMessage.getData().size() > 0) {
            Log.d("FCM", "Data Payload: " + remoteMessage.getData());
        }
    }

    private void showNotification(String title, String message) {
        // ✅ Create the notification channel first
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);  // Dismiss on click

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 33) {
            // You may want to notify the app to ask for permission in the UI
            Log.w("FCM", "POST_NOTIFICATIONS permission not granted.");
            return;
        }

        manager.notify(101, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "General Notifications";
            String description = "Channel for general app notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
