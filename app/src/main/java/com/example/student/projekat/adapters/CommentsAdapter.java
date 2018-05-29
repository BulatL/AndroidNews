package com.example.student.projekat.adapters;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.projekat.R;
import com.example.student.projekat.activities.LoginActivity;
import com.example.student.projekat.activities.ReadPostActivity;
import com.example.student.projekat.model.Comment;
import com.example.student.projekat.model.Post;
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
    private TextView likeView;
    private TextView disLikesView;
    private TextView dislikes;
    private TextView likes;
    private ImageButton btnLike;
    private ImageButton btnDislike;

    public CommentsAdapter(Context context, List<Comment> comments) {
        super(context, 0, comments);
        this.comments = comments;
        this.context = context;
    }


    public View getView(int position, View convertView,  ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        }
        Context context = parent.getContext();

        comment = comments.get(position);

        ImageView image = convertView.findViewById(R.id.comment_image);
        TextView title = convertView.findViewById(R.id.comment_title);
        TextView desc = convertView.findViewById(R.id.comment_description);
        TextView author = convertView.findViewById(R.id.comment_author);
        likes = convertView.findViewById(R.id.likes_comment);
        dislikes = convertView.findViewById(R.id.disLikes_comment);
        ImageButton btnDele = convertView.findViewById(R.id.btnDeleteComment);
        btnLike = convertView.findViewById(R.id.image_likes_comment);
        btnDislike = convertView.findViewById(R.id.image_dislikes_comment);

        Boolean provera = ReadPostActivity.checkUserAndAuthro(comment.getAuthor().getUsername());
        if(provera== true){
            btnDele.setVisibility(View.VISIBLE);
        }else{
            btnDele.setVisibility(View.GONE);
        }

        btnLike.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btnLikeComment(view, comment);
            }
        });

        btnDislike.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btnDislikeComment(view, comment);
            }
        });

        btnDele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDeleteComment(comment.getId());
            }
        });

        /*Bitmap myBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.android_robot);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.comment_image);
        imageView.setImageBitmap(myBitmap);*/

        title.setText(comment.getTitle());
        desc.setText(comment.getDescription());
        author.setText("by " +comment.getAuthor().getUsername());
        likes.setText(String.valueOf(comment.getLikes()));
        dislikes.setText(String.valueOf(comment.getDislikes()));

        return convertView;
    }

    public void btnDeleteComment(int commentId){
        commentService = ServiceUtils.commentService;
        Call<Void> call = commentService.deleteComment(commentId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(getContext(), "Comment is deleted", Toast.LENGTH_SHORT).show();
                ReadPostActivity.getComments(comment);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
        this.notifyDataSetChanged();
    }

    public void btnLikeComment(View view, final Comment commentt) {

        boolean provera = ReadPostActivity.checkUserAndAuthro(commentt.getAuthor().getUsername());
        if(provera != true){
            if (btnLike.isEnabled()) {
                commentt.setLikes(commentt.getLikes() + 1);
                likes.setText(String.valueOf(commentt.getLikes()));
                commentService = ServiceUtils.commentService;
                Call<Comment> call = commentService.addLikeDislike(commentt, commentt.getId());
                call.enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(Call<Comment> call, Response<Comment> response) {

                    }

                    @Override
                    public void onFailure(Call<Comment> call, Throwable t) {

                    }
                });

            } else {
                Toast.makeText(context, "Already liked comment", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "Can't liked your comment", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(context, "Liked comment", Toast.LENGTH_SHORT).show();
        if (!btnDislike.isEnabled()) {
            btnDislike.setImageResource(R.drawable.ic_thumb_down_dark_red_24dp);
        }
        btnLike.setEnabled(false);
        btnDislike.setEnabled(true);
        btnLike.setImageResource(R.drawable.ic_thumb_up_light_green_24dp);

        this.notifyDataSetChanged();
    }

    public void btnDislikeComment(View view, final Comment comment1){

        boolean provera = ReadPostActivity.checkUserAndAuthro(comment1.getAuthor().getUsername());
        if(provera != true){
            if(btnDislike.isEnabled()) {
                comment1.setDislikes(comment1.getDislikes() + 1);
                dislikes.setText(String.valueOf(comment1.getDislikes()));
                Toast.makeText(context, "Disliked comment", Toast.LENGTH_SHORT).show();
                commentService = ServiceUtils.commentService;
                Call<Comment> call = commentService.addLikeDislike(comment1, comment1.getId());
                call.enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(Call<Comment> call, Response<Comment> response) {
                        Toast.makeText(context, "Disliked comment", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Comment> call, Throwable t) {

                    }
                });

            }else{
                Toast.makeText(context, "Already disliked comment", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "Can't dislike your comment", Toast.LENGTH_SHORT).show();
        }
        if (!btnLike.isEnabled()) {
            btnLike.setImageResource(R.drawable.ic_thumb_up_dark_green_24dp);
        }
        btnDislike.setEnabled(false);
        btnLike.setEnabled(true);
        btnDislike.setImageResource(R.drawable.ic_thumb_down_light_red_24dp);

        this.notifyDataSetChanged();
    }
}
