package com.example.android.gardenscanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class gridActivity extends AppCompatActivity {
    ArrayList<Integer> list = new ArrayList<>();
    HashMap<String,String> states;
    HashMap<String,String> infected;
     String project_name;
     String site_no;
     String millis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        Log.d("Grid Activity","GridActivity");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        project_name = getIntent().getStringExtra("projectName");
        site_no = getIntent().getStringExtra("siteNo");
        millis = getIntent().getStringExtra("millis");
        Log.d("Grid Activity","GridActivity");
        states = loadMap("evaluate");
        Log.d("Grid Activity","GridActivit1");
        infected = loadMap("infected");
        Log.d("Grid Activity","GridActivit1");
        Button submit = findViewById(R.id.submit_data);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evaluateData();
            }
        });
        DatabaseReference ref = database.getReference("projects").child(project_name).child(site_no);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int rows = Integer.parseInt(dataSnapshot.child("rows").getValue().toString());
                int cols = Integer.parseInt(dataSnapshot.child("columns").getValue().toString());
                int f=0;
                if(states!=null&&!states.isEmpty())
                {
                    f=1;
                }
                for(int i=0;i<rows*cols;i++)
                {
                    list.add(i+1);
                    if(f==0) {
                        states.put(String.valueOf(i), "0");
                    }
                }
                if(f==0)
                {
                    for(int i=0;i<12;i++)
                    {
                        infected.put(String.valueOf(i), "0");
                    }
                }
                SharedPreferences preferences =getSharedPreferences(project_name+site_no+".txt", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("Running",true);
                editor.putString("millis",millis);
                editor.apply();
                saveMap("evaluate",states);
                saveMap("infected",infected);
                RecyclerView gridRecyclerView = findViewById(R.id.gridRecycler);
                gridRecyclerView.getRecycledViewPool().setMaxRecycledViews(0,10);
                gridRecyclerView.setItemViewCacheSize(10);
                gridRecyclerView.setLayoutManager(new GridLayoutManager(gridActivity.this,cols));
                gridAdapter adapter = new gridAdapter(gridActivity.this, list,project_name,site_no,millis);
                gridRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void evaluateData() {
        HashMap<String,String> map = loadMap("evaluate");
        int k=0;
        for(int i=0;i<list.size();i++)
        {
            if(map.get(String.valueOf(i)).equals("0"))
            {
                k++;
            }
        }
        if(k!=0)
        {
            Toast.makeText(this,"Please Fill all the Grids",Toast.LENGTH_LONG).show();
        }
        else
        {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(gridActivity.this);
            LayoutInflater li1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View customLayout1 = li1.inflate(R.layout.hardscape2, null);
            builder1.setView(customLayout1);
            final AlertDialog ad1 = builder1.show();
            Button submit = customLayout1.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText drip_pipes = customLayout1.findViewById(R.id.editText3);
                    EditText drippers = customLayout1.findViewById(R.id.editText4);
                    EditText frame_condition = customLayout1.findViewById(R.id.editText5);
                    EditText work_done = customLayout1.findViewById(R.id.editText6);
                    if(drip_pipes!=null&&drippers!=null&&frame_condition!=null&&work_done!=null)
                    {
                        if(!drip_pipes.getText().toString().isEmpty()&&!drippers.getText().toString().isEmpty()&&!frame_condition.getText().toString().isEmpty()
                        &&!work_done.getText().toString().isEmpty())
                        {
                            Intent intent = new Intent(gridActivity.this,ResultActivity.class);
                            intent.putExtra("projectName",project_name);
                            intent.putExtra("siteNo",site_no);
                            intent.putExtra("millis",millis);
                            Log.d("gridActivity",frame_condition.getText().toString());
                            intent.putExtra("frame_condition",frame_condition.getText().toString());
                            intent.putExtra("drip_damage",drip_pipes.getText().toString());
                            intent.putExtra("drippers",drippers.getText().toString());
                            intent.putExtra("work_done",work_done.getText().toString());
                            intent.putExtra("drinage","Good");//TODO: update it.
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(gridActivity.this,"Enter Details Properly",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(gridActivity.this,"Enter Details Properly",Toast.LENGTH_SHORT).show();
                    }
                }
            });
             //TODO:generate PDFs
//            Intent intent = new Intent(gridActivity.this,ResultActivity.class);
//            intent.putExtra("projectName",project_name);
//            intent.putExtra("siteNo",site_no);
//            intent.putExtra("millis",millis);
//            startActivity(intent);

        }
    }

    private void saveMap(String key, HashMap<String,String> inputMap){
        Log.d("Test1",project_name+site_no+millis+".txt");
        SharedPreferences pSharedPref = getSharedPreferences(project_name+site_no+millis+".txt", Context.MODE_PRIVATE);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove(key).commit();
            editor.putString(key, jsonString);
            editor.commit();
        }
    }
    private  HashMap<String,String> loadMap(String key){
        HashMap<String,String> outputMap = new HashMap<String,String>();
        Log.d("Test",project_name+site_no+millis+".txt");
        SharedPreferences pSharedPref = getSharedPreferences(project_name+site_no+millis+".txt", Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString(key, (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String k = keysItr.next();
                    String v = (String) jsonObject.get(k);
                    outputMap.put(k,v);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }
}
