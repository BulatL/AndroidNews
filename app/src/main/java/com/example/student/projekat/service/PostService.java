package com.example.student.projekat.service;

import com.example.student.projekat.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PostService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @GET("posts")
    Call<List<Post>> getPosts();

    @GET("posts/{id}")
    Call<Post> getPost(@Path("id") int id);

    @GET("posts/sort/bylikes")
    Call<List<Post>> getPostSortByLikes();

    @GET("posts/sort/bycomments")
    Call<List<Post>> getPostSortByComments();

    @GET("posts/author/{authorName}")
    Call<List<Post>> getPostsByAuthor(@Path("authorName") String authorName);

    @GET("posts/tagName/{tagName}")
    Call<List<Post>> getPostsByTagName(@Path("tagName") String tagName);

    @GET("posts/tagId/{id}")
    Call<List<Post>> getPostsByTag(@Path("id") int id);

    @POST("posts")
    Call<Post> createPost(@Body Post post);

    @PUT("posts/{id}")
    Call<Post> updatePost(@Body Post post,@Path("id") int id);

    @POST("posts/setTags/{postId}/{tagId}")
    Call<Post> setTagsInPost(@Path("postId") int postId,@Path("tagId") int tagId);

    @DELETE("posts/{id}")
    Call<Void> deletePost(@Path("id") int id);

}