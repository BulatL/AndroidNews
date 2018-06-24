package com.example.student.projekat.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.projekat.R;
import com.example.student.projekat.adapters.PostsAdapter;
import com.example.student.projekat.model.Post;
import com.example.student.projekat.model.User;
import com.example.student.projekat.service.PostService;
import com.example.student.projekat.service.ServiceUtils;
import com.example.student.projekat.util.SearchDialog;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity  implements SearchDialog.SearchDialogListener{


    private DrawerLayout mDrawerLayout;
    private ListView postsListView;

    private PostsAdapter postsAdapter;
    private SharedPreferences sharedPreferences;
    private PostService postService;

    private List<Post> posts= new ArrayList<>();
    private Post post;
    private User loggedInUser;

    private String searchBy;
    private boolean searchUser;
    private boolean searchTag;

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
        Bundle extras2 = getIntent().getExtras();
        if(extras2 != null){
            if(extras2.getSerializable("loggedInUser") != null){
                loggedInUser = (User)extras2.getSerializable("loggedInUser");
                navigationView.getMenu().findItem(R.id.accountNV).setVisible(true);
            }else{
                navigationView.getMenu().findItem(R.id.accountNV).setVisible(false);
            }
        }
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        if (menuItem.getItemId() == R.id.homeNV) {
                            Toast.makeText(PostActivity.this, "U click on home button",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(PostActivity.this, PostActivity.class);
                            i.putExtra("loggedInUser", loggedInUser);
                            startActivity(i);
                        }

                        if (menuItem.getItemId() == R.id.accountNV) {
                            Toast.makeText(PostActivity.this, "U click on account button",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(PostActivity.this, UserInfoActivity.class);
                            i.putExtra("loggedInUser", loggedInUser);
                            i.putExtra("userInfo", loggedInUser);
                            startActivity(i);
                        }

                        if (menuItem.getItemId() == R.id.settingsNV) {
                            Toast.makeText(PostActivity.this, "u click on settings button",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(PostActivity.this, SettingsActivity.class);
                            startActivity(i);
                        }

                        return true;
                    }
                });

        postsListView = findViewById(R.id.listOfPosts);
        postService = ServiceUtils.postService;

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            posts = (List<Post>) extras.getSerializable("posts");
            if(posts!=null) {
                if (posts.size() > 0) {
                    postsAdapter = new PostsAdapter(getApplicationContext(), posts);
                    postsListView.setAdapter(postsAdapter);
                }
            }else{
                Call call = postService.getPosts();

                call.enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                        posts = response.body();
                        if(posts!=null) {
                            if (posts.size() > 0) {
                                postsAdapter = new PostsAdapter(getApplicationContext(), posts);
                                postsListView.setAdapter(postsAdapter);
                                //sortPosts(getApplicationContext(),posts);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {

                    }
                });
            }
        }else {
            Call call = postService.getPosts();

            call.enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    posts = response.body();
                    if(posts!=null) {
                        if (posts.size() > 0) {
                            postsAdapter = new PostsAdapter(getApplicationContext(), posts);
                            postsListView.setAdapter(postsAdapter);
                            //sortPosts(getApplicationContext(),posts);
                        }
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });
        }
        postsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                post = posts.get(i);

                Call<Post> call = postService.getPost(post.getId());

                call.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        post = response.body();
                        openReadPostActivity(post);
                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {

                    }
                });
            }
        });

        System.out.println("da li je loggedInUser null " + loggedInUser);
        if(loggedInUser!= null){
            View headerLayout = navigationView.getHeaderView(0);
            TextView userNameHeader = headerLayout.findViewById(R.id.username_header);
            TextView nameHeader = headerLayout.findViewById(R.id.name_header);

            nameHeader.setTextColor(Color.parseColor("#00ff19"));
            nameHeader.setText(loggedInUser.getName());
            userNameHeader.setText(loggedInUser.getUsername());
            userNameHeader.setTextColor(Color.parseColor("#00ff19"));

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.homeNV:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.loginMenu:
                Toast.makeText(PostActivity.this, "U click on login button",
                        Toast.LENGTH_SHORT).show();
                Intent iLogin = new Intent(PostActivity.this, LoginActivity.class);
                startActivity(iLogin);
                return true;
            case R.id.searchMenu:
                Toast.makeText(PostActivity.this, "U click on search button",
                        Toast.LENGTH_SHORT).show();
                openSearchDialog();
                return true;
            case R.id.addPostMenu:
                Toast.makeText(PostActivity.this, "U click on add button",
                        Toast.LENGTH_SHORT).show();

                Intent iAdd = new Intent(PostActivity.this, CreatePostActivity.class);
                iAdd.putExtra("loggedInUser", loggedInUser);
                startActivity(iAdd);
                return true;
            case R.id.settingsMenu:
                Toast.makeText(PostActivity.this, "U click on settings button",
                        Toast.LENGTH_SHORT).show();
                Intent iSettings = new Intent(PostActivity.this, SettingsActivity.class);
                startActivity(iSettings);
                return true;
            case R.id.logoutMenu:
                SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.clear();
                editor.commit();
                Intent iLogout = new Intent(this, PostActivity.class);
                iLogout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(iLogout);
                finish();
                return  true;
            case R.id.registerMenu:
                Toast.makeText(PostActivity.this, "U click on register button",
                        Toast.LENGTH_SHORT).show();
                Intent iRegister = new Intent(PostActivity.this, RegisterActivity.class);
                startActivity(iRegister);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            loggedInUser = (User) extras.getSerializable("loggedInUser");
        }
        if(loggedInUser!= null) {
            menu.findItem(R.id.registerMenu).setVisible(false);
            menu.findItem(R.id.loginMenu).setVisible(false);
            if(loggedInUser.getRole().equals("ADMIN") || loggedInUser.getRole().equals("PUBLISHER")){
                menu.findItem(R.id.addPostMenu).setVisible(true);
            }
        }else{
            menu.findItem(R.id.logoutMenu).setVisible(false);
            menu.findItem(R.id.addPostMenu).setVisible(false);
        }
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
        sortPosts(getApplicationContext(), posts);
    }

    public void openSearchDialog(){
        SearchDialog searchDialog = new SearchDialog();
        searchDialog.show(getSupportFragmentManager(), "search dialog");
    }

    @Override
    public void applayTextS(String searchBy, boolean searchUser, boolean searchTag) {
        this.searchBy=searchBy;
        this.searchUser=searchUser;
        this.searchTag=searchTag;
        System.out.println(searchBy + "  " + searchUser + "  " +searchTag);
        if(searchUser==true){
            postService = ServiceUtils.postService;
            Call<List<Post>> call =postService.getPostsByAuthor(searchBy);
            call.enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    Toast.makeText(getApplicationContext(), "search result", Toast.LENGTH_SHORT).show();
                    List<Post> searchPosts =response.body();
                    Intent intent = new Intent(PostActivity.this, PostActivity.class);
                    intent.putExtra("loggedInUser", loggedInUser);
                    intent.putExtra("posts", (Serializable) searchPosts);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {

                }
            });
        }
        if(searchTag==true){
            postService = ServiceUtils.postService;
            Call<List<Post>> call =postService.getPostsByTagName(searchBy);
            call.enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    Toast.makeText(getApplicationContext(), "search result", Toast.LENGTH_SHORT).show();
                    List<Post> searchPosts =response.body();
                    Intent intent = new Intent(PostActivity.this, PostActivity.class);
                    intent.putExtra("loggedInUser", loggedInUser);
                    intent.putExtra("posts", (Serializable) searchPosts);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {

                }
            });
        }
    }

    public void sortPosts(Context context, List<Post> posts){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sort_post = preferences.getString("sort_post", null);

        if(posts== null){
            posts = new ArrayList<>();
        }
        if(sort_post != null) {
            if (sort_post.equals("0")) {
                sortByDate(context,posts);
            } else if(sort_post.equals("1")) {
                sortByPopularity(context,posts);
            }else if(sort_post.equals("2")){
                sortByComments(context,posts);
            }
        }
    }

    public void sortByDate(Context context, List<Post> posts){
        System.out.println(posts);
        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post post, Post t1) {
                return t1.getDate().compareTo(post.getDate());
            }
        });

        postsAdapter = new PostsAdapter(context, posts);
        postsListView = findViewById(R.id.listOfPosts);
        postsListView.setAdapter(postsAdapter);
        //postsAdapter.notifyDataSetChanged();
    }

    public void sortByPopularity(Context context, List<Post> posts){

        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post post, Post t1) {
                int first;
                int second ;
                first = post.getLikes() - post.getDislikes();
                second = t1.getLikes() - t1.getDislikes();
                return Integer.valueOf(second).compareTo(first);
            }
        });
        postsAdapter = new PostsAdapter(context, posts);
        postsListView = findViewById(R.id.listOfPosts);
        postsListView.setAdapter(postsAdapter);
        //postsAdapter.notifyDataSetChanged();

    }

    public void sortByComments(Context context, List<Post> posts){
        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post post, Post t1) {
                int first;
                int second ;
                first = post.getComments().size();
                second = t1.getComments().size();
                return Integer.valueOf(second).compareTo(first);
            }
        });
        postsAdapter = new PostsAdapter(context, posts);
        postsListView = findViewById(R.id.listOfPosts);
        postsListView.setAdapter(postsAdapter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void openReadPostActivity(Post post) {
        post.setPhoto(null);
        Intent intent = new Intent(getApplicationContext(), ReadPostActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("post", post);
        intent.putExtra("loggedInUser", loggedInUser);
        startActivity(intent);
    }

}
