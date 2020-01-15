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
import android.widget.TabHost;
import android.widget.TextView;
import java.lang.*;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class search_fr extends Fragment {

    View mainView;

    private EditText etxtSearchBox;
    private LinearLayout listv_search_result_list;
    private TextView txv_no_result,search_btn_fr;

    private FirebaseDatabase database;

    private ArrayList<User> workers; //Default Workers.

    public search_fr() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView =  inflater.inflate(R.layout.fragment_search_fr, container, false);
        init();
        return mainView;
    }

    private void init(){
        etxtSearchBox = mainView.findViewById(R.id.etxt_fr_searchbox);
        search_btn_fr = mainView.findViewById(R.id.search_btn_fr);
        listv_search_result_list = mainView.findViewById(R.id.listv_search_result_list);
        txv_no_result = mainView.findViewById(R.id.txv_no_result);

        etxtSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                listv_search_result_list.removeAllViews();

                Log.e("Finding","Finding new Workers");

                ArrayList<User> workersFound = search(etxtSearchBox.getText().toString().trim());

                if(!workersFound.isEmpty()){
                    txv_no_result.setVisibility(View.INVISIBLE);

                    //Load users
                    for(final User u : workersFound){
                        View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.search_item_layout,null,false);
                        TextView textView6 = itemView.findViewById(R.id.textView6), textView7 = itemView.findViewById(R.id.textView7);

                        textView6.setText(u.getFirstname() + " " + u.getLastname());
                        textView7.setText(u.getWorkerProfession() + " - " + u.getWorkerCoverLocation());

                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(),ProfileViewer.class);
                                intent.putExtra("STRING_WORKER_UID",u.getUid());
                                startActivity(intent);
                                getActivity().finish();
                            }
                        });

                        Log.e("ADDING NEW ITEM","ADDS NEW WORKER TO THE LIST");

                        listv_search_result_list.addView(itemView);
                    }

                }else{
                    txv_no_result.setVisibility(View.VISIBLE);

                    Log.e("Search Result","No Search Results.");
                }
            }
        });

        search_btn_fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        database = FirebaseDatabase.getInstance();

        createWorkers();
    }

    private void createWorkers(){
        workers = new ArrayList<>();

        database.getReference("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);

                    if(user.getUserType().equalsIgnoreCase("WORKER")){
                        Log.e("WORKER FOUND", user.getFirstname() + " " + user.getLastname());
                        workers.add(user);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
    }

    private ArrayList<User> search(String searchKey){
        Log.d("Search.","Currently searching for users.");
        ArrayList<User> users = new ArrayList<>();

        for(User u : workers){
            if(u.getFirstname().startsWith(searchKey) ||
                    u.getLastname().startsWith(searchKey) ||
                    u.getWorkerProfession().startsWith(searchKey) ||
                    u.getWorkerCoverLocation().startsWith(searchKey)){
                users.add(u);
            }
        }
        return users;
    }

}
