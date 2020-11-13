package edu.ivytech.newsreaderfall2020;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class FeedDownloader {
    private static String CNN_URL = "http://rss.cnn.com/rss/cnn_tech.rss";
    private final static String TAG = "Feed Downloader";
    private static final String API_KEY = "AWjPeE3hqlzvrQxyhaA9il60sTG2ofX8";
    private static final String NYT_URL = "https://api.nytimes.com/svc/topstories/v2/science.json";
    private static final Uri NYT_ENDPOINT = Uri.parse(NYT_URL)
            .buildUpon()
            .appendQueryParameter("api-key", API_KEY)
            .build();
    final String FILENAME = "news_feed.xml";

    Context mContext;

    public FeedDownloader(Context context) {
        mContext = context;
    }

    void downloadFeed() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences((mContext));
        String source = sharedPreferences.getString(mContext.getString(R.string.newsSource),"cnn");
        if(source.equals("cnn")) {
            NewsItem.setDateInFormat("EEE, dd MMM yyyy HH:mm:ss Z");
            NewsFeed.get().setDateInFormat("EEE, dd MMM yyyy HH:mm:ss Z");
            downloadCNN();
            readCNN();
        } else if(source.equals("nyt")) {
            NewsItem.setDateInFormat("yyyy-MM-dd'T'HH:mm:ssXXX"); //2020-11-12T14:27:42-05:00
            NewsFeed.get().setDateInFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            downloadNYT();
        }
        NewsReaderApp app = (NewsReaderApp)mContext.getApplicationContext();
        app.setFeedMillis(NewsFeed.get().getPubDateMillis());
    }


    private void downloadCNN() {
        if(isNetworkConnected()) {
            try {
                URL url = new URL(CNN_URL);
                InputStream in = url.openStream();
                FileOutputStream out = mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                byte[] buffer = new byte[1024];
                int bytesRead = in.read(buffer);
                while (bytesRead != -1) {
                    out.write(buffer, 0, bytesRead);
                    bytesRead = in.read(buffer);
                }
                out.close();
                in.close();
            } catch(IOException e) {
                Log.e(TAG, e.toString());
            }
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)) {
                return true;
            } else {
                return false;
            }
        } else {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            } else {
                return false;
            }

        }
    }
    private void readCNN() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            RSSFeedHandler rssFeedHandler = new RSSFeedHandler();
            xmlReader.setContentHandler(rssFeedHandler);
            FileInputStream in = mContext.openFileInput(FILENAME);

            InputSource is = new InputSource(in);
            xmlReader.parse(is);

        } catch (Exception e) {
            Log.e("News Reader View Model", e.toString());
        }
    }

    private byte[] getUrlBytes(String urlSpec) throws IOException {
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

    private String getURLString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    private void downloadNYT() {
        try {
            if(isNetworkConnected()) {
                String url = NYT_ENDPOINT.toString();
                String jsonString = getURLString(url);
                Log.i(TAG, "Recieived JSON: " + jsonString);
                JSONObject jsonBody = new JSONObject(jsonString);
                parseItems(jsonBody);
            }
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
