package com.example.student.projekat.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.projekat.R;
import com.example.student.projekat.activities.ReadPostActivity;
import com.example.student.projekat.activities.UserInfoActivity;
import com.example.student.projekat.model.Comment;
import com.example.student.projekat.model.User;
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
    private User loggedInUser;
    private TextView dislikes;
    private TextView likes;


    public CommentsAdapter(Context context, List<Comment> comments, User loggedInUser) {
        super(context, 0, comments);
        this.comments = comments;
        this.context = context;
        this.loggedInUser = loggedInUser;
    }


    public View getView(int position, View convertView,  ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        }
        final Context context = parent.getContext();

        final Comment comment = getItem(position);


        ImageView image = convertView.findViewById(R.id.comment_image);
        final TextView title = convertView.findViewById(R.id.comment_title);
        final TextView desc = convertView.findViewById(R.id.comment_description);
        TextView author = convertView.findViewById(R.id.comment_author);
        likes = convertView.findViewById(R.id.likes_comment);
        dislikes = convertView.findViewById(R.id.disLikes_comment);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDeleteComment);
        final ImageButton btnLike = convertView.findViewById(R.id.image_likes_comment);
        final ImageButton btnDislike = convertView.findViewById(R.id.image_dislikes_comment);
        final ImageButton btnEdit = convertView.findViewById(R.id.btnEditComment);

        if(loggedInUser==null){
            btnLike.setEnabled(false);
            btnDislike.setEnabled(false);
        }

        String provera = ReadPostActivity.checkUserAndAuthro(comment.getAuthor().getUsername());
        if(provera.equals("same")){
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        }else{
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }

        btnLike.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btnLikeComment(view, comment,btnLike, btnDislike);
            }
        });

        btnDislike.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btnDislikeComment(view, comment,btnLike, btnDislike);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnDeleteComment(comment.getId());
            }

        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReadPostActivity.editComment(comment);
            }
        });

        author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent i = new Intent(context, UserInfoActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("loggedInUser", loggedInUser);
                    i.putExtra("userInfo", comment.getAuthor());
                    context.startActivity(i);
            }
        });

        Bitmap myBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.android_robot);
        /*String uri = Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +R.drawable.android_robot).toString();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap myBitmap = BitmapFactory.decodeFile(uri ,options);*/
        ImageView imageView = (ImageView) convertView.findViewById(R.id.comment_image);
        imageView.setImageBitmap(myBitmap);

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
                Toast.makeText(context, "Comment is deleted", Toast.LENGTH_SHORT).show();
                ReadPostActivity.getComments(comment);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
        this.notifyDataSetChanged();
    }

    public void btnLikeComment(View view, final Comment commentt, final ImageButton btnLike, final ImageButton btnDislike) {

        String provera = ReadPostActivity.checkUserAndAuthro(commentt.getAuthor().getUsername());
        if(provera.equals("")){
            if (btnLike.isEnabled()) {
                commentt.setLikes(commentt.getLikes() + 1);
                likes.setText(String.valueOf(commentt.getLikes()));
                commentService = ServiceUtils.commentService;
                Call<Comment> call = commentService.updateComment(commentt, commentt.getId());
                call.enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(Call<Comment> call, Response<Comment> response) {

                    }

                    @Override
                    public void onFailure(Call<Comment> call, Throwable t) {

                    }
                });
                Toast.makeText(context, "Liked comment", Toast.LENGTH_SHORT).show();
                if (!btnDislike.isEnabled()) {
                    btnDislike.setImageResource(R.drawable.ic_thumb_down_dark_red_24dp);
                }
                btnLike.setEnabled(false);
                btnDislike.setEnabled(true);
                btnLike.setImageResource(R.drawable.ic_thumb_up_light_green_24dp);

            } else {
                Toast.makeText(context, "Already liked comment", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "Can't liked your comment", Toast.LENGTH_SHORT).show();
        }
        this.notifyDataSetChanged();
    }

    public void btnDislikeComment(View view, final Comment comment1, final ImageButton btnLike, final ImageButton btnDislike){

        String provera = ReadPostActivity.checkUserAndAuthro(comment1.getAuthor().getUsername());
        if(provera.equals("")){
            if(btnDislike.isEnabled()) {
                comment1.setDislikes(comment1.getDislikes() + 1);
                dislikes.setText(String.valueOf(comment1.getDislikes()));
                Toast.makeText(context, "Disliked comment", Toast.LENGTH_SHORT).show();
                commentService = ServiceUtils.commentService;
                Call<Comment> call = commentService.updateComment(comment1, comment1.getId());
                call.enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(Call<Comment> call, Response<Comment> response) {
                        Toast.makeText(context, "Disliked comment", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Comment> call, Throwable t) {

                    }
                });
                if (!btnLike.isEnabled()) {
                    btnLike.setImageResource(R.drawable.ic_thumb_up_dark_green_24dp);
                }
                btnDislike.setEnabled(false);
                btnLike.setEnabled(true);
                btnDislike.setImageResource(R.drawable.ic_thumb_down_light_red_24dp);


            }else{
                Toast.makeText(context, "Already disliked comment", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "Can't dislike your comment", Toast.LENGTH_SHORT).show();
        }

        this.notifyDataSetChanged();
    }

    public void btnEditComment(final Comment comment, final TextView titleTV, final TextView descTV){
        String title = titleTV.getText().toString();
        String desc = descTV.getText().toString();

        final EditText newTitleTV = new EditText(context);
        final EditText newDescTV = new EditText(context);
        newTitleTV.setText(title);
        newDescTV.setText(desc);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Edit comment");
        alert.setTitle("Edit comment");
        alert.setView(newTitleTV);
        alert.setView(newDescTV);
        alert.setPositiveButton("Yes Option", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newTitle = newTitleTV.getText().toString();
                String newDesc = newDescTV.getText().toString();
                System.out.println(newTitle+ "    " + newDesc);
            }
        });
        alert.show();
    }
}
