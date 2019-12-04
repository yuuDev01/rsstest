package com.example.rss;

public class RSSNewsItem {
    private String title; //뉴스제목
    private String date; //날짜
    private String link; //링크

    public RSSNewsItem(){

    }

    public RSSNewsItem(String title, String date, String link){
        this.title = title;
        this.date = date;
        this.link = link;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title =title;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getLink(){
        return link;
    }

}
