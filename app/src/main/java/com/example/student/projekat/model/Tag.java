package com.example.student.projekat.model;

import java.io.Serializable;
import java.util.List;

public class Tag implements Serializable {
    private int id;
    private String name;
    private List<Post> posts;

    public Tag(int id, String name, List<Post> posts) {
        this.id = id;
        this.name = name;
        this.posts = posts;
    }

    public Tag(){}

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
