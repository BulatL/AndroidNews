package com.example.student.projekat.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.projekat.R;
import com.example.student.projekat.dialog.LocationDialog;
import com.example.student.projekat.model.Comment;
import com.example.student.projekat.model.Post;
import com.example.student.projekat.model.Tag;
import com.example.student.projekat.model.User;
import com.example.student.projekat.service.PostService;
import com.example.student.projekat.service.ServiceUtils;
import com.example.student.projekat.service.TagService;
import com.example.student.projekat.service.UserService;
import com.example.student.projekat.util.SearchDialog;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity implements LocationListener, SearchDialog.SearchDialogListener{
    private DrawerLayout mDrawerLayout ;
    private EditText titleEdit;
    private EditText descriptionEdit;
    private EditText tagsEdit;
    private TextView location_text;
    private Button location_btn;

    private Post postResponse;
    private Bitmap bitmap;
    private byte[] byteArray;
    private SharedPreferences sharedPreferences;
    private User author = new User();
    private Post post = new Post();
    private User loggedInUser;


    private double longitude;
    private double latitude;
    private String userName = "";
    private String provider;
    private String addOrEdit = "add";
    private String searchBy;
    private boolean searchUser;
    private boolean searchTag;

    private LocationManager locationManager;
    private AlertDialog dialog;
    private Location location;
    private PostService postService;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int PICK_IMAGE = 1;

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
        Bundle extras2 = getIntent().getExtras();
        if(extras2 != null){
            if(extras2.getSerializable("loggedInUser") != null){
                loggedInUser = (User)extras2.getSerializable("loggedInUser");
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

                        if(menuItem.getItemId() == R.id.homeNV){
                            Toast.makeText(CreatePostActivity.this, "U click on home button",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(CreatePostActivity.this, PostActivity.class);
                            i.putExtra("loggedInUser", loggedInUser);
                            startActivity(i);
                        }

                        if (menuItem.getItemId() == R.id.accountNV) {
                            Toast.makeText(CreatePostActivity.this, "U click on account button",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(CreatePostActivity.this, UserInfoActivity.class);
                            i.putExtra("loggedInUser", loggedInUser);
                            i.putExtra("userInfo", loggedInUser);
                            startActivity(i);
                        }

                        if(menuItem.getItemId() == R.id.settingsNV){
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

        Bundle extras = getIntent().getExtras();
        if(extras.getSerializable("post") != null){
            post = (Post) extras.getSerializable("post");
            title.setText(post.getTitle());
            description.setText(post.getDescription());
            for (Tag t:post.getTags()) {
                tags.append("#"+t.getName() + " ");
            }
            addOrEdit = "edit";
        }

        location_text = findViewById(R.id.textViewLocation);
        location_btn = findViewById(R.id.getLocation);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.loginMenu:
                Toast.makeText(CreatePostActivity.this, "U click on login button",
                        Toast.LENGTH_SHORT).show();
                Intent iLogin = new Intent(CreatePostActivity.this, LoginActivity.class);
                startActivity(iLogin);
                return true;
            case R.id.searchMenu:
                Toast.makeText(CreatePostActivity.this, "U click on search button",
                        Toast.LENGTH_SHORT).show();
                openSearchDialog();
                return true;
            case R.id.addPostMenu:
                Toast.makeText(CreatePostActivity.this, "U click on add button",
                        Toast.LENGTH_SHORT).show();
                Intent i = new Intent(CreatePostActivity.this, CreatePostActivity.class);
                startActivity(i);
                return true;
            case R.id.settingsMenu:
                Toast.makeText(CreatePostActivity.this, "U click on settings button",
                        Toast.LENGTH_SHORT).show();
                Intent i2 = new Intent(CreatePostActivity.this, SettingsActivity.class);
                startActivity(i2);
                return true;
            case R.id.logoutMenu:
                SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.clear();
                editor.commit();
                Intent logoutIntent = new Intent(this, PostActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
                return  true;
            case R.id.registerMenu:
                Toast.makeText(CreatePostActivity.this, "U click on register button",
                        Toast.LENGTH_SHORT).show();
                Intent iRegister = new Intent(CreatePostActivity.this, RegisterActivity.class);
                startActivity(iRegister);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.registerMenu).setVisible(false);
        Bundle extras = getIntent().getExtras();
        if(extras.getSerializable("loggedInUser") != null){
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

    private boolean validate(String title, String desc, String tag){
        if(title == null || title.trim().length() == 0){
            Toast.makeText(this,"Title is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(desc == null || desc.trim().length() == 0){
            Toast.makeText(this,"Description is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(tag == null || tag.trim().length() == 0){
            Toast.makeText(this,"Tag is required",Toast.LENGTH_SHORT).show();
            return false;
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
                    Intent intent = new Intent(CreatePostActivity.this, PostActivity.class);
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
                    Intent intent = new Intent(CreatePostActivity.this, PostActivity.class);
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
        tagsEdit = findViewById(R.id.tagsCreate);

        String tagsString = tagsEdit.getText().toString().trim();
        String title = titleEdit.getText().toString();
        String description = descriptionEdit.getText().toString();

        if(validate(title,description,tagsString)) {
            if (addOrEdit.equals("add")) {
                System.out.println("Add je");

                Date date = Calendar.getInstance().getTime();


                if (latitude == 0 && longitude == 0) {
                    latitude = 45.2613;
                    longitude = 19.8336;
                }
                ;
                post.setTitle(title);
                post.setDescription(description);
                post.setDate(date);
                post.setAuthor(loggedInUser);
                post.setPhoto(bitmap);
                post.setLatitude(latitude);
                post.setLongitude(longitude);


                PostService postService = ServiceUtils.postService;
                Call<Post> call = postService.createPost(post);
                call.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        postResponse = response.body();
                        Toast.makeText(CreatePostActivity.this, "u create post successfully",
                                Toast.LENGTH_SHORT).show();
                        addTag();
                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {

                    }
                });

            } else if (addOrEdit.equals("edit")) {

                post.setTitle(title);
                post.setDescription(description);
                PostService postService = ServiceUtils.postService;
                Call<Post> call = postService.updatePost(post, post.getId());
                call.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        Toast.makeText(CreatePostActivity.this, "u updated post successfully",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ReadPostActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("loggedInUser", loggedInUser);
                        intent.putExtra("post", post);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {

                    }
                });
            }
        }
    }

    public void addTag(){
        System.out.println("add tag");
        tagsEdit = findViewById(R.id.tagsCreate);
        String tagsString = tagsEdit.getText().toString().trim();
        System.out.println(tagsString);
        String[] separated = tagsString.split("#");

        List<String> tagFilter =Arrays.asList(separated);
        Tag tagg = new Tag();
        for(String ts: tagFilter.subList(1,tagFilter.size())){
            System.out.println(ts);
            tagg.setName(ts);

            TagService tagService = ServiceUtils.tagService;
            Call<Tag> callTags = tagService.addTag(tagg);
            callTags.enqueue(new Callback<Tag>() {
                @Override
                public void onResponse(Call<Tag> call, Response<Tag> response) {
                    Tag tagResponse = response.body();

                    System.out.println("zavrsio tag add");
                    setTagsInPost(postResponse.getId(), tagResponse.getId());
                }

                @Override
                public void onFailure(Call<Tag> call, Throwable t) {

                }
            });
        }
    }

    public void setTagsInPost(int postId, int tagId){
        System.out.println("set tags in post");
        PostService postService = ServiceUtils.postService;
        Call<Post> call = postService.setTagsInPost(postId,tagId);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                System.out.println("zavrsio i ovo");
                openReadPostActivity();
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
            }
        });
    }

    public void openReadPostActivity(){
        post.setPhoto(null);
        System.out.println("usao u openreadpostactivity");
        Intent intent = new Intent(getApplicationContext(), ReadPostActivity.class);
        intent.putExtra("loggedInUser", loggedInUser);
        intent.putExtra("post", post);
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
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProvider();

                if (location == null) {
                    Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_SHORT).show();
                }
                else{
                    getAddress(location.getLatitude(),location.getLongitude());
                    onLocationChanged(location);
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void showLocatonDialog() {
        if (dialog == null) {
            dialog = new LocationDialog(this).prepareDialog();
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        dialog.show();
    }

    public void getProvider(){
        Criteria criteria = new Criteria();

        provider = locationManager.getBestProvider(criteria, true);

        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean wifi = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!gps &&  !wifi){
            showLocatonDialog();
        }else{
            if(checkLocationPermission()){
                if(ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    locationManager.requestLocationUpdates(provider,0,0,this);

                }else if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    locationManager.requestLocationUpdates(provider,0,0,this);
                }
            }
        }

        location = null;

        if(checkLocationPermission()){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                location = locationManager.getLastKnownLocation(provider);
            }else if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                location = locationManager.getLastKnownLocation(provider);
            }
        }
    }

    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("Allow user location")
                        .setMessage("To continue working we need your locations... Allow now?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(CreatePostActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();

            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);

            }
            return false;
        }else{
            return true;
        }
    }

    public void getAddress(double latitude,double longitude){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());



        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            location_text.setText(city + "," + country);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
