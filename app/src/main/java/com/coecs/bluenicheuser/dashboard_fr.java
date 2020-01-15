package com.coecs.bluenicheuser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import java.util.Random;

public class dashboard_fr extends Fragment {
    //Contacts

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
        workers = new ArrayList<User>();

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
        listview_topworker.removeAllViews();
        try{
            database.getReference("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        final User user = ds.getValue(User.class);

                        Log.e("CURRENT USER",user.getFirstname() + " " + user.getLastname() + " - " + user.getUserType());

                        if(user.getUserType().equalsIgnoreCase("WORKER")){
                            View singleView = LayoutInflater.from(getActivity()).inflate(R.layout.dashboard_normal_list_item,null);

                            TextView dashboard_user_profile_name = singleView.findViewById(R.id.dashboard_user_profile_name), dashboard_user_profile_sub_text =
                                    singleView.findViewById(R.id.dashboard_user_profile_sub_text);
                            CardView cardView = singleView.findViewById(R.id.dashboard_list_card);

                            dashboard_user_profile_name.setText(user.getFirstname() + " " + user.getLastname());
                            dashboard_user_profile_sub_text.setText(user.getWorkerProfession() + "\n" + user.getWorkerCoverLocation());

                            ImageView imgv_userProfile = singleView.findViewById(R.id.dashboard_user_profile_photo);

                            int randomProfile = user.getUserProfileImage();

                            if(randomProfile == 0){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    imgv_userProfile.setImageDrawable(getActivity().getDrawable(R.drawable.job_logo_1));
                                }
                            }else if(randomProfile == 1){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    imgv_userProfile.setImageDrawable(getActivity().getDrawable(R.drawable.job_logo_2));
                                }
                            }else{
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    imgv_userProfile.setImageDrawable(getActivity().getDrawable(R.drawable.job_logo_3));
                                }
                            }

                            cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(),ProfileViewer.class);
                                    intent.putExtra("STRING_WORKER_UID",user.getUid());
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            });

                            Log.d("ADDVIEW","Added a new view.");

                            listview_topworker.addView(singleView);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });

        }catch (Exception ex){
            ex.printStackTrace();
        }

        //Create New Workers

//        User user = new User();
//        user.setFirstname("Justine");
//        user.setLastname("Leyba");
//        user.setUserType("WORKER");
//        user.setWorkerCoverLocation("Obando");
//        user.setWorkerProfession("Gamer");
//        user.setUid(database.getReference("USERS").push().getKey());
//
//        database.getReference("USERS").child(user.getUid()).setValue(user);
//
//        CurriculumVitae cv = new CurriculumVitae();
//        cv.setObjective("To be able to teach people, in order to have money.");
//
//        ArrayList<String> skills = new ArrayList<>();
//        skills.add("Game Tester");
//        skills.add("Documentation");
//        skills.add("Microsoft Office");
//
//        cv.setSkills(skills);
//
//        CurriculumVitae.CollegeInfo ci = new CurriculumVitae.CollegeInfo();
//        ci.setUniversity("STI College");
//        ci.setYear("2022");
//        ci.setTertiary("College");
//        ci.setCourse("BS Information Technology");
//
//        cv.setCollegeInfo(ci);
//
//        database.getReference("USERS").child(user.getUid()).child("CurriculumVitae").setValue(cv);
    }

    private void loadWorkerList(ArrayList<User> users){
        if(!users.isEmpty()){
            for(final User u : users){


            }
        }else{
            Log.e("Got nothiing","nothing");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
