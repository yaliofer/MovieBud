package com.example.yali.grrrrrrrrrrrrrrrrr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference usersReference;
    private EditText nameBox;
    private EditText mailBox;
    private EditText passBox;
    private Button registerButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersReference = database.getReference().child("users");
        nameBox = (EditText)findViewById(R.id.nameBox);
        mailBox = (EditText)findViewById(R.id.mailBox);
        passBox = (EditText)findViewById(R.id.passBox);
        registerButton = (Button) findViewById(R.id.registerButton);
        progressDialog = new ProgressDialog(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
    }

    private void startRegister ()
    {
        final String name, passowrd, mail;
        name = nameBox.getText().toString().trim();
        mail = mailBox.getText().toString().trim();
        passowrd = passBox.getText().toString().trim();

        boolean ableToSignUp = !TextUtils.isEmpty(name) && !TextUtils.isEmpty(mail) && !TextUtils.isEmpty(passowrd);
        if (ableToSignUp)
        {
            progressDialog.setMessage("Signing Up");
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(mail, passowrd).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        String userID = mAuth.getCurrentUser().getUid();
                        DatabaseReference currentUserReference = usersReference.child(userID);
                        currentUserReference.child("Name").setValue(name);
                        currentUserReference.child("Page Number").setValue(1);

                        progressDialog.dismiss();

                        Intent begin = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(begin);
                    }
                    else
                    {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "User already exists for this email address", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}
