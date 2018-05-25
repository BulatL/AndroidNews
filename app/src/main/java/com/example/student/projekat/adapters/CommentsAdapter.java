package com.example.student.projekat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.projekat.R;
import com.example.student.projekat.model.Comment;
import com.example.student.projekat.service.CommentService;
import com.example.student.projekat.service.ServiceUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsAdapter extends ArrayAdapter<Comment> {

    private List<Comment> comments;
    private Context context;
    private CommentService commentService;
    private Comment comment;

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

        comment = comments.get(position);

        ImageView image = convertView.findViewById(R.id.comment_image);
        TextView title = convertView.findViewById(R.id.comment_title);
        TextView desc = convertView.findViewById(R.id.comment_description);
        TextView author = convertView.findViewById(R.id.comment_author);
        TextView likes = convertView.findViewById(R.id.likes);
        TextView dislikes = convertView.findViewById(R.id.disLikes);

        ImageButton btnDele = convertView.findViewById(R.id.btnDeleteComment);
        btnDele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDeleteComment(v);
            }
        });

        title.setText(comment.getTitle());
        desc.setText(comment.getDescription());
        author.setText("by " +comment.getAuthor().getUsername());
        likes.setText(String.valueOf(comment.getLikes()));
        dislikes.setText(String.valueOf(comment.getDislikes()));

        System.out.println("--*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
        System.out.println(comment.getDescription());
        return convertView;
    }

    public void btnDeleteComment(View view){
        commentService = ServiceUtils.commentService;
        Call<Void> call = commentService.deleteComment(comment.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(getContext(), "Comment is deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}
