package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Register extends AppCompatActivity {
    EditText reg_username , reg_password,reg_phone,reg_email,reg_profession;
    Button nxt_btn;
    ProgressBar pb;
    FirebaseAuth fAuth;
    public String username_data_reg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg_username = findViewById(R.id.user_reg);
        reg_password = findViewById(R.id.password_reg);
        reg_password.setTypeface(Typeface.DEFAULT);
        reg_password.setTransformationMethod(new PasswordTransformationMethod());
        reg_phone = findViewById(R.id.user_ph);
        reg_email = findViewById(R.id.user_mail);
        reg_profession = findViewById(R.id.prof);
        nxt_btn = findViewById(R.id.button_next);
        fAuth = FirebaseAuth.getInstance();
        pb = findViewById(R.id.progressBar2);
        /*if(fAuth.getCurrentUser()!=null){

        }*/
        nxt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = reg_email.getText().toString().trim();
                String password = reg_password.getText().toString().trim();
                final String phone = "+91" + reg_phone.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    reg_email.setError("Email field is required!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    reg_password.setError("Password field is required!");
                    return;
                }
                if(TextUtils.isEmpty(phone)||phone.length()<10){
                    reg_phone.setError("Enter valid number!");
                }
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this,"User Registered!",Toast.LENGTH_SHORT).show();
                            String name = reg_username.getText().toString().trim();
                            String email = reg_email.getText().toString().trim();
                            String ph = reg_phone.getText().toString().trim();
                            String prof = reg_profession.getText().toString().trim();

                            DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                            DatabaseReference currentUserID = myDatabase.child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
                            currentUserID.child("username").setValue(name);
                            currentUserID.child("email").setValue(email);
                            currentUserID.child("phone").setValue(ph);
                            currentUserID.child("profession").setValue(prof);

                            Intent intent = new Intent(Register.this,VerifyNumber.class);
                            intent.putExtra("phone",phone);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(Register.this,"Registration Failed! Error!"+task.getException().getMessage() ,Toast.LENGTH_SHORT).show();

                        }
                    }
                });


            }
        });

    }
}
