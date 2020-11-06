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
    private static final String API_KEY = "AWjPeE3hqlzvrQxyhaA9il60sTG2ofX8";
    private static final String FETCH_URL = "https://api.nytimes.com/svc/topstories/v2/science.json";
    private static final Uri ENDPOINT = Uri.parse(FETCH_URL)
            .buildUpon()
            .appendQueryParameter("api-key", API_KEY)
            .build();


    public NewsViewModel(@NonNull Application application) {
        super(application);
    }

    LiveData<NewsFeed> getNewsList() {
        if(mNewsList == null) {
            mNewsList = new MutableLiveData<>();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "Started Feed Download");
                    downloadNewsItems();
                    Log.i(TAG, "Finished Feed Download");
                    mNewsList.postValue(NewsFeed.get());
                }
            }).start();


        }
        return mNewsList;
    }

    private void readFile() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            RSSFeedHandler rssFeedHandler = new RSSFeedHandler();
            xmlReader.setContentHandler(rssFeedHandler);
            FileInputStream in = getApplication().openFileInput(FILENAME);

            InputSource is = new InputSource(in);
            xmlReader.parse(is);

        } catch (Exception e) {
            Log.e("News Reader View Model", e.toString());
        }
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection. getResponseMessage() + ": with " + urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0 ) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getURLString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    private void downloadNewsItems() {
        try {
            String url = ENDPOINT.toString();
            String jsonString = getURLString(url);
            Log.i(TAG, "Recieived JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
    }

    private void parseItems(JSONObject jsonBody) throws IOException, JSONException {
        NewsFeed feed = NewsFeed.get();
        feed.setTitle(jsonBody.getString("section"));
        feed.setPubDate(jsonBody.getString("last_updated"));
        JSONArray newsJsonArray = jsonBody.getJSONArray("results");
        for(int i = 0; i< newsJsonArray.length(); i++) {
            JSONObject article = newsJsonArray.getJSONObject(i);
            NewsItem item = new NewsItem();
            item.setPubDate(article.getString("published_date"));
            item.setDescription(article.getString("abstract"));
            item.setLink(article.getString("url"));
            item.setTitle(article.getString("title"));
            feed.addItem(item);
        }

    }
}
