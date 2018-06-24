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
import com.example.student.projekat.model.Post;

import java.util.List;

public class UserPostsAdapter extends ArrayAdapter<Post> {
    private List<Post> posts;
    private Context context;

    public UserPostsAdapter(Context context, List<Post> posts){
        super(context, 0, posts);
        this.posts = posts;
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_comments, parent, false);
        }
        final Context context = parent.getContext();

        final Post post = getItem(position);

        System.out.println(post+ " post");
        final TextView commentTitleTV = convertView.findViewById(R.id.userCommentTitle);
        final TextView commentDescTV = convertView.findViewById(R.id.userCommentDesc);
        final TextView commentLikesTV = convertView.findViewById(R.id.userCommentLikes);
        final TextView commentDislikesTV = convertView.findViewById(R.id.userCommentDislikes);

        String title = "";
        String desc = "";
        if(post.getTitle().length()>50){
            title = post.getTitle().substring(0,50);
        }else title = post.getTitle();
        if(post.getDescription().length()>50){
            desc = post.getDescription().substring(0,50);
            desc +="...";
        }else desc = post.getDescription();
        String likes = String.valueOf(post.getLikes());
        String dislikes = String.valueOf(post.getDislikes());

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
