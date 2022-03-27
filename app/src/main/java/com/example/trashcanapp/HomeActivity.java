package com.example.trashcanapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.trashcanapp.databinding.ActivityHomeBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    // TAGS
    private static final String TAG = "FIREBASE_SING_IN_TAG";

    // view binding
    private ActivityHomeBinding binding;

    // actionbar
    private ActionBar actionBar;



    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    GoogleSignInAccount googleAccount;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // configure action bar, title, backbutton
        actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.login_button));


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Integer signInMethod = extras.getInt("signInMethod");

            switch(signInMethod) {
                case 0:
                    GetFirebaseAccountUser();
                    break;
                case 1:
                    GetGoogleAccountUser();
                    break;
                default:
            }
        }
        binding.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });
    }

    // Email ile giris yapilan kullanicini bilgilerini almak icin
    private void GetFirebaseAccountUser() {

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignOutFirebase();
            }
        });
    }

    private void checkUser() {
        // kullanici loggedin mi diye kontrol et, degil ise LoginActivitye yonlendir

        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            // kullanici logged in degil LoginActivitye yonlendir
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else {
            String email = firebaseUser.getEmail();
            binding.mail.setText(email);
        }
    }

    // Google ile giris yapilan kullanicini bilgilerini almak icin
    private void GetGoogleAccountUser() {

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        googleAccount = GoogleSignIn.getLastSignedInAccount(this);

        // google hesabi var
        if(googleAccount!= null){
            String Name = googleAccount.getDisplayName();
            String Mail = googleAccount.getEmail();
            Log.i(TAG,Name + Mail);
            binding.name.setText(Name);
            binding.mail.setText(Mail);
        }

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignOutGoogle();
            }
        });
    }

    private void SignOutFirebase() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    private void SignOutGoogle() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }
}