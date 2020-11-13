package edu.ivytech.newsreaderfall2020;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class NewsViewModel extends AndroidViewModel {
    final String FILENAME = "news_feed.xml";
    private String TAG = NewsViewModel.class.getSimpleName();
    private MutableLiveData<NewsFeed> mNewsList;



    public NewsViewModel(@NonNull Application application) {
        super(application);
    }

    LiveData<NewsFeed> getNewsList() {
        if(mNewsList == null) {
            mNewsList = new MutableLiveData<>();
            NewsFeed.get().clear();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "Started Feed Download");
                    new FeedDownloader(getApplication().getApplicationContext()).downloadFeed();
                    Log.i(TAG, "Finished Feed Download");
                    mNewsList.postValue(NewsFeed.get());
                }
            }).start();


        }
        return mNewsList;
    }


}
