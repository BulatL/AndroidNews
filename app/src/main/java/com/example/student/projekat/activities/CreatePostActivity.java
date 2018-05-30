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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity implements LocationListener{
    private DrawerLayout mDrawerLayout ;
    private EditText titleEdit;
    private EditText descriptionEdit;
    private EditText tagsEdit;
    private Post postResponse;
    private Bitmap bitmap;
    private byte[] byteArray;
    public static final int PICK_IMAGE = 1;
    private SharedPreferences sharedPreferences;
    private User author = new User();
    private Post post = new Post();

    private Button location_btn;
    private TextView location_text;
    private double longitude;
    private double latitude;
    private LocationManager locationManager;
    private AlertDialog dialog;
    private String provider;
    private Location location;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

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
        post.setLatitude(latitude);
        post.setLongitude(longitude);


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
        location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProvider();

                if (location == null) {
                    Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_SHORT).show();
                }
                if (location != null) {
                    System.out.println("LONGITUDEEE: "+location.getLongitude() + "LATITUDEEEE:" + location.getLatitude());
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
        System.out.println("******************");
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
                    System.out.println("FINE LOC");

                }else if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    locationManager.requestLocationUpdates(provider,0,0,this);
                    System.out.println("COARSE LOC");
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


            System.out.println(city);
            System.out.println(country);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
