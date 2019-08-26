package com.coecs.bluenicheuser;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText etxt_email,etxt_pass;

    private ErrorMessageCreator emc;

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

        emc = new ErrorMessageCreator();
    }

    public void onClickLogin(View v){
        String email = etxt_email.getText().toString().trim();
        String pass = etxt_pass.getText().toString().trim();

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
                        emc.createSimpleErrorMessage(Login.this,"Login",e.getMessage());
                    }
                });
    }

    public void onClickSignUp(View v){
        startActivity(new Intent(getApplicationContext(),Registration.class));
        this.finish();
    }
}
