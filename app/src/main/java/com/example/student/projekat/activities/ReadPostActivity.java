package com.example.student.projekat.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.projekat.R;
import com.example.student.projekat.adapters.CommentsAdapter;
import com.example.student.projekat.model.Comment;
import com.example.student.projekat.model.Post;
import com.example.student.projekat.model.Tag;
import com.example.student.projekat.model.User;
import com.example.student.projekat.service.CommentService;
import com.example.student.projekat.service.PostService;
import com.example.student.projekat.service.ServiceUtils;
import com.example.student.projekat.service.TagService;
import com.example.student.projekat.service.UserService;
import com.example.student.projekat.util.SearchDialog;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadPostActivity extends AppCompatActivity implements SearchDialog.SearchDialogListener {
    private TextView title;
    private TextView dateTime;
    private TextView description;
    private TextView tag;
    private TextView like;
    private TextView disLikes;
    private TextView location;
    private Button btnAuthor;
    private static TextView titleComment;
    private static TextView descComment;
    private Bitmap myBitmap;
    private DrawerLayout mDrawerLayout;
    private static ListView commentsListView;

    private SharedPreferences sharedPreferences;
    private static Context mContext;
    private static CommentsAdapter commentsAdapter;
    private PostService postService;
    private UserService userService;

    private static List<Comment> comments = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();
    private static Post post = new Post();
    private static Comment editedComment;
    private static User loggedInUser;


    private static String addCheck = "add";
    private String searchBy;
    private String stringBitmap;
    private boolean searchUser;
    private boolean searchTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_post);
        this.setTitle("Read post");


        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView_read_post);
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
                            Toast.makeText(ReadPostActivity.this, "u click on home button",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(ReadPostActivity.this, PostActivity.class);
                            i.putExtra("loggedInUser", loggedInUser);
                            startActivity(i);
                        }

                        if (menuItem.getItemId() == R.id.accountNV) {
                            Toast.makeText(ReadPostActivity.this, "U click on account button",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(ReadPostActivity.this, UserInfoActivity.class);
                            i.putExtra("loggedInUser", loggedInUser);
                            i.putExtra("userInfo", loggedInUser);
                            startActivity(i);
                        }

                        if (menuItem.getItemId() == R.id.settingsNV) {
                            Toast.makeText(ReadPostActivity.this, "u click on settings button",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(ReadPostActivity.this, SettingsActivity.class);
                            startActivity(i);
                        }

                        return true;
                    }
                });

        post = (Post) getIntent().getSerializableExtra("post");
        ImageButton btnEditPost = findViewById(R.id.btnEditPost);
        ImageButton btnDelete = findViewById(R.id.btnDeletePost);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            loggedInUser = (User) extras.getSerializable("loggedInUser");
            if(extras.getSerializable("stringBitmap1")!=null){
                stringBitmap =(String) extras.getSerializable("stringBitmap1") + (String) extras.getSerializable("stringBitmap2");
            }
        }
        if(loggedInUser!=null){
            System.out.println("loggedInUser nije null");
            if(loggedInUser.getUsername().equals(post.getAuthor().getUsername()) || loggedInUser.getRole().equals("ADMIN")){
                btnEditPost.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
            }
            else{
                //nije ni admin ni author
                btnEditPost.setVisibility(View.INVISIBLE);
                btnDelete.setVisibility(View.INVISIBLE);
            }
        }else{
            //nije niko ulogovan
            ImageButton btnLikePost = findViewById(R.id.image_likes_post);
            ImageButton btnDislikePost = findViewById(R.id.image_dislikes_post);
            System.out.println("niko nije ulogovan ");
            btnLikePost.setClickable(false);
            btnDislikePost.setClickable(false);
            btnEditPost.setVisibility(View.INVISIBLE);
            btnDelete.setVisibility(View.INVISIBLE);
        }

        title =  findViewById(R.id.titleRead);
        title.setText(post.getTitle());

        btnAuthor =  findViewById(R.id.AuthorRead);
        btnAuthor.setText(post.getAuthor().getName());

        btnAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ReadPostActivity.this, UserInfoActivity.class);
                i.putExtra("loggedInUser", loggedInUser);
                i.putExtra("userInfo", post.getAuthor());
                startActivity(i);
            }
        });

        dateTime = findViewById(R.id.dateTime);
        String date = DateFormat.getDateTimeInstance().format(post.getDate());
        dateTime.setText(date);


        description =  findViewById(R.id.description);
        description.setText(post.getDescription());


        setTagsInPost();
        tag =  findViewById(R.id.tagRead);
        if(post.getTags()!= null) {
            for (Tag t : post.getTags()) {
                tag.append("#" + t.getName() + " ");
            }
        }

        location = findViewById(R.id.locationReadPost);
        if(post.getLatitude()!= 0 && post.getLongitude()!= 0) {
            getAddress(post.getLatitude(), post.getLongitude());
        }

        like =  findViewById(R.id.likesPost);
        like.setText(String.valueOf(post.getLikes()));

        disLikes = findViewById(R.id.disLikesPost);
        disLikes.setText(String.valueOf(post.getDislikes()));

        myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.keyboard);
        /*String uri = Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +R.drawable.keyboard).toString();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        myBitmap = BitmapFactory.decodeFile(uri ,options);*/
        loadPost();

        titleComment = findViewById(R.id.titleComments);
        descComment = findViewById(R.id.EnterComments);
        titleComment.setFilters(new InputFilter[] {new InputFilter.LengthFilter(250)});
        descComment.setFilters(new InputFilter[] {new InputFilter.LengthFilter(250)});

        commentsAdapter = new CommentsAdapter(getApplicationContext(), comments, loggedInUser);
        commentsListView = findViewById(R.id.listOfComments);
        CommentService commentService = ServiceUtils.commentService;

        Call call = commentService.getCommentsByPost(post.getId());

        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                comments = response.body();
                post.setComments(comments);
                if(comments== null){
                    comments = new ArrayList<>();
                }
                if(comments!=null) {
                    if (comments.size() > 0) {
                        commentsAdapter = new CommentsAdapter(getApplicationContext(), comments, loggedInUser);
                        commentsListView.setAdapter(commentsAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
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

    public void btnLikePost(View view) {
        final ImageButton btnLikePost = findViewById(R.id.image_likes_post);
        final ImageButton btnDislikePost = findViewById(R.id.image_dislikes_post);
        like = findViewById(R.id.likesPost);

        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        String loggedInUser= sharedPreferences.getString(LoginActivity.Username, "");
        if(!loggedInUser.equals(post.getAuthor().getUsername())){
            if (btnLikePost.isEnabled()) {
                post.setLikes(post.getLikes() + 1);
                postService = ServiceUtils.postService;
                Call<Post> call = postService.updatePost(post, post.getId());
                call.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        Toast.makeText(getApplicationContext(), "Liked post", Toast.LENGTH_SHORT).show();
                        if (!btnDislikePost.isEnabled()) {
                            btnDislikePost.setImageResource(R.drawable.ic_thumb_down_dark_red_24dp);
                        }
                        btnLikePost.setEnabled(false);
                        btnDislikePost.setEnabled(true);
                        btnLikePost.setImageResource(R.drawable.ic_thumb_up_light_green_24dp);

                        TextView likeView = findViewById(R.id.likesPost);
                        likeView.setText(String.valueOf(post.getLikes()));

                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {

                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Already liked post", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Can't liked your post", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate(String title, String desc){
        if(title == null || title.trim().length() == 0){
            Toast.makeText(this,"Title is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(desc == null || desc.trim().length() == 0){
            Toast.makeText(this,"Description is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void btnDislikePost(View view){
        final ImageButton btnLikePost = findViewById(R.id.image_likes_post);
        final ImageButton btnDislikePost = findViewById(R.id.image_dislikes_post);
        disLikes = findViewById(R.id.disLikesPost);

        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        String loggedInUser= sharedPreferences.getString(LoginActivity.Username, "");
        if(!loggedInUser.equals(post.getAuthor().getUsername())){
            if(btnDislikePost.isEnabled()) {
                post.setDislikes(post.getDislikes() + 1);
                postService = ServiceUtils.postService;
                Call<Post> call = postService.updatePost(post, post.getId());
                call.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        Toast.makeText(getApplicationContext(), "Disliked post", Toast.LENGTH_SHORT).show();
                        if(!btnLikePost.isEnabled()){
                            btnLikePost.setImageResource(R.drawable.ic_thumb_up_dark_green_24dp);
                        }
                        btnDislikePost.setEnabled(false);
                        btnLikePost.setEnabled(true);
                        btnDislikePost.setImageResource(R.drawable.ic_thumb_down_light_red_24dp);

                        TextView dislikeView = findViewById(R.id.disLikesPost);
                        dislikeView.setText(String.valueOf(post.getDislikes()));

                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {

                    }
                });
            }else{
                Toast.makeText(getApplicationContext(), "Already disliked post", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Can't dislike your post", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnDeletePost(View view){
        postService = ServiceUtils.postService;
        Call<Void> call = postService.deletePost(post.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(getApplicationContext(), "Post is deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("loggedInUser", loggedInUser);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public void btnAddComment(View view){
        EditText editTextComment = findViewById(R.id.EnterComments);
        String commentText = editTextComment.getText().toString();

        EditText editTitleComment = findViewById(R.id.titleComments);
        String titleComment = editTitleComment.getText().toString();

        if(validate(titleComment,commentText)) {
            if (addCheck.equals("add")) {
                Date date = Calendar.getInstance().getTime();

                Comment addComment = new Comment();
                addComment.setAuthor(loggedInUser);
                addComment.setDate(date);
                addComment.setDescription(commentText);
                addComment.setLikes(0);
                addComment.setDislikes(0);
                addComment.setTitle(titleComment);
                addComment.setPost(post);

                CommentService commentService = ServiceUtils.commentService;
                Call<Comment> call = commentService.addComment(addComment);
                call.enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(Call<Comment> call, Response<Comment> response) {
                        Toast.makeText(getApplicationContext(), "Successfully added comment", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Comment> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
                post.getComments().add(addComment);
                Toast.makeText(getApplicationContext(), "Comment is created", Toast.LENGTH_SHORT).show();
            } else if (addCheck.equals("edit")) {
                editedComment.setTitle(titleComment);
                editedComment.setDescription(commentText);
                CommentService commentService = ServiceUtils.commentService;
                Call<Comment> call = commentService.updateComment(editedComment, editedComment.getId());
                call.enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(Call<Comment> call, Response<Comment> response) {
                        Toast.makeText(getApplicationContext(), "Successfully updated comment", Toast.LENGTH_SHORT).show();
                        addCheck = "add";
                    }

                    @Override
                    public void onFailure(Call<Comment> call, Throwable t) {

                    }
                });

            }
        }

        Intent intent = new Intent(getApplicationContext(), ReadPostActivity.class);
        post.setPhoto(null);
        loggedInUser.setPhoto(null);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("post", post);
        intent.putExtra("loggedInUser", loggedInUser);
        startActivity(intent);
        finish();

    }

    public static void editComment(final Comment comment){
        addCheck= "edit";
        editedComment = comment;
        titleComment.setText(comment.getTitle());
        descComment.setText(comment.getDescription());
    }

    public void btnCancelComment(View view){
        System.out.println("usao je u metodu");
        TextView title = findViewById(R.id.titleComments);
        System.out.println(title.getText().toString() + "  ispis");
        title.clearComposingText();
        title.setText("");
        TextView desc = findViewById(R.id.EnterComments);
        desc.clearComposingText();
        desc.setText("");
    }

    public void btnEditPost(View view){
        post.setPhoto(null);
        Intent iAdd = new Intent(ReadPostActivity.this, CreatePostActivity.class);
        iAdd.putExtra("post", post);
        iAdd.putExtra("loggedInUser", loggedInUser);
        startActivity(iAdd);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.loginMenu:
                Toast.makeText(ReadPostActivity.this, "U click on login button",
                        Toast.LENGTH_SHORT).show();
                Intent iLogin = new Intent(ReadPostActivity.this, LoginActivity.class);
                startActivity(iLogin);
                return true;
            case R.id.searchMenu:
                Toast.makeText(ReadPostActivity.this, "U click on search button",
                        Toast.LENGTH_SHORT).show();
                openSearchDialog();
                return true;
            case R.id.addPostMenu:
                Toast.makeText(ReadPostActivity.this, "U click on add button",
                        Toast.LENGTH_SHORT).show();
                Intent iAdd = new Intent(ReadPostActivity.this, CreatePostActivity.class);
                iAdd.putExtra("loggedInUser", loggedInUser);
                startActivity(iAdd);
                return true;
            case R.id.settingsMenu:
                Toast.makeText(ReadPostActivity.this, "U click on settings button",
                        Toast.LENGTH_SHORT).show();
                Intent iSettings = new Intent(ReadPostActivity.this, SettingsActivity.class);
                startActivity(iSettings);
                return true;
            case R.id.logoutMenu:
                SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.clear();
                editor.commit();
                Intent logoutIntent = new Intent(this, PostActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
                return true;
            case R.id.registerMenu:
                Toast.makeText(ReadPostActivity.this, "U click on register button",
                        Toast.LENGTH_SHORT).show();
                Intent iRegister = new Intent(ReadPostActivity.this, RegisterActivity.class);
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
            menu.findItem(R.id.loginMenu).setVisible(false);
            menu.findItem(R.id.registerMenu).setVisible(false);
            if(loggedInUser.getRole().equals("ADMIN") || loggedInUser.getRole().equals("PUBLISHER")){
                menu.findItem(R.id.addPostMenu).setVisible(true);
            }
            else{
                menu.findItem(R.id.addPostMenu).setVisible(false);
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
                    Intent intent = new Intent(ReadPostActivity.this, PostActivity.class);
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
                    Intent intent = new Intent(ReadPostActivity.this, PostActivity.class);
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sort_comments = preferences.getString("sort_comments", null);

        if(sort_comments!=null){
            if (sort_comments.equals("0")) {
                /*Collections.sort(comments, new CommentsDateComparator());
                commentsAdapter.notifyDataSetChanged();*/
                Toast.makeText(ReadPostActivity.this, "Sorted by date",
                        Toast.LENGTH_SHORT).show();
                sortDate(getApplicationContext(), comments);
            } else {
                /*Collections.sort(comments, new CommentsPopularityComparator());
                commentsAdapter.notifyDataSetChanged();*/
                Toast.makeText(ReadPostActivity.this, "Sorted by popularity",
                        Toast.LENGTH_SHORT).show();
                sortByPopularity(getApplicationContext(), comments);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void sortDate(Context context, List<Comment> comments){
        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment comment, Comment comment1) {
                return comment1.getDate().compareTo(comment.getDate());
            }
        });
        CommentsAdapter commentsAdapter = new CommentsAdapter(context, comments, loggedInUser);
        commentsListView = findViewById(R.id.listOfComments);
        commentsListView.setAdapter(commentsAdapter);
        commentsAdapter.notifyDataSetChanged();
    }

    public void sortByPopularity(Context context, List<Comment> comments){
        System.out.println("Sortbypopularity");
        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment comment, Comment comment1) {
                int first;
                int second ;
                first = comment.getLikes() - comment.getDislikes();
                second = comment1.getLikes() - comment1.getDislikes();
                System.out.println("prvi " +comment.getId() + " " + first);
                System.out.println("drugi " +comment1.getId() + " " + second+"\n");
                return Integer.valueOf(second).compareTo(first);
            }
        });
        CommentsAdapter commentsAdapter = new CommentsAdapter(context, comments, loggedInUser);
        commentsListView = findViewById(R.id.listOfComments);
        commentsListView.setAdapter(commentsAdapter);
        commentsAdapter.notifyDataSetChanged();
    }

    public static void getComments(Comment comment){
        post.getComments().remove(comment);
        commentsAdapter = new CommentsAdapter(mContext,post.getComments(),loggedInUser);
        commentsListView.setAdapter(commentsAdapter);
    }

    public static String checkUserAndAuthorEditDelete(String authro){
        String result= "";
        if(loggedInUser!=null) {
            if (authro.equals(loggedInUser.getUsername()) || loggedInUser.getRole().equals("ADMIN")) {
                result = "same";
            }
        }
        return result;
    }

    public static String checkUserAndAuthorLikeDislike(String authro){
        String result= "";
        if(loggedInUser!=null) {
            if (authro.equals(loggedInUser.getUsername())) {
                result = "same";
            }
        }
        return result;
    }

    public void getAddress(double latitude,double longitude){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            location.setText(city + "," + country);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setTagsInPost(){
        TagService tagService = ServiceUtils.tagService;
        Call callT = tagService.getTagsByPost(post.getId());
        callT.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                List<Tag> tags = (List<Tag>)response.body();
                post.setTags(tags);
            }
            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    public void loadPost(){
        PostService postService2 =ServiceUtils.postService;
        Call<Post> call = postService2.getPost(post.getId());

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                post = response.body();
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(post.getPhoto());
                setTagsInPost();
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });
    }

}
