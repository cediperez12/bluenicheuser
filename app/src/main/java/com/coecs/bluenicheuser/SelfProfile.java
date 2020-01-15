package com.coecs.bluenicheuser;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SelfProfile extends AppCompatActivity {

    FirebaseDatabase db;
    FirebaseAuth auth;

    ImageView sp_imgv_profile_img;
    TextView sp_tv_username;

    Toolbar sp_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_profile);

        sp_imgv_profile_img = findViewById(R.id.sp_imgv_profile_img);
        sp_tv_username = findViewById(R.id.sp_tv_username);
        sp_toolbar = findViewById(R.id.sp_toolbar);

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        db.getReference("USERS").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                sp_tv_username.setText(user.getFirstname() + " " + user.getLastname());

                switch (user.getUserProfileImage()){
                    case 1:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            sp_imgv_profile_img.setImageDrawable(getDrawable(R.drawable.job_logo_1));
                        }
                        break;

                    case 2:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            sp_imgv_profile_img.setImageDrawable(getDrawable(R.drawable.job_logo_2));
                        }
                        break;

                    case 3:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            sp_imgv_profile_img.setImageDrawable(getDrawable(R.drawable.job_logo_3));
                        }

                    default:
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });

        setSupportActionBar(sp_toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(this,Main.class);
        startActivity(intent);
        this.finish();
        super.onBackPressed();
    }
}
