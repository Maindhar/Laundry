package com.example.manidharkodurupaka.laundry;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchActivity extends AppCompatActivity {
    private EditText searchedt;
    private ImageButton searchbtn;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
         searchedt=(EditText)findViewById(R.id.search_edt);
         searchbtn=(ImageButton) findViewById(R.id.search_btn);
         recyclerView=(RecyclerView)findViewById(R.id.search_recyclerview);
         recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String eno=searchedt.getText().toString().toUpperCase();
                if (!TextUtils.isEmpty(eno)){
                    databaseReference.child("Uids").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(eno)){
                                String uid=dataSnapshot.child(eno).child("uid").getValue().toString();
                                Toast.makeText(SearchActivity.this,uid,Toast.LENGTH_LONG).show();
                                DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("UsersCart").child(uid);
                                FirebaseRecyclerAdapter<Search,SearchViewHoler> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Search, SearchViewHoler>(
                                        Search.class,
                                        R.layout.notification_fragment_layout,
                                        SearchViewHoler.class,
                                        reference
                                ) {
                                    @Override
                                    protected void populateViewHolder(SearchViewHoler viewHolder, Search model, final int position) {
                                        viewHolder.setEnrollmentNo(model.getEnrollmentno());
                                        viewHolder.setName(model.getName());
                                        viewHolder.setTotal(model.getTotal());
                                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                final Intent itemsSingleIntent=new Intent(SearchActivity.this,ItemsSingleActivity.class);
                                                itemsSingleIntent.putExtra("key",getRef(position).getKey().toString());
                                                startActivity(itemsSingleIntent);
                                                //Toast.makeText(getContext(),key,Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                };
                                recyclerView.setAdapter(firebaseRecyclerAdapter);
                            }
                            else {
                                recyclerView.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
    public static class SearchViewHoler extends RecyclerView.ViewHolder{
        View view;
        public SearchViewHoler(@NonNull View itemView) {
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
