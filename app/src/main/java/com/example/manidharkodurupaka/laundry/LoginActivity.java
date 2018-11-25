package com.example.manidharkodurupaka.laundry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private EditText userEdt,passEdt;
    private Button loginBtn;
    private TextView registerTxt;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        userEdt=(EditText)findViewById(R.id.login_id);
        passEdt=(EditText)findViewById(R.id.login_password);
        loginBtn=(Button)findViewById(R.id.login_btn);
        registerTxt=(TextView)findViewById(R.id.login_register);
        progressDialog=new ProgressDialog(this);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });
        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snedUserToRegisterActivity();
            }
        });
    }

    private void snedUserToRegisterActivity() {
        Intent registerIntent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void LoginUser() {
        progressDialog.setTitle("Logging In.....");
        progressDialog.setMessage("Please wait while we login you....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String userId=userEdt.getText().toString();
        String pass=passEdt.getText().toString();
        if(TextUtils.isEmpty(userId)||TextUtils.isEmpty(pass)){
            Toast.makeText(LoginActivity.this, "Please fill the credentials", Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.signInWithEmailAndPassword(userId,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this,"Login Successful!",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();

                    }
                }
            });
        }
    }
}
