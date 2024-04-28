package com.example.itubeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;

public class YoutubePlayerFragment extends Fragment {
    private static String VIDEO_URL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_youtube_player, container, false);
        WebView webView = view.findViewById(R.id.webView);

        setupWebView(webView);

        if(getArguments() != null)
        {
            VIDEO_URL = getArguments().getString("VIDEO_URL");
            loadVideo(webView);
        }

        return view;
    }

    public void setupWebView(WebView webView)
    {

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webView.setWebChromeClient(new WebChromeClient());
    }
    public void loadVideo(WebView webView)
    {
        int scrHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        VIDEO_URL = VIDEO_URL.replace("watch?v=", "embed/");
        String html = "<iframe width=100% height=" + (scrHeight / 3) + " src=\"" + VIDEO_URL + "\" frameborder=\"0\"></iframe>";
        webView.loadDataWithBaseURL("youtube.com", html, "text/html", "utf-8", "youtube.com");
    }
}
