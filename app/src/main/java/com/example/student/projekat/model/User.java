package com.example.student.projekat.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private int id;
    private String name;
    private Bitmap photo;
    private String username;
    private String password;
    private List<Post> posts;
    private List<Comment> comments;

    public User(int id, String name, Bitmap photo, String username, String password, List<Post> posts, List<Comment> comments) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.username = username;
        this.password = password;
        this.posts = posts;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
