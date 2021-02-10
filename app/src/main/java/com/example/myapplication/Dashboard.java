package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dashboard extends AppCompatActivity implements CategoryAdapter.ItemClickListener{

    FirebaseAuth fAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    String uid;
    String user_name,ph;
    List<Category> categoryList;
    RecyclerView recyclerView;
    CategoryAdapter adapter;

    FirebaseAuth mAuth;
    DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

       // ref=user.getUid();


        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(this,2));


        categoryList = new ArrayList<>();


        //adding some items to our list
        categoryList.add( new Category("Groceries", R.drawable.groceries));
        categoryList.add( new Category("Home", R.drawable.hm));
        categoryList.add( new Category("Meetings", R.drawable.meetings));
        categoryList.add( new Category("Personal details", R.drawable.personaldetails));
        categoryList.add( new Category("Miscellaneous", R.drawable.miscellaneous));
        categoryList.add( new Category("Upload Image", R.drawable.img));


        adapter = new CategoryAdapter(this, categoryList);

        adapter.setClickListener(this);

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);
    }



    @Override
    public void onItemClick(View view,int position) {

        String text=categoryList.get(position).getText();
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
        //pass the item to inventory activity
       if(text.equals("Groceries")){
       Intent intent=new Intent(this, Inventory.class);
        intent.putExtra("category",text);
        startActivity(intent);}
       if(text.equals("Personal details")){
           fAuth = FirebaseAuth.getInstance();
           user = fAuth.getCurrentUser();
           assert user != null;
           uid  = user.getUid();
           databaseReference = FirebaseDatabase.getInstance().getReference();
           databaseReference.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   user_name = Objects.requireNonNull(snapshot.child("users").child(uid).child("email").getValue()).toString();
                   ph = Objects.requireNonNull(snapshot.child("users").child(uid).child("profession").getValue()).toString();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {
                   Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();

               }
           });
           Toast.makeText(getApplicationContext(),ph,Toast.LENGTH_LONG).show();



           Intent intent2=new Intent(this, Profile2.class);
           startActivity(intent2);
       }
       if(text.equals("Upload Image")){
           startActivity(new Intent(this,AddImg.class));
       }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.search:
                break;

            case R.id.share:
                break;

            case R.id.notification:
                startActivity(new Intent(this,Reminder.class));
                break;

            case R.id.about:
                break;

            case R.id.logout:
                mAuth.signOut();
                Intent intent = new Intent(this, Title.class);
                startActivity(intent);
                break;

        }

        return true;
    }




    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, Title.class));
        }
    }


}
