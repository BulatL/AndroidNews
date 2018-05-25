package com.example.student.projekat.util;

import com.example.student.projekat.model.Comment;

import java.util.Comparator;

public class CommentsPopularityComparator implements Comparator<Comment> {

    @Override
    public int compare(Comment comm1, Comment comm2) {
        if ((comm1.getLikes() - comm1.getDislikes()) > (comm2.getLikes()-comm2.getDislikes())) {
            return -1;
        } else if ((comm1.getLikes() - comm1.getDislikes()) < (comm2.getLikes()-comm2.getDislikes())) {
            return 1;
        } else {
            return 0;
        }
    }
}