package com.the21codes.do_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.ArrayList;

public class CompletedProjects extends AppCompatActivity {


    RecyclerView completedProjectsRecyclerView;
    projectAdapter projectAdapter;
    ArrayList<ProjectModel> projectLists;
    DataBaseHelper myDB;
    SearchView projectSearchBarID;
    AdRequest adRequest;
    InterstitialAd mInterstitialAd;
    ImageView noCompletedProjectsImageView;
    SharedPreferences adsCounterDetails;
    SharedPreferences.Editor adsCounterDetailsEditor;
    int adCounter= -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_projects);
        myDB = new DataBaseHelper(this);
        completedProjectsRecyclerView = (RecyclerView) findViewById(R.id.completedProjectsRecyclerView);
        noCompletedProjectsImageView = findViewById(R.id.noCompletedProjectsImageView);
        projectLists = new ArrayList<>();
        completedProjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        projectLists = myDB.getAllCompletedProjects();
        projectAdapter = new projectAdapter(projectLists, CompletedProjects.this);
        completedProjectsRecyclerView.setAdapter(projectAdapter);
        projectAdapter.notifyDataSetChanged();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        if(projectLists.size() == 0){
            noCompletedProjectsImageView.setVisibility(View.VISIBLE);
        }else{
            noCompletedProjectsImageView.setVisibility(View.GONE);
        }

        adsCounterDetails = getSharedPreferences("UserLoginAndNameDetails", MODE_PRIVATE);
        adCounter = adsCounterDetails.getInt("Completed_page_ad", 0);
        adsCounterDetailsEditor = adsCounterDetails.edit();



        AdView adView = findViewById(R.id.adView);;
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        if(adCounter<=2){
            adCounter++;
            adsCounterDetailsEditor.putInt("Completed_page_ad", adCounter);
            adsCounterDetailsEditor.apply();
        }else {
            showInterstitialAd();
            adCounter=0;
            adsCounterDetailsEditor.putInt("Completed_page_ad", 0);
            adsCounterDetailsEditor.apply();
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

    private void showInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-6952672354974833/5540661123", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;

                        mInterstitialAd.show(CompletedProjects.this);
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                mInterstitialAd = null;
                                super.onAdFailedToShowFullScreenContent(adError);
                            }

                            @Override
                            public void onAdImpression() {
                                super.onAdImpression();
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent();
                                mInterstitialAd = null;
                            }
                        });

                    }



                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });


    }

    @Override
    protected void onResume() {
        super.onResume();
        projectLists = myDB.getAllCompletedProjects();
        projectAdapter = new projectAdapter(projectLists, CompletedProjects.this);
        completedProjectsRecyclerView.setAdapter(projectAdapter);
        projectAdapter.notifyDataSetChanged();
        if(projectLists.size() == 0){
            noCompletedProjectsImageView.setVisibility(View.VISIBLE);
        }else{
            noCompletedProjectsImageView.setVisibility(View.GONE);
        }
    }
}