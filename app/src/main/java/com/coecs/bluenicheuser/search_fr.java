package com.coecs.bluenicheuser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class search_fr extends Fragment {

    View mainView;

    private TabHost tabHost;
    private EditText etxtSearchBox;
    private TextView tv;

    private FirebaseDatabase database;

    private ArrayList<User> workers;

    public search_fr() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView =  inflater.inflate(R.layout.fragment_search_fr, container, false);
        init();
        return mainView;
    }

    private void init(){
        tabHost = mainView.findViewById(R.id.search_fr_tabhost);
        etxtSearchBox = mainView.findViewById(R.id.etxt_fr_searchbox);
        tv = mainView.findViewById(R.id.search_btn_fr);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSearchButton();
            }
        });

        database = FirebaseDatabase.getInstance();
    }

    public void onClickSearchButton(){
        final String fetchQuery = etxtSearchBox.getText().toString();

        database.getReference("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workers = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    User u = ds.getValue(User.class);
                    if(u.getUserType().equals("WORKER") && u.getFirstname().equals(fetchQuery) || u.getLastname().equals(fetchQuery)){
                        workers.add(u);
                    }
                }
                setUpTabHost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpTabHost(){
        TabHost.TabSpec spec = tabHost.newTabSpec("WORKER").setIndicator("Workers").setContent(R.id.tab1);

        LinearLayout tab1Layout = mainView.findViewById(R.id.tab1);

        for(final User u : workers){
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.dashboard_normal_list_item,null);
            TextView tvUserProfileName,tvUserSubtext;
            tvUserProfileName = v.findViewById(R.id.dashboard_user_profile_name);
            tvUserSubtext = v.findViewById(R.id.dashboard_user_profile_sub_text);
            tvUserProfileName.setText(u.getFirstname() + " " + u.getLastname());
            tvUserSubtext.setText(u.getWorkerProfession() + " - " + u.getWorkerCoverLocation());
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(),ProfileViewer.class);
                    i.putExtra("STRING_WORKER_UID",u.getUid());
                    startActivity(i);
                    getActivity().finish();
                }
            });
            tab1Layout.addView(v);
        }

        tabHost.addTab(spec);
    }

}
