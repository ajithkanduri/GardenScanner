package com.example.android.gardenscanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OtpView;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class OtpActivity extends AppCompatActivity {
    private OtpView otpView;
    private String verificationid;
    private FirebaseAuth mAuth;
    private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        String phonenumber = getIntent().getStringExtra("phoneNumber").toString();
        userName = getIntent().getStringExtra("userName");
        mAuth = FirebaseAuth.getInstance();
        otpView = findViewById(R.id.otp_view);
        sendVerificationCode(phonenumber);
        findViewById(R.id.verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = otpView.getText().toString().trim();

                if ((code.isEmpty() || code.length() < 6)){

                    otpView.setError("Enter code...");
                    otpView.requestFocus();
                    return;
                }
                else {
                    verifyCode(code);
                }

            }
        });
    }
    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(OtpActivity.this,ProjectsActivity.class);
                            i.putExtra("userName",userName);
                            startActivity(i);

                            Toast.makeText(OtpActivity.this,"OTP Verified",Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(OtpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(OtpActivity.this, PhoneNumber.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("Status","Not Successful");
                            startActivity(intent);


                        }
                    }

                });
    }
    private void sendVerificationCode(String number){
        Log.d("sendingOTP","sending");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationid = s;
            Log.d("OTP",verificationid);
          //  mResendToken = forceResendingToken;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
//                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OtpActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();

        }
    };
}
