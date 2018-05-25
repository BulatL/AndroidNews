package com.example.student.projekat.util;

import com.example.student.projekat.model.Post;

import java.util.Comparator;


public class PostsPopularityComparator implements Comparator<Post> {

    @Override
    public int compare(Post post1, Post post2) {
        if ((post1.getLikes() - post1.getDislikes()) > (post2.getLikes()-post2.getDislikes())) {
            return -1;
        } else if ((post1.getLikes() - post1.getDislikes()) < (post2.getLikes()-post2.getDislikes())) {
            return 1;
        } else {
            return 0;
        }
    }
}
