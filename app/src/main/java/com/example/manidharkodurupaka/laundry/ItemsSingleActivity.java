package com.example.manidharkodurupaka.laundry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ItemsSingleActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView textView;
    String items[]={"kurtis","shirts","pants","tshirts","pajama","bedsheet","lowers","shorts","pillowcover","towel","dupatta"};
    String values[]={};
    private DatabaseReference databaseReference;
    private TextView detailstxt;
    String key;
    Button modifybtn,updatebtn;
    //String user="0r2HDRdQmYef5SCZrZ2SGZPdX8z2";
    private FirebaseAuth mAuth;
    private Button deliveredbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_single);
        toolbar=(Toolbar)findViewById(R.id.itemssingle_app_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Receipt");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textView=(TextView)findViewById(R.id.itemssingle_textview);
        detailstxt=(TextView)findViewById(R.id.itemssingle_details);
        modifybtn=(Button)findViewById(R.id.itemssingle_modify);
        updatebtn=(Button)findViewById(R.id.itemssingle_update);
        deliveredbtn=(Button)findViewById(R.id.itemssingle_delivered);
        key=getIntent().getExtras().getString("key");
        mAuth=FirebaseAuth.getInstance();
        DatabaseReference adminReference=FirebaseDatabase.getInstance().getReference();
        adminReference.child("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!mAuth.getCurrentUser().getUid().toString().equals(dataSnapshot.child("uid").getValue().toString())){
                    modifybtn.setClickable(false);
                    updatebtn.setClickable(false);
                    modifybtn.setVisibility(View.INVISIBLE);
                    updatebtn.setVisibility(View.INVISIBLE);
                    deliveredbtn.setVisibility(View.INVISIBLE);
                    deliveredbtn.setClickable(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        final DatabaseReference finalDatabaseReference = databaseReference;
        databaseReference.child("Cart").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("status").getValue().toString().equals("completed")){
                    finalDatabaseReference.child("Notifications").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(key)){
                                deliveredbtn.setVisibility(View.INVISIBLE);
                                deliveredbtn.setClickable(false);
                            }
                            else {
                                deliveredbtn.setClickable(true);
                                deliveredbtn.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    deliveredbtn.setVisibility(View.INVISIBLE);
                    deliveredbtn.setClickable(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        deliveredbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference notificationReference=FirebaseDatabase.getInstance().getReference();
                notificationReference.child("Cart").child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        String d=dateFormat.format(date);
                        Map map=new HashMap();
                        map.put("date",d);
                        map.put("uid",dataSnapshot.child("uid").getValue().toString());
                        notificationReference.child("Notifications").child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    deliveredbtn.setVisibility(View.INVISIBLE);
                                    deliveredbtn.setClickable(false);
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        modifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itemsIntent=new Intent(ItemsSingleActivity.this,ItemsActivity.class);
                itemsIntent.putExtra("from","notfication");
                itemsIntent.putExtra("key",key);
                startActivity(itemsIntent);
                finish();
            }
        });
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog=new ProgressDialog(ItemsSingleActivity.this);
                progressDialog.setMessage("Updating status....");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                final Map map=new HashMap();
                map.put("status","completed");
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Cart").child(key).updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String uid=dataSnapshot.child("uid").getValue().toString();
                                DatabaseReference usersReference=FirebaseDatabase.getInstance().getReference();
                                usersReference.child("UsersCart").child(uid).child(key).updateChildren(map, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        DatabaseReference cartReference=FirebaseDatabase.getInstance().getReference();
                                        cartReference.child("Cart").child(key).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                progressDialog.dismiss();
                                                sendEmail(dataSnapshot.child("email").getValue().toString());
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        });
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Cart").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("status")){
                    if (dataSnapshot.child("status").getValue().toString().equals("completed")){
                        modifybtn.setClickable(false);
                        updatebtn.setClickable(false);
                        modifybtn.setVisibility(View.INVISIBLE);
                        updatebtn.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child("Items").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s="";
                for (String item:items){
                    s+=item+"    :     "+dataSnapshot.child(item).getValue().toString()+"\n";
                }
                textView.setText(s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child("Cart").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s="";
                s+="Name   :    "+dataSnapshot.child("name").getValue().toString()+"\n";
                s+="Enrollment No    :    "+dataSnapshot.child("enrollmentno").getValue().toString()+"\n";
                s+="Date    :     "+dataSnapshot.child("date").getValue().toString()+"\n";
                s+="Total     :      "+dataSnapshot.child("total").getValue().toString()+"\n";
                s+="Items     :      \n";
                detailstxt.setText(s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendEmail(String email) {
        //Log.i("Send email", "");
        String[] TO = {email};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            //Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ItemsSingleActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
