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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Conversation extends AppCompatActivity {

    private LinearLayout layout_message;
    private EditText messageBox;
    private Toolbar toolbar;
    private Button btnSend;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private String conversationUID;
    private ScrollView scrollView;

    private Job job;

    private User worker;

    private boolean isErrorMessageShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        init();
    }

    private void init(){
        conversationUID = getIntent().getExtras().getString("JOBKEY");

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        layout_message = findViewById(R.id.conversation_linear_layout_message);
        messageBox = findViewById(R.id.etxt_conversation_messagebox);
        btnSend = findViewById(R.id.button5);
        toolbar = findViewById(R.id.conversation_toolbar);
        scrollView = findViewById(R.id.scrollView2);

        database.getReference("JOB").child(conversationUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Job j = dataSnapshot.getValue(Job.class);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(j.getJobTitle());

                if(j.getStatus().equalsIgnoreCase("DONE") || j.getStatus().equalsIgnoreCase("NULL")){
                    messageBox.setEnabled(false);
                    btnSend.setEnabled(false);

                    if(!isErrorMessageShown){
                        Toast.makeText(Conversation.this,"This Job Offer is already Done or Nullified. It means that you can visit your conversations, but you cannot message anymore.",Toast.LENGTH_SHORT).show();
                        isErrorMessageShown = true;
                    }
                }

                setJob(j);

                if(j.getStatus().equalsIgnoreCase("DONE") && j.getWorkerRating() == 0 && !isErrorMessageShown){
                    final View rateView = LayoutInflater.from(Conversation.this).inflate(R.layout.rating_layout,null,false);
                    TextView mainText = rateView.findViewById(R.id.textView9_ratingLayout);
                    TextView subText = rateView.findViewById(R.id.textView5_ratingLayout);

                    mainText.setText("How did " + job.getWorker().getFirstname() + " do?");
                    subText.setText("Did you already rate your worker? Please give him 1 if he failed to do a job.");

                    AlertDialog ratingDialog = new AlertDialog.Builder(Conversation.this)
                            .setTitle("Rate the worker")
                            .setView(rateView)
                            .setPositiveButton("SEND MY RATINGS", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    RatingBar ratingBar = rateView.findViewById(R.id.ratingBar_RatingLayout);

                                    int rating = (int)ratingBar.getRating();

                                    database.getReference("JOB").child(job.getPushId()).child("workerRating").setValue(rating);
                                    database.getReference("JOB").child(job.getPushId()).child("dateEnd").setValue(new Date().getTime());
                                    createMessage("WORKER HAVE BEEN RATED WITH " + rating + " stars!","FUNCTIONAL","ADMIN");
                                    createMessage("Thank you for your rating!","NORMAL",job.getWorker().getUid());
                                }
                            })
                            .setCancelable(false)
                            .create();
                    ratingDialog.show();
                }

                database.getReference("USERS").child(j.getWorker().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        worker = dataSnapshot.getValue(User.class);

                        Log.d("WORKER:",worker.getUid());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
    }

    private void setJob(Job j){
        job = j;
    }

    public void sendMessage(View view){
        String message = messageBox.getText().toString().trim();

        if(!message.isEmpty()){
            createMessage(message,"NORMAL",currentUser.getUid());
            if(message.equalsIgnoreCase("JOB START BRO")){
                if(!worker.getWokrerCurrentStatus().equalsIgnoreCase("ON WORK")){
                    if(job.getStatus().equalsIgnoreCase("WORKING")){
                        createMessage("The job has already started.","FUNCTIONAL",currentUser.getUid());
                    }else{
                        database.getReference("JOB").child(job.getPushId()).child("status").setValue("WORKING");
                        database.getReference("JOB").child(job.getPushId()).child("dateStart").setValue(new Date().getTime());
                        createMessage("JOB STARTED AT " + new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime()),"FUNCTIONAL",job.getWorker().getUid());
                        createMessage("I will do the job asap.","NORMAL",job.getWorker().getUid());

                        database.getReference("USERS").child(job.getWorker().getUid()).child("wokrerCurrentStatus").setValue("ON WORK");
                    }
                }else{
                    createMessage("I'm currently working. Sorry.","NORMAL",job.getWorker().getUid());
                }
            }

            if(message.equalsIgnoreCase("JOB END BRO")){
                if(job.getStatus().equalsIgnoreCase("WORKING")){
                    database.getReference("JOB").child(job.getPushId()).child("status").setValue("DONE");
                    createMessage("JOB ENDED AT " + new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime()),"FUNCTIONAL",job.getWorker().getUid());
                    createMessage("Thank you! I hope I can work with you again.","NORMAL",job.getWorker().getUid());

                    final View rateView = LayoutInflater.from(this).inflate(R.layout.rating_layout,null,false);
                    TextView mainText = rateView.findViewById(R.id.textView9_ratingLayout);
                    TextView subText = rateView.findViewById(R.id.textView5_ratingLayout);

                    mainText.setText("How did " + job.getWorker().getFirstname() + " do?");
                    subText.setText("Rating your worker will help them, get more jobs! This is another way to say Thank you to them.");

                    AlertDialog ratingDialog = new AlertDialog.Builder(this)
                            .setTitle("Rate the worker")
                            .setView(rateView)
                            .setPositiveButton("SEND MY RATINGS", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    RatingBar ratingBar = rateView.findViewById(R.id.ratingBar_RatingLayout);

                                    int rating = (int)ratingBar.getRating();

                                    EditText etxt_rating = rateView.findViewById(R.id.ratingLayout_etxt);

                                    String review = etxt_rating.getText().toString().trim();

                                    database.getReference("JOB").child(job.getPushId()).child("workerRating").setValue(rating);
                                    database.getReference("JOB").child(job.getPushId()).child("dateEnd").setValue(new Date().getTime());
                                    createMessage("WORKER HAVE BEEN RATED WITH " + rating + " stars!","FUNCTIONAL","ADMIN");
                                    createMessage("Thank you for your rating!","NORMAL",job.getWorker().getUid());

                                    Review rev;
                                    String reviewKey = database.getReference("USERS").child(job.getWorker().getUid()).child("REVIEW").push().getKey();
                                    if(review.isEmpty()){
                                        review = "Thank you for doing the job. You did great!";
                                    }
                                    rev = new Review(reviewKey,review,job.getJobTitle(),job.getUser(),rating);
                                    database.getReference("USERS").child(job.getWorker().getUid()).child("REVIEW").child(rev.getReviewID()).setValue(rev);

                                    database.getReference("USERS").child(job.getWorker().getUid()).child("wokrerCurrentStatus").setValue("AVAILABLE");
                                }
                            })
                            .setCancelable(false)
                            .create();
                    ratingDialog.show();
                }
            }
        }
    }

    private void createMessage(String message,String messageType,String fromUID){
        Message newMessage = new Message();
        newMessage.setMessageType(messageType);
        newMessage.setTimestamp(new Date().getTime());
        newMessage.setMessage(message);
        newMessage.setFrom(fromUID);
        database.getReference("JOB").child(conversationUID).child("CONVERSATION").push().setValue(newMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    messageBox.setText("");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.convomenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.convo_menu_report:
                if((job.getWorkerRating() == 0 && job.getStatus().equalsIgnoreCase("DONE"))  && !job.getStatus().equalsIgnoreCase("NULL")){
                    final View rateView = LayoutInflater.from(this).inflate(R.layout.rating_layout,null,false);
                    TextView mainText = rateView.findViewById(R.id.textView9_ratingLayout);
                    TextView subText = rateView.findViewById(R.id.textView5_ratingLayout);

                    mainText.setText("How did " + job.getWorker().getFirstname() + " do?");
                    subText.setText("Rating your worker will help them, get more jobs! This is another way to say Thank you to them.");

                    AlertDialog ratingDialog = new AlertDialog.Builder(this)
                            .setTitle("Rate the worker")
                            .setView(rateView)
                            .setPositiveButton("SEND MY RATINGS", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    RatingBar ratingBar = rateView.findViewById(R.id.ratingBar_RatingLayout);

                                    int rating = (int)ratingBar.getRating();

                                    EditText etxt_rating = rateView.findViewById(R.id.ratingLayout_etxt);

                                    String review = etxt_rating.getText().toString().trim();

                                    database.getReference("JOB").child(job.getPushId()).child("workerRating").setValue(rating);
                                    database.getReference("JOB").child(job.getPushId()).child("dateEnd").setValue(new Date().getTime());
                                    createMessage("WORKER HAVE BEEN RATED WITH " + rating + " stars!","FUNCTIONAL","ADMIN");
                                    createMessage("Thank you for your rating!","NORMAL",job.getWorker().getUid());

                                    Review rev;
                                    String reviewKey = database.getReference("USERS").child(job.getWorker().getUid()).child("REVIEW").push().getKey();
                                    if(review.isEmpty()){
                                        review = "Thank you for doing the job. You did great!";
                                    }
                                    rev = new Review(reviewKey,review,job.getJobTitle(),job.getUser(),rating);
                                    database.getReference("USERS").child(job.getWorker().getUid()).child("REVIEW").child(rev.getReviewID()).setValue(rev);

                                    //database.getReference("USERS").child(job.getWorker().getUid()).child("wokrerCurrentStatus").setValue("AVAILABLE");
                                }
                            })
                            .setCancelable(false)
                            .create();
                    ratingDialog.show();
                }else{
                    Toast.makeText(this, "The job must be finish first before you can rate the worker and you can only rate them once in every job.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.convo_menu_view_job_info:
                //Show Message Dialog that shows the Job Information.
                AlertDialog jobInfoDialog = new AlertDialog.Builder(this)
                        .setTitle(job.getJobTitle())
                        .setMessage("Description: " + job.getJobDescription() + "\n" +
                        "Payment: " + job.getPay() + "\n" + "Status: " + job.getStatus()
                        ).setPositiveButton("Okay",null)
                        .create();
                jobInfoDialog.show();
                break;

            case R.id.convo_menu_change_pay:
                if(job.getStatus().equalsIgnoreCase("WORKING") || job.getStatus().equalsIgnoreCase("NULL") || job.getStatus().equalsIgnoreCase("DONE")){
                    AlertDialog changePayDialog = new AlertDialog.Builder(this)
                            .setTitle("Change Payment")
                            .setMessage("The Job is either on WORKING, NULLIFIED or DONE. You can't change payment after that.")
                            .setPositiveButton("OKAy",null)
                            .create();
                    changePayDialog.show();
                }else{
                    final View changePayLayout = LayoutInflater.from(this).inflate(R.layout.send_job_desc_layout,null,false);
                    EditText etxt_job_title_send_job_desc = changePayLayout.findViewById(R.id.etxt_job_title_send_job_desc);
                    EditText etxt_job_desc_send_job_desc = changePayLayout.findViewById(R.id.etxt_job_desc_send_job_desc);

                    etxt_job_title_send_job_desc.setVisibility(View.GONE);
                    etxt_job_desc_send_job_desc.setVisibility(View.GONE);

                    AlertDialog changePayDialog = new AlertDialog.Builder(this)
                            .setView(changePayLayout)
                            .setTitle("Change Payment")
                            .setPositiveButton("CHANGE PAYMENT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText etxt_job_payment_send_job_desc = changePayLayout.findViewById(R.id.etxt_job_payment_send_job_desc);

                                    double newPayment = Double.parseDouble(etxt_job_payment_send_job_desc.getText().toString().trim());

                                    String functionMessage = "CHANGE PAYMENT FROM " + job.getPay()
                                            + " TO " + newPayment;

                                    createMessage(functionMessage,"FUNCTIONAL",currentUser.getUid());

                                    database.getReference("JOB").child(job.getPushId()).child("pay").setValue(newPayment);
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create();
                    changePayDialog.show();
                }
                break;

            case R.id.convo_menu_request_job_end:
                if(job.getStatus().equalsIgnoreCase("WORKING") || job.getStatus().equalsIgnoreCase("NULL") || job.getStatus().equalsIgnoreCase("DONE")){
                    new AlertDialog.Builder(this)
                            .setTitle("Cancel this Job Offer")
                            .setMessage("Either the Job is already on WORKING, NULLIFIED or DONE. After that, you cannot Nullify a job.")
                            .setPositiveButton("Okay",null)
                            .create().show();
                }else{
                    new AlertDialog.Builder(this)
                            .setTitle("Cancel this Job Offer")
                            .setMessage("Are you sure you want to cancel this Job Offer?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    createMessage("THIS JOB HAS BEEN NULLIFIED BY THE USER","FUNCTIONAL",currentUser.getUid());
                                    database.getReference("JOB").child(job.getPushId()).child("status").setValue("NULL");
                                }
                            })
                            .setNegativeButton("No",null)
                            .create().show();
                }
                break;

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

    @Override
    protected void onStart() {
        super.onStart();

        database.getReference("JOB").child(conversationUID).child("CONVERSATION").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                layout_message.removeAllViews();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Message m = ds.getValue(Message.class);

                    View view = LayoutInflater.from(Conversation.this).inflate(R.layout.message_item,null);
                    TextView tvFunctionMessage = view.findViewById(R.id.functionMessageText),
                            tvSelfMessage = view.findViewById(R.id.selfMessageText),
                            tvOthersMessage = view.findViewById(R.id.othersmessageText);

                    if(m.getMessageType().equals("FUNCTIONAL")){
                        tvFunctionMessage.setText(m.getMessage());
                        tvSelfMessage.setVisibility(View.GONE);
                        tvOthersMessage.setVisibility(View.GONE);
                    }else{
                        if(m.getFrom().equals(currentUser.getUid())){
                            tvFunctionMessage.setVisibility(View.GONE);
                            tvSelfMessage.setText(m.getMessage());
                            tvOthersMessage.setVisibility(View.GONE);
                        }else{
                            tvFunctionMessage.setVisibility(View.GONE);
                            tvSelfMessage.setVisibility(View.GONE);
                            tvOthersMessage.setText(m.getMessage());
                        }
                    }

                    layout_message.addView(view);
                    Log.d(m.getFrom(),m.getMessage());
                }
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
    }
}
