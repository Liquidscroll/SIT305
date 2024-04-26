package com.example.newsapp;

// These classes are used to parse the JSON response from NewsApi.org
public class Article {
    private Source source;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String content;
    public Article() {}
    public Source getSource() {
        return source;
    }
    public String getTitle() {
        return title;
    }
    public String getDesc() {
        return description;
    }
    public String getUrl() {
        return url;
    }
    public String getImgURL() { return urlToImage; }
    public String getContent() { return content; }
}

class Source {
    private String name;
    public String getName() {
        return name;
    }
}