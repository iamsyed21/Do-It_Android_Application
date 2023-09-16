package com.the21codes.do_it;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;

public class PendingProjects extends AppCompatActivity {


    RecyclerView pendingProjectsRecyclerView;
    projectAdapter projectAdapter;
    ArrayList<ProjectModel> projectLists;
    DataBaseHelper myDB;
    SearchView projectSearchBarID;
    ImageView noPendingProjectsImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_projects);
        myDB = new DataBaseHelper(this);

        projectLists = new ArrayList<>();
        pendingProjectsRecyclerView = (RecyclerView) findViewById(R.id.pendingProjectsRecyclerView);
        noPendingProjectsImageView = findViewById(R.id.noPendingProjectsImageView);
        pendingProjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        projectLists = myDB.getAllPendingProjects();
        projectAdapter = new projectAdapter(projectLists, PendingProjects.this);
        pendingProjectsRecyclerView.setAdapter(projectAdapter);
        projectAdapter.notifyDataSetChanged();
        if(projectLists.size() == 0){
            noPendingProjectsImageView.setVisibility(View.VISIBLE);
        }else{
            noPendingProjectsImageView.setVisibility(View.GONE);
        }

        if(isDeviceConnected(getApplicationContext())) {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });

            AdView adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
        projectSearchBarID = (SearchView) findViewById(R.id.projectSearchBarID);
        projectSearchBarID.clearFocus();
        projectSearchBarID.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                projectSearchBarID.clearFocus();
                filterArrayList(query, 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterArrayList(newText,1);
                return true;
            }
        });


    }

    private void filterArrayList(String query, int type){
        ArrayList<ProjectModel> newQueryList = new ArrayList<>();
        for(ProjectModel item: projectLists){
            if(item.getProjectName().toLowerCase().contains(query.toLowerCase())){
                newQueryList.add(item);
            }
        }

        if(type == 0){
            int TotalResults = newQueryList.size();
            Toast.makeText(this, "Total Projects Found:"+TotalResults, Toast.LENGTH_LONG).show();
        }

        projectAdapter.filteredList(newQueryList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        projectLists = myDB.getAllPendingProjects();
        projectAdapter = new projectAdapter(projectLists, PendingProjects.this);
        pendingProjectsRecyclerView.setAdapter(projectAdapter);
        projectAdapter.notifyDataSetChanged();
        if(projectLists.size() == 0){
            noPendingProjectsImageView.setVisibility(View.VISIBLE);
        }else{
            noPendingProjectsImageView.setVisibility(View.GONE);
        }
    }

    private static boolean isDeviceConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo!= null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }
}