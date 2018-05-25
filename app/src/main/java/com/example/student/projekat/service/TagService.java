package com.example.student.projekat.service;

import com.example.student.projekat.model.Tag;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TagService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @GET("tags/post/{id}")
    Call<List<Tag>> getTagsByPost(@Path("id") int id);

    @POST("tags")
    Call<Tag> addTag(@Body Tag tag);


}
