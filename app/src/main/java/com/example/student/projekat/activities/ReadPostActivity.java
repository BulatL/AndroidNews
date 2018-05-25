package com.example.student.projekat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.projekat.R;
import com.example.student.projekat.adapters.CommentsAdapter;
import com.example.student.projekat.adapters.PostsAdapter;
import com.example.student.projekat.model.Comment;
import com.example.student.projekat.model.Post;
import com.example.student.projekat.model.Tag;
import com.example.student.projekat.model.User;
import com.example.student.projekat.service.CommentService;
import com.example.student.projekat.service.PostService;
import com.example.student.projekat.service.ServiceUtils;
import com.example.student.projekat.util.CommentsDateComparator;
import com.example.student.projekat.util.CommentsPopularityComparator;
import com.example.student.projekat.util.PostsDateComparator;
import com.example.student.projekat.util.PostsPopularityComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadPostActivity extends AppCompatActivity {
    TextView title;
    TextView author;
    TextView dateTime;
    TextView description;
    TextView tag;
    TextView like;
    TextView disLikes;
    Bitmap myBitmap;
    private DrawerLayout mDrawerLayout;
    private List<Comment> comments = new ArrayList<>();
    private CommentsAdapter commentsAdapter;
    private SharedPreferences sharedPreferences;
    private ListView commentsListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_post);
        this.setTitle("Read post");

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

                        if (menuItem.getItemId() == R.id.home) {
                            Toast.makeText(ReadPostActivity.this, "u click on home button",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(ReadPostActivity.this, PostActivity.class);
                            startActivity(i);
                        }
                        if (menuItem.getItemId() == R.id.settings) {
                            Toast.makeText(ReadPostActivity.this, "u click on settings button",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(ReadPostActivity.this, SettingsActivity.class);
                            startActivity(i);
                        }

                        return true;
                    }
                });

        Post post = (Post) getIntent().getSerializableExtra("post");

        title = (TextView) findViewById(R.id.titleRead);
        title.setText(post.getTitle());

        author = (TextView) findViewById(R.id.AuthorRead);
        author.setText(post.getAuthor().getUsername());

        dateTime = (TextView) findViewById(R.id.dateTime);
        dateTime.setText(post.getDate().toString());

        description = (TextView) findViewById(R.id.description);
        description.setText(post.getDescription());

        tag = (TextView) findViewById(R.id.tagRead);
        tag.setText("#GrandVesti");

        like = (TextView) findViewById(R.id.likes);
        like.setText(String.valueOf(post.getLikes()));

        disLikes = (TextView) findViewById(R.id.disLikes);
        disLikes.setText(String.valueOf(post.getDislikes()));

        myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.keyboard);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(myBitmap);

        commentsAdapter = new CommentsAdapter(this, comments);
        commentsListView = findViewById(R.id.listOfComments);

        /*User author = new User(1, "A", null, "admin", "admin", Collections.<Post>emptyList(), Collections.<Comment>emptyList());
        Bitmap postImage = BitmapFactory.decodeResource(getResources(), R.drawable.keyboard);

        comments.addAll(Arrays.asList(
                new Comment(1, "vrh komentar", "jako mi se svidja ovaj post", author, new Date(1525199174000L), post, 16,
                        21),
                new Comment(2, "vrh komentar2", "jako mi se svidja ovaj post", author, new Date(1525199174000L), post, 15,
                        1),
                new Comment(3, "vrh komentar3", "jako mi se svidja ovaj post", author, new Date(1525199174000L), post, 15,
                        1)
        ));*/

        CommentService commentService = ServiceUtils.commentService;

        Call call = commentService.getCommentsByPost(post.getId());

        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                comments = response.body();
                commentsAdapter = new CommentsAdapter(getApplicationContext(), comments);
                for (Comment c: comments) {
                    System.out.println(c.getDescription());
                    System.out.println("-------------------------------------------");
                }
                commentsListView.setAdapter(commentsAdapter);

            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.add:
                Toast.makeText(ReadPostActivity.this, "U click on add button",
                        Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ReadPostActivity.this, CreatePostActivity.class);
                startActivity(i);
                return true;
            case R.id.settings:
                Toast.makeText(ReadPostActivity.this, "U click on settings button",
                        Toast.LENGTH_SHORT).show();
                Intent i2 = new Intent(ReadPostActivity.this, SettingsActivity.class);
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
        String sortiranje_po = sharedPreferences.getString("sortiranje_komentara_objava", "Datumu");

        if (sortiranje_po.equals("Datumu")) {
            Collections.sort(comments, new CommentsDateComparator());
            commentsAdapter.notifyDataSetChanged();
        } else {
            Collections.sort(comments, new CommentsPopularityComparator());
            commentsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
