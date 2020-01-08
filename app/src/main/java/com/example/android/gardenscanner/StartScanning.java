package com.example.android.gardenscanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.Service.LocationTrack;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class StartScanning extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String projectName;
    private String siteNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_scanning);
        final Button start = findViewById(R.id.start);
        final TextView textView = findViewById(R.id.textView2);
        projectName = getIntent().getStringExtra("projectName");
        siteNo = getIntent().getStringExtra("siteNo");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        SharedPreferences preferences =getSharedPreferences(projectName+siteNo+".txt", Context.MODE_PRIVATE);
        if(preferences.getBoolean("Running",false))
        {
            textView.setText("You Are Already In The Middle Of Scanning Process");
            start.setText("Resume Scanning");
        }
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = getCurrentLoctaion();
                Date currentTime = Calendar.getInstance().getTime();

                //long millis =12334567;
                SharedPreferences preferences =getSharedPreferences(projectName+siteNo+".txt", Context.MODE_PRIVATE);
                if(!preferences.getBoolean("Running",false))
                {
                    long millis = currentTime.getTime();
                    SharedPreferences sharedPreferences = getSharedPreferences("MyLogin.txt", Context.MODE_PRIVATE);
                    String username = sharedPreferences.getString("Username","admin");
                    Log.d("IN Start Scanning","in start scanning");
                    mDatabase.child("users").child(username).child("visits").child(projectName).child(siteNo).child(String.valueOf(millis)).child("Address").setValue(address);
                    Log.d("IN Start Scanning1","in start scanning");
                    mDatabase.child("users").child(username).child("visits").child(projectName).child(siteNo).child(String.valueOf(millis)).child("Date").setValue(String.valueOf(currentTime));
                    Log.d("IN Start Scanning2","in start scanning");
                    Intent intent = new Intent(StartScanning.this,gridActivity.class);
                    Log.d("IN Start Scanning3","in start scanning");
                    intent.putExtra("projectName",projectName);
                    intent.putExtra("siteNo",siteNo);
                    intent.putExtra("millis",String.valueOf(millis));
                    Log.d("IN Start Scanning4","in start scanning");
                    startActivity(intent);
                    Log.d("IN Start Scanning5","in start scanning");
                }
                else
                {

                    String millis = preferences.getString("millis","1234567");
                    Intent intent = new Intent(StartScanning.this,gridActivity.class);
                    intent.putExtra("projectName",projectName);
                    intent.putExtra("siteNo",siteNo);
                    intent.putExtra("millis",millis);
                    startActivity(intent);
                }
            }
        });
    }
    public String getCurrentLoctaion()
    {
        LocationTrack locationTrack = new LocationTrack(this);

        String address="";

        if (locationTrack.canGetLocation()) {


            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            address = getCompleteAddressString(latitude,longitude);
             Toast.makeText(this, "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {

            //locationTrack.showSettingsAlert();
            address = "Sangareddy";
        }
        return address;

    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction", "Canont get Address!");
        }
        return strAdd;
    }
}
