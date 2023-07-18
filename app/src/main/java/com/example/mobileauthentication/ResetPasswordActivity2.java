package com.example.mobileauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity2 extends AppCompatActivity {

    Button btnReset;
    EditText eEmail;
    ProgressBar bar;
    FirebaseAuth mAuth;
    String Email;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password2);

        btnReset = findViewById(R.id.btnReset);
        eEmail = findViewById(R.id.InputMail);
        bar = findViewById(R.id.bar);
        mAuth=FirebaseAuth.getInstance();
        
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Email = eEmail.getText().toString().trim();
                if(!Email.matches(emailPattern)){
                    Toast.makeText(ResetPasswordActivity2.this, "Enter Valid Password", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(Email)) {
                    eEmail.setError("Email field can't be empty");
                    eEmail.requestFocus();
                }else {
                    ResetPassword();
                }
            }
        });
    }

    private void ResetPassword() {
        bar.setVisibility(View.VISIBLE);
        btnReset.setVisibility(View.INVISIBLE);
        mAuth.sendPasswordResetEmail(Email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ResetPasswordActivity2.this, "Reset Password link has been sent to your registered Email", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPasswordActivity2.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |  Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ResetPasswordActivity2.this, "Error:-"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        bar.setVisibility(View.VISIBLE);
                        btnReset.setVisibility(View.VISIBLE);
                    }
                });
    }
}