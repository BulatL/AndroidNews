package com.example.student.projekat.activities;

import android.app.Activity;
import android.content.Intent;
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
            System.out.println(data.getData());
            Uri selectedImage = data.getData();
            System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-----------------------------*");
            System.out.println(selectedImage);
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


        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.android_robot);
        User author = new User(1, "Marko", null, "marko", "123", null, null);


        Post post = new Post();
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
                Toast.makeText(CreatePostActivity.this, "u click on create post button",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });
        addTag();

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
        System.out.println(postId);
        System.out.println(tagId);
        Call<Post> call = postService.setTagsInPost(postId,tagId);
        System.out.println(call);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                System.out.println(response.errorBody());
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
