package edu.ivytech.newsreaderfall2020;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class NewsItemActivity extends SingleFragmentActivity {
    public static final String EXTRA_NEWS_ID = NewsItemActivity.class.getSimpleName() + ".news_item";
    @Override
    protected Fragment createFragment() {
        int position = getIntent().getIntExtra(EXTRA_NEWS_ID,0);
        return NewsItemFragment.newInstance(position);
    }

    public static Intent newIntent(Context packageContext, int position) {
        Intent intent = new Intent(packageContext, NewsItemActivity.class);
        intent.putExtra(EXTRA_NEWS_ID,position);
        return intent;
    }
}
