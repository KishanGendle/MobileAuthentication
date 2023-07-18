package com.example.mobileauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity2 extends AppCompatActivity {

    public static final String TAG = "TAG";
    TextView alredyHaveAcc;
    EditText inputEmail,inputPassword,inputConformPass;
    Button btnRegister;
    ProgressDialog progressDialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+";
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore firestore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        alredyHaveAcc = findViewById(R.id.AlreadyAcc);
        inputEmail = findViewById(R.id.InputMail);
        inputPassword = findViewById(R.id.InputPassword);
        inputConformPass = findViewById(R.id.InputConformPassword);
        btnRegister = findViewById(R.id.btnReset);
        firestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();



        alredyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity2.this,MainActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewRegister();
            }
        });
    }

    private void NewRegister() {
       String email = inputEmail.getText().toString();
       String password = inputPassword.getText().toString();
       String ConfrimPass = inputConformPass.getText().toString();
       if(!email.matches(emailPattern)){
           inputEmail.setError("Enter Valid Email");
           inputEmail.requestFocus();
       }else if(password.isEmpty()){
           inputPassword.setError("Please Enter Password");
           inputPassword.requestFocus();
       }else if(password.length()<6){
           inputPassword.setError("Please Enter Valid Password");
           inputPassword.requestFocus();
       }else if(!password.equals(ConfrimPass)){
           inputConformPass.setError("Password does not match in both the fields");
           inputConformPass.requestFocus();
       }else{
           progressDialog.setMessage("Please Wait While Registration....");
           progressDialog.setTitle("Registration");
           progressDialog.setCanceledOnTouchOutside(false);
           progressDialog.show();

           mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task) {

                   if(task.isSuccessful()){

                       progressDialog.dismiss();
                       NextActivity();
                       Toast.makeText(RegisterActivity2.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                       userId = mAuth.getCurrentUser().getUid();
                       DocumentReference documentReference = firestore.collection("users").document(userId);
                       Map<String,Object> user = new HashMap<>();
                       user.put("email",email);
                       user.put("password",password);
                       documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                              Log.d(TAG,"onSuccess: user profile is created for "+userId);
                           }
                       });
                   }else{
                       progressDialog.dismiss();
                       Toast.makeText(RegisterActivity2.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                   }
               }
           });
       }
    }

    private void NextActivity() {
        Intent intent = new Intent(RegisterActivity2.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |  Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}