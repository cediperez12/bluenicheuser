package com.coecs.bluenicheuser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class dashboard_fr extends Fragment {

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private ArrayList<User> workers;

    private static final String USER_TYPE_1 = "WORKER",USER_TYPE_2 = "USER";

    private LinearLayout listview_topworker;

    private View mainView;

    public dashboard_fr() {
        // Required empty public constructor
    }

    private void init(){
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init();

        mainView = inflater.inflate(R.layout.fragment_dashboard_fr, container, false);
        listview_topworker = mainView.findViewById(R.id.dashboard_top_worker_listview);
        return mainView;
    }

    String userUid;
    ArrayList<String> userUIDs = new ArrayList<>();

    @Override
    public void onStart() {
        super.onStart();

        //Fetch Workers.
        database.getReference("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workers = new ArrayList<>();
                listview_topworker.removeAllViews();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    final User user = ds.getValue(User.class);
                    if(user.getUserType().equals(USER_TYPE_1)){
                        workers.add(user);

                        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dashboard_normal_list_item,null);
                        TextView tvUserProfileName,tvUserSubtext;
                        tvUserProfileName = v.findViewById(R.id.dashboard_user_profile_name);
                        tvUserSubtext = v.findViewById(R.id.dashboard_user_profile_sub_text);
                        tvUserProfileName.setText(user.getFirstname() + " " + user.getLastname());
                        tvUserSubtext.setText(user.getWorkerProfession() + " - " + user.getWorkerCoverLocation());
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getContext(),ProfileViewer.class);
                                i.putExtra("STRING_WORKER_UID",user.getUid());
                                startActivity(i);
                                getActivity().finish();
                            }
                        });
                        listview_topworker.addView(v);
                        Log.d("WORKER",user.getFirstname() + " " + user.getLastname());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });

    }
}
