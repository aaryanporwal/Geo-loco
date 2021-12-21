package com.example.android.geo_loco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LoginPage extends AppCompatActivity {

    private EditText editTextLoginEmail , editTextLoginPwd;
    private FirebaseAuth authProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Login In");

        editTextLoginEmail = findViewById(R.id.loginemail);
        editTextLoginPwd = findViewById(R.id.loginpassword);

        authProfile = FirebaseAuth.getInstance();

        //button for login activity
        Button buttonLogin = findViewById(R.id.loginbutton);
        //button for signup page
        Button buttonRegister = findViewById(R.id.signuppage);


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoginPage.this , "please enter your email", Toast.LENGTH_LONG).show();
                    editTextLoginEmail.setError("email is required");
                    editTextLoginEmail.requestFocus();
                }else if(TextUtils.isEmpty(textPwd)){
                    Toast.makeText(LoginPage.this , "please enter your Password", Toast.LENGTH_LONG).show();
                    editTextLoginPwd.setError("Password is required");
                    editTextLoginPwd.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(LoginPage.this , "please enter a valid email address",Toast.LENGTH_LONG).show();
                    editTextLoginEmail.setError("email format is incorrect");
                    editTextLoginEmail.requestFocus();

                    //noe we clear what is inside the email and give a fresh one
                    editTextLoginEmail.clearComposingText();
                }else if(textPwd.length()<6){
                    Toast.makeText(LoginPage.this , "password must be atleast 6 character long",Toast.LENGTH_LONG).show();
                    editTextLoginPwd.setError("email format is incorrect");
                    editTextLoginPwd.requestFocus();

                    editTextLoginPwd.clearComposingText();
                }else{
                    loginUser(textEmail,textPwd);
                }

            }


        });


        // intent for the registration activity
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this , registration_page.class);
                startActivity(intent);
            }
        });


    }
    private void loginUser(String textEmail, String textPwd) {
        authProfile.signInWithEmailAndPassword(textEmail ,textPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginPage.this , "User successfully logged in 🥳" ,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginPage.this , MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(LoginPage.this , "Something Went Wrong",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser()!=null){
                startActivity(new Intent(LoginPage.this , MainActivity.class));
                finish();
                        //start the main activity
        }else{
            Toast.makeText(LoginPage.this , "Ready to login ",Toast.LENGTH_LONG).show();
        }
    }
}