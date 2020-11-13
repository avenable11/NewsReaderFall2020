package edu.ivytech.newsreaderfall2020;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NewsItemFragment extends Fragment {
    private static final String ARG_NEWS_ID = NewsItemFragment.class.getSimpleName() + ".position";
    private NewsItem mNewsItem;
    private TextView mTitleTextView;
    private TextView mPubDateTextView;
    private TextView mDescriptionTextView;
    private TextView mLinkTextView;

    public static NewsItemFragment newInstance(int position)
    {
        Bundle args = new Bundle();
        args.putInt(ARG_NEWS_ID,position);

        NewsItemFragment fragment = new NewsItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int position = getArguments().getInt(ARG_NEWS_ID);
        mNewsItem = NewsFeed.get().getItem(position);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item, container, false);
        mTitleTextView = v.findViewById(R.id.titleTextView);
        mPubDateTextView = v.findViewById(R.id.pubDateTextView);
        mDescriptionTextView = v.findViewById(R.id.descriptionTextView);
        mLinkTextView = v.findViewById(R.id.linkTextView);

        mTitleTextView.setText(mNewsItem.getTitle());
        mPubDateTextView.setText(mNewsItem.getPubDateFormatted());
        mDescriptionTextView.setText(mNewsItem.getDescription());
        mLinkTextView.setText(mNewsItem.getLink());
        return v;
    }
}
