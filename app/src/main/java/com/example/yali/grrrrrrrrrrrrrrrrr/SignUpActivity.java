package com.example.yali.grrrrrrrrrrrrrrrrr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
                    String userID = mAuth.getCurrentUser().getUid();
                    DatabaseReference currentUserReference = usersReference.child(userID);
                    currentUserReference.child("name").setValue(name);
                    progressDialog.dismiss();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }
    }
}
