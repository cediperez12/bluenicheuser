package com.coecs.bluenicheuser;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ProfileViewer extends AppCompatActivity {

    private TextView etxt_name,etxt_subtext,etxt_jsr,tv_pv_review;
    private Toolbar toolbar;
    private LinearLayout post_layout;
    private ImageView imgv_userProfile;

    private String UID;

    private User worker;
    User jobUser;

    private FirebaseDatabase database;
    private FirebaseAuth authentication;

    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);
        init();
    }

    private void init(){
        etxt_name = findViewById(R.id.profile_view_name);
        etxt_subtext = findViewById(R.id.profile_view_subtext);
        etxt_jsr = findViewById(R.id.profile_view_jsr);
        post_layout = findViewById(R.id.ll_pv_review);
        toolbar = findViewById(R.id.profile_view_toolbar);
        tv_pv_review = findViewById(R.id.tv_pv_review);
        imgv_userProfile = findViewById(R.id.profile_view_profile_Img);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        UID = getIntent().getExtras().getString("STRING_WORKER_UID");

        database = FirebaseDatabase.getInstance();
        authentication = FirebaseAuth.getInstance();

        database.getReference("USERS").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                worker = dataSnapshot.getValue(User.class);
                try{

                    int randomProfile = worker.getUserProfileImage();

                    if(randomProfile == 0){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            imgv_userProfile.setImageDrawable(getDrawable(R.drawable.job_logo_1));
                        }
                    }else if(randomProfile == 1){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            imgv_userProfile.setImageDrawable(getDrawable(R.drawable.job_logo_2));
                        }
                    }else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            imgv_userProfile.setImageDrawable(getDrawable(R.drawable.job_logo_3));
                        }
                    }

                    if(worker.getWokrerCurrentStatus() == null){
                        database.getReference("USERS").child(UID).child("wokrerCurrentStatus").setValue("AVAILABLE");
                    }

                    etxt_name.setText(worker.getFirstname() + " " + worker.getLastname());

                    if(worker.getWokrerCurrentStatus().equalsIgnoreCase("ON WORK")){
                        etxt_subtext.setText(worker.getWorkerProfession() + " - " + worker.getWorkerCoverLocation() + " - " + "On Work");
                    }else{
                        etxt_subtext.setText(worker.getWorkerProfession() + " - " + worker.getWorkerCoverLocation() + " - " + "Currently Available");
                    }

                }catch (NullPointerException npe){
                    npe.printStackTrace();
                    new ErrorMessageCreator().createSimpleErrorMessage(ProfileViewer.this,"Error","There is no worker in this UID: " + UID);
                }

                database.getReference("USERS").child(worker.getUid()).child("REVIEW").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        post_layout.removeAllViews();

                        if(dataSnapshot.getChildrenCount() > 0){
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                Review rev = ds.getValue(Review.class);

                                View v = LayoutInflater.from(ProfileViewer.this).inflate(R.layout.review_layout,null,false);

                                ImageView review_layout_sender_img = v.findViewById(R.id.review_layout_sender_img);
                                TextView review_layout_sender_name_txtv = v.findViewById(R.id.review_layout_sender_name_txtv);
                                TextView review_layout_sender_activity_txtv = v.findViewById(R.id.review_layout_sender_activity_txtv);
                                TextView review_layout_sender_rating_txtv = v.findViewById(R.id.review_layout_sender_rating_txtv);

                                review_layout_sender_name_txtv.setText(rev.getFromUser().getFirstname() + " " + rev.getFromUser().getLastname());
                                review_layout_sender_activity_txtv.setText(rev.getReview());
                                review_layout_sender_rating_txtv.setText("Rated " + rev.getRating() + " stars from the job[" + rev.getFromJobTitle() + "]");

                                post_layout.addView(v);
                            }
                        }else{
                            tv_pv_review.setText("This user has no Reviews yet.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                database.getReference("USERS").child(UID).child("CurriculumVitae").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        CurriculumVitae cv = dataSnapshot.getValue(CurriculumVitae.class);

                        if(cv == null){
                            CurriculumVitae createCv = new CurriculumVitae();

                            List<String> skillList = new ArrayList<>();
                            skillList.add("Plumbing");

                            createCv.setSkills((ArrayList)skillList);

                            CurriculumVitae.CollegeInfo createCI = new CurriculumVitae.CollegeInfo();
                            createCI.setCourse("Livelihood");
                            createCI.setTertiary("Senior High School");
                            createCI.setYear("2014");
                            createCI.setUniversity("STI College Meycauayan");

                            createCv.setCollegeInfo(createCI);
                            createCv.setObjective("To be able to have money because I'm so broke. Both in heart and money.");

                            cv = createCv;
                        }

                        TextView objective_tv = findViewById(R.id.tv_pv_objective);
                        TextView skills_tv = findViewById(R.id.tv_pv_skills);
                        TextView education_tv = findViewById(R.id.tv_pv_education);

                        String skills = "";
                        for(String i : cv.getSkills()){
                            skills+=(i+"\n");
                            Log.d("SKILLS:",i);
                        }

                        CurriculumVitae.CollegeInfo ci = cv.getCollegeInfo();

                        String education = ci.getCourse()
                                + "\n"
                                + ci.getTertiary()
                                + "\n"
                                + ci.getYear()
                                + "\n"
                                + ci.getUniversity();

                        education.trim();

                        objective_tv.setText(cv.getObjective());
                        skills_tv.setText(skills);
                        education_tv.setText(education);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });

        database.getReference("USERS").child(authentication.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jobUser = dataSnapshot.getValue(User.class);
                Log.d("CURRENT USER",jobUser.getFirstname() + " " + jobUser.getLastname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });

    }

    public void onClickGiveJob(View view){
        final View v = LayoutInflater.from(this).inflate(R.layout.send_job_desc_layout,null);
        AlertDialog alertdialog = new AlertDialog.Builder(this)
                .setTitle("Send Job Description to this Worker")
                .setView(v)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etxt_jobtitle = v.findViewById(R.id.etxt_job_title_send_job_desc);
                        EditText etxt_jobdesc = v.findViewById(R.id.etxt_job_desc_send_job_desc);
                        EditText etxt_pay = v.findViewById(R.id.etxt_job_payment_send_job_desc);

                        String jobtitle = etxt_jobtitle.getText().toString().trim();
                        String jobdesc = etxt_jobdesc.getText().toString().trim();
                        double pay = Double.parseDouble(etxt_pay.getText().toString().trim());

                        if(jobtitle.isEmpty() || jobdesc.isEmpty()){
                            new ErrorMessageCreator().createSimpleErrorMessage(ProfileViewer.this,"Job Description Send","You cannot send empty Job Description or without Job Title");
                        }else{
                            FirebaseUser currentuser = authentication.getCurrentUser();
                            createJobDescription(worker.getUid(),currentuser.getUid(),jobtitle,jobdesc,pay);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setCancelable(false)
                .create();
        alertdialog.show();
    }

    Job job;
    String conversation_key,job_key;

    User fetchUser;

    private User fetchPeople(String workerUID){
        fetchUser = new User();
//        database.getReference("USERS").child(workerUID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User currentUser = dataSnapshot.getValue(User.class);
//                fetchUser.setFirstname(currentUser.getFirstname());
//                fetchUser.setLastname(currentUser.getLastname());
//                Log.d("USER - FETCH PEOPLE",fetchUser.getFirstname() + " " + fetchUser.getLastname());
//                Log.d("WORKING-FETCH PEOPLE","FETCH-PEOPLE DATA CHANGE");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("ERROR - FETCH PEOPLE",databaseError.getMessage());
//            }
//        });

        database.getReference("USERS").child(workerUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d("USER - FETCH PEOPLE 2",fetchUser.getFirstname() + " " + fetchUser.getLastname());
        return fetchUser;
    }



    private void createJobDescription(String workerUID, String userUID, String jobtitle, String jobdesc, double pay){
        //NEW CODE
        DatabaseReference newJobReference = database.getReference("JOB").push();

        Job job = new Job(newJobReference.getKey()); //New Job

        //Job Main Information
        job.setJobTitle(jobtitle);
        job.setJobDescription(jobdesc);
        job.setPay(pay);

        //Job People Involve
        job.setUser(jobUser);
        job.setWorker(worker);

        addConversationToUser(userUID,job.getPushId());
        addConversationToUser(workerUID,job.getPushId());

        //Default Setting
        job.setStatus("PENDING");

        newJobReference.setValue(job);

        Message primaryMessage = new Message();
        primaryMessage.setMessageType("FUNCTIONAL");
        primaryMessage.setMessage(job.getUser().getFirstname() + " " + job.getUser().getLastname() + " Create a Job[" + job.getJobTitle() + "] offer for " + job.getWorker().getFirstname() + " " + job.getWorker().getLastname());
        primaryMessage.setTimestamp(new Date().getTime());

        newJobReference.child("CONVERSATION").push().setValue(primaryMessage);

        Intent intent = new Intent();
        intent.putExtra("JOBKEY",job.getPushId());
        intent.setClass(this,Conversation.class);
        startActivity(intent);
        finish();

    }

    private void addConversationToUser(String uid,String jobKey){
        database.getReference("USERS").child(uid).child("JOBS").push().setValue(jobKey);
    }

//    private int addConversationToUser(String uid,String convokey){
//        database.getReference("USERS/"+uid).child("CONVERSATIONS").push().setValue(convokey).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                i = task.isSuccessful() ? 1 : -1;
//            }
//        });
//
//        return i; OLD CODE
//    }

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
        startActivity(new Intent(getApplicationContext(),Main.class));
        finish();
    }
}
