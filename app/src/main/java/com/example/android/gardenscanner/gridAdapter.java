package com.example.android.gardenscanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class gridAdapter extends RecyclerView.Adapter<gridAdapter.ViewHolder> {
    private  Context context;
    private  ArrayList<Integer> list;
    private HashMap<String,String> states;
    private LayoutInflater mInflater;
    private String projectName;
    private String site_no;
    private String millis;
    HashMap<String,String> infected;
    gridAdapter(Context context, ArrayList<Integer> list,String projectName,String site_no,String millis)
    {
        this.context = context;
        this.list = list;
        this.mInflater = LayoutInflater.from(context);
        this.millis = millis;
        this.site_no = site_no;
        this.projectName = projectName;
        states = loadMap("evaluate");
    }
    @NonNull
    @Override
    public gridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.grid_cell, parent, false);
        ViewHolder vh = new gridAdapter.ViewHolder(view);
        vh.setIsRecyclable(false);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final gridAdapter.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        holder.myTextView.setText(String.valueOf(list.get(position)));
        if(states.get(String.valueOf(position)).equals("0"))
        {
            holder.myTextView.setBackgroundColor(Color.parseColor("#5CDB95"));
        }
        else if(states.get(String.valueOf(position)).equals("1"))
        {
            holder.myTextView.setBackgroundColor(Color.parseColor("#379683"));
        }
        else {
            holder.myTextView.setBackgroundColor(Color.parseColor("#F44336"));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(states.get(String.valueOf(position)).equals("0"))
                {
                    holder.myTextView.setBackgroundColor(Color.parseColor("#379683"));
                    states.put(String.valueOf(position),"1");
                    saveMap("evaluate",states);
                }
                else if(states.get(String.valueOf(position)).equals("1"))
                {
                    holder.myTextView.setBackgroundColor(Color.parseColor("#5CDB95"));
                    states.put(String.valueOf(position),"0");
                    saveMap("evaluate",states);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d("Long Click",list.get(position).toString());

               // notifyDataSetChanged();
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View customLayout = li.inflate(R.layout.custom_layout, null);
                builder.setView(customLayout);
                final AlertDialog ad = builder.show();
                states.put(String.valueOf(position),"2");
                Button softscape = customLayout.findViewById(R.id.button3);
                Button hardscape = customLayout.findViewById(R.id.button4);
                hardscape.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onHardscapeClicked(ad,holder,position);
                    }
                });
                softscape.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        LayoutInflater li1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View customLayout1 = li1.inflate(R.layout.softscape, null);
                        builder1.setView(customLayout1);
                        final AlertDialog ad1 = builder1.show();
                        Button submit = customLayout1.findViewById(R.id.button2);
                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                int f=0;
                                CheckBox pestInfected = customLayout1.findViewById(R.id.pest);
                                CheckBox disease = customLayout1.findViewById(R.id.disease);
                                CheckBox nutrient = customLayout1.findViewById(R.id.nutient);
                                CheckBox water = customLayout1.findViewById(R.id.water);
                                CheckBox temp = customLayout1.findViewById(R.id.temporary);
                                CheckBox perm = customLayout1.findViewById(R.id.permanent);
                                SharedPreferences sharedPreferences = context.getSharedPreferences("MyLogin.txt", Context.MODE_PRIVATE);
                                String username = sharedPreferences.getString("Username","admin");
                                String plant_no = holder.myTextView.getText().toString();
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(username).child("visits").child(projectName).child(site_no).child(String.valueOf(millis)).child("Defective");
                                if(pestInfected.isChecked()||disease.isChecked()||nutrient.isChecked()||water.isChecked()||temp.isChecked()||perm.isChecked())
                                {
                                    f=1;
                                }
                                if(f==1)
                                {
                                    infected = loadMap("infected");
                                    holder.myTextView.setBackgroundColor(Color.parseColor("#F44336"));
                                    states.put(String.valueOf(position),"2");
                                    saveMap("evaluate",states);
                                    ad.dismiss();
                                    ad1.dismiss();
                                    if(pestInfected.isChecked())
                                    {
                                        mDatabase.child(plant_no).child("pest_infected").setValue("YES");
                                        String updated = String.valueOf(Integer.parseInt(infected.get("0"))+1);
                                        infected.put("0",updated);
                                        saveMap("infected",infected);
                                    }
                                    else
                                    {
                                        mDatabase.child(plant_no).child("pest_infected").setValue("NO");
                                    }
                                    if(disease.isChecked())
                                    {
                                        mDatabase.child(plant_no).child("disease_infected").setValue("YES");
                                        mDatabase.child(plant_no).child("pest_infected").setValue("YES");
                                        String updated = String.valueOf(Integer.parseInt(infected.get("1"))+1);
                                        infected.put("1",updated);
                                        saveMap("infected",infected);
                                    }
                                    else
                                    {
                                        mDatabase.child(plant_no).child("disease_infected").setValue("YES");
                                    }
                                    if(nutrient.isChecked())
                                    {
                                        mDatabase.child(plant_no).child("nutrient_deficiency").setValue("YES");
                                        mDatabase.child(plant_no).child("pest_infected").setValue("YES");
                                        String updated = String.valueOf(Integer.parseInt(infected.get("2"))+1);
                                        infected.put("2",updated);
                                        saveMap("infected",infected);
                                    }
                                    else
                                    {
                                        mDatabase.child(plant_no).child("nutrient_deficiency").setValue("NO");
                                    }
                                    if(water.isChecked())
                                    {
                                        mDatabase.child(plant_no).child("water_deficiency").setValue("YES");
                                        mDatabase.child(plant_no).child("pest_infected").setValue("YES");
                                        String updated = String.valueOf(Integer.parseInt(infected.get("3"))+1);
                                        infected.put("3",updated);
                                        saveMap("infected",infected);
                                    }
                                    else
                                    {
                                        mDatabase.child(plant_no).child("water_deficiency").setValue("NO");
                                    }
                                    if(temp.isChecked())
                                    {
                                        mDatabase.child(plant_no).child("temporary_wilting").setValue("YES");
                                        mDatabase.child(plant_no).child("pest_infected").setValue("YES");
                                        String updated = String.valueOf(Integer.parseInt(infected.get("4"))+1);
                                        infected.put("4",updated);
                                        saveMap("infected",infected);
                                    }
                                    else
                                    {
                                        mDatabase.child(plant_no).child("temporary_wilting").setValue("NO");
                                    }
                                    if(perm.isChecked())
                                    {
                                        mDatabase.child(plant_no).child("permanent_wilting").setValue("YES");
                                        mDatabase.child(plant_no).child("pest_infected").setValue("YES");
                                        String updated = String.valueOf(Integer.parseInt(infected.get("5"))+1);
                                        infected.put("5",updated);
                                        saveMap("infected",infected);
                                    }
                                    else
                                    {
                                        mDatabase.child(plant_no).child("permanent_wilting").setValue("NO");
                                    }
                                }
                                else
                                {
                                    Toast.makeText(context,"Please Enter Valid Submission",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView myTextView;
       // LinearLayout linearLayout;
        ViewHolder(View itemView) {
            super(itemView);
            //linearLayout = itemView.findViewById(R.id.layout);
            myTextView = itemView.findViewById(R.id.info_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
    private void onHardscapeClicked(final AlertDialog ad, final ViewHolder holder, final int position)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        LayoutInflater li1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View customLayout1 = li1.inflate(R.layout.hardscape, null);
        builder1.setView(customLayout1);
        final AlertDialog ad1 = builder1.show();
        Button submit = customLayout1.findViewById(R.id.button2);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioGroup rg = customLayout1.findViewById(R.id.rg);
                RadioGroup rg1 = customLayout1.findViewById(R.id.rg1);
                RadioGroup rg2 = customLayout1.findViewById(R.id.rg2);
                if(rg.getCheckedRadioButtonId()==-1)
                {
                    Toast.makeText(context,"Please Choose Pot's Condition",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    int selectedId = rg.getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton) customLayout1.findViewById(selectedId);
                    if(rg1.getCheckedRadioButtonId()==-1)
                    {
                        Toast.makeText(context,"Please Choose Medium Condition",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        int selectedId1 = rg1.getCheckedRadioButtonId();
                        RadioButton radioButton1 = (RadioButton) customLayout1.findViewById(selectedId1);
                        if(rg2.getCheckedRadioButtonId()==-1)
                        {
                            Toast.makeText(context,"Please Choose Watering Condition",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            ad.dismiss();
                            ad1.dismiss();
                            int selectedId2 = rg2.getCheckedRadioButtonId();
                            RadioButton radioButton2 = (RadioButton) customLayout1.findViewById(selectedId2);
                            if(radioButton.getText().equals("Good")&&radioButton1.getText().equals("Good")&&radioButton2.getText().equals("Good"))
                            {
                                holder.myTextView.setBackgroundColor(Color.parseColor("#379683"));
                                states.put(String.valueOf(position),"1");
                                saveMap("evaluate",states);
                            }
                            else
                            {
                                holder.myTextView.setBackgroundColor(Color.parseColor("#F44336"));
                                states.put(String.valueOf(position),"2");
                                saveMap("evaluate",states);
                            }
                            if(radioButton.getText().equals("Missing"))
                            {
                                String updated = String.valueOf(Integer.parseInt(infected.get("6"))+1);
                                infected.put("6",updated);
                                saveMap("infected",infected);
                            }
                            if(radioButton.getText().equals("Broken"))
                            {
                                String updated = String.valueOf(Integer.parseInt(infected.get("7"))+1);
                                infected.put("7",updated);
                                saveMap("infected",infected);
                            }
                            if(radioButton1.getText().equals("Average"))
                            {
                                String updated = String.valueOf(Integer.parseInt(infected.get("8"))+1);
                                infected.put("8",updated);
                                saveMap("infected",infected);
                            }
                            if(radioButton1.getText().equals("Poor"))
                            {
                                String updated = String.valueOf(Integer.parseInt(infected.get("9"))+1);
                                infected.put("9",updated);
                                saveMap("infected",infected);
                            }
                            if(radioButton2.getText().equals("Average"))
                            {
                                String updated = String.valueOf(Integer.parseInt(infected.get("10"))+1);
                                infected.put("10",updated);
                                saveMap("infected",infected);
                            }
                            if(radioButton2.getText().equals("Poor"))
                            {
                                String updated = String.valueOf(Integer.parseInt(infected.get("11"))+1);
                                infected.put("11",updated);
                                saveMap("infected",infected);
                            }
                        }
                    }
                }


            }
        });
    }
    private void saveMap(String key, HashMap<String,String> inputMap){
        Log.d("Test2",projectName+site_no+millis+".txt");
        SharedPreferences pSharedPref = context.getSharedPreferences(projectName+site_no+millis+".txt", Context.MODE_PRIVATE);
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
        Log.d("Test3",projectName+site_no+millis+".txt");
        HashMap<String,String> outputMap = new HashMap<String,String>();
        SharedPreferences pSharedPref = context.getSharedPreferences(projectName+site_no+millis+".txt", Context.MODE_PRIVATE);
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
