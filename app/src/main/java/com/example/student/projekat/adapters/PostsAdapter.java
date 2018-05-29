package com.example.student.projekat.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.projekat.R;
import com.example.student.projekat.activities.LoginActivity;
import com.example.student.projekat.activities.ReadPostActivity;
import com.example.student.projekat.model.Comment;
import com.example.student.projekat.model.Post;
import com.example.student.projekat.model.Tag;
import com.example.student.projekat.service.CommentService;
import com.example.student.projekat.service.PostService;
import com.example.student.projekat.service.ServiceUtils;
import com.example.student.projekat.service.TagService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsAdapter extends ArrayAdapter<Post> {

    private List<Post> posts;
    private Context context;
    private List<Comment> comments = new ArrayList<>();
    private PostService postService;
    private Post post;
    private SharedPreferences sharedPreferences;

    public PostsAdapter(Context context, List<Post> posts) {
        super(context, 0, posts);
        this.posts = posts;
        this.context = context;
    }

    public View getView(int position,  View convertView,  ViewGroup parent) {
        post = getItem(position);
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
            image.setImageBitmap(post.getPhoto());
        }
        image.setImageResource(R.drawable.keyboard);
        title.setText(post.getTitle());
        desc.setText(post.getDescription());
        author.setText("by " +post.getAuthor().getUsername());
        likes.setText(String.valueOf(post.getLikes()));
        dislikes.setText(String.valueOf(post.getDislikes()));


        TagService tagService = ServiceUtils.tagService;
        Call callT = tagService.getTagsByPost(post.getId());
        callT.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                List<Tag> tags = (List<Tag>)response.body();
                post.setTags(tags);

            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });


        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("title posta " +post.getTitle());
                openReadPostActivity(post);
            }
        });*/

        return convertView;
    }

    private void openReadPostActivity(Post post) {
        Intent intent = new Intent(context, ReadPostActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("post", post);
        context.startActivity(intent);
    }

}
