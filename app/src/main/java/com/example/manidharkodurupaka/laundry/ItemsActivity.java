package com.example.manidharkodurupaka.laundry;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

public class ItemsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private RecyclerView recyclerView;
    private int total=0;
    private TextView total_tv;
    private Button done_btn;
    private Map cartmap=new HashMap();
    private Map itemsmap=new HashMap();
    private String from;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        from=getIntent().getStringExtra("from");
        mAuth=FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance().getReference().child("Laundry_Items");
        recyclerView=(RecyclerView)findViewById(R.id.items_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        total_tv=(TextView)findViewById(R.id.items_total);
        done_btn=(Button)findViewById(R.id.items_btn);
        final ProgressDialog progressDialog=new ProgressDialog(ItemsActivity.this);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SharedPreferences sharedPreferences=getSharedPreferences("UIDS", Context.MODE_PRIVATE);
                //sharedPreferences.edit().clear().commit();
                //final String uid=sharedPreferences.getString(editText.getText().toString().toUpperCase(),"");
                cartmap.put("total",String.valueOf(total));
                if (from.equals("main")){
                    AlertDialog.Builder alertbuilder=new AlertDialog.Builder(ItemsActivity.this);
                    final View mView=getLayoutInflater().inflate(R.layout.alert_box_layout,null);
                    alertbuilder.setView(mView);
                    alertbuilder.setMessage("Enter details to finish");
                    final AlertDialog alertDialog=alertbuilder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    final EditText editText=(EditText)mView.findViewById(R.id.abl_en);
                    alertDialog.show();
                    Button button=(Button)mView.findViewById(R.id.abl_submit);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressDialog.setMessage("Adding data......");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            final DatabaseReference userscartReference=FirebaseDatabase.getInstance().getReference();
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date();
                            final String d=dateFormat.format(date);
                            cartmap.put("date",d);
                            cartmap.put("enrollmentno",editText.getText().toString());
                            cartmap.put("status","pending");
                            userscartReference.child("Uids").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final String uid=dataSnapshot.child(editText.getText().toString().toUpperCase()).child("uid").getValue().toString();
                                    cartmap.put("uid",uid);
                                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot) {
                                            cartmap.put("name",dataSnapshot.child("name").getValue().toString());
                                            cartmap.put("email",dataSnapshot.child("email").getValue().toString());
                                            DatabaseReference cartReference=FirebaseDatabase.getInstance().getReference().child("Cart");
                                            cartReference.child(d).setValue(cartmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    DatabaseReference itemsReference=FirebaseDatabase.getInstance().getReference().child("Items");
                                                    itemsReference.child(d).setValue(itemsmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            userscartReference.child("UsersCart").child(uid).child(d).setValue(cartmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    progressDialog.dismiss();
                                                                    Intent mainIntent=new Intent(ItemsActivity.this,MainActivity.class);
                                                                    startActivity(mainIntent);
                                                                    alertDialog.dismiss();
                                                                    finish();
                                                                }
                                                            });
                                                        }
                                                    });
                                                }});
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

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
                else {

                    final String d=getIntent().getStringExtra("key");
                    progressDialog.setMessage("Updating.....");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    final DatabaseReference usercartReference=FirebaseDatabase.getInstance().getReference();
                    usercartReference.child("Cart").child(d).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String id=dataSnapshot.child("uid").getValue().toString();
                            Toast.makeText(ItemsActivity.this,id,Toast.LENGTH_LONG).show();
                            usercartReference.child("UsersCart").child(id).child(d).updateChildren(cartmap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    DatabaseReference cartReference=FirebaseDatabase.getInstance().getReference().child("Cart");
                                    cartReference.child(d).updateChildren(cartmap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            DatabaseReference itemsReference=FirebaseDatabase.getInstance().getReference().child("Items");
                                            itemsReference.child(d).updateChildren(itemsmap, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    progressDialog.dismiss();
                                                    //Intent mainIntent=new Intent(ItemsActivity.this,MainActivity.class);
                                                    //startActivity(mainIntent);
                                                    finish();
                                                }
                                            });
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
            }
        });
    }
    @Override
    public void onStart() {
        String items[]={"kurtis","shirts","pants","tshirts","pajama","bedsheet","lowers","shorts","pillowcover","towel","dupatta"};
        for (final String item:items){
            if (from.equals("main")){
                db.child(item).child("value").setValue("0");
                //cartmap.put(item,"0");
                itemsmap.put(item,"0");
            }
            else {
                final String key=getIntent().getStringExtra("key");
                final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Items").child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(item)){
                            db.child(item).child("value").setValue(dataSnapshot.child(item).getValue().toString());
                            itemsmap.put(item,dataSnapshot.child(item).getValue().toString());
                            total+=Integer.parseInt(dataSnapshot.child(item).getValue().toString());
                            total_tv.setText(String.valueOf(total));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        super.onStart();
        FirebaseRecyclerAdapter<Item,ItemsActivity.ItemViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Item, ItemsActivity.ItemViewHolder>(
                Item.class,
                R.layout.layout_item,
                ItemsActivity.ItemViewHolder.class,
                db
        ) {
            @Override
            protected void populateViewHolder(final ItemsActivity.ItemViewHolder viewHolder, Item model, int position) {
                final String post_key=getRef(position).getKey().toString();
                viewHolder.setName(model.getName());
                viewHolder.setValue(model.getValue());
                if (total>=10){
                    Toast.makeText(ItemsActivity.this,"Maximum limit reached",Toast.LENGTH_LONG).show();
                }
                viewHolder.addbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        db.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("NewApi")
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (total>=10){
                                    Toast.makeText(ItemsActivity.this,"Maximum limit reached",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    String key=dataSnapshot.child("name").getValue().toString();
                                    String value=String.valueOf(Integer.parseInt(dataSnapshot.child("value").getValue().toString())+1);
                                    db.child(post_key).child("value").setValue(String.valueOf(Integer.parseInt(dataSnapshot.child("value").getValue().toString())+1));
                                    total+=1;
                                    total_tv.setText(String.valueOf(total));
                                    //cartmap.put(key,value);
                                    itemsmap.put(key,value);
                                    //Toast.makeText(ItemsActivity.this,key,Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                viewHolder.minusbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        db.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("NewApi")
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int cv=Integer.parseInt(dataSnapshot.child("value").getValue().toString());
                                if(cv>0){
                                    String key=dataSnapshot.child("name").getValue().toString();
                                    String value=String.valueOf(Integer.parseInt(dataSnapshot.child("value").getValue().toString())-1);
                                    db.child(post_key).child("value").setValue(String.valueOf(Integer.parseInt(dataSnapshot.child("value").getValue().toString())-1));
                                    total-=1;
                                    total_tv.setText(String.valueOf(total));
                                    if (Integer.parseInt(value)!=0){
                                        //cartmap.put(key,value);
                                        itemsmap.put(key,value);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        View view;
        ImageButton addbtn,minusbtn;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            addbtn=(ImageButton)view.findViewById(R.id.item_add);
            minusbtn=(ImageButton)view.findViewById(R.id.item_minus);
        }

        public void setName(String name){
            TextView itemtv= (TextView)view.findViewById(R.id.item_name);
            itemtv.setText(name);
        }

        public void setValue(String value){
            TextView itemvaluetv=(TextView)view.findViewById(R.id.item_value);
            itemvaluetv.setText(value);
        }

    }

}
