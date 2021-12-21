package com.example.android.geo_loco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;

public class registration_page extends AppCompatActivity {

    private EditText editText_register_full_name , editText_register_email , editText_register_pwd , editText_register_confirm_pwd , editText_register_enrollment , editText_register_dob , editText_register_batch;
    private RadioGroup radioGroup_register_gender;
    private RadioButton radioButton_register_button_gender;
    private ProgressBar progressBar_register;
    private DatePickerDialog picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        // we are modifying the action bar of the app to register
        Objects.requireNonNull(getSupportActionBar()).setTitle("Register");
        Toast.makeText(registration_page.this , "you can register now" , Toast.LENGTH_LONG).show();

        // linking the defined variable to thier respected ids using find view by id
        progressBar_register = findViewById(R.id.progress_bar_register_page);
        editText_register_full_name = findViewById(R.id.edit_view_registration_page_full_name);
        editText_register_email = findViewById(R.id.edit_view_registration_page_email);
        editText_register_pwd = findViewById(R.id.edit_view_registration_page_password);
        editText_register_confirm_pwd = findViewById(R.id.edit_view_registration_page_confirm_password);
        editText_register_enrollment = findViewById(R.id.edit_view_registration_page_enrollment);
        editText_register_dob = findViewById(R.id.edit_view_registration_page_dob);
        editText_register_batch = findViewById(R.id.edit_view_registration_page_batch);

        radioGroup_register_gender = findViewById(R.id.radio_group_registration_page_gender);
        radioGroup_register_gender.clearCheck();

        // adding the calender intent to show the calender option
        editText_register_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //Date picker Dialog
                picker = new DatePickerDialog(registration_page.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editText_register_dob.setText(dayOfMonth+ "/" + (month+1)+"/" + year);
                    }
                    },year,month,day);
                picker.show();
            }
        });

        //linking the register button to its respected id
        Button buttonRegister = findViewById(R.id.button_registration_page_register);

        // making the OnClickListener method on the register button to assign the work done after the registration
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int selectedGenderId = radioGroup_register_gender.getCheckedRadioButtonId();
                radioButton_register_button_gender = findViewById(selectedGenderId);

                //<!-- taking the input and creating var for them -->
                String textFullName = editText_register_full_name.getText().toString();
                String textEmail = editText_register_email.getText().toString();
                String textPwd = editText_register_pwd.getText().toString();
                String textConfirmPwd = editText_register_confirm_pwd.getText().toString();
                String textEnrollment = editText_register_enrollment.getText().toString();
                String textDob = editText_register_dob.getText().toString();
                String textBatch = editText_register_batch.getText().toString();
                String textGender;

                //checking the fulfillment of criteria required in each input type
                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(registration_page.this , "please enter the full name", Toast.LENGTH_LONG).show();
                    editText_register_full_name.setError("full name is required");
                    editText_register_full_name.requestFocus();
                }else if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(registration_page.this , "please enter the Email", Toast.LENGTH_LONG).show();
                    editText_register_email.setError("Email is required");
                    editText_register_email.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(registration_page.this , "please re-enter the Email", Toast.LENGTH_LONG).show();
                    editText_register_email.setError("Valid Email is required");
                    editText_register_email.requestFocus();
                }else if(TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(registration_page.this, "please enter the Password", Toast.LENGTH_LONG).show();
                    editText_register_pwd.setError("password is required");
                    editText_register_pwd.requestFocus();
                }else if(textPwd.length()<6) {
                    Toast.makeText(registration_page.this, "password length should be atleast 6", Toast.LENGTH_LONG).show();
                    editText_register_pwd.setError("password too weak");
                    editText_register_pwd.requestFocus();
                }else if(TextUtils.isEmpty(textConfirmPwd)) {
                    Toast.makeText(registration_page.this, "please enter the Password", Toast.LENGTH_LONG).show();
                    editText_register_confirm_pwd.setError("password is required");
                    editText_register_confirm_pwd.requestFocus();
                }else if(!textPwd.equals(textConfirmPwd)) {
                    Toast.makeText(registration_page.this, "please enter the same Password", Toast.LENGTH_LONG).show();
                    editText_register_confirm_pwd.setError("password must be same");
                    editText_register_confirm_pwd.requestFocus();

                    //clearing the previous password

                    editText_register_pwd.clearComposingText();
                    editText_register_confirm_pwd.clearComposingText();
                } else if(TextUtils.isEmpty(textEnrollment)) {
                    Toast.makeText(registration_page.this, "please enter the Enrolment number", Toast.LENGTH_LONG).show();
                    editText_register_enrollment.setError("enrollment number is required is required");
                    editText_register_enrollment.requestFocus();
                } else if(textEnrollment.length()<10) {
                    Toast.makeText(registration_page.this, "re-enter the Enrolment number", Toast.LENGTH_LONG).show();
                    editText_register_enrollment.setError("please enter a valid enrollment");
                    editText_register_enrollment.requestFocus();
                    editText_register_enrollment.clearComposingText();
                }else if(TextUtils.isEmpty(textDob)) {
                    Toast.makeText(registration_page.this, "please enter the date of birth", Toast.LENGTH_LONG).show();
                    editText_register_dob.setError("date of birth is required");
                    editText_register_dob.requestFocus();
                }else if(radioGroup_register_gender.getCheckedRadioButtonId()== -1){
                    Toast.makeText(registration_page.this , "please select a gender" ,Toast.LENGTH_LONG).show();
                    radioButton_register_button_gender.setError("Gender is requires");
                    radioButton_register_button_gender.requestFocus();
                }
                else{
                    textGender = radioButton_register_button_gender.getText().toString();
                    progressBar_register.setVisibility(View.VISIBLE);
                    registerUser(textFullName , textEmail , textPwd  , textEnrollment , textDob , textGender, textBatch);
                }


            }

        });

    }

    private void registerUser(String textFullName, String textEmail, String textPwd, String textEnrollment, String textDob, String textGender , String textBatch) {
        //having a instance of firebase in auth variable
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(registration_page.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                    FirebaseUser firebaseUser = auth.getCurrentUser();
//                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
//                    assert firebaseUser != null;
//                    firebaseUser.updateProfile(profileChangeRequest);

                    //Enter user data into the firebase realtime database

                    ReadWriteUserDetail userDetail = new ReadWriteUserDetail(textFullName , textEnrollment, textDob, textGender,textBatch);


                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("registeredUsers");

                    referenceProfile.child(Objects.requireNonNull(firebaseUser).getUid()).setValue(userDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                //send email verification
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(registration_page.this, "user registration is successful", Toast.LENGTH_LONG).show();

                                // creating a intent and creating a  intent with same activity meaning
                                // closing and jumping back to same activity using flags

                Intent intent = new Intent(registration_page.this , LoginPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                            } else {
                                Toast.makeText(registration_page.this, "user registration failed ,please try again , ", Toast.LENGTH_LONG).show();

                            }

                            progressBar_register.setVisibility(View.GONE);
                        }
                    });



                }
                progressBar_register.setVisibility(View.GONE);
            }
        });
    }
}