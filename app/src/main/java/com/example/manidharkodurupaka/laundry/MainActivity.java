package com.example.manidharkodurupaka.laundry;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference db;
    private FloatingActionButton fab;
    private TextView statustxt;
    private RecyclerView recyclerView;
    private String userId;
    String user="0r2HDRdQmYef5SCZrZ2SGZPdX8z2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=(Toolbar)findViewById(R.id.main_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Laundry");
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        recyclerView=(RecyclerView)findViewById(R.id.main_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itemsIntent=new Intent(MainActivity.this,ItemsActivity.class);
                itemsIntent.putExtra("from","main");
                itemsIntent.putExtra("key","");
                startActivity(itemsIntent);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(mUser==null){
            Intent loginIntnet=new Intent(MainActivity.this,LoginActivity.class);
            loginIntnet.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntnet);
            finish();
        }
        else {
            final String userId=mAuth.getCurrentUser().getUid().toString();
            DatabaseReference adminReference=FirebaseDatabase.getInstance().getReference();
            adminReference.child("Admin").addValueEventListener(new ValueEventListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.child("uid").getValue().toString().equals(userId)){
                        fab.setVisibility(View.INVISIBLE);
                        fab.setClickable(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            if (mAuth.getCurrentUser().getUid().toString().equals(user)){
                db=FirebaseDatabase.getInstance().getReference().child("Cart");
            }
            else {
                db= FirebaseDatabase.getInstance().getReference().child("UsersCart").child(mAuth.getCurrentUser().getUid().toString());
            }
            statustxt=(TextView)findViewById(R.id.main_status);
            final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("User status")){
                        statustxt.setText("Status:"+dataSnapshot.child("User status").getValue().toString());
                    }
                    else {
                        databaseReference.child("User status").setValue("Closed");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("User status")){
                        statustxt.setText("Status:"+dataSnapshot.child("User status").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            FirebaseRecyclerAdapter<UserItem,UserItenViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<UserItem, UserItenViewHolder>(
                    UserItem.class,
                    R.layout.notification_fragment_layout,
                    UserItenViewHolder.class,
                    db
            ) {
                @Override
                protected void populateViewHolder(final UserItenViewHolder viewHolder, final UserItem model, int position) {
                    viewHolder.setTotal(model.getTotal());
                    final String key=getRef(position).getKey().toString();
                    DatabaseReference adminReference=FirebaseDatabase.getInstance().getReference();
                    adminReference.child("Admin").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (userId.equals(dataSnapshot.child("uid").getValue().toString())){
                                viewHolder.setEnrollmentNo(model.getEnrollmentno());
                                viewHolder.setName(model.getName());
                            }
                            else {
                                viewHolder.setEnrollmentNo(model.getStatus());
                                viewHolder.setName(model.getDate());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    viewHolder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Intent itemsSingleIntent=new Intent(MainActivity.this,ItemsSingleActivity.class);
                            itemsSingleIntent.putExtra("key",key);
                            startActivity(itemsSingleIntent);
                            Toast.makeText(MainActivity.this,key,Toast.LENGTH_LONG).show();
                        }
                    });
                    viewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            DatabaseReference adminReference=FirebaseDatabase.getInstance().getReference();
                            adminReference.child("Admin").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (userId.equals(dataSnapshot.child("uid").getValue().toString())){
                                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
                                        reference.child("Cart").child(key).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild("status")){
                                                    if (!dataSnapshot.child("status").getValue().toString().equals("completed")){
                                                        CharSequence options[]=new CharSequence[]{
                                                                "Delete",
                                                                "Update"
                                                        };
                                                        final android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(MainActivity.this);
                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                if (which==0){
                                                                    final android.app.AlertDialog.Builder alertDialog=new android.app.AlertDialog.Builder(MainActivity.this);
                                                                    builder.setMessage("Do you want to delete?")
                                                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    final ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
                                                                                    progressDialog.setMessage("Deleting.......");
                                                                                    progressDialog.setCanceledOnTouchOutside(false);
                                                                                    progressDialog.show();
                                                                                    final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
                                                                                    databaseReference.child("Items").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            databaseReference.child("Cart").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                    databaseReference.child("UsersCart").child(dataSnapshot.child("uid").getValue().toString()).child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            databaseReference.child("Cart").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                    progressDialog.dismiss();
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
                                                                            })
                                                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {

                                                                                }
                                                                            });
                                                                    builder.show();
                                                                }
                                                                else if (which==1){
                                                                    final ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
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
                                                                                            progressDialog.dismiss();
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
                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            return true;
                        }
                    });
                }
            };
            recyclerView.setAdapter(firebaseRecyclerAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        DatabaseReference adminReference=FirebaseDatabase.getInstance().getReference();
        adminReference.child("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("uid").getValue().toString().equals(mAuth.getCurrentUser().getUid().toString())){
                    getMenuInflater().inflate(R.menu.main_menu,menu);
                }
                else {
                    getMenuInflater().inflate(R.menu.student_main_menu,menu);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.Search_main){
            Intent searchIntent=new Intent(MainActivity.this,SearchActivity.class);
            startActivity(searchIntent);
        }
        if (item.getItemId()==R.id.Status_main){
            CharSequence options[]=new CharSequence[]{
              "Open","Close"
            };
            final AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Updating......");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
                    if (which==0){
                        databaseReference.child("User status").setValue("Open").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                            }
                        });
                    }
                    else if (which==1){
                        databaseReference.child("User status").setValue("Closed").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            });
            builder.show();
        }
        if (item.getItemId()==R.id.Logout_main){
            mAuth.signOut();
            Intent loginIntnet=new Intent(MainActivity.this,LoginActivity.class);
            loginIntnet.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntnet);
            finish();
        }
        if (item.getItemId()==R.id.Settings__main){
            Intent settingsIntent=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }
    public static class UserItenViewHolder extends RecyclerView.ViewHolder{
        View view;
        public UserItenViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
        }
        public void setTotal(String total){
            TextView txttotal=(TextView)view.findViewById(R.id.notification_total);
            txttotal.setText(total);
        }
        public void setEnrollmentNo(String enrollmentNo){
            TextView entxt=(TextView)view.findViewById(R.id.enrollment_number);
            entxt.setText(enrollmentNo);
        }
        public void setName(String name){
            TextView nametxt=(TextView)view.findViewById(R.id.user_name);
            nametxt.setText(name);
        }
    }
}
