package com.example.student.projekat.activities;

import android.content.Intent;
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
import android.widget.Toast;

import com.example.student.projekat.R;

public class CreatePostActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout ;

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
    public void btnCreatePost(View view) {
        Toast.makeText(CreatePostActivity.this, "u click on create post button",
                Toast.LENGTH_SHORT).show();

    }

    public void btnUploadImage(View view) {
        Toast.makeText(CreatePostActivity.this, "u click on upload image button",
                Toast.LENGTH_SHORT).show();

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
