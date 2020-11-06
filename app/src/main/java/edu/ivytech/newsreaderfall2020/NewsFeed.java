package edu.ivytech.newsreaderfall2020;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsFeed {
    private static NewsFeed sNewsFeed;
    public final static String NEW_FEED = "edu.ivytech.newsreaderfall2020.NEW_FEED";

    private String mTitle;
    private String mPubDate;
    private ArrayList<NewsItem> mItems;
    private String mSource;
    private SimpleDateFormat mDateInFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

    public static NewsFeed get() {
        if (sNewsFeed == null) {
            sNewsFeed = new NewsFeed();
        }
        return sNewsFeed;
    }
    private NewsFeed() {
        mItems = new ArrayList<NewsItem>();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getPubDate() {
        return mPubDate;
    }

    public void setPubDate(String pubDate) {
        mPubDate = pubDate;
    }

    public long getPubDateMillis() {
        try {
            Date date = mDateInFormat.parse(mPubDate.trim());
            return date.getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<NewsItem> getAllItems() {
        return mItems;
    }

    public int addItem(NewsItem item) {
        mItems.add(item);
        return mItems.size();
    }

    public NewsItem getItem(int index) {
        return mItems.get(index);
    }

    public String getSource() {
        return mSource;
    }

    public void setSource(String source) {
        mSource = source;
    }
}

