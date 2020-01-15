package com.coecs.bluenicheuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    FirebaseDatabase database;

    NavigationView navigationView;
    DrawerLayout drawer;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        init();

    }

    private void init(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        if(currentUser == null){
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        }else{
            getSupportActionBar().setTitle("Dashboard");
            getSupportFragmentManager().beginTransaction().replace(R.id.fr_replacer_c_main,new dashboard_fr()).commit();

            database.getReference("USERS").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    UpdateUserRating(user);

//                    if(user.getUserProfileImage() == -1){
//                        database.getReference("USERS").child(currentUser.getUid()).child("userProfileImage").setValue(new Random().nextInt(3));
//                    }
//
//                    ImageView img = navigationView.findViewById(R.id.nav_header_img);
//                    TextView mainText = navigationView.findViewById(R.id.nav_header_main_text);
//                    TextView subText = navigationView.findViewById(R.id.nav_header_sub_text);
//
//                    mainText.setText(user.getFirstname() + " " + user.getLastname());
//                    subText.setText("Job Given: " + user.getJob());
//
//                    switch (user.getUserProfileImage()){
//                        case 1:
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                img.setImageDrawable(getDrawable(R.drawable.job_logo_1));
//                            }
//                            break;
//
//                        case 2:
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                img.setImageDrawable(getDrawable(R.drawable.job_logo_2));
//                            }
//                            break;
//
//                        case 3:
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                img.setImageDrawable(getDrawable(R.drawable.job_logo_3));
//                            }
//
//                        default:
//                            break;
//                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    ProgressDialog pd;

    private void UpdateUserRating(final User user){
        pd = new ProgressDialog(Main.this);
        database.getReference("JOB").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                Main.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.show();
                        int count = user.getJob();
                        double rates = user.getUserRate();
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            Job job = ds.getValue(Job.class);
                            if(job.getUser().getUid().equalsIgnoreCase(currentUser.getUid())){
                                if(job.getStatus().equalsIgnoreCase("DONE") && job.getUserRatingConfirmed() == 0){
                                    count++;
                                    rates += job.getUserRating();
                                    database.getReference("JOB").child(job.getPushId()).child("userRatingConfirmed").setValue(1);
                                }
                            }
                        }
                        database.getReference("USERS").child(currentUser.getUid()).child("userRate").setValue(rates);
                        database.getReference("USERS").child(currentUser.getUid()).child("job").setValue(count);
                        pd.dismiss();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){

            case R.id.nav_search:
                getSupportActionBar().setTitle("Search");
                getSupportFragmentManager().beginTransaction().replace(R.id.fr_replacer_c_main,new search_fr()).commit();
                break;

            case R.id.nav_dashboard:
                getSupportActionBar().setTitle("Dashboard");
                getSupportFragmentManager().beginTransaction().replace(R.id.fr_replacer_c_main,new dashboard_fr()).commit();
                break;

            case R.id.nav_logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
                break;

            case R.id.nav_message:
                getSupportActionBar().setTitle("Messages");
                getSupportFragmentManager().beginTransaction().replace(R.id.fr_replacer_c_main,new messages_fr()).commit();
                break;

            case R.id.nav_user_profile:
                Intent intent = new Intent();
                intent.setClass(this,SelfProfile.class);
                startActivity(intent);
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
