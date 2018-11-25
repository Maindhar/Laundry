package com.example.manidharkodurupaka.laundry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText fnEdt,idEdt,enEdt,rnEdt,mnEdt,passEdt;
    private Button registerBtn;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private DatabaseReference db;
    public String[] uids={};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        toolbar=(Toolbar)findViewById(R.id.register_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fnEdt=(EditText)findViewById(R.id.register_fn);
        idEdt=(EditText)findViewById(R.id.register_id);
        enEdt=(EditText)findViewById(R.id.register_en);
        rnEdt=(EditText)findViewById(R.id.register_rn);
        mnEdt=(EditText)findViewById(R.id.register_mn);
        registerBtn=(Button)findViewById(R.id.register_btn);
        passEdt=(EditText)findViewById(R.id.register_pass);
        progressDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance().getReference();
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterUser();
            }
        });
    }

    private void RegisterUser() {
        progressDialog.setTitle("Registering");
        progressDialog.setMessage("Please wait while we register you.....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        final String pass=passEdt.getText().toString();
        final String id=idEdt.getText().toString();
        final String fn=fnEdt.getText().toString();
        final String en=enEdt.getText().toString().toUpperCase();
        final String rn=rnEdt.getText().toString();
        final String mn=mnEdt.getText().toString();
        if(TextUtils.isEmpty(fn) && TextUtils.isEmpty(en) && TextUtils.isEmpty(rn) && TextUtils.isEmpty(mn) && TextUtils.isEmpty(pass) && TextUtils.isEmpty(id)){
            Toast.makeText(RegisterActivity.this,"Please fill the credentials", Toast.LENGTH_LONG);
            progressDialog.dismiss();
        }
        else {
            mAuth.createUserWithEmailAndPassword(id,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        final String uid=mAuth.getCurrentUser().getUid().toString();
                        //SharedPreferences sharedPreferences=getSharedPreferences("UIDS", Context.MODE_PRIVATE);
                        //SharedPreferences.Editor editor=sharedPreferences.edit();
                        //editor.putString(en,uid);
                        //editor.apply();
                        Map map=new HashMap();
                        map.put("name",fn);
                        map.put("eno",en);
                        map.put("rno",rn);
                        map.put("mno",mn);
                        map.put("uid",uid);
                        map.put("email",id);
                        map.put("pass",pass);
                        db.child("Users").child(uid).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                db.child("Uids").child(en).child("uid").setValue(uid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }
}
