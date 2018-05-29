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
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.student.projekat.util.CommentsDateComparator;
import com.example.student.projekat.util.CommentsPopularityComparator;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadPostActivity extends AppCompatActivity {
    private TextView title;
    private TextView author;
    private TextView dateTime;
    private TextView description;
    private TextView tag;
    private TextView like;
    private TextView disLikes;
    private Bitmap myBitmap;
    private DrawerLayout mDrawerLayout;
    private static List<Comment> comments = new ArrayList<>();
    private static CommentsAdapter commentsAdapter;
    private SharedPreferences sharedPreferences;
    private static ListView commentsListView;
    private List<Tag> tags = new ArrayList<>();
    private static Post post = new Post();
    private PostService postService;
    private static Context mContext;


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

        post = (Post) getIntent().getSerializableExtra("post");



        title =  findViewById(R.id.titleRead);
        title.setText(post.getTitle());

        author =  findViewById(R.id.AuthorRead);
        author.setText(post.getAuthor().getUsername());


        dateTime = findViewById(R.id.dateTime);
        String date = DateFormat.getDateTimeInstance().format(post.getDate());
        dateTime.setText(date);


        description =  findViewById(R.id.description);
        description.setText(post.getDescription());


        tag =  findViewById(R.id.tagRead);
        String tags = "";
        for (Tag t:post.getTags()) {
            tags += t.getName() + " ";
        }
        tag.setText(tags);

        like =  findViewById(R.id.likesPost);
        like.setText(String.valueOf(post.getLikes()));

        disLikes = findViewById(R.id.disLikesPost);
        disLikes.setText(String.valueOf(post.getDislikes()));

        myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.keyboard);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(myBitmap);

        EditText newComment = findViewById(R.id.EnterComments);
        newComment.setFilters(new InputFilter[] {new InputFilter.LengthFilter(250)});

        commentsAdapter = new CommentsAdapter(getApplicationContext(), comments);
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
                commentsAdapter = new CommentsAdapter(getApplicationContext(), comments);
                commentsListView.setAdapter(commentsAdapter);

            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

        System.out.println("pre shared");
        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        System.out.println("pre ifa");
        String nesto = sharedPreferences.getString(LoginActivity.Username, "");
        System.out.println(nesto + " procitao je");
        if(sharedPreferences.contains(LoginActivity.Username)) {
            System.out.println("ovo je prvi if");
            String userName = sharedPreferences.getString(LoginActivity.Username, "");
            System.out.println(userName + " userName is pref " + post.getAuthor().getUsername() + " post.getAuthro");
            if(!userName.equals(post.getAuthor().getUsername())){
                System.out.println("nije isti autor ");
                ImageButton btnDelete = findViewById(R.id.btnDeletePost);
                btnDelete.setVisibility(View.GONE);
            }else{
                System.out.println("Isti je autor");
                ImageButton btnDelete = findViewById(R.id.btnDeletePost);
                btnDelete.setVisibility(View.VISIBLE);
            }
        }


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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    public void btnLikePost(View view) {
        final ImageButton btnLikePost = findViewById(R.id.image_likes_post);
        final ImageButton btnDislikePost = findViewById(R.id.image_dislikes_post);
        like = findViewById(R.id.likesPost);

        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        String loggedInUser= sharedPreferences.getString(LoginActivity.Username, "");
        if(loggedInUser.equals(post.getAuthor().getUsername())){
            if (btnLikePost.isEnabled()) {
                post.setLikes(post.getLikes() + 1);
                postService = ServiceUtils.postService;
                Call<Post> call = postService.addLikeDislike(post, post.getId());
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

                       /* Intent intent = new Intent(getApplicationContext(), ReadPostActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("post", post);
                        startActivity(intent);
                        finish();*/
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

    public void btnDislikePost(View view){
        final ImageButton btnLikePost = findViewById(R.id.image_likes_post);
        final ImageButton btnDislikePost = findViewById(R.id.image_dislikes_post);
        disLikes = findViewById(R.id.disLikesPost);

        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        String loggedInUser= sharedPreferences.getString(LoginActivity.Username, "");
        if(loggedInUser.equals(post.getAuthor().getUsername())){
            if(btnDislikePost.isEnabled()) {
                post.setDislikes(post.getDislikes() + 1);
                postService = ServiceUtils.postService;
                Call<Post> call = postService.addLikeDislike(post, post.getId());
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
        Date date = Calendar.getInstance().getTime();

        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        String authroName = sharedPreferences.getString(LoginActivity.Username, "");
        User newAuthro = new User();
        newAuthro.setUsername(authroName);

        EditText editTextComment = findViewById(R.id.EnterComments);
        String commentText = editTextComment.getText().toString();

        EditText editTitleComment = findViewById(R.id.titleComments);
        String titleComment = editTitleComment.getText().toString();

        Comment addComment = new Comment();
        addComment.setAuthor(newAuthro);
        addComment.setDate(date);
        addComment.setDescription(commentText);
        addComment.setLikes(0);
        addComment.setDislikes(0);
        addComment.setTitle(titleComment);
        addComment.setPost(post);

        CommentService commentService = ServiceUtils.commentService;
        Call<Comment> call = commentService.addComment(addComment);
        System.out.println("pre poziva resta");
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {

            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {

            }
        });

        post.getComments().add(addComment);
        Toast.makeText(getApplicationContext(), "Comment is created", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), ReadPostActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("post", post);
        startActivity(intent);
        finish();
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sort_comments = preferences.getString("sort_comments", null);

        if(sort_comments!=null){
            if (sort_comments.equals("0")) {
                /*Collections.sort(comments, new CommentsDateComparator());
                commentsAdapter.notifyDataSetChanged();*/
                System.out.println("sortiranje komentara po datumu");
                sortDate();
            } else {
                /*Collections.sort(comments, new CommentsPopularityComparator());
                commentsAdapter.notifyDataSetChanged();*/
                System.out.println("sortiranje komentara po popularnosti");
                sortByPopularity();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void sortDate(){
        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment comment, Comment comment1) {
                return comment1.getDate().compareTo(comment.getDate());
            }
        });
        commentsAdapter.notifyDataSetChanged();
    }

    public void sortByPopularity(){

        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment comment, Comment comment1) {
                int first;
                int second ;
                first = comment.getLikes() - comment.getDislikes();
                second = comment1.getLikes() - comment1.getDislikes();
                return Integer.valueOf(second).compareTo(first);
            }
        });
        commentsAdapter.notifyDataSetChanged();
    }

    public static void getComments(Comment comment){
        System.out.println("ddada " +  post.getTitle() );
        System.out.println(post.getComments());
        post.getComments().remove(comment);
        commentsAdapter = new CommentsAdapter(mContext,post.getComments());
        commentsListView.setAdapter(commentsAdapter);
    }

    public static boolean checkUserAndAuthro(String authro){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        String loggedInUserName = sharedPreferences.getString(LoginActivity.Username, "");
        if(authro.equals(loggedInUserName)) return true;
        else return false;
    }

}
