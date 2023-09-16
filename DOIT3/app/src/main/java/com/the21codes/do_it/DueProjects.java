package com.the21codes.do_it;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DueProjects extends AppCompatActivity {

    RecyclerView dueProjectsRecyclerView;
    projectAdapter projectAdapter;
    ArrayList<ProjectModel> projectLists;
    ArrayList<ProjectModel> dueLists;
    DataBaseHelper myDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_due_projects);
        myDB = new DataBaseHelper(this);
        projectLists = myDB.getAllPendingProjects();
        dueLists = new ArrayList<>();
        getDueProjectsRemaining(projectLists);
        dueProjectsRecyclerView = (RecyclerView) findViewById(R.id.dueProjectsRecyclerView);
        projectAdapter = new projectAdapter(dueLists, DueProjects.this);
        dueProjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dueProjectsRecyclerView.setAdapter(projectAdapter);
        projectAdapter.notifyDataSetChanged();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView adView = findViewById(R.id.adView);;
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    public void getDueProjectsRemaining(ArrayList<ProjectModel> deadlines){
        for(int i=0;i<deadlines.size();i++){
            ProjectModel current = deadlines.get(i);
            boolean hasTimePassedHere;
            try{
                hasTimePassedHere = hasTimePassed(current.getProjectDeadline());
            }catch (Exception e){
                e.printStackTrace();
                hasTimePassedHere = false;
            }
            if(hasTimePassedHere){
                dueLists.add(current);
            }
        }
    }

    public static boolean hasTimePassed(String dateString) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy, HH.mm");
        Date date = formatter.parse(dateString);
        return date.before(new Date());
    }
}