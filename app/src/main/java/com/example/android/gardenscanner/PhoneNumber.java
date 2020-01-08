package com.example.android.gardenscanner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PhoneNumber extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference table_user = database.getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonenumber);
        final String username = getIntent().getStringExtra("Username");
        Log.d("Username",username);
        final EditText phoneNumber = findViewById(R.id.editText);
        Button verify = findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneNumber!=null&&phoneNumber.getText().toString().length()!=10)
                {
                    final ProgressDialog mDialog = new ProgressDialog(PhoneNumber.this);
                    mDialog.setMessage("Loading...");
                    mDialog.show();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child(username).exists())
                            {
                                String pass = dataSnapshot.child(username).child("phonenumber").getValue().toString();
                                mDialog.dismiss();
                                if(pass.equals(phoneNumber.getText().toString()))
                                {
                                    Intent intent = new Intent(PhoneNumber.this,OtpActivity.class);
                                    intent.putExtra("userName",username);
                                    intent.putExtra("phoneNumber",pass);
                                    startActivity(intent);
                                }
                            }
                            else
                            {
                                mDialog.dismiss();
                                Toast.makeText(PhoneNumber.this,"Please Enter Valid Details",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(PhoneNumber.this,"Please Enter Valid Phone Number",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
