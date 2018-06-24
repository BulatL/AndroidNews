package com.example.student.projekat.service;

import com.example.student.projekat.model.Comment;
import com.example.student.projekat.model.Post;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CommentService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @GET("comments/post/{id}")
    Call<List<Comment>> getCommentsByPost(@Path("id") int id);

    @GET("comments/post/sort/bylikes/{id}")
    Call<List<Comment>> getCommentsSortByLikes(@Path("id") int id);

    @GET("comments/author/{userName}")
    Call<List<Comment>> getCommentsByAuthor(@Path("userName") String username);

    @POST("comments")
    Call<Comment> addComment(@Body Comment comment);

    @PUT("comments/{id}")
    Call<Comment> updateComment(@Body Comment comment, @Path("id") int id);

    @DELETE("comments/{id}")
    Call<Void> deleteComment(@Path("id") int id);
}