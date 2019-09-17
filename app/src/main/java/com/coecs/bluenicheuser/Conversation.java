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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private String conversationUID;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        init();
    }

    private void init(){
        layout_message = findViewById(R.id.conversation_linear_layout_message);
        messageBox = findViewById(R.id.etxt_conversation_messagebox);
        toolbar = findViewById(R.id.conversation_toolbar);
        scrollView = findViewById(R.id.scrollView2);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Worker");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        conversationUID = getIntent().getExtras().getString("CONVOKEY");
    }

    public void sendMessage(View view){
        String message = messageBox.getText().toString().trim();
        if(!message.isEmpty()){
            Message m = new Message();
            m.setTimestamp(new Date().getTime());
            m.setFrom(currentUser.getUid());
            m.setMessage(message);
            m.setMessageType("NORMAL");
            database.getReference("CONVOS/"+conversationUID+"/MESSAGES").push().setValue(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        messageBox.setText("");
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.convomenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.job_start:
                jobStart();
                break;

            case R.id.job_done:
                jobEnd();
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

    int rate;

    private void jobEnd(){
        new AlertDialog.Builder(this)
                .setTitle("Rate the worker first.")
                .setItems(new String[]{"1 Star", "2 Stars", "3 Stars", "4 Stars", "5 Stars"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rate = which+1;

                        Message m = new Message();
                        m.setFrom(currentUser.getUid());
                        m.setTimestamp(new Date().getTime());
                        m.setMessage("Job Ended at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime()) + " User Rated the worker " + rate + " Stars");
                        m.setMessageType("FUNCTIONAL");
                        database.getReference("CONVOS/"+conversationUID+"/MESSAGES").push().setValue(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    messageBox.setText("");
                                }
                            }
                        });

                        Message thankyou = new Message();
                        thankyou.setFrom(currentUser.getUid());
                        thankyou.setTimestamp(new Date().getTime());
                        thankyou.setMessage("Thank you for your service. -Administrator.");
                        thankyou.setMessageType("FUNCTIONAL");
                        database.getReference("CONVOS/"+conversationUID+"/MESSAGES").push().setValue(thankyou).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    messageBox.setText("");
                                }
                            }
                        });
                    }
                })
                .setCancelable(false)
                .create().show();
    }

    private void jobStart(){
        Message m = new Message();
        m.setFrom(currentUser.getUid());
        m.setTimestamp(new Date().getTime());
        m.setMessage("Job Started at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime()));
        m.setMessageType("FUNCTIONAL");
        database.getReference("CONVOS/"+conversationUID+"/MESSAGES").push().setValue(m).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    messageBox.setText("");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        database.getReference("CONVOS").child(conversationUID).child("MESSAGES").addValueEventListener(new ValueEventListener() {
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
