package com.the21codes.do_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    ImageView CompletedTask, PendingTasks, AddNewTask, earliestDropDownArrow,
            highImportanceDropDownArrow, deadlineDueImageView, openSettingImageView,
            noGraphActivityYetImageView;
    RelativeLayout earliestDeadlineTab, deadlineArcodion, highestImportanceTab, missedDeadlinesTile,
            highestArcodion, quickOverViewTile, taskTrackerTile, addNewProjectIconRelativeLayout, nextProjectUpcomingRelativeLayout;
    LinearLayout arcodionsLinearLayout;
    RecyclerView deadlineRecyclerView, importanceRecyclerView;
    simpleProjectAdapter deadlineAdapter, importanceAdapter;
    DataBaseHelper myDB;
    private boolean InternetWorking = false;
    ArrayList<ProjectModel> deadlines, important, sortedDates;
    private static final String DATABASE_NAME = "DO_IT_DATABASE";
    private static final String PROJECT_TABLE = "PROJECT_TABLE";
    private static final String PHASE_TABLE_PREFIX= "PHASE_TABLE_";
    private static final String STEPS_TABLE_PREFIX = "STEPS_TABLE_";
    private static final String PROJECT_KEY_ID  = "id";
    private static final String PHASE_KEY_ID  = "id";
    private static final String STEP_KEY_ID  = "id";
    private static final String PHASE_NAME = "PHASES_NAME";
    private static final String PHASE_PROJECT_ID = "PROJECT_ID";
    private static final String PHASE_DEADLINE = "PHASES_DEADLINE";
    private static final String PHASE_PRIORITY = "PHASES_PRIORITY";
    private static final String PHASE_NOTIFICATION = "PHASES_NOTIFICATION";
    private static final String PHASE_STEPS = "PHASES_STEPS";
    private static final String PHASE_TYPE = "PHASES_TYPE";
    private static final String PHASE_DEADLINE_SET = "PHASE_DEADLINE_SET";
    private static final String PHASE_NOTIFICATION_SET = "PHASES_NOTIFICATION_SET";
    private static final String PHASE_IS_COMPLETED = "PHASES_IS_COMPLETED";
    private static final String STEP_NAME = "STEP_NAME";
    private static final String STEP_PHASE_ID = "PHASE_ID";
    private static final String STEP_DEADLINE = "STEP_DEADLINE";
    private static final String STEP_PROGRESS = "STEP_PROGRESS";
    private static final String STEP_NOTES = "STEP_NOTES";
    private static final String STEP_NOTIFICATION = "STEP_NOTIFICATION";
    private static final String STEP_DEADLINE_SET = "STEP_DEADLINE_SET";
    private static final String STEP_NOTIFICATION_SET = "STEP_NOTIFICATION_SET";
    private static final String STEP_IS_STEP_COMPLETED = "STEP_IS_COMPLETED";
    private FirebaseAuth mAuth;
    private DatabaseReference rootDatabaseReference;
    String theFlattenedString;
    ScrollView mainScrollView;
    BarChart trackerBarChart;
    ArcProgress projectProgressTotalBar, projectTotalPhasesBar;
    TextView totalPendingProjectsTextView, nextProjectNameTextView, TotalPhasesOfProject, numberOfDueProjectsTextView,
            relaxTextView, relaxTextViewDescription, hiTextView, userNameTextViw ,deadlineArcodionTextView, highestArcodionTextView;
    SharedPreferences instructionsGiven;
    SharedPreferences.Editor instructionsGivenEditor;
    boolean mainPageInstructionGiven;
    SharedPreferences whenToUpload;
    SharedPreferences.Editor whenToUploadEditor;
    int firebaseUploadCounter=0;
    boolean userFirstTimeLogin = false;
    SharedPreferences extraLoginDetails;
    SharedPreferences.Editor extraLoginDetailsEditor;
    String userName = "none";
    SharedPreferences userLoginDetails;
    private boolean doubleClickedForBack = false;

    SharedPreferences vibrationDetails;
    SharedPreferences.Editor vibrationDetailsEditor;
    Boolean vibrationsEnabled = false;
    private Vibrator vibrator;
    private boolean isNextProjectAvailable;

    private boolean isDataLoaded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDB = new DataBaseHelper(this);
        Context context = getApplicationContext();
        InternetWorking = isDeviceConnected(context);
        deadlines = new ArrayList<>();
        important = new ArrayList<>();
        sortedDates = new ArrayList<>();
        instructionsGiven = getSharedPreferences("instructionGivenDetails", MODE_PRIVATE);
        instructionsGivenEditor = instructionsGiven.edit();
        mainPageInstructionGiven = instructionsGiven.getBoolean("mainPageInstructions", false);
        whenToUpload = getSharedPreferences("whenToUploadDetails", MODE_PRIVATE);
        whenToUploadEditor = whenToUpload.edit();
        firebaseUploadCounter = whenToUpload.getInt("uploadCounter", 0);
        extraLoginDetails = getSharedPreferences("extraLoginDetails", MODE_PRIVATE);
        extraLoginDetailsEditor = extraLoginDetails.edit();
        userFirstTimeLogin = extraLoginDetails.getBoolean("FirstEverLogin", true);
        userLoginDetails = getSharedPreferences("UserLoginAndNameDetails", MODE_PRIVATE);

        if(InternetWorking) {
            mAuth = FirebaseAuth.getInstance();
                if (mAuth.getCurrentUser() == null) {
                    extraLoginDetailsEditor.putBoolean("UserInSession", false);
                    extraLoginDetailsEditor.apply();
                } else {
                    extraLoginDetailsEditor.putBoolean("UserInSession", true);
                    extraLoginDetailsEditor.apply();
                }
        }


        vibrationDetails = getSharedPreferences("vibrationPreferences", MODE_PRIVATE);
        vibrationDetailsEditor = vibrationDetails.edit();
        vibrationsEnabled = vibrationDetails.getBoolean("vibrations", true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        CompletedTask = (ImageView) findViewById(R.id.CompletedTask);
        PendingTasks = (ImageView) findViewById(R.id.PendingTasks);
        AddNewTask = (ImageView) findViewById(R.id.AddNewTask);
        noGraphActivityYetImageView = (ImageView) findViewById(R.id.noGraphActivityYetImageView);
        earliestDeadlineTab = (RelativeLayout) findViewById(R.id.earliestDeadlineTab);
        deadlineArcodion = (RelativeLayout) findViewById(R.id.deadlineArcodion);
        highestImportanceTab = (RelativeLayout) findViewById(R.id.highestImportanceTab);
        missedDeadlinesTile = (RelativeLayout) findViewById(R.id.missedDeadlinesTile);
        highestArcodion = (RelativeLayout) findViewById(R.id.highestArcodion);
        quickOverViewTile = (RelativeLayout) findViewById(R.id.quickOverViewTile);
        taskTrackerTile = (RelativeLayout) findViewById(R.id.taskTrackerTile);
        nextProjectUpcomingRelativeLayout = (RelativeLayout) findViewById(R.id.nextProjectUpcomingRelativeLayout);
        arcodionsLinearLayout = (LinearLayout) findViewById(R.id.arcodionsLinearLayout);
        deadlineRecyclerView = (RecyclerView) findViewById(R.id.deadlineRecyclerView);
        importanceRecyclerView = (RecyclerView) findViewById(R.id.importanceRecyclerView);
        addNewProjectIconRelativeLayout = (RelativeLayout) findViewById(R.id.addNewProjectIconRelativeLayout);
        mainScrollView = (ScrollView) findViewById(R.id.mainScrollView);
        highImportanceDropDownArrow = (ImageView) findViewById(R.id.highImportanceDropDownArrow);
        earliestDropDownArrow = (ImageView) findViewById(R.id.earliestDropDownArrow);
        deadlineDueImageView = (ImageView) findViewById(R.id.deadlineDueImageView);
        openSettingImageView = (ImageView) findViewById(R.id.openSettingImageView);
        projectTotalPhasesBar =  findViewById(R.id.projectTotalPhasesBar);
        projectProgressTotalBar = findViewById(R.id.projectProgressTotalBar);
        totalPendingProjectsTextView = (TextView) findViewById(R.id.totalPendingProjectsTextView);
        TotalPhasesOfProject = (TextView) findViewById(R.id.TotalPhasesOfProject);
        hiTextView = (TextView) findViewById(R.id.hiTextView);
        nextProjectNameTextView = (TextView) findViewById(R.id.nextProjectNameTextView);
        numberOfDueProjectsTextView = (TextView) findViewById(R.id.numberOfDueProjectsTextView);
        userNameTextViw = (TextView) findViewById(R.id.userNameTextViw);
        relaxTextView = (TextView) findViewById(R.id.relaxTextView);
        relaxTextViewDescription = (TextView) findViewById(R.id.relaxTextViewDescription);
        deadlineArcodionTextView = (TextView) findViewById(R.id.deadlineArcodionTextView);
        highestArcodionTextView = (TextView) findViewById(R.id.highestArcodionTextView);
        trackerBarChart = (BarChart) findViewById(R.id.trackerBarChart);
        try{
            InternetWorking = isDeviceConnected(getApplicationContext());
            if(InternetWorking) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                rootDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
            }else {
                Toast.makeText(getApplicationContext(), "Not connected to the internet, please connect to the internet", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Oh No something went wrong", Toast.LENGTH_SHORT).show();
        }finally {
            if(InternetWorking) {
                if(userFirstTimeLogin) {
                    try {
                        rootDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);

                                if (user != null) {
                                    userName = user.userName;
                                    String email = user.userEmail;
                                    String theDatabaseString = user.userDatabase;
                                    unFlattenAndPopulateDatabase(theDatabaseString);
                                    extraLoginDetailsEditor.putBoolean("FirstEverLogin", false);
                                    extraLoginDetailsEditor.apply();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                extraLoginDetailsEditor.putBoolean("FirstEverLogin", true);
                                extraLoginDetailsEditor.apply();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Something went wrong while downloading backup data", Toast.LENGTH_SHORT).show();
                        extraLoginDetailsEditor.putBoolean("FirstEverLogin", true);
                        extraLoginDetailsEditor.apply();
                    }
                }
            }
        }

        if(userName ==null || userName.equals("none")){
            userName = userLoginDetails.getString("USER_NAME", "none");
        }

        if(!userName.equals("none")){

        }else{

        }

        InternetWorking = isDeviceConnected(getApplicationContext());
        if(InternetWorking){
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            AdView adView = findViewById(R.id.adView);;
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }


        deadlines = myDB.getAllPendingProjects();
        sortedDates = (ArrayList<ProjectModel>) DatesSorter.sortProjectsByDeadline(deadlines);
        deadlineAdapter = new simpleProjectAdapter(sortedDates, MainActivity.this);
        deadlineRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deadlineRecyclerView.setAdapter(deadlineAdapter);
        deadlineAdapter.notifyDataSetChanged();
        important = myDB.getTopFiveHighestImportantProjects();
        importanceAdapter = new simpleProjectAdapter(important, MainActivity.this);
        importanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        importanceRecyclerView.setAdapter(importanceAdapter);
        importanceAdapter.notifyDataSetChanged();

        if(deadlines.size() == 0){
            highestArcodionTextView.setVisibility(View.VISIBLE);
            deadlineArcodionTextView.setVisibility(View.VISIBLE);
        }else{
            highestArcodionTextView.setVisibility(View.GONE);
            deadlineArcodionTextView.setVisibility(View.GONE);
        }

        if(userName ==null || userName.equals("none")){
            userName = userLoginDetails.getString("USER_NAME", "none");
        }

        if(!userName.equalsIgnoreCase("none")){
            hiTextView.setVisibility(View.VISIBLE);
            String s[] = userName.split(" ");
            userNameTextViw.setText(s[0]);
        }else{
            hiTextView.setVisibility(View.GONE);
            userNameTextViw.setText(getGreeting());
        }

        earliestDeadlineTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deadlines = myDB.getAllPendingProjects();
                sortedDates = (ArrayList<ProjectModel>) DatesSorter.sortProjectsByDeadline(deadlines);
                deadlineAdapter.filteredList(sortedDates);
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                if(deadlineArcodion.getVisibility() == View.GONE){
                    deadlineArcodion.setVisibility(View.VISIBLE);
                    earliestDropDownArrow.animate().rotationBy(-91).setDuration(200).start();
                }else if(deadlineArcodion.getVisibility() == View.VISIBLE){
                    deadlineArcodion.setVisibility(View.GONE);
                    earliestDropDownArrow.animate().rotationBy(91).setDuration(200).start();
                }

                mainScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mainScrollView.smoothScrollTo(0, mainScrollView.getBottom());
                    }
                });
            }
        });
        highestImportanceTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                important = myDB.getTopFiveHighestImportantProjects();
                importanceAdapter.filteredList(important);

                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                if(highestArcodion.getVisibility() == View.GONE){
                    highestArcodion.setVisibility(View.VISIBLE);
                    highImportanceDropDownArrow.animate().rotationBy(-90).setDuration(200).start();
                }else if(highestArcodion.getVisibility() == View.VISIBLE){
                    highestArcodion.setVisibility(View.GONE);
                    highImportanceDropDownArrow.animate().rotationBy(90).setDuration(200).start();
                }

                mainScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mainScrollView.smoothScrollTo(0, mainScrollView.getBottom());
                    }
                });


            }
        });
        if(!isDataLoaded) {
            ProjectModel nextProject = new ProjectModel();
            deadlines = myDB.getAllPendingProjects();
            sortedDates = (ArrayList<ProjectModel>) DatesSorter.sortProjectsByDeadline(deadlines);
            if (sortedDates.size() > 0) {

                isNextProjectAvailable = true;
                nextProject = sortedDates.get(0);
                int size = sortedDates.size();
                String silly;
                if(size >9){
                    silly = Integer.toString(size)+"+";
                }else{
                    silly = Integer.toString(size);
                }
                totalPendingProjectsTextView.setText(silly);
                String nextProjectName = nextProject.getProjectName();
                if (nextProjectName.length() > 16) {
                    nextProjectNameTextView.setText(nextProjectName.substring(0, 16) + "..");
                } else {
                    nextProjectNameTextView.setText(nextProjectName);
                }
                setTotalPhasesProgress(nextProject.getProjectID());
                setTheProjectProgress(nextProject.getProjectID());
            } else {
                isNextProjectAvailable = false;
                nextProject = new ProjectModel(-1, "",
                        "None", "None", 0,
                        "None", 0, "None");
                totalPendingProjectsTextView.setText("0");
                nextProjectNameTextView.setText(" ");
                projectTotalPhasesBar.setProgress(0);
                projectTotalPhasesBar.invalidate();
                TotalPhasesOfProject.setText("0");
                projectProgressTotalBar.setProgress(0);
                projectProgressTotalBar.invalidate();
            }
            getDueTileDetails();
        }
        if(firebaseUploadCounter >= 1){
            InternetWorking = isDeviceConnected(getApplicationContext());
            if(InternetWorking) {
                whenToUploadEditor.putInt("uploadCounter", 0);
                whenToUploadEditor.apply();
                 theFlattenedString = flattenTheDatabase();
                 uploadDatabaseToFireBase();
            }
        }else{
            InternetWorking = isDeviceConnected(getApplicationContext());
            if(InternetWorking) {
                firebaseUploadCounter++;
                whenToUploadEditor.putInt("uploadCounter", firebaseUploadCounter);
                whenToUploadEditor.apply();
            }
        }

        trackerBarChart.setOnChartValueSelectedListener(this);
        if(!isDataLoaded) {
            makeTheGraph();
        }
        CompletedTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                openCompletedTasks();
            }
        });
        AddNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                openAddNewTasks();
            }
        });
        PendingTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                openPendingTasks();
            }
        });
        openSettingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                openSettingImageView.animate().rotationBy(-180).setDuration(200).start();
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                openSettingsActivity();
            }
        });

        if(!mainPageInstructionGiven) {
            openInstructions();
        }

        if(userNameTextViw.getText().toString().equalsIgnoreCase("none")){
            hiTextView.setVisibility(View.GONE);
            userNameTextViw.setText(getGreeting());
        }

        RatingDialog.Builder ratingDialog = new RatingDialog.Builder(this);
        ratingDialog
                .session(5)
                .icon(R.drawable.doit_logo)
                .title(R.string.experience1, R.color.FOURTH_SLIDE)
                .ratingBarColor(R.color.gold, R.color.Yet_To_Start);
        RatingDialog rate = ratingDialog.build();
        rate.show();

        nextProjectUpcomingRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNextProjectAvailable){
                    deadlines = myDB.getAllPendingProjects();
                    sortedDates = (ArrayList<ProjectModel>) DatesSorter.sortProjectsByDeadline(deadlines);
                    ProjectModel thisProject = sortedDates.get(0);
                    int thisProjectID = thisProject.getProjectID();
                    openNextProject(thisProjectID);
                }
            }
        });

    }

    private void openNextProject(int i){
        Intent intent = new Intent(this, phasesForProjectActivity.class);
        intent.putExtra("projectID", i);
        intent.setAction("NORMAL_OPERATION");
        startActivity(intent);
    }

    private void openInstructions() {
        Typeface myTypeface1 = ResourcesCompat.getFont(getApplicationContext(), R.font.lato);
        Typeface myTypeface2 = ResourcesCompat.getFont(getApplicationContext(), R.font.merriweather);
        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(quickOverViewTile, "Welcome to your HomePage", "Get a quick overview of your upcoming projects. \nClick to continue")
                                .outerCircleColor(R.color.FIRST_SLIDE)
                                .outerCircleAlpha(0.95f)
                                .targetCircleColor(R.color.white)
                                .textColor(R.color.FIRST_SLIDE_TEXT_ONE)
                                .descriptionTextColor(R.color.FIRST_SLIDE_TEXT_TWO)
                                .descriptionTextSize(20)
                                .descriptionTextAlpha(1)
                                .titleTextSize(30)
                                .tintTarget(false)
                                .drawShadow(true)
                                .titleTypeface(myTypeface1)
                                .descriptionTypeface(myTypeface2)
                                .transparentTarget(false)
                                .cancelable(false)
                                .id(1)
                                .targetRadius(90)
                        ,                  // Specify the target radius (in dp),
                        TapTarget.forView(missedDeadlinesTile, "Missed Project Deadline?", "Do not worry you are covered, get quick access to all your due deadlines")
                                .outerCircleColor(R.color.SECOND_SLIDE)
                                .outerCircleAlpha(0.95f)
                                .targetCircleColor(R.color.white)
                                .textColor(R.color.SECOND_SLIDE_TEXT_ONE)
                                .descriptionTextColor(R.color.SECOND_SLIDE_TEXT_TWO)
                                .descriptionTextSize(20)
                                .descriptionTextAlpha(1)
                                .titleTextSize(30)
                                .tintTarget(false)
                                .drawShadow(true)
                                .titleTypeface(myTypeface1)
                                .descriptionTypeface(myTypeface2)
                                .transparentTarget(false)
                                .cancelable(false)
                                .id(2)
                                .cancelable(false)
                                .targetRadius(90),
                        TapTarget.forView(taskTrackerTile, "Track your Daily Activity", "Track how many steps are completed everyday! \n\nNote: Click anywhere else to exit the instructions")
                                .outerCircleColor(R.color.THIRD_SLIDE)
                                .outerCircleAlpha(0.95f)
                                .targetCircleColor(R.color.white)
                                .textColor(R.color.THIRD_SLIDE_TEXT_ONE)
                                .descriptionTextColor(R.color.THIRD_SLIDE_TEXT_TWO)
                                .descriptionTextSize(20)
                                .descriptionTextAlpha(1)
                                .titleTextSize(30)
                                .tintTarget(false)
                                .drawShadow(true)
                                .titleTypeface(myTypeface1)
                                .descriptionTypeface(myTypeface2)
                                .transparentTarget(false)
                                .cancelable(false)
                                .id(3)
                                .cancelable(true)
                                .targetRadius(64),
                        TapTarget.forView(addNewProjectIconRelativeLayout, "And so many more features to help you get those tasks done!\nLets get started", "Create your first project! \n\nNote: Click anywhere else to exit the instructions")
                                .outerCircleColor(R.color.FOURTH_SLIDE)
                                .outerCircleAlpha(0.95f)
                                .targetCircleColor(R.color.white)
                                .textColor(R.color.FOURTH_SLIDE_ONE)
                                .descriptionTextColor(R.color.FOURTH_SLIDE_TWO)
                                .descriptionTextSize(20)
                                .descriptionTextAlpha(1)
                                .titleTextSize(30)
                                .tintTarget(false)
                                .drawShadow(true)
                                .titleTypeface(myTypeface1)
                                .descriptionTypeface(myTypeface2)
                                .transparentTarget(true)
                                .cancelable(true)
                                .id(5)
                                .targetRadius(50))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {

                       instructionsGivenEditor.putBoolean("mainPageInstructions", true);
                       instructionsGivenEditor.apply();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        if(targetClicked && lastTarget.id()==5){
                            openAddNewTasks();
                        }
                        instructionsGivenEditor.putBoolean("mainPageInstructions", true);
                        instructionsGivenEditor.apply();

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        instructionsGivenEditor.putBoolean("mainPageInstructions", true);
                        instructionsGivenEditor.apply();
                    }
                }).start();






    }

    private void openSettingsActivity() {
        Intent openSettingsActivityIntent = new Intent(this, SettingsActivity.class);
        startActivity(openSettingsActivityIntent);
    }

    public void getDueTileDetails(){
        deadlines = myDB.getAllPendingProjects();
        int dueProjects = getDueProjectsRemaining(deadlines);
        if(dueProjects>0){
            numberOfDueProjectsTextView.setText(Integer.toString(dueProjects));
            numberOfDueProjectsTextView.setTextColor(Color.parseColor("#C0392B"));
            relaxTextView.setText("Oh No!");
            relaxTextView.setTextColor(Color.parseColor("#D98880"));
            String silly;
            if(dueProjects == 1){
                silly = "Seems like 1 Project is due!";
            }else{
                silly = dueProjects+" Projects are due";
            }
            relaxTextViewDescription.setText(silly);
            deadlineDueImageView.setImageResource(R.drawable.tensed_panda);
            missedDeadlinesTile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(vibrationsEnabled){
                        vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                    }
                    openDueProjects();
                }
            });
        }else{
            relaxTextView.setTextColor(Color.parseColor("#B2D8B2"));
            numberOfDueProjectsTextView.setText("0");
            numberOfDueProjectsTextView.setTextColor(Color.parseColor("#FBFBFB"));
            relaxTextView.setText("Relax!");
            String silly = "No deadlines are due, Hurray!";
            relaxTextViewDescription.setText(silly);
            deadlineDueImageView.setImageResource(R.drawable.relaxed_panda_sleeping);
        }

    }

    private void openDueProjects() {
        Intent openDueIntent = new Intent(this, DueProjects.class);
        startActivity(openDueIntent);
    }

    public int getDueProjectsRemaining(ArrayList<ProjectModel> deadlines){
        int dueProjects=0;
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
                dueProjects++;
            }
        }
        return dueProjects;
    }

    public static boolean hasTimePassed(String dateString) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy, HH.mm");
        Date date = formatter.parse(dateString);
        return date.before(new Date());
    }

    private void makeTheGraph(){
        //myDB.updateOrInsertTask(getTodayDate(), 2,5);
        int average =0;
        ArrayList<tasksModel> numbers = myDB.getLastSevenDaysTasks();
        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i=0;i<numbers.size();i++){
              average = average+ numbers.get(i).getSteps();
              entries.add(new BarEntry(i, numbers.get(numbers.size() - (i+1)).getSteps()));
        }
        if(average == 0){
            noGraphActivityYetImageView.setVisibility(View.VISIBLE);
        }else{
            noGraphActivityYetImageView.setVisibility(View.GONE);
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Steps");
        barDataSet.setColor(Color.parseColor("#C0EEE4")); // set bar color
        barDataSet.setDrawValues(false); // remove values from bars
        barDataSet.setBarBorderWidth(0.1f); // set bar border width
        barDataSet.setHighlightEnabled(true); // disable bar highlighting
        barDataSet.setDrawValues(false);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.3f);


        XAxis xAxis = trackerBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.parseColor("#B3E8D9"));
        xAxis.setTextSize(10f);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.getDefault());

            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_WEEK, (int)value);
                Date date = calendar.getTime();
                if (value == 6) {
                    return "Today";
                } else if (value == 5) {
                    return "Yest";
                } else {
                    return sdf.format(date);
                }
            }
        });
        trackerBarChart.getAxisRight().setEnabled(false);
        trackerBarChart.getAxisLeft().setTextColor(Color.parseColor("#B3E8D9"));
        trackerBarChart.getLegend().setEnabled(false);
        trackerBarChart.getDescription().setEnabled(false);
        trackerBarChart.getAxisLeft().setDrawGridLines(false);
        trackerBarChart.setData(barData);
        trackerBarChart.invalidate();
        trackerBarChart.animateY(2000);
    }

    private void setTheProjectProgress(int projectID) {
        int newAverage = 0;
        if(projectID>=0){
            ArrayList<Integer> phaseIDs = myDB.getPhaseIDsForProject(projectID);
            int average = 0;

            for(int i=0;i<phaseIDs.size();i++){
                average = average+myDB.getAverageStepProgress(phaseIDs.get(i), projectID);
            }

            if(phaseIDs.size() > 0) {
                projectProgressTotalBar.setProgress(average/phaseIDs.size());
                projectProgressTotalBar.invalidate();
            }else{
                projectProgressTotalBar.setProgress(0);
                projectProgressTotalBar.invalidate();
            }

        }else{
            projectProgressTotalBar.setProgress(0);
            projectProgressTotalBar.invalidate();
        }


    }

    private void setTotalPhasesProgress(int projectID) {
        if(projectID>=0) {
            int TotalPhasesOfProjectNumber = myDB.getPhaseCountForProject(projectID);
            int TotalCompletedProjects = myDB.getCompletedPhasesCountForProject(projectID);
            int completion = Math.round(100.0f*((100.0f * TotalCompletedProjects)/(100.0f* TotalPhasesOfProjectNumber)));
            projectTotalPhasesBar.setProgress(completion);
            projectTotalPhasesBar.invalidate();
            String s = TotalCompletedProjects +"/"+ TotalPhasesOfProjectNumber;
            TotalPhasesOfProject.setText(s);
        }else{
            projectTotalPhasesBar.setProgress(0);
            projectTotalPhasesBar.invalidate();
            TotalPhasesOfProject.setText("0");
        }






    }

    private void uploadDatabaseToFireBase() {
        if(theFlattenedString.length() == 0){
            rootDatabaseReference.child("userDatabase").setValue("0").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                }
            });
        }else{
            rootDatabaseReference.child("userDatabase").setValue(theFlattenedString).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                }
            });
        }

    }

    private void openPendingTasks() {
        Intent openPendingTasksIntent = new Intent(MainActivity.this, PendingProjects.class);
        startActivity(openPendingTasksIntent);
    }

    private void openAddNewTasks() {
        Intent openAddNewTasksIntent = new Intent(MainActivity.this, AddNewProjects.class);
        startActivity(openAddNewTasksIntent);
    }

    private void openCompletedTasks() {
        Intent openCompletedTasksIntent = new Intent(MainActivity.this, CompletedProjects.class);
        startActivity(openCompletedTasksIntent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        vibrationsEnabled = vibrationDetails.getBoolean("vibrations", true);
        deadlines = myDB.getAllPendingProjects();
        if(deadlines.size() == 0){
            highestArcodionTextView.setVisibility(View.VISIBLE);
            deadlineArcodionTextView.setVisibility(View.VISIBLE);
        }else{
            highestArcodionTextView.setVisibility(View.GONE);
            deadlineArcodionTextView.setVisibility(View.GONE);
        }
        if(deadlines.size()>0){
            sortedDates = (ArrayList<ProjectModel>) DatesSorter.sortProjectsByDeadline(deadlines);
            ProjectModel nextProject1 = new ProjectModel();
            if(sortedDates.size()>0){
                nextProject1 = sortedDates.get(0);
            }
            int size = sortedDates.size();
            String silly;
            if(size >9){
                silly = Integer.toString(size)+"+";
            }else{
                silly = Integer.toString(size);
            }
            totalPendingProjectsTextView.setText(silly);
            String nextProjectName = nextProject1.getProjectName();
            if(nextProjectName.length()>16){
                nextProjectNameTextView.setText(nextProjectName.substring(0,16)+"..");
            }else{
                nextProjectNameTextView.setText(nextProjectName);
            }
            setTotalPhasesProgress(nextProject1.getProjectID());
            setTheProjectProgress(nextProject1.getProjectID());
        }else{
            projectTotalPhasesBar.setProgress(0);
            projectTotalPhasesBar.invalidate();
            TotalPhasesOfProject.setText("0");
            projectProgressTotalBar.setProgress(0);
            projectProgressTotalBar.invalidate();
        }

        getDueTileDetails();
        reloadLists();
        makeTheGraph();
        highImportanceDropDownArrow.setRotation(90);
        earliestDropDownArrow.setRotation(90);
    }

    private void reloadLists() {
        importanceAdapter.notifyDataSetChanged();
        deadlineAdapter.notifyDataSetChanged();
        if(deadlineArcodion.getVisibility() == View.VISIBLE){
            deadlineArcodion.setVisibility(View.GONE);
        }
        if(highestArcodion.getVisibility() == View.VISIBLE) {
            highestArcodion.setVisibility(View.GONE);
        }

        if(userName ==null || userName.equals("none")){
            userName = userLoginDetails.getString("USER_NAME", "none");
        }
    }

    private String flattenTheDatabase(){
            try {
            ArrayList<String> allDataBase = new ArrayList<>();
            ArrayList<ProjectModel> projectsList = myDB.getAllTheProjects();

            StringBuilder projects = new StringBuilder();
            StringBuilder phases = new StringBuilder();
            StringBuilder steps = new StringBuilder();
            for(ProjectModel project: projectsList) {
                projects.append(Integer.toString(project.getProjectID())).append(":-basic-Separator-:").
                        append(project.getProjectName()).append(":-basic-Separator-:").
                        append(project.getProjectStart()).append(":-basic-Separator-:").
                        append(project.getProjectDeadline()).append(":-basic-Separator-:").
                        append(Integer.toString(project.getProjectPhases())).append(":-basic-Separator-:").
                        append(project.getProjectImportance()).append(":-basic-Separator-:").
                        append(Integer.toString(project.getProjectCompleted())).append(":-basic-Separator-:").
                        append(project.getProjectTag()).append(":-project-Separator-:");

                ArrayList<phasesModelClass> phasesList = myDB.getAllPhasesForaProject(project.getProjectID());
                for(phasesModelClass phase: phasesList) {
                    phases.append(Integer.toString(phase.getPhaseID())).append(":-basic-Separator-:").
                            append(Integer.toString(phase.getProjectID())).append(":-basic-Separator-:").
                            append(phase.getPhaseName()).append(":-basic-Separator-:").
                            append(phase.getPhaseDeadline()).append(":-basic-Separator-:").
                            append(phase.getPhasePriority()).append(":-basic-Separator-:").
                            append(phase.getPhaseNotificationDetails()).append(":-basic-Separator-:").
                            append(Integer.toString(phase.getPhaseTotalSteps())).append(":-basic-Separator-:").
                            append(phase.getPhaseType()).append(":-basic-Separator-:").
                            append(Integer.toString(phase.isDeadlineSet())).append(":-basic-Separator-:").
                            append(Integer.toString(phase.isNotificationSet())).append(":-basic-Separator-:").
                            append(Integer.toString(phase.isPhaseCompleted())).append(":-phase-Separator-:");

                    ArrayList<newStepsModel> stepsList = myDB.getAllStepsForaPhase(phase.getPhaseID(), phase.getProjectID());
                    for(newStepsModel step: stepsList) {
                        steps.append(Integer.toString(step.getStepID())).append(":-basic-Separator-:").
                                append(step.getStepName()).append(":-basic-Separator-:").
                                append(Integer.toString(step.getPhaseID())).append(":-basic-Separator-:").
                                append(step.getStepDeadline()).append(":-basic-Separator-:").
                                append(Integer.toString(step.getStepProgress())).append(":-basic-Separator-:").
                                append(step.getStepNotes()).append(":-basic-Separator-:").
                                append(step.getStepNotificationDetails()).append(":-basic-Separator-:").
                                append(Integer.toString(step.isNotificationSet())).append(":-basic-Separator-:").
                                append(Integer.toString(step.isDeadlineSet())).append(":-basic-Separator-:").
                                append(Integer.toString(step.isStepCompleted())).append(":-step-Separator-:");
                    }
                    steps.append("::PHASE--INDICATOR::");
                }
                phases.append("::PROJECT--INDICATOR::");
                steps.append("::PROJECT--INDICATOR::");
            }

            allDataBase.add(projects.toString());
            allDataBase.add(phases.toString());
            allDataBase.add(steps.toString());


            StringBuilder finalString = new StringBuilder();
            finalString.append(projects).append("~~~FIRST##SEPARATOR~~~").append(phases).append("~~~FIRST##SEPARATOR~~~").append(steps);
            return finalString.toString();

        }catch(Exception e){
            e.printStackTrace();
            return "0";
        }

    }

    private void unFlattenAndPopulateDatabase(String theString){
        if(theString.isEmpty() || theString.equals("0")){
            return;
        }else {
            try {
                ArrayList<Integer> projectsID = new ArrayList<>();
                String[] spittedString = theString.split("~~~FIRST##SEPARATOR~~~");
                String projects = spittedString[0];
                String[] spiltProjects = projects.split(":-project-Separator-:");
                for (int i = 0; i < spiltProjects.length; i++) {
                    String eachProject = spiltProjects[i];
                    String[] projectInfo = eachProject.split(":-basic-Separator-:");
                    ProjectModel newProject = new ProjectModel();
                    for (int j = 0; j < projectInfo.length; j++) {
                        newProject.setProjectName(projectInfo[1]);
                        newProject.setProjectStart(projectInfo[2]);
                        newProject.setProjectDeadline(projectInfo[3]);
                        newProject.setProjectPhases(Integer.parseInt(projectInfo[4]));
                        newProject.setProjectImportance(projectInfo[5]);
                        newProject.setProjectCompleted(Integer.parseInt(projectInfo[6]));
                        newProject.setProjectTag(projectInfo[7]);
                    }
                    int newProjectID = (int) myDB.insertProjectData(newProject.getProjectName(), newProject.getProjectStart(),
                            newProject.getProjectDeadline(), newProject.getProjectPhases(), newProject.getProjectImportance(), newProject.getProjectCompleted()
                            , newProject.getProjectTag());
                    projectsID.add(newProjectID);
                    createThePhaseTable(newProjectID);
                }


                String phases = spittedString[1];
                String[] spilInduvialtProjects = phases.split("::PROJECT--INDICATOR::");
                for (int i = 0; i < projectsID.size(); i++) {
                    String eachProjectPhases = spilInduvialtProjects[i];
                    String[] eachPhase = eachProjectPhases.split(":-phase-Separator-:");
                    for (int j = 0; j < eachPhase.length; j++) {
                        String phaseValues = eachPhase[j];
                        String[] eachphaseValues = phaseValues.split(":-basic-Separator-:");
                        phasesModelClass phase = new phasesModelClass();
                        for (int k = 0; k < eachphaseValues.length; k++) {
                            phase.setPhaseName(eachphaseValues[2]);
                            phase.setPhaseDeadline(eachphaseValues[3]);
                            phase.setPhasePriority(eachphaseValues[4]);
                            phase.setPhaseNotificationDetails(eachphaseValues[5]);
                            phase.setPhaseTotalSteps(Integer.parseInt(eachphaseValues[6]));
                            phase.setPhaseType(eachphaseValues[7]);
                            phase.setDeadlineSet(Integer.parseInt(eachphaseValues[8]));
                            phase.setNotificationSet(Integer.parseInt(eachphaseValues[9]));
                            phase.setPhaseCompleted(Integer.parseInt(eachphaseValues[10]));
                        }
                        int newPhaseID = (int) myDB.insertPhaseData(projectsID.get(i), phase.getPhaseName(), phase.getPhaseDeadline(),
                                phase.getPhasePriority(), phase.getPhaseNotificationDetails(), phase.getPhaseTotalSteps(), phase.getPhaseType(),
                                phase.isDeadlineSet(), phase.isNotificationSet(), phase.isPhaseCompleted());
                        createStepTableForEachPhase(newPhaseID, projectsID.get(i));


                    }
                }

                String steps = spittedString[2];
                String[] stepsForEachProject = steps.split("::PROJECT--INDICATOR::");
                for (int q = 0; q < projectsID.size(); q++) {
                    String eachStepsForEachPhase = stepsForEachProject[q];
                    String[] stepsForEachPhase = eachStepsForEachPhase.split("::PHASE--INDICATOR::");
                    for (int w = 0; w < stepsForEachPhase.length; w++) {
                        String eachInduviualSeparateStep = stepsForEachPhase[w];
                        String[] eachSeparateStep = eachInduviualSeparateStep.split(":-step-Separator-:");
                        for (int e = 0; e < eachSeparateStep.length; e++) {
                            String theStep = eachSeparateStep[e];
                            String[] eachValueOfTheStep = theStep.split(":-basic-Separator-:");
                            newStepsModel step = new newStepsModel();
                            for (int r = 0; r < eachValueOfTheStep.length; r++) {
                                step.setStepName(eachValueOfTheStep[1]);
                                step.setStepDeadline(eachValueOfTheStep[3]);
                                step.setStepProgress(Integer.parseInt(eachValueOfTheStep[4]));
                                step.setStepNotes(eachValueOfTheStep[5]);
                                step.setStepNotificationDetails(eachValueOfTheStep[6]);
                                step.setNotificationSet(Integer.parseInt(eachValueOfTheStep[7]));
                                step.setDeadlineSet(Integer.parseInt(eachValueOfTheStep[8]));
                                step.setStepCompleted(Integer.parseInt(eachValueOfTheStep[9]));
                            }
                            //enter here with q as project w as phase
                            myDB.insertStepData((w + 1), (projectsID.get(q)), step.getStepName(), step.getStepDeadline(), step.getStepProgress(), step.getStepNotes(),
                                    step.getStepNotificationDetails(), step.isDeadlineSet(), step.isNotificationSet(), step.isStepCompleted());

                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        getDueTileDetails();
        makeTheGraph();
        ProjectModel nextProject = new ProjectModel();
        deadlines = myDB.getAllPendingProjects();
        sortedDates = (ArrayList<ProjectModel>) DatesSorter.sortProjectsByDeadline(deadlines);
        if(sortedDates.size()>0){
            isNextProjectAvailable = true;
            nextProject = sortedDates.get(0);
            int size = sortedDates.size();
            String silly;
            if(size >9){
                silly = Integer.toString(size)+"+";
            }else{
                silly = Integer.toString(size);
            }
            totalPendingProjectsTextView.setText(silly);
            String nextProjectName = nextProject.getProjectName();
            if(nextProjectName.length()>16){
                nextProjectNameTextView.setText(nextProjectName.substring(0,16)+"..");
            }else{
                nextProjectNameTextView.setText(nextProjectName);
            }
            setTotalPhasesProgress(nextProject.getProjectID());
            setTheProjectProgress(nextProject.getProjectID());
        }else{
            isNextProjectAvailable = false;
            nextProject = new ProjectModel(-1,"",
                    "None", "None", 0,
                    "None", 0, "None");
            totalPendingProjectsTextView.setText("0");
            nextProjectNameTextView.setText(" ");
            projectTotalPhasesBar.setProgress(0);
            projectTotalPhasesBar.invalidate();
            TotalPhasesOfProject.setText("0");
            projectProgressTotalBar.setProgress(0);
            projectProgressTotalBar.invalidate();
        }
        isDataLoaded = true;
    }

    private void createThePhaseTable(int projectID) {
        final String CREATE_TABLE_PHASES =
                "CREATE TABLE IF NOT EXISTS " + PHASE_TABLE_PREFIX + projectID + "(" + PHASE_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + PHASE_NAME + " TEXT,"
                        + PHASE_PROJECT_ID + " INTEGER,"
                        + PHASE_DEADLINE + " TEXT,"
                        + PHASE_PRIORITY + " TEXT,"
                        + PHASE_NOTIFICATION + " TEXT,"
                        + PHASE_STEPS + " INTEGER,"
                        + PHASE_TYPE + " TEXT,"
                        + PHASE_DEADLINE_SET + " INTEGER,"
                        + PHASE_NOTIFICATION_SET + " INTEGER,"
                        + PHASE_IS_COMPLETED + " INTEGER,"
                        + "FOREIGN KEY(" + PHASE_PROJECT_ID + ") REFERENCES "
                        + PROJECT_TABLE + "(" + PROJECT_KEY_ID + ") ON DELETE CASCADE);";

        String dbPath = this.getDatabasePath(DATABASE_NAME).getPath();
        try (SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbPath, null)) {
            db.execSQL(CREATE_TABLE_PHASES);
        } catch (SQLException E) {
            E.printStackTrace();
        }



    }

    private void createStepTableForEachPhase(int phaseID, int projectID){
            final String CREATE_TABLE_STEPS =
                    "CREATE TABLE IF NOT EXISTS "+STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID+"("+STEP_KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + STEP_PHASE_ID+ " INTEGER,"
                            + STEP_NAME+ " TEXT,"
                            + STEP_DEADLINE+ " TEXT,"
                            + STEP_PROGRESS+ " INTEGER,"
                            + STEP_NOTES+ " TEXT,"
                            + STEP_NOTIFICATION+ " TEXT,"
                            + STEP_DEADLINE_SET+ " INTEGER,"
                            + STEP_NOTIFICATION_SET+ " INTEGER,"
                            + STEP_IS_STEP_COMPLETED+ " INTEGER,"
                            + "FOREIGN KEY(" +STEP_KEY_ID+ ") REFERENCES "
                            + PHASE_TABLE_PREFIX+phaseID + "(" + PHASE_KEY_ID + ") ON DELETE CASCADE);";

            String dbPath = this.getDatabasePath(DATABASE_NAME).getPath();
            try(SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbPath, null)){
                db.execSQL(CREATE_TABLE_STEPS);
            }catch (SQLException E){
                E.printStackTrace();
            }
        }

    private String getTodayDate(){
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return today.format(formatter);
    }

    private static boolean isDeviceConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo!= null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;

        trackerBarChart.getBarData().setHighlightEnabled(true);
        trackerBarChart.highlightValue(h);
        h.toString();
        int steps = (int)  e.getY();
        if(steps<0){
            int newStpes = -1*steps;
            if(newStpes == 1){
                String ToastString = "1 completed step was undone on this day";
                Toast.makeText(this, ToastString, Toast.LENGTH_LONG).show();
            }else{
                String ToastString = -1*steps + " completed steps were undone on this day";
                Toast.makeText(this, ToastString, Toast.LENGTH_LONG).show();
            }

        }else {
            String ToastString = steps + " Steps completed on this day";
            Toast.makeText(this, ToastString, Toast.LENGTH_SHORT).show();
        }
    }

    public String getGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 4 && hour < 12) {
            return "Good \nMorning!";
        } else if (hour >= 12 && hour < 17) {
            return "Good \nAfternoon!";
        } else {
            return "Good \nEvening!";
        }
    }

    @Override
    public void onNothingSelected() {
        trackerBarChart.highlightValue(null);
    }

    @Override
    public void onBackPressed() {
        if(doubleClickedForBack){
            super.onBackPressed();
            return;
        }
        Toast.makeText(this, "Please Press Back Again To Exit", Toast.LENGTH_SHORT).show();
        doubleClickedForBack = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleClickedForBack = false;
            }
        },2500);
    }
}