package com.example.android.gardenscanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("MyLogin.txt", Context.MODE_PRIVATE);
                Boolean loginCheck = sharedPreferences.getBoolean("FirstLogin", false);
                String username = sharedPreferences.getString("Username","admin");
                if(loginCheck)
                {
                    Intent mainIntent = new Intent(SplashActivity.this, ProjectsActivity.class);
                    mainIntent.putExtra("Username",username);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }
                else
                {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }
                /* Create an Intent that will start the Menu-Activity. */

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
