package com.example.student.projekat.activities;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.projekat.R;
import com.example.student.projekat.adapters.PostsAdapter;
import com.example.student.projekat.model.Comment;
import com.example.student.projekat.model.Post;
import com.example.student.projekat.model.Tag;
import com.example.student.projekat.model.User;
import com.example.student.projekat.service.PostService;
import com.example.student.projekat.service.ServiceUtils;
import com.example.student.projekat.util.PostsDateComparator;
import com.example.student.projekat.util.PostsPopularityComparator;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity {

    private List<Post> posts= new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    private PostsAdapter postsAdapter;
    private SharedPreferences sharedPreferences;
    private ListView postsListView;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        this.setTitle("Home");

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
                            Toast.makeText(PostActivity.this, "U click on home button",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(PostActivity.this, PostActivity.class);
                            startActivity(i);
                        }

                        if (menuItem.getItemId() == R.id.settings) {
                            Toast.makeText(PostActivity.this, "u click on settings button",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(PostActivity.this, SettingsActivity.class);
                            startActivity(i);
                        }

                        return true;
                    }
                });

        postsListView = findViewById(R.id.listOfPosts);
        PostService postService = ServiceUtils.postService;

        Call call = postService.getPosts();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                posts = response.body();
                postsAdapter = new PostsAdapter(getApplicationContext(), posts);

                postsListView.setAdapter(postsAdapter);

            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

        /*postsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                post = posts.get(position);
                Intent startReadPost = new Intent(PostActivity.this, ReadPostActivity.class);
                startReadPost.putExtra("Post", new Gson().toJson(post));
                startActivity(startReadPost);
            }
        });

        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        postsAdapter = new PostsAdapter(this, posts);
        ListView postsListView = findViewById(R.id.listOfPosts);
        postsListView.setAdapter(postsAdapter);

        User author = new User(1, "A", null, "admin", "admin", Collections.<Post>emptyList(), Collections.<Comment>emptyList());
        Bitmap postImage = BitmapFactory.decodeResource(getResources(), R.drawable.keyboard);

         posts.addAll(Arrays.asList(
                new Post(1, "Title 1", "Neki deskripshn, lalala",
                       postImage, author, new Date(1525199174000L), null,
                        Collections.<Tag>emptyList(), Collections.<Comment>emptyList(),
                        10, 13),
                new Post(2, "Title 2", "Neki deskripshn, lalala",
                        postImage, author, new Date(1525803974000L), null,
                        Collections.<Tag>emptyList(), Collections.<Comment>emptyList(),
                        1, 0),
                new Post(3, "Title 3", "Neki deskripshn, lalala",
                        postImage, author, new Date(1525544774000L), null,
                        Collections.<Tag>emptyList(), Collections.<Comment>emptyList(),
                        18, 2)
        ));

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);*/
        //mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        //mDrawerLayout.addDrawerListener(mToggle);

        //mToggle.syncState();

        View headerView = getLayoutInflater().inflate(R.layout.header,null);

        TextView name = headerView.findViewById(R.id.name_header);
        TextView userName = headerView.findViewById(R.id.username_header);



        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(LoginActivity.Username)){
            name.setText(sharedPreferences.getString(LoginActivity.Name, ""));
            userName.setText(sharedPreferences.getString(LoginActivity.Username, ""));
            userName.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.add:
                Toast.makeText(PostActivity.this, "U click on add button",
                        Toast.LENGTH_SHORT).show();
                Intent i = new Intent(PostActivity.this, CreatePostActivity.class);
                startActivity(i);
                return true;
            case R.id.settings:
                Toast.makeText(PostActivity.this, "U click on settings button",
                        Toast.LENGTH_SHORT).show();
                Intent i2 = new Intent(PostActivity.this, SettingsActivity.class);
                startActivity(i2);
                return true;
            case R.id.logout:
                SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.clear();
                editor.commit();
                Intent logoutIntent = new Intent(this, LoginActivity.class);
                startActivity(logoutIntent);

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
        /*String sortiranje_po = sharedPreferences.getString("sortiranje_objava_objava", "Datumu");

        if (sortiranje_po.equals("Datumu")) {
            Collections.sort(posts, new PostsDateComparator());
            postsAdapter.notifyDataSetChanged();
        } else {
            Collections.sort(posts, new PostsPopularityComparator());
            postsAdapter.notifyDataSetChanged();
        }*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

}
