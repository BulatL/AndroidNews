package com.example.student.projekat.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.projekat.R;
import com.example.student.projekat.model.Comment;
import com.example.student.projekat.model.Post;
import com.example.student.projekat.model.Tag;
import com.example.student.projekat.model.User;
import com.example.student.projekat.service.PostService;
import com.example.student.projekat.service.ServiceUtils;
import com.example.student.projekat.service.TagService;
import com.example.student.projekat.service.UserService;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout ;
    private EditText titleEdit;
    private EditText descriptionEdit;
    private EditText tagsEdit;
    private Post postResponse;
    public static final int PICK_IMAGE = 1;
    private Bitmap bitmap;
    private byte[] byteArray;
    private User author = new User();
    private SharedPreferences sharedPreferences;
    private Post post = new Post();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        this.setTitle("Create post");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        if(menuItem.getItemId() == R.id.home){
                            Toast.makeText(CreatePostActivity.this, "U click on home button",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(CreatePostActivity.this, PostActivity.class);
                            startActivity(i);
                        }
                        if(menuItem.getItemId() == R.id.settings){
                            Toast.makeText(CreatePostActivity.this, "u click on settings button",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(CreatePostActivity.this, SettingsActivity.class);
                            startActivity(i);
                        }

                        return true;
                        }
                });
        EditText title = findViewById(R.id.titleCreate);
        title.setFilters(new InputFilter[] {new InputFilter.LengthFilter(250)});
        EditText description = findViewById(R.id.textCreate);
        description.setFilters(new InputFilter[] {new InputFilter.LengthFilter(250)});
        EditText tags = findViewById(R.id.tagsCreate);
        tags.setFilters(new InputFilter[] {new InputFilter.LengthFilter(250)});
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.add:
                Toast.makeText(CreatePostActivity.this, "U click on add button",
                        Toast.LENGTH_SHORT).show();
                Intent i = new Intent(CreatePostActivity.this, CreatePostActivity.class);
                startActivity(i);
                return true;
            case R.id.settings:
                Toast.makeText(CreatePostActivity.this, "U click on settings button",
                        Toast.LENGTH_SHORT).show();
                Intent i2 = new Intent(CreatePostActivity.this, SettingsActivity.class);
                startActivity(i2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void btnUploadImage(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

        Toast.makeText(CreatePostActivity.this, "u click on upload image button",
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {

            Uri selectedImage = data.getData();
            bitmap = null;
            try {

                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                ImageView previewImage = findViewById(R.id.previewImage);
                previewImage.setImageBitmap(bitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                byteArray = stream.toByteArray();

            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void btnCreatePost(View view) {

        titleEdit = findViewById(R.id.titleCreate);
        descriptionEdit = findViewById(R.id.textCreate);

        String title = titleEdit.getText().toString();
        String description = descriptionEdit.getText().toString();

        Date date = Calendar.getInstance().getTime();

        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(LoginActivity.Username)) {
            String userName = sharedPreferences.getString(LoginActivity.Username, "");

            UserService userService = ServiceUtils.userService;

            Call<User> call = userService.getUserByUsername(userName);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    author= response.body();
                    author.setPhoto(bitmap);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }
        author.setUsername(sharedPreferences.getString(LoginActivity.Username, ""));
        post.setTitle(title);
        post.setDescription(description);
        post.setDate(date);
        post.setAuthor(author);
        post.setPhoto(null);


        PostService postService = ServiceUtils.postService;
        Call<Post> call = postService.createPost(post);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                postResponse = response.body();
                System.out.println("post id " + postResponse);
                Toast.makeText(CreatePostActivity.this, "u create post successfully",
                        Toast.LENGTH_SHORT).show();
                addTag();
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });


    }

    public void addTag(){
        tagsEdit = findViewById(R.id.tagsCreate);
        TagService tagService = ServiceUtils.tagService;
        String tags = tagsEdit.getText().toString();
        Tag tag = new Tag();
        tag.setName(tags);
        Call<Tag> callTags = tagService.addTag(tag);
        callTags.enqueue(new Callback<Tag>() {
            @Override
            public void onResponse(Call<Tag> call, Response<Tag> response) {
                Tag tagResponse = response.body();
                System.out.println("tag name " + tagResponse.getName());
                setTagsInPost(postResponse.getId(), tagResponse.getId());
            }

            @Override
            public void onFailure(Call<Tag> call, Throwable t) {

            }
        });
    }

    public void setTagsInPost(int postId, int tagId){
        PostService postService = ServiceUtils.postService;
        System.out.println("-*-*-*-*-*-*//////////////////////----------------------------------------------------");
        System.out.println("post id "+postId);
        System.out.println("tag id "+tagId);
        Call<Post> call = postService.setTagsInPost(postId,tagId);
        System.out.println(call);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                System.out.println(response.errorBody());
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("post", post);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
