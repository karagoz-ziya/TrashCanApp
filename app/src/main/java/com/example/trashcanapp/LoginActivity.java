package com.example.trashcanapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.trashcanapp.databinding.ActivityLoginBinding;
import com.example.trashcanapp.dbmodel.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity  {

    // TAGS
    private static final String TAG = "GOOGLE_SING_IN_TAG";

    // ViewBinding
    private ActivityLoginBinding binding;



    private String email = "", password = "";

    // actionbar
    private ActionBar actionBar;

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // configure action bar, title, backbutton
        actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.login_button));

        // get instance of firestore db
        db = FirebaseFirestore.getInstance();
        // get instance of firebase auth


        GoogleSignInAction();
        FirebaseSignInAction();


    }

    // Firebase Email ile giriş işlemlerinin yapıldığı metod
    private void FirebaseSignInAction() {


        checkUser();

        binding.signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

    binding.signInButton.setOnClickListener(view -> {
        // validate data
        validateData();
    });
    }

    private void checkUser() {
        // kullanici zaten giris yapmis mi
        // oyleyse dogrudan home activitye yonlendir
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            // kullanici onceden giris yapmis
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("signInMethod", 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();
        }
    }

    private void validateData() {
        // get data
        email = binding.emailEditText.getText().toString().trim();
        password =  binding.passwordEditText.getText().toString().trim();

        // validate data
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
            firebaseEmailLogin();
        }
    }

    private void firebaseEmailLogin() {
        binding.progressBar2.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // login basarili
                // kullanici bilgilerini getir
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                String email = firebaseUser.getEmail();
                Toast.makeText(LoginActivity.this, getString(R.string.logged_in_succesfuly) + "\n" + email, Toast.LENGTH_SHORT).show();

                // HomeActivity yonlendirmesi
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("signInMethod", 0);// home activity kisminda hangi signin metodu ile islem yapilacagini
                startActivity(intent);                        // belirlemek icin kkullanilir (0 --> firebase email signIn)
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // login basarisiz, hatayi goster
                binding.progressBar2.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    // Google ile giriş işlemlerinin yapıldığı metod
    private void GoogleSignInAction(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("604683046025-isq3clcbuajbgnf6svemfkg0lnffmqs2.apps.googleusercontent.com")
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);
        binding.googleSignInButton.setOnClickListener(view -> {
            SignInGoogle();
        });
    }

    // Google ile giriş intentinin dispatchlendiği method
    private void SignInGoogle() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 100);
    }

    // Google ile giriş yapma işlemleri sonucunu yakalayan
    //daha sonrasında yapılacak işlemleri konfigüre etmeye yarayan method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(signInAccountTask.isSuccessful()){
                // When google sign in successful
                // Initialize string
                String s="Google sign in successful";
                // Display Toast
                Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount=signInAccountTask
                            .getResult(ApiException.class);
                    // Check condition
                    if(googleSignInAccount!=null)
                    {
                        // When sign in account is not equal to null
                        // Initialize auth credential
                        AuthCredential authCredential= GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken()
                                        ,null);
                        // Check credential
                        firebaseAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // Check condition
                                        if(task.isSuccessful())
                                        {

                                            // HomeActivity yonlendirmesi
                                            Toast.makeText(LoginActivity.this, getString(R.string.logged_in_succesfuly) + "\n" + email, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                            intent.putExtra("signInMethod",1);// home activity kisminda hangi signin metodu ile islem yapilacagini
                                            startActivity(intent);                       // belirlemek icin kkullanilir (1 --> google signIn)
                                            finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                }
                catch (ApiException e)
                {
                    Log.wtf(TAG, String.valueOf(e));
                    Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            }


        }
    }

    public void writeNewUser(String userId, String nameSurname) {
        User user = new User( userId, nameSurname);
        CollectionReference dbUser = db.collection("User");
        dbUser.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(LoginActivity.this, "Product Added", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}