package com.example.yali.grrrrrrrrrrrrrrrrr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class LogInActivity extends AppCompatActivity {

    private ArrayList<String> anonNames;
    private EditText emailLogin;
    private EditText passwordLogin;
    private Button loginButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        emailLogin = (EditText)findViewById(R.id.emailSignIn);
        passwordLogin = (EditText)findViewById(R.id.passwordSignIn);
        loginButton = (Button)findViewById(R.id.buttonSignIn);
        mAuth = FirebaseAuth.getInstance();

        anonNames = new ArrayList<>();
        anonNames.add("NOY AND HADAR");
        anonNames.add("ELIAD MALKI");
        anonNames.add("MASHIHAH");
        anonNames.add("YOTAM HARRARI");
        anonNames.add("ZALMAN?");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startLogin();
            }
        });
    }

    private void startLogin ()
    {
        String email, pass;
        email = emailLogin.getText().toString().trim();
        pass = passwordLogin.getText().toString().trim();

        if (anonNames.contains(email))
        {
            mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }
        else
        {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Signing Up");
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Log.i("Error in Log In", task.getException().toString());
                        Toast.makeText(getApplicationContext(), "Error in Log-In", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
