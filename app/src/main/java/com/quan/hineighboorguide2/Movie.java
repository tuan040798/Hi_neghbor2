package com.quan.hineighboorguide2;

public class Movie {
    private String title;
    private int image;

    public Movie() {
    }

    public Movie(String title,int image ){
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public int getImage() {
        return image ;
    }

    public void setImage(String genre) {
        this.image = image;
    }
}