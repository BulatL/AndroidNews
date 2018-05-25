package com.example.student.projekat.util;

import com.example.student.projekat.model.Post;

import java.util.Comparator;

public class PostsDateComparator implements Comparator<Post> {

    @Override
    public int compare(Post post1, Post post2) {
        if (post1.getDate().after(post2.getDate())) {
            return -1;
        } else if (post1.getDate().before(post2.getDate())) {
            return 1;
        } else {
            return 0;
        }
    }
}
