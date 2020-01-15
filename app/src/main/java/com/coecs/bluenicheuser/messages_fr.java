package com.coecs.bluenicheuser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class messages_fr extends Fragment {

    LinearLayout listLayout;

    TextView tv;
    EditText etxt_fr_searchbox_messages;

    FirebaseDatabase database;
    FirebaseAuth authentication;

    ArrayList<Job> jobs;

    public messages_fr() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages_fr, container, false);

        listLayout = view.findViewById(R.id.messages_fr_linearlayout_list);
        tv = view.findViewById(R.id.messages_empty_job_txtv);
        etxt_fr_searchbox_messages = view.findViewById(R.id.etxt_fr_searchbox_messages);

        database = FirebaseDatabase.getInstance();
        authentication = FirebaseAuth.getInstance();

        database.getReference("JOB").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jobs = new ArrayList<>();

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    jobs.add(ds.getValue(Job.class));

                }

                Collections.reverse(jobs);

                if(jobs.isEmpty()){
                    tv.setVisibility(View.VISIBLE);
                }else{
                    tv.setVisibility(View.GONE);
                    for(Job j : jobs){
                        final Job job = j;

                        if(job.getUser().getUid().equalsIgnoreCase(authentication.getCurrentUser().getUid())){
                            View v = LayoutInflater.from(getActivity()).inflate(R.layout.messages_item_layout,null,false);
                            TextView tvMainText = v.findViewById(R.id.messages_item_layout_user_name);
                            TextView tvSubtext = v.findViewById(R.id.messages_item_layout_user_new_message);
                            TextView tvTimestamp = v.findViewById(R.id.messages_item_layout_user_timestamp);

                            tvMainText.setText(job.getJobTitle());
                            tvSubtext.setText(job.getWorker().getFirstname() + " " + job.getWorker().getLastname() + " * STATUS: " + job.getStatus());

                            if(job.getStatus().equalsIgnoreCase("DONE")){
                                tvTimestamp.setText(sdf.format(job.getDateEnd()));
                            }else if(job.getStatus().equalsIgnoreCase("WORKING")){
                                tvTimestamp.setText(sdf.format(job.getDateStart()));
                            }else if(job.getStatus().equalsIgnoreCase("NULL")){
                                tvTimestamp.setText("Nullified");
                            }else{
                                tvTimestamp.setText("Waiting to be accepted.");
                            }

                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.putExtra("JOBKEY",job.getPushId());
                                    intent.setClass(getActivity(),Conversation.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            });

                            listLayout.addView(v);
                        }
                    }
                }

                etxt_fr_searchbox_messages.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(!etxt_fr_searchbox_messages.toString().trim().isEmpty() && !jobs.isEmpty()){
                            ArrayList<Job> j = new ArrayList<>();

                            for(Job job : jobs){
                                if(job.getJobTitle().startsWith(etxt_fr_searchbox_messages.toString().trim()) || job.getWorker().getFirstname().startsWith(etxt_fr_searchbox_messages.toString().trim())){
                                    j.add(job);
                                    Log.e("JOB SEARCH",job.getJobTitle());
                                }
                            }

                            for(final Job job : j){

                                if(job.getUser().getUid().equalsIgnoreCase(authentication.getCurrentUser().getUid())){
                                    View v = LayoutInflater.from(getActivity()).inflate(R.layout.messages_item_layout,null,false);
                                    TextView tvMainText = v.findViewById(R.id.messages_item_layout_user_name);
                                    TextView tvSubtext = v.findViewById(R.id.messages_item_layout_user_new_message);
                                    TextView tvTimestamp = v.findViewById(R.id.messages_item_layout_user_timestamp);

                                    tvMainText.setText(job.getJobTitle());
                                    tvSubtext.setText(job.getWorker().getFirstname() + " " + job.getWorker().getLastname() + " * STATUS: " + job.getStatus());

                                    if(job.getStatus().equalsIgnoreCase("DONE")){
                                        tvTimestamp.setText(sdf.format(job.getDateEnd()));
                                    }else if(job.getStatus().equalsIgnoreCase("WORKING")){
                                        tvTimestamp.setText(sdf.format(job.getDateStart()));
                                    }else if(job.getStatus().equalsIgnoreCase("NULL")){
                                        tvTimestamp.setText("Nullified");
                                    }else{
                                        tvTimestamp.setText("Waiting to be accepted.");
                                    }

                                    v.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent();
                                            intent.putExtra("JOBKEY",job.getPushId());
                                            intent.setClass(getActivity(),Conversation.class);
                                            startActivity(intent);
                                        }
                                    });

                                    listLayout.addView(v);
                                }
                            }

                        }else{

                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });

        return view;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void onStart() {
        super.onStart();
    }
}
