package com.coecs.bluenicheuser;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText etxt_email,etxt_pass;

    private ErrorMessageCreator emc;

    private ProgressBar pgb_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();

        etxt_email = findViewById(R.id.editText_email_login);
        etxt_pass = findViewById(R.id.editText_password_login);
        pgb_login = findViewById(R.id.progressBar_login);

        emc = new ErrorMessageCreator();
    }

    public void onClickLogin(View v){
        pgb_login.setVisibility(View.VISIBLE);

        String email = etxt_email.getText().toString().trim();
        String pass = etxt_pass.getText().toString().trim();

        try{
            mAuth.signInWithEmailAndPassword(email,pass)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            startActivity(new Intent(getApplicationContext(),Main.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new ErrorMessageCreator().createSimpleErrorMessage(Login.this,"Login",e.getMessage());
                            pgb_login.setVisibility(View.INVISIBLE);
                        }
                    });
        }catch (IllegalArgumentException iae){
            new ErrorMessageCreator().createSimpleErrorMessage(Login.this,"Login",iae.getMessage());
            pgb_login.setVisibility(View.INVISIBLE);
        }catch (Exception ex){
            new ErrorMessageCreator().createSimpleErrorMessage(Login.this,"Login",ex.getMessage());
            pgb_login.setVisibility(View.INVISIBLE);
        }

    }

    public void onClickSignUp(View v){
        startActivity(new Intent(getApplicationContext(),Registration.class));
        this.finish();
    }
}
