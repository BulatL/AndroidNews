package com.example.student.projekat.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.student.projekat.R;
import com.example.student.projekat.model.User;
import com.example.student.projekat.service.ServiceUtils;
import com.example.student.projekat.service.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public static final String MyPreferences = "MyPrefs";
    public static final String Username = "usernameKey";
    public static final String Name = "nameKey";
    private UserService userService;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button btnLogin;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userService = ServiceUtils.userService;
        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.loginBtn);

        editTextUsername.setFilters(new InputFilter[] {new InputFilter.LengthFilter(250)});
        editTextUsername.setFilters(new InputFilter[] {new InputFilter.LengthFilter(250)});
    }
    public void btnLogin (View view){
        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);

        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        if(validateLogin(username, password)){
            doLogin(username, password);
        }
    }

    private boolean validateLogin(String username,String password){
        if(username == null || username.trim().length() == 0){
            Toast.makeText(this,"Username is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.trim().length() == 0){
            Toast.makeText(this,"Password is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doLogin(final String username, final String password){
        Call<User> call = userService.login(username,password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();

                if(response.code()!= 200){
                    Toast.makeText(LoginActivity.this,"Username or password is incorrect",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {


                        sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
                        String userNamePref = sharedPreferences.getString(Username, "");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Name,user.getName());
                        editor.putString(Username, username);
                        editor.commit();
                        String nesto = sharedPreferences.getString(Username, "");
                        user.setPhoto(null);
                        Intent intent = new Intent(LoginActivity.this, PostActivity.class);
                        intent.putExtra("loggedInUser", user);
                        startActivity(intent);


                    } else {
                        Toast.makeText(LoginActivity.this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

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
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }
}
