package com.example.student.projekat.model;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable{
    private int id;
    private String title;
    private String description;
    private User author;
    private Date date;
    private Post post;
    private int likes;
    private int dislikes;

    public Comment(){}

    public Comment(int id, String title, String description, User author, Date date, Post post, int likes, int dislikes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.date = date;
        this.post = post;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public User getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public Post getPost() {
        return post;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public void setDate(Date date) {
        this.date = date;

    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setDescription(String description) {

        this.description = description;
    }
}
