package edu.ivytech.newsreaderfall2020;

import android.app.Application;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class NewsReaderApp extends Application {
    private long feedMillis = -1;

    public long getFeedMillis() {
        return feedMillis;
    }

    public void setFeedMillis(long feedMillis) {
        this.feedMillis = feedMillis;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PeriodicWorkRequest newsRequest = new PeriodicWorkRequest.Builder(NewsWorker.class, 1, TimeUnit.HOURS).build();
        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("newsRequest", ExistingPeriodicWorkPolicy.REPLACE, newsRequest);
    }
}
