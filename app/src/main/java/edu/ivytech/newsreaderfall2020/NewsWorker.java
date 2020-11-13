package edu.ivytech.newsreaderfall2020;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NewsWorker extends Worker {
    private final String TAG = "News Worker";

    public NewsWorker(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
    }

    @NonNull
    @Override
    public Result doWork() {
        NewsReaderApp app = (NewsReaderApp) getApplicationContext();
        long feedMillis = app.getFeedMillis();
        Log.d(TAG, "The worker is trying to download something.");
        new FeedDownloader(app).downloadFeed();
        Log.d(TAG, "Download Finished");
        if(app.getFeedMillis() != feedMillis) {
            sendNotification("There are new news items available");
        } else {
            sendNotification("The news items are the same");
        }
        return Result.success();
    }

    private void sendNotification(String text) {
        Log.d(TAG, text + " Notification displayed");

        Intent notificationIntent = new Intent(this.getApplicationContext(), MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, flags);
        int icon = R.drawable.ic_launcher_foreground;
        CharSequence tickerText = "News Feed Download Completed";
        CharSequence contentTitle = getApplicationContext().getText(R.string.app_name);
        CharSequence contentText = text;
        Notification notification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(icon)
                .setTicker(tickerText)
                .setContentText(contentText)
                .setContentTitle(contentTitle)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        NotificationManager manager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,notification);
    }
}
