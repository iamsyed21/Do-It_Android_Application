package com.the21codes.do_it;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class broadcast_receiver extends BroadcastReceiver {
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationId", 0);
        int projectID = intent.getIntExtra("NOTIFICATION_PROJECT_ID", -1);
        int phaseID = intent.getIntExtra("NOTIFICATION_PHASE_ID", -1);
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");

        Intent notificationIntent = new Intent(context, StepsActivity.class);
        notificationIntent.putExtra("NOTIFICATION_PROJECT_ID", projectID);
        notificationIntent.putExtra("NOTIFICATION_PHASE_ID", phaseID);
        notificationIntent.setAction("Notification_Intent");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "DO_IT:NOTIFICATION")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.doit_logo)
                .setColor(ContextCompat.getColor(context, R.color.Blender_Dark))
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }
}
