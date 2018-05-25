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
import com.example.student.projekat.model.Post;

import java.util.List;

public class PostsAdapter extends ArrayAdapter<Post> {

    private List<Post> posts;
    private Context context;

    public PostsAdapter(Context context, List<Post> posts) {
        super(context, 0, posts);
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Post post = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        }

        final ImageView image = convertView.findViewById(R.id.post_image);
        final TextView title = convertView.findViewById(R.id.post_title);
        final TextView desc = convertView.findViewById(R.id.post_description);
        final TextView author = convertView.findViewById(R.id.post_author);
        final TextView likes = convertView.findViewById(R.id.post_likes);
        final TextView dislikes = convertView.findViewById(R.id.post_dislikes);

        if (post.getPhoto() != null) {
            image.setImageBitmap(post.getPhoto().getBitmap());
        }
        title.setText(post.getTitle());
        desc.setText(post.getDescription());
        author.setText("by " +post.getAuthor().getUsername());
        likes.setText(String.valueOf(post.getLikes()));
        dislikes.setText(String.valueOf(post.getDislikes()));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReadPostActivity(post);
            }
        });

        return convertView;
    }

    private void openReadPostActivity(Post post) {
        Intent intent = new Intent(context, ReadPostActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("post", post);
        post.setPhoto(null);
        context.startActivity(intent);
    }
}
