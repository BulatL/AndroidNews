package com.example.student.projekat.util;

import com.example.student.projekat.model.Comment;

import java.util.Comparator;

public class CommentsDateComparator implements Comparator<Comment> {

    @Override
    public int compare(Comment comm1, Comment comm2) {
        if (comm1.getDate().after(comm2.getDate())) {
            return -1;
        } else if (comm1.getDate().before(comm2.getDate())) {
            return 1;
        } else {
            return 0;
        }
    }
}