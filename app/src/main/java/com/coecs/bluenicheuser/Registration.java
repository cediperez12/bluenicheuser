package com.coecs.bluenicheuser;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase mDatabase;


    private EditText etxt_email,etxt_pass,etxt_fname,etxt_lname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();
    }

    private void init(){
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        etxt_email = findViewById(R.id.editText_email_register);
        etxt_pass = findViewById(R.id.editText_password_register);
        etxt_fname = findViewById(R.id.editText_firstname_register);
        etxt_lname = findViewById(R.id.editText_lastname_register);
    }

    private void toLogin(){
        startActivity(new Intent(getApplicationContext(),Login.class));
        this.finish();
    }

    ProgressDialog progressDialog;
    ErrorMessageCreator emc;

    public void onClickSignUp(View view){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("Signing Up"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);

        emc = new ErrorMessageCreator();

         // Display Progress Dialog
        new Thread(new Runnable() {
            @Override
            public void run() {
                String email = etxt_email.getText().toString().trim();
                String pass = etxt_pass.getText().toString().trim();
                final String fname = etxt_fname.getText().toString().trim();
                final String lname = etxt_lname.getText().toString().trim();

                if(email.isEmpty() || pass.isEmpty()){
                    emc.createSimpleErrorMessage(Registration.this,"Registration","Email and Password cannot be empty.");
                }else if(fname.isEmpty() || lname.isEmpty()){
                    emc.createSimpleErrorMessage(Registration.this,"Registration","First name and Last name cannot be empty.");
                }else{
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(final AuthResult authResult) {
                            User u = new User();
                            u.setFirstname(fname);
                            u.setLastname(lname);
                            u.setUid(authResult.getUser().getUid());
                            u.setUserType("USER");
                            u.setUserRate(0.0);
                            u.setJob(0);

                            mDatabase.getReference("USERS").child(u.getUid()).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        auth.signOut();
                                        emc.createSimpleMessage(Registration.this, "Registration", "Thank you for your registration, you may now log in to the application.", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                toLogin();
                                            }
                                        });
                                    }else{
                                        emc.createSimpleErrorMessage(Registration.this,"Registration",task.getException().getMessage());
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            emc.createSimpleErrorMessage(Registration.this,"Registration",e.getMessage());
                        }
                    });
                }
                progressDialog.show();
            }
        }).start();
    }

    public void onClickSignIn(View v){
        toLogin();
    }
}
