package com.example.student.projekat.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.student.projekat.R;
import com.example.student.projekat.activities.ReadPostActivity;
import com.example.student.projekat.model.Comment;
import com.example.student.projekat.model.Post;

import java.util.List;

public class CommentsAdapter extends ArrayAdapter<Comment> {

    private List<Comment> comments;
    private Context context;

    public CommentsAdapter(Context context, List<Comment> comments) {
        super(context, 0, comments);
        this.comments = comments;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        }

        final Comment comment = comments.get(position);

        ImageView image = convertView.findViewById(R.id.comment_image);
        TextView title = convertView.findViewById(R.id.comment_title);
        TextView desc = convertView.findViewById(R.id.comment_description);
        TextView author = convertView.findViewById(R.id.comment_author);
        TextView likes = convertView.findViewById(R.id.likes);
        TextView dislikes = convertView.findViewById(R.id.disLikes);


        title.setText(comment.getTitle());
        desc.setText(comment.getDescription());
        author.setText("by " +comment.getAuthor().getUsername());
        likes.setText(String.valueOf(comment.getLikes()));
        dislikes.setText(String.valueOf(comment.getDislikes()));

        return convertView;
    }
}
