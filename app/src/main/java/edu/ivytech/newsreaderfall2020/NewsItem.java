package edu.ivytech.newsreaderfall2020;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsItem {
    private String mTitle;
    private String mDescription;
    private String mLink;
    private String mPubDate;

    private SimpleDateFormat mDateOutFormat = new SimpleDateFormat("EEEE h:mm a (MMMM d)");
    private static SimpleDateFormat sDateInFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public String getPubDate() {
        return mPubDate;
    }

    public void setPubDate(String pubDate) {
        mPubDate = pubDate;
    }

    public static void setDateInFormat(String format) {
        sDateInFormat.applyPattern(format);
    }

    public String getPubDateFormatted() {
        try {
            if(mPubDate != null) {
                Date date = sDateInFormat.parse(mPubDate.trim());
                String pubDateFormatted = mDateOutFormat.format(date);
                return pubDateFormatted;
            }
        } catch (ParseException e) {
            return "Date Error";
        }
        return "Unknown Date";
    }
}
