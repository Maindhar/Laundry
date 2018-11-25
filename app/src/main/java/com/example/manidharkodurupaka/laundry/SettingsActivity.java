package com.example.manidharkodurupaka.laundry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView nametxt,entxt,rntxt;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    String user="0r2HDRdQmYef5SCZrZ2SGZPdX8z2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.settings_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nametxt=(TextView)findViewById(R.id.settings_textView);
        entxt=(TextView)findViewById(R.id.settings_textView2);
        rntxt=(TextView)findViewById(R.id.tsettins_extView3);
        mAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        if (!mAuth.getCurrentUser().getUid().toString().equals(user)){
            databaseReference.child("Users").child(mAuth.getCurrentUser().getUid().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    nametxt.setText("Name:"+dataSnapshot.child("name").getValue().toString());
                    entxt.setText("Enrollment Number:"+dataSnapshot.child("eno").getValue().toString());
                    rntxt.setText("Room Number:"+dataSnapshot.child("rno").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
