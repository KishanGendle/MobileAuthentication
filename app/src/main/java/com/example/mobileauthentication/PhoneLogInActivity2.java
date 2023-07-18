package com.example.mobileauthentication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.TimeUnit;

public class PhoneLogInActivity2 extends AppCompatActivity {
    EditText phone,otp;
    Button btnSndotp,btnVerifyotp;
    FirebaseAuth mAuth;
    String verificationID;
    ProgressBar bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_log_in2);
        phone = findViewById(R.id.PhoneNo);
        otp = findViewById(R.id.OTP);
        btnSndotp=findViewById(R.id.BtnSendOtp);
        bar = findViewById(R.id.Bar);
        btnVerifyotp = findViewById(R.id.BtnVerifyOtp);
        mAuth=FirebaseAuth.getInstance();

        btnSndotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(phone.getText().toString())){
                    phone.setError("Enter Valid Phone Number");
                    phone.requestFocus();
                }else{
                    String num = phone.getText().toString();
                    bar.setVisibility(view.VISIBLE);

                    sendCode(num);

                }

            }
        });
        btnVerifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(otp.getText().toString())){
                    otp.setError("Wrong OTP Entered");
                    otp.requestFocus();
                }else{
                    verifyCode(otp.getText().toString());
                }

            }
        });



    }
    private void sendCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            final String code = credential.getSmsCode();
            if(code!=null){
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(PhoneLogInActivity2.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            bar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onCodeSent(@NonNull String s,
                               PhoneAuthProvider.@NonNull ForceResendingToken token) {

            super.onCodeSent(s,token);
            verificationID = s;
            Toast.makeText(PhoneLogInActivity2.this, "OTP Sent", Toast.LENGTH_SHORT).show();
            btnSndotp.setVisibility(View.INVISIBLE);
            phone.setVisibility(View.INVISIBLE);
            btnVerifyotp.setEnabled(true);
            bar.setVisibility(View.INVISIBLE);
            btnVerifyotp.setVisibility(View.VISIBLE);
            otp.setVisibility(View.VISIBLE);


        }
    };

    private void verifyCode(String Code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,Code);
        SignCredential(credential);

    }

    private void SignCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(PhoneLogInActivity2.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PhoneLogInActivity2.this,HomeActivity2.class));
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!=null){
            startActivity(new Intent(PhoneLogInActivity2.this,HomeActivity2.class));
            finish();
        }
    }
}