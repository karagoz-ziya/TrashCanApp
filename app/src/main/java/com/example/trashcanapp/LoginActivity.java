package com.example.trashcanapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
public class LoginActivity extends AppCompatActivity  {

    // TAGS
    private static final String TAG = "GOOGLE_SING_IN_TAG";

   SignInButton google_sign_in_button;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        GoogleSignInAction();

        // Configuring map
        /*button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });*/
    }

    // Google ile giriş işlemlerinin yapıldığı metod
    private void GoogleSignInAction(){
        google_sign_in_button = findViewById(R.id.google_sign_in_button);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);
        google_sign_in_button.setOnClickListener(view -> {
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
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                HomeActivity();
            } catch (ApiException e) {
                Log.i(TAG, String.valueOf(e));
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //  Anasayfa'ya geçiş için kullanılan method
    private void HomeActivity(){
        finish();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }
}