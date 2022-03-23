package com.example.trashcanapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.trashcanapp.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    //ViewBinding
    ActivitySignupBinding binding;

    FirebaseAuth firebaseAuth;

    // progress dialog
    private ProgressDialog progressDialog;

    private String email = "", password = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        // configure progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.progress_dialog_please_wait));
        progressDialog.setMessage(getString(R.string.progress_dialog_creating_account));
        progressDialog.setCanceledOnTouchOutside(false);


        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }



    private void validateData() {
        email= binding.emailEditText.getText().toString().trim();
        password = binding.passwordEditText.getText().toString().trim();

        // email adresi valid degil ise devam etme
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEditText.setError(getString(R.string.invalid_email_error));
        }
        // password girilmemis ise
        else if(TextUtils.isEmpty(password)){
            binding.passwordEditText.setError(getString(R.string.empty_password_error));
        }
        // password uznlugu 6 karakterden kucuk ise
        else if(password.length()< 6){
            binding.passwordEditText.setError(getString(R.string.password_length_error));
        }
        else{
            // data valide edilmistir, signup islemi baslangici
            firebaseEmailSignUp();
        }
    }

    private void firebaseEmailSignUp() {
        // progressi gosteren kod
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // signup basarili ise buraya girer
                progressDialog.dismiss();

                // user info alan kod
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                String email = firebaseUser.getEmail();
                Toast.makeText(
                        SignupActivity.this,
                        getString(R.string.account_created_succesfuly) + "\n" + email,
                        Toast.LENGTH_SHORT);

                // HomeActivity yonlendirmesi
                startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // signup basarili degil ise buraya girer
                progressDialog.dismiss();
                Toast.makeText(SignupActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}