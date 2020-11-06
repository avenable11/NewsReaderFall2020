package edu.ivytech.newsreaderfall2020;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class NewsReaderFragment extends Fragment  {
    private static String URL_STRING = "http://rss.cnn.com/rss/cnn_tech.rss";
    private final static String TAG = "News Reader Fragment";

    final String FILENAME = "news_feed.xml";
    private Button loadBtn;
    private Button readBtn;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_page, container, false);
        final NewsViewModel nvm = new ViewModelProvider(this).get(NewsViewModel.class);

        loadBtn = v.findViewById(R.id.loadBtn);
        readBtn = v.findViewById(R.id.readBtn);

        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "Started Feed Download");
                        downloadFeed();
                        Log.i(TAG, "Finished Feed Download");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Feed Downloaded", Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                }).start();
            }
        });

        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nvm.getNewsList().observe(getViewLifecycleOwner(), new Observer<NewsFeed>() {
                    @Override
                    public void onChanged(NewsFeed newsFeed) {
                        Toast.makeText(getContext(), "Feed Loaded", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


        return v;
    }


    private void downloadFeed() {
        if(isNetworkConnected()) {
            try {
                URL url = new URL(URL_STRING);
                InputStream in = url.openStream();
                FileOutputStream out = getActivity().getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
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
        ConnectivityManager cm = (ConnectivityManager) getActivity().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

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


}
