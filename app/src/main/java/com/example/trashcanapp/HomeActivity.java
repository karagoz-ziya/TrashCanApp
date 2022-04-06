package com.example.trashcanapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.trashcanapp.constants.BINTYPE;
import com.example.trashcanapp.databinding.ActivityHomeBinding;
import com.example.trashcanapp.dbmodel.RecycleBin;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    // TAGS
    private static final String TAG = "DB_CONTROL";

    // view binding
    private ActivityHomeBinding binding;

    // actionbar
    private ActionBar actionBar;

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private GoogleSignInAccount googleAccount;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // configure action bar, title, backbutton
        actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.login_button));

        // Initialize firebase auth
        firebaseAuth=FirebaseAuth.getInstance();

        // Initialize firebase user
         firebaseUser=firebaseAuth.getCurrentUser();

        // Check condition
        if(firebaseUser != null)
        {
            String email = firebaseUser.getEmail();
            binding.mail.setText(email);
        }

        // Initialize sign in client
        gsc= GoogleSignIn.getClient(HomeActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        binding.logout.setOnClickListener(view -> {
            // Sign out from google
            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // Check condition
                    if(task.isSuccessful())
                    {
                        // When task is successful
                        // Sign out from firebase
                        firebaseAuth.signOut();

                        // Display Toast
                        Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();

                        // Finish activity
                        finish();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));

                    }
                }
            });
        });

        // Deneme RecycleBin databse kaydedici
        binding.dbButton.setOnClickListener(view -> {
            AddRecycleBinToDB();

        });

    }

    private void AddRecycleBinToDB() {
        db = FirebaseFirestore.getInstance();
        RecycleBin recycleBin = new RecycleBin();
        recycleBin.setBinType(BINTYPE.BATTERY);
        Log.i(TAG,  recycleBin.getBinType().toString());
        CollectionReference dbUser = db.collection("RecycleBin");
        dbUser.add(recycleBin).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                Toast.makeText(HomeActivity.this, "Product Added", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}