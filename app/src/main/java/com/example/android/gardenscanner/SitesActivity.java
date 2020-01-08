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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SitesActivity extends AppCompatActivity implements SitesAdapter.ItemClickListener{
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    ArrayList<String> sitesList = new ArrayList<>();
    SitesAdapter adapter;
    String projectName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sites);
        final RecyclerView sitesRecycler = findViewById(R.id.sites_recycler);
        projectName = getIntent().getStringExtra("projectName");
        DatabaseReference ref = database.getReference("projects").child(projectName);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Log.d("sitesList",ds.getKey());
                    sitesList.add(ds.getKey().toString());
                }
                sitesRecycler.setLayoutManager(new GridLayoutManager(SitesActivity.this,2));
                adapter = new SitesAdapter(SitesActivity.this, sitesList);
                adapter.setClickListener(SitesActivity.this);
                sitesRecycler.setAdapter(adapter);
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(SitesActivity.this, StartScanning.class);
        intent.putExtra("projectName",projectName);
        intent.putExtra("siteNo",adapter.getItem(position));
        startActivity(intent);
    }
}
