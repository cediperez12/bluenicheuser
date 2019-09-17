package com.coecs.bluenicheuser;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class ProfileViewer extends AppCompatActivity {

    private TextView etxt_name,etxt_subtext,etxt_jsr;
    private Toolbar toolbar;
    private LinearLayout post_layout;
    private ImageView imgv_userProfile;

    private String UID;

    private User worker;

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
        post_layout = findViewById(R.id.profile_view_posts_layout);
        toolbar = findViewById(R.id.profile_view_toolbar);
        imgv_userProfile = findViewById(R.id.profile_view_profile_Img);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        UID = getIntent().getExtras().getString("STRING_WORKER_UID");

        database = FirebaseDatabase.getInstance();
        authentication = FirebaseAuth.getInstance();

        database.getReference("USERS").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                worker = dataSnapshot.getValue(User.class);
                try{
                    etxt_name.setText(worker.getFirstname() + " " + worker.getLastname());
                    etxt_subtext.setText(worker.getWorkerProfession() + " " + worker.getWorkerCoverLocation() + " " +worker.getJob() + " Job Done");
                }catch (NullPointerException npe){
                    npe.printStackTrace();
                    new ErrorMessageCreator().createSimpleErrorMessage(ProfileViewer.this,"Error","There is no worker in this UID: " + UID);
                }
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

                        String jobtitle = etxt_jobtitle.getText().toString().trim();
                        String jobdesc = etxt_jobdesc.getText().toString().trim();

                        if(jobtitle.isEmpty() || jobdesc.isEmpty()){
                            new ErrorMessageCreator().createSimpleErrorMessage(ProfileViewer.this,"Job Description Send","You cannot send empty Job Description or without Job Title");
                        }else{
                            FirebaseUser currentuser = authentication.getCurrentUser();
                            createJobDescription(worker.getUid(),currentuser.getUid(),jobtitle,jobdesc);
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

    private void createJobDescription(String workerUID, String userUID, String jobtitle, String jobdesc){
        job_key = database.getReference("JOBS").push().getKey();

        job = new Job();
        job.setUser(userUID);
        job.setWorker(workerUID);
        job.setTitle(jobtitle);
        job.setJobdesc(jobdesc);
        job.setPushId(job_key);

        database.getReference("JOBS").child(job_key).setValue(job);

        conversation_key = database.getReference("CONVOS").push().getKey();

        addConversationToUser(workerUID,conversation_key);
        addConversationToUser(userUID,conversation_key);

        JobConversation jobConversation = new JobConversation();
        jobConversation.setJobId(job_key);
        jobConversation.setWorkerUID(workerUID);
        jobConversation.setUserUID(userUID);

        database.getReference("CONVOS").child(conversation_key).setValue(jobConversation);

        Message message = new Message();
        message.setFrom(userUID);
        message.setMessageType("FUNCTIONAL");
        message.setMessage("Job Title: " + jobtitle + "\nJob Description: " + jobdesc + "\nJob ID: " + job_key);
        message.setTimestamp(new Date().getTime());

        database.getReference("CONVOS").child(conversation_key).child("MESSAGES").push().setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(getApplicationContext(),Conversation.class);
                intent.putExtra("CONVOKEY",conversation_key);
                startActivity(intent);
            }
        });
    }

    private int addConversationToUser(String uid,String convokey){
        database.getReference("USERS/"+uid).child("CONVERSATIONS").push().setValue(convokey).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                i = task.isSuccessful() ? 1 : -1;
            }
        });

        return i;
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
        startActivity(new Intent(getApplicationContext(),Main.class));
        finish();
    }
}
