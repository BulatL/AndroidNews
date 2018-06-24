package com.example.student.projekat.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.projekat.R;
import com.example.student.projekat.adapters.CommentsAdapter;
import com.example.student.projekat.adapters.PostsAdapter;
import com.example.student.projekat.adapters.UserCommentsAdapter;
import com.example.student.projekat.adapters.UserPostsAdapter;
import com.example.student.projekat.model.Comment;
import com.example.student.projekat.model.Post;
import com.example.student.projekat.model.User;
import com.example.student.projekat.service.CommentService;
import com.example.student.projekat.service.PostService;
import com.example.student.projekat.service.ServiceUtils;
import com.example.student.projekat.service.UserService;
import com.example.student.projekat.util.EditUserDialog;
import com.example.student.projekat.util.NonScrollListView;
import com.example.student.projekat.util.SearchDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoActivity extends AppCompatActivity implements SearchDialog.SearchDialogListener, EditUserDialog.EditUserDialogListener{
    private DrawerLayout mDrawerLayout;
    private Bitmap myBitmap;
    private ImageView userImage;
    private TextView userNameTextView;
    private TextView userRoleTextView;
    private NonScrollListView listViewPosts;
    private NonScrollListView listViewComments;
    private UserPostsAdapter postsAdapter;
    private PostService postService;
    private UserCommentsAdapter commentsAdapter;
    private CommentService commentService;

    private static Context mContext;
    private User loggedInUser;
    private User userInfo;
    private List<Post> posts = new ArrayList<Post>();
    private List<Comment> comments = new ArrayList<Comment>();

    private String searchBy;
    private boolean searchUser;
    private boolean searchTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        this.setTitle("User info");

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView_user_info);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        mContext = getApplicationContext();
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
                loggedInUser = (User) extras2.getSerializable("loggedInUser");
                navigationView.getMenu().findItem(R.id.accountNV).setVisible(true);
            }else{
                navigationView.getMenu().findItem(R.id.accountNV).setVisible(false);
            }
        }
        if(loggedInUser!= null){
            View headerLayout = navigationView.getHeaderView(0);
            TextView userNameHeader = headerLayout.findViewById(R.id.username_header);
            TextView nameHeader = headerLayout.findViewById(R.id.name_header);

            nameHeader.setTextColor(Color.parseColor("#00ff19"));
            nameHeader.setText(loggedInUser.getName());
            userNameHeader.setText(loggedInUser.getUsername());
            userNameHeader.setTextColor(Color.parseColor("#00ff19"));

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
                            Toast.makeText(UserInfoActivity.this, "U click on home button",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(UserInfoActivity.this, PostActivity.class);
                            i.putExtra("loggedInUser", loggedInUser);
                            startActivity(i);
                        }

                        if (menuItem.getItemId() == R.id.settingsNV) {
                            Toast.makeText(UserInfoActivity.this, "u click on settings button",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(UserInfoActivity.this, SettingsActivity.class);
                            startActivity(i);
                        }

                        return true;
                    }
                });
        userImage = findViewById(R.id.userImage);
        userNameTextView = findViewById(R.id.userNameInfo);
        userRoleTextView = findViewById(R.id.userRoleInfo);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            loggedInUser = (User) extras.getSerializable("loggedInUser");
            userInfo = (User) extras.getSerializable("userInfo");

        }

        if(userInfo!=null)
        {
            userNameTextView.setText("name: " + userInfo.getName());
            userRoleTextView.setText("role: " + userInfo.getRole());

            if(userInfo.getPhoto()!= null){
                myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.android_robot);
                userImage.setImageBitmap(myBitmap);
            }else{
                myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.android_robot);
                userImage.setImageBitmap(myBitmap);
            }
        }

        listViewPosts = findViewById(R.id.listViewUserPosts);
        postService = ServiceUtils.postService;

        if(userInfo!=null) {
            Call call = postService.getPostsByAuthor(userInfo.getUsername());

            call.enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    posts = response.body();
                    if(posts!=null) {
                        if (posts.size() > 0) {
                            postsAdapter = new UserPostsAdapter(getApplicationContext(), posts);
                            listViewPosts.setAdapter(postsAdapter);
                            //sortPosts(getApplicationContext(),posts);
                        }
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });
        }

        commentsAdapter = new UserCommentsAdapter(getApplicationContext(), comments);
        listViewComments = findViewById(R.id.listViewUserComments);
        CommentService commentService = ServiceUtils.commentService;

        if(userInfo!=null) {
            Call callComments = commentService.getCommentsByAuthor(userInfo.getUsername());

            callComments.enqueue(new Callback<List<Comment>>() {
                @Override
                public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                    comments = response.body();
                    if (comments == null) {
                        comments = new ArrayList<>();
                    }
                    if(comments!=null) {
                        if (comments.size() > 0) {
                            commentsAdapter = new UserCommentsAdapter(getApplicationContext(), comments);
                            listViewComments.setAdapter(commentsAdapter);
                        }
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });
        }
        ImageButton btnEditUser = findViewById(R.id.editUser);
        ImageButton btnDeleteUser = findViewById(R.id.deleteUser);

        if(loggedInUser!=null) {
            System.out.println(loggedInUser.getUsername());
            System.out.println(userInfo.getUsername());
            if(loggedInUser.getUsername().equals(userInfo.getUsername()) || loggedInUser.getRole().equals("ADMIN")){
                btnEditUser.setVisibility(View.VISIBLE);
                btnDeleteUser.setVisibility(View.VISIBLE);
            }else {
                btnEditUser.setVisibility(View.GONE);
                btnDeleteUser.setVisibility(View.GONE);
            }
        }else {
            btnEditUser.setVisibility(View.GONE);
            btnDeleteUser.setVisibility(View.GONE);
        }



        btnEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditUserDialog editUserDialog = new EditUserDialog();
                Bundle args = new Bundle();
                args.putString("userName", userInfo.getUsername());
                args.putString("name", userInfo.getName());
                args.putString("password", userInfo.getPassword());
                editUserDialog.setArguments(args);
                editUserDialog.show(getSupportFragmentManager(), "edit user dialog");

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.homeNV:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.loginMenu:
                Toast.makeText(UserInfoActivity.this, "U click on login button",
                        Toast.LENGTH_SHORT).show();
                Intent iLogin = new Intent(UserInfoActivity.this, LoginActivity.class);
                startActivity(iLogin);
                return true;
            case R.id.searchMenu:
                Toast.makeText(UserInfoActivity.this, "U click on search button",
                        Toast.LENGTH_SHORT).show();
                openSearchDialog();
                return true;
            case R.id.addPostMenu:
                Toast.makeText(UserInfoActivity.this, "U click on add button",
                        Toast.LENGTH_SHORT).show();

                Intent iAdd = new Intent(UserInfoActivity.this, CreatePostActivity.class);
                iAdd.putExtra("loggedInUser", loggedInUser);
                startActivity(iAdd);
                return true;
            case R.id.settingsMenu:
                Toast.makeText(UserInfoActivity.this, "U click on settings button",
                        Toast.LENGTH_SHORT).show();
                Intent iSettings = new Intent(UserInfoActivity.this, SettingsActivity.class);
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
                Toast.makeText(UserInfoActivity.this, "U click on register button",
                        Toast.LENGTH_SHORT).show();
                Intent iRegister = new Intent(UserInfoActivity.this, RegisterActivity.class);
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
                    Intent intent = new Intent(UserInfoActivity.this, PostActivity.class);
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
                    Intent intent = new Intent(UserInfoActivity.this, PostActivity.class);
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

    @Override
    public void applayTextS(String userName, String name, String password) {
        userInfo.setUsername(userName);
        userInfo.setName(name);
        userInfo.setPassword(password);
        System.out.println(userName +" " + name+" " + password);
        if(validateEdit(userName,name,password)) {
            System.out.println("prosao validaciju");
            UserService userService = ServiceUtils.userService;
            Call<User> call = userService.editUser(userInfo,userInfo.getId());
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Toast.makeText(getApplicationContext(), "successfully updated user", Toast.LENGTH_SHORT).show();
                    userInfo = response.body();
                    Intent intent = new Intent(UserInfoActivity.this, UserInfoActivity.class);
                    intent.putExtra("loggedInUser", loggedInUser);
                    intent.putExtra("userInfo", userInfo);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }
    }

    private boolean validateEdit(String username, String name, String password){
        if(username == null || username.trim().length() == 0){
            Toast.makeText(this,"Username is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(name == null || password.trim().length() == 0){
            Toast.makeText(this,"Name is required",Toast.LENGTH_SHORT).show();
            return false;
        }if(password == null || password.trim().length() == 0){
            Toast.makeText(this,"Password is required",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void btnDeleteUser(View view){
        if(loggedInUser.getUsername().equals(userInfo.getUsername())){
            new AlertDialog.Builder(this)
                    .setTitle("Delete user")
                    .setMessage("Are you sure u want to delete you'r account?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UserService userService = ServiceUtils.userService;
                            Call<User> call =userService.deleteUser(userInfo.getId());
                            call.enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    Toast.makeText(UserInfoActivity.this, "Successfully delete",
                                            Toast.LENGTH_SHORT).show();
                                    openPostActivity(null);
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    Toast.makeText(UserInfoActivity.this, "Successfully delete",
                                            Toast.LENGTH_SHORT).show();
                                    openPostActivity(null);
                                }
                            });
                        }
                    })
                    .create()
                    .show();
        }else{
            new AlertDialog.Builder(this)
                    .setTitle("Delete user")
                    .setMessage("Are you sure u want to delete " + userInfo.getName()+" ?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UserService userService = ServiceUtils.userService;
                            Call<User> call =userService.deleteUser(userInfo.getId());
                            call.enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    Toast.makeText(UserInfoActivity.this, "Successfully delete " + userInfo.getName(),
                                            Toast.LENGTH_SHORT).show();
                                    openPostActivity(loggedInUser);
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    Toast.makeText(UserInfoActivity.this, "Successfully delete " + userInfo.getName(),
                                            Toast.LENGTH_SHORT).show();
                                    openPostActivity(loggedInUser);
                                }
                            });
                        }
                    })
                    .create()
                    .show();
        }
    }

    public void openPostActivity(User user){
        Intent intent = new Intent(UserInfoActivity.this, PostActivity.class);
        if(user!= null){
            intent.putExtra("loggedInUser", user);
        }
        startActivity(intent);
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
