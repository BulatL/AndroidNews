package com.example.student.projekat.service;

import com.example.student.projekat.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @GET("users/{username}/{password}")
    Call<User> login(@Path("username") String username, @Path("password") String password);

    @GET("users/user/{username}")
    Call<User> getUserByUsername(@Path("username") String username);

    @PUT("users/{id}")
    Call<User> editUser(@Body User user, @Path("id") int id);

    @POST("users")
    Call<User> addUser(@Body User user);

    @DELETE("users/{id}")
    Call<User> deleteUser(@Path("id") int id);
}