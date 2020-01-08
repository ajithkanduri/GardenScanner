package com.example.android.gardenscanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProjectsActivity extends AppCompatActivity implements ProjectsAdapter.ItemClickListener{
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    ArrayList<String> projectList = new ArrayList<>();
    ProjectsAdapter adapter;
    DatabaseReference table_user;
    ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        final RecyclerView projectsRecycler = findViewById(R.id.project_recycler);
        String username = getIntent().getStringExtra("Username");
         table_user = database.getReference("users").child(username).child("projects");
         valueEventListener =new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Log.d("val",ds.getValue(String.class));
                    projectList.add(ds.getValue(String.class));
                }
                projectsRecycler.setLayoutManager(new LinearLayoutManager(ProjectsActivity.this));
                adapter = new ProjectsAdapter(ProjectsActivity.this, projectList);
                adapter.setClickListener(ProjectsActivity.this);
                projectsRecycler.setAdapter(adapter);
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        table_user.addListenerForSingleValueEvent(valueEventListener);
        //
       // projectList.add("1");
    }

    @Override
    public void onItemClick(View view, int position) {
        table_user.removeEventListener(valueEventListener);
        Intent intent = new Intent(ProjectsActivity.this,SitesActivity.class);
        intent.putExtra("projectName",adapter.getItem(position));
        startActivity(intent);
    }
}
