package com.example.student.projekat.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.student.projekat.R;
import com.example.student.projekat.model.Comment;

import java.util.List;

public class UserCommentsAdapter extends ArrayAdapter<Comment> {
    private List<Comment> comments;
    private Context context;

    public UserCommentsAdapter(Context context, List<Comment> comments){
        super(context, 0, comments);
        this.comments = comments;
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_comments, parent, false);
        }
        final Context context = parent.getContext();

        final Comment comment = getItem(position);

        System.out.println(comment+ " comment");
        final TextView commentTitleTV = convertView.findViewById(R.id.userCommentTitle);
        final TextView commentDescTV = convertView.findViewById(R.id.userCommentDesc);
        final TextView commentLikesTV = convertView.findViewById(R.id.userCommentLikes);
        final TextView commentDislikesTV = convertView.findViewById(R.id.userCommentDislikes);

        System.out.println(comment.getTitle());
        String title = "";
        String desc = "";
        if(comment.getTitle().length()>20){
            title = comment.getTitle().substring(0,20);
        }else title = comment.getTitle();
        if(comment.getDescription().length()>20){
            desc = comment.getDescription().substring(0,20);
            desc +="...";
        }else desc = comment.getDescription();
        String likes = String.valueOf(comment.getLikes());
        String dislikes = String.valueOf(comment.getDislikes());

        commentTitleTV.setText(title);
        commentTitleTV.setTextColor(Color.parseColor("#333333"));

        commentDescTV.setText(desc);
        commentDescTV.setTextColor(Color.parseColor("#333333"));

        commentLikesTV.setText(likes);
        commentLikesTV.setTextColor(Color.parseColor("#333333"));

        commentDislikesTV.setText(dislikes);
        commentDislikesTV.setTextColor(Color.parseColor("#333333"));

        return convertView;
    }
}
