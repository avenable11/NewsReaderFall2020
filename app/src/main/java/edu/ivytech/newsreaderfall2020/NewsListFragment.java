package edu.ivytech.newsreaderfall2020;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NewsListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private NewsAdapter mNewsAdapter;
    private NewsFeed mNewsFeed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_newslist, container, false);
        mRecyclerView = v.findViewById(R.id.news_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        NewsViewModel nvm = new ViewModelProvider(this).get(NewsViewModel.class);
        nvm.getNewsList().observe(getViewLifecycleOwner(), new Observer<NewsFeed>() {
            @Override
            public void onChanged(NewsFeed newsFeed) {
                mNewsFeed = newsFeed;
                updateUI();
            }
        });

        return v;
    }

    private void updateUI() {
        List<NewsItem> items = mNewsFeed.getAllItems();
        mNewsAdapter = new NewsAdapter(items);
        mRecyclerView.setAdapter(mNewsAdapter);
    }

    private class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mPubDateTextView;
        private TextView mTitleTextView;
        private NewsItem mNewsItem;
        private int mPosition;

        public NewsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));
            mPubDateTextView = itemView.findViewById(R.id.pubDateTextView);
            mTitleTextView = itemView.findViewById(R.id.titleTextView);
            itemView.setOnClickListener(this);

        }
        public void bind(NewsItem newsItem, int position) {
            mNewsItem = newsItem;
            mPosition = position;
            mPubDateTextView.setText(mNewsItem.getPubDateFormatted());
            mTitleTextView.setText(mNewsItem.getTitle());
        }
        public void onClick(View view) {
            Intent intent = NewsItemActivity.newIntent(getActivity(),mPosition);
            startActivity(intent);
        }
    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {
        private List<NewsItem> mNewsItems;
        public NewsAdapter(List<NewsItem> items) {
            mNewsItems = items;
        }
        public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new NewsHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
            NewsItem item = mNewsItems.get(position);
            holder.bind(item, position);
        }

        @Override
        public int getItemCount() {
            return mNewsItems.size();
        }
    }
}
