package com.example.android.gardenscanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference table_user = database.getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText username =findViewById(R.id.username);
        final EditText password = findViewById(R.id.editText2);
        TextView forgotPassword = findViewById(R.id.textView);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username!=null)
                {
                    if(username.getText().toString().isEmpty())
                    {
                        Toast.makeText(MainActivity.this,"Please Enter Username",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent = new Intent(MainActivity.this, PhoneNumber.class);
                        //intent.putExtra("PhoneNumber",String.valueOf(stringBuilder));
                        intent.putExtra("Username",username.getText().toString());
                        startActivity(intent);
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Please Enter Username",Toast.LENGTH_SHORT).show();
                }


            }
        });
        Button login = findViewById(R.id.generate);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                if(username==null)
                {
                    Toast.makeText(MainActivity.this,"Please Enter Username",Toast.LENGTH_SHORT).show();
                }
                else if(username.getText().toString().isEmpty())
                {
                    Toast.makeText(MainActivity.this,"Please Enter Username",Toast.LENGTH_SHORT).show();
                }
                else if(password == null)
                {
                    Toast.makeText(MainActivity.this,"Please Enter Password",Toast.LENGTH_SHORT).show();
                }
                else if(password.getText().toString().isEmpty())
                {
                    Toast.makeText(MainActivity.this,"Please Enter Password",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mDialog.setMessage("Loading...");
                    mDialog.show();
                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child(username.getText().toString()).exists())
                            {
                                Log.d("username",username.getText().toString());
                                String pass = dataSnapshot.child(username.getText().toString()).child("password").getValue().toString();
                                mDialog.dismiss();
                                if(pass.equals(password.getText().toString()))
                                {
                                    SharedPreferences preferences =getSharedPreferences("MyLogin.txt", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("Username",username.getText().toString());
                                    editor.putBoolean("FirstLogin",true);
                                    editor.apply();

                                    Intent intent = new Intent(MainActivity.this,ProjectsActivity.class);
                                    //intent.putExtra("PhoneNumber",String.valueOf(stringBuilder));
                                    intent.putExtra("Username",username.getText().toString());
                                    startActivity(intent);
                                }
                            }
                            else
                            {
                                mDialog.dismiss();
                                Toast.makeText(MainActivity.this,"Please Enter Valid Details",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        });
    }
}
