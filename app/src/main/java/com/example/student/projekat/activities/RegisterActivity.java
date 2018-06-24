package com.example.student.projekat.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.projekat.R;
import com.example.student.projekat.model.User;
import com.example.student.projekat.service.ServiceUtils;
import com.example.student.projekat.service.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends Activity {
    private TextView usernameTV;
    private TextView nameTV;
    private TextView passwordTV;
    private Button btnRegister;
    private Button btnUploadImage;
    private ImageView image;

    private Bitmap bitmap;
    private UserService userService;
    private User user = new User();

    private String username;
    private String name;
    private String password;
    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameTV = findViewById(R.id.registerUserName);
        nameTV = findViewById(R.id.registerName);
        passwordTV = findViewById(R.id.registerPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        image = findViewById(R.id.imageViewRegister);

        usernameTV.setFilters(new InputFilter[] {new InputFilter.LengthFilter(250)});
        nameTV.setFilters(new InputFilter[] {new InputFilter.LengthFilter(250)});
        passwordTV.setFilters(new InputFilter[] {new InputFilter.LengthFilter(250)});

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

                Toast.makeText(RegisterActivity.this, "u click on upload image button",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnRegister(View view){
        username = usernameTV.getText().toString().trim();
        System.out.println("username je " + username);
        name = nameTV.getText().toString().trim();
        System.out.println("name je " + name);
        password = passwordTV.getText().toString().trim();
        System.out.println("password je " + password);

        if(validate(username,name,password)){
            userService = ServiceUtils.userService;
            Call call = userService.getUserByUsername(username);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if(response.code()==200){
                        Toast.makeText(RegisterActivity.this, "Username already taken",
                                Toast.LENGTH_SHORT).show();
                    }else if(response.code()==404){
                        register();
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });
        }
    }

    public void register(){
        /*ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,0,out);
        Bitmap compressedBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));*/
        user.setUsername(username);
        user.setName(name);
        user.setPassword(password);
        user.setPhoto(bitmap);
        user.setRole("COMMENTATOR");

        System.out.println(bitmap.getByteCount());

        userService = ServiceUtils.userService;
        Call call = userService.addUser(user);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Toast.makeText(RegisterActivity.this, "Successfully register",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, PostActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("loggedInUser", user);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    private boolean validate(String username, String name, String password){
        System.out.println(username + " " + name + " " + password);
        if(username == null || username.trim().length() == 0){
            Toast.makeText(this,"Username is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(name == null || name.trim().length() == 0){
            Toast.makeText(this,"Name is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.trim().length() == 0){
            Toast.makeText(this,"Password is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {

            Uri selectedImage = data.getData();
            bitmap = null;
            try {

                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                System.out.println(bitmap);
                image.setImageBitmap(bitmap);

            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
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
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
