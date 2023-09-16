package com.the21codes.do_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class phasesForProjectActivity extends AppCompatActivity {

    RecyclerView allStepsForPhaseRecyclerView;
    phasesForProjectAdapter phasesForProjectAdapter;
    ArrayList<phasesModelClass> phasesList;
    DataBaseHelper myDB;
    TextView projectNameTextView, projectDeadLineTextView, daysToGoTextView, daysToGoDescriptionTextView,
            projectImportanceTextView, projectProgressState, detailsTextView, projectTagTextView, isDateDueTextView;
    int projectID=-1;
    Button addPhaseToTheProjectButton;
    RelativeLayout detailsDropRelativeLayout, editProjectNameRelativeLayout, projectImportanceRelativeLayout;
    LinearLayout otherDetailsLinearLayouts;
    EditText stepTitleEditText;
    ImageView showDetailsDropDownArrow, deleteThisProjectImageView, no_phases_panda_image;
    ArcProgress projectProgressTotalBar;
    ProjectModel currentProject;
    private Handler mHandler = new Handler();
    private static final int DELAY = 2000; // milliseconds


    private static final String DATABASE_NAME = "DO_IT_DATABASE";
    private static final String PHASE_TABLE_PREFIX= "PHASE_TABLE_";
    private static final String STEPS_TABLE_PREFIX = "STEPS_TABLE_";
    private static final String PHASE_KEY_ID  = "id";
    private static final String STEP_KEY_ID  = "id";
    private static final String STEP_NAME = "STEP_NAME";
    private static final String STEP_PHASE_ID = "PHASE_ID";
    private static final String STEP_DEADLINE = "STEP_DEADLINE";
    private static final String STEP_PROGRESS = "STEP_PROGRESS";
    private static final String STEP_NOTES = "STEP_NOTES";
    private static final String STEP_NOTIFICATION = "STEP_NOTIFICATION";
    private static final String STEP_DEADLINE_SET = "STEP_DEADLINE_SET";
    private static final String STEP_NOTIFICATION_SET = "STEP_NOTIFICATION_SET";
    private static final String STEP_IS_STEP_COMPLETED = "STEP_IS_COMPLETED";
    public boolean projectAvailable = false;
    AdRequest adRequest;
    InterstitialAd mInterstitialAd;

    SharedPreferences adsCounterDetails;
    SharedPreferences.Editor adsCounterDetailsEditor;
    int adCounter;

    SharedPreferences vibrationDetails;
    SharedPreferences.Editor vibrationDetailsEditor;
    Boolean vibrationsEnabled = false;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phases_for_project);
        myDB = new DataBaseHelper(this);
        Intent intent = getIntent();
        String action = intent.getAction();

        if(action.equals("Notification_Intent_Phase")){
            projectID = intent.getIntExtra("NOTIFICATION_PROJECT_ID", -1);
        }else{
            projectID = intent.getIntExtra("projectID", -1);
        }

        if(projectID == -1){
            projectAvailable = false;
            phasesList = new ArrayList<>();
            currentProject = new ProjectModel(-1,"Project", "NA", "NA",
                    0,"Low",0,"NA");
        }else{
            projectAvailable = true;
            phasesList = myDB.getAllPhasesForaProject(projectID);
            currentProject = myDB.getAllProjectData(projectID);
        }

        adsCounterDetails = getSharedPreferences("UserLoginAndNameDetails", MODE_PRIVATE);
        adCounter = adsCounterDetails.getInt("Phases_page_ad", 0);
        adsCounterDetailsEditor = adsCounterDetails.edit();
        if(isDeviceConnected(getApplicationContext())) {
            adRequest = new AdRequest.Builder().build();
            if (adCounter <= 5) {
                adCounter++;
                adsCounterDetailsEditor.putInt("Phases_page_ad", adCounter);
                adsCounterDetailsEditor.apply();
            } else {
                adCounter = 0;
                adsCounterDetailsEditor.putInt("Phases_page_ad", 0);
                adsCounterDetailsEditor.apply();
                showInterstitialAd();
            }
        }

        vibrationDetails = getSharedPreferences("vibrationPreferences", MODE_PRIVATE);
        vibrationDetailsEditor = vibrationDetails.edit();
        vibrationsEnabled = vibrationDetails.getBoolean("vibrations", true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        allStepsForPhaseRecyclerView = (RecyclerView) findViewById(R.id.allStepsForPhaseRecyclerView);
        phasesForProjectAdapter = new phasesForProjectAdapter(phasesList, phasesForProjectActivity.this);
        allStepsForPhaseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        allStepsForPhaseRecyclerView.setAdapter(phasesForProjectAdapter);
        phasesForProjectAdapter.notifyDataSetChanged();
        detailsDropRelativeLayout = (RelativeLayout) findViewById(R.id.detailsDropRelativeLayout);
        otherDetailsLinearLayouts = (LinearLayout) findViewById(R.id.otherDetailsLinearLayouts);
        projectNameTextView = (TextView) findViewById(R.id.projectNameTextView);
        projectDeadLineTextView = (TextView) findViewById(R.id.projectDeadLineTextView);
        daysToGoTextView = (TextView) findViewById(R.id.daysToGoTextView);
        daysToGoDescriptionTextView = (TextView) findViewById(R.id.daysToGoDescriptionTextView);
        projectImportanceTextView = (TextView) findViewById(R.id.projectImportanceTextView);
        projectProgressState = (TextView) findViewById(R.id.projectProgressState);
        projectTagTextView = (TextView) findViewById(R.id.projectTagTextView);
        showDetailsDropDownArrow = (ImageView) findViewById(R.id.showDetailsDropDownArrow);
        no_phases_panda_image = (ImageView) findViewById(R.id.no_phases_panda_image);
        deleteThisProjectImageView = (ImageView) findViewById(R.id.deleteThisProjectImageView);
        stepTitleEditText = (EditText) findViewById(R.id.stepTitleEditText);
        editProjectNameRelativeLayout = (RelativeLayout) findViewById(R.id.editProjectNameRelativeLayout);
        projectProgressTotalBar = findViewById(R.id.projectProgressTotalBar);
        isDateDueTextView = findViewById(R.id.isDateDueTextView);
        projectImportanceRelativeLayout = (RelativeLayout) findViewById(R.id.projectImportanceRelativeLayout);
        addPhaseToTheProjectButton = (Button) findViewById(R.id.addPhaseToTheProjectButton);
        detailsTextView = (TextView) findViewById(R.id.detailsTextView);
        if(phasesList.size() <=0 || !projectAvailable){
            no_phases_panda_image.setVisibility(View.VISIBLE);
        }else{
            no_phases_panda_image.setVisibility(View.GONE);
        }


        setTheProjectProgress(projectID);
        setAllProjectData();
        if(projectAvailable){
            projectTagTextView.setText(currentProject.getProjectTag());
        }else{
            projectTagTextView.setText("NA");
        }

        Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.accordion_drop_down);
        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.accordion_drop_up);
        showDetailsDropDownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(10,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                if (otherDetailsLinearLayouts.getVisibility() == View.GONE) {
                    otherDetailsLinearLayouts.setVisibility(View.VISIBLE);
                    projectNameTextView.setVisibility(View.GONE);
                    editProjectNameRelativeLayout.setVisibility(View.VISIBLE);
                    otherDetailsLinearLayouts.startAnimation(slideDown);
                    showDetailsDropDownArrow.animate().rotationBy(-90).setDuration(200).start();
                    detailsTextView.setVisibility(View.GONE);
                    deleteThisProjectImageView.setVisibility(View.VISIBLE);
                } else {
                    otherDetailsLinearLayouts.startAnimation(slideUp);
                    otherDetailsLinearLayouts.setVisibility(View.GONE);
                    projectNameTextView.setVisibility(View.VISIBLE);
                    editProjectNameRelativeLayout.setVisibility(View.GONE);
                    showDetailsDropDownArrow.animate().rotationBy(90).setDuration(200).start();
                    detailsTextView.setVisibility(View.VISIBLE);
                    deleteThisProjectImageView.setVisibility(View.GONE);
                }
            }
        });
        projectDeadLineTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }

                openDateChanger();
            }
        });
        projectImportanceRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                String s = projectImportanceTextView.getText().toString().trim();
                if(s.equals("Low")){
                    projectImportanceTextView.setText("Medium");
                    projectImportanceRelativeLayout.setBackgroundResource(R.drawable.medium_badge_background);
                    if(projectAvailable){
                        myDB.updateProjectImportance(projectID, "Medium");
                    }
                } else if (s.equals("Medium")) {
                    projectImportanceTextView.setText("High");
                    projectImportanceRelativeLayout.setBackgroundResource(R.drawable.high_badge_background);
                    if(projectAvailable){
                        myDB.updateProjectImportance(projectID, "High");
                    }
                } else if (s.equals("High")) {
                    projectImportanceTextView.setText("Low");
                    projectImportanceRelativeLayout.setBackgroundResource(R.drawable.green_badge_background);
                    if(projectAvailable){
                        myDB.updateProjectImportance(projectID, "Low");
                    }
                }else{
                    projectImportanceTextView.setText("Low");
                    projectImportanceRelativeLayout.setBackgroundResource(R.drawable.green_badge_background);
                    if(projectAvailable){
                        myDB.updateProjectImportance(projectID, "Low");
                    }
                }
            }
        });
        addPhaseToTheProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                addNewPhase();
            }
        });
        stepTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mHandler.removeCallbacks(mRunnable);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mHandler.removeCallbacks(mRunnable); // remove any pending callbacks
                mHandler.postDelayed(mRunnable, DELAY); // post a new callback with delay
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        deleteThisProjectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(15,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                String projectName = currentProject.getProjectName();
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(phasesForProjectActivity.this);
                deleteDialog.setTitle("Delete The Project: "+projectName+"?");
                deleteDialog.setMessage("This will delete the project and all its phases and steps as well. The changes can not be Undone?");
                deleteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                deleteDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(projectAvailable){
                            int theDeletingProjectID = projectID;
                            ArrayList<phasesModelClass> phases = myDB.getAllPhasesForaProject(projectID);
                            for(int j=0; j<phases.size();j++){
                                int currentPhaseID = phases.get(j).getPhaseID();
                                myDB.deleteEntireStepTable(currentPhaseID, theDeletingProjectID);
                            }

                            myDB.deleteEntirePhaseTable(theDeletingProjectID);
                            myDB.deleteProject(theDeletingProjectID);

                        }else{
                            Toast.makeText(phasesForProjectActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                        }

                        dialogInterface.dismiss();
                        onBackPressed();
                        finish();
                    }
                });

                AlertDialog DELETEDialog = deleteDialog.create();
                DELETEDialog.show();
            }
        });

    }

    private void showInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-6952672354974833/4662144981", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;

                        mInterstitialAd.show(phasesForProjectActivity.this);
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
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            String text = stepTitleEditText.getText().toString();
            projectNameTextView.setText(text);
            try{
                myDB.updateProjectName(projectID, text);
            }catch (Exception e){
                Toast.makeText(phasesForProjectActivity.this, "Could Not Update the project Name", Toast.LENGTH_SHORT).show();
            }

        }
    };
    private void addNewPhase() {
        if(projectID<0){
            Toast.makeText(this, "Something Went Wrong\nCant Add A new Phase!", Toast.LENGTH_LONG).show();
        }else{
            int currentPhasesNumber = myDB.getPhaseCountForProject(projectID);
            currentPhasesNumber += 1;
            phasesModelClass newPhase = new phasesModelClass("Phase "+currentPhasesNumber, projectID, "Set Deadline", "Low",
                    "NOT_SET", 1, currentProject.getProjectTag(), 0,0,0);

            int newPhaseID = insertNewPhase(projectID, newPhase);
            createStepTableForEachPhase(newPhaseID, projectID);
            newStepsModel newStepsModel = new newStepsModel("Step 1", newPhaseID, "Set Deadline", 0, "Write your notes here!", "NOT_SET", 0,0,0);
            insertNewStep(newPhaseID, projectID, newStepsModel);
            newPhase.setPhaseID(newPhaseID);
            phasesList.add(newPhase);
            phasesForProjectAdapter = new phasesForProjectAdapter(phasesList, phasesForProjectActivity.this);
            allStepsForPhaseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            allStepsForPhaseRecyclerView.setAdapter(phasesForProjectAdapter);
            phasesForProjectAdapter.notifyDataSetChanged();
            allStepsForPhaseRecyclerView.smoothScrollToPosition(phasesList.size()-1);
            setTheProjectProgress(projectID);
        }
        if(phasesList.size()>0){
            no_phases_panda_image.setVisibility(View.GONE);
        }else{
            no_phases_panda_image.setVisibility(View.VISIBLE);
        }
    }
    private void insertNewStep(int newerPhaseID, int projectID, newStepsModel newStep){
        myDB.insertStepData(newerPhaseID, projectID,
                newStep.getStepName(), newStep.getStepDeadline(), newStep.getStepProgress(),
                newStep.getStepNotes(), newStep.getStepNotificationDetails(),
                newStep.isDeadlineSet(), newStep.isNotificationSet(), newStep.isStepCompleted());
    }
    private int insertNewPhase(int projectID, phasesModelClass newPhase){
        return (int) myDB.insertPhaseData(projectID, newPhase.getPhaseName(),
                newPhase.getPhaseDeadline(), newPhase.getPhasePriority(),
                newPhase.getPhaseNotificationDetails(), newPhase.getPhaseTotalSteps(),
                newPhase.getPhaseType(), newPhase.isDeadlineSet(),
                newPhase.isNotificationSet(), newPhase.isPhaseCompleted());

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
    private void openDateChanger() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Change Project Deadline?");
        alertDialogBuilder.setMessage("This will change the Project Deadline.");
        alertDialogBuilder.setPositiveButton("Change Deadline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
              openDatePicker();
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(phasesForProjectActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR, i);
                        calendar.set(Calendar.MONTH, i1);
                        calendar.set(Calendar.DAY_OF_MONTH, i2);


                        TimePickerDialog timePickerDialog = new TimePickerDialog(phasesForProjectActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                        calendar.set(Calendar.HOUR_OF_DAY, i);
                                        calendar.set(Calendar.MINUTE, i1);
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy, HH.mm", Locale.getDefault());
                                        String deadlineDate = dateFormat.format(calendar.getTime());
                                        if(projectID < 0){
                                            Toast.makeText(phasesForProjectActivity.this, "Could Not Update The Deadline", Toast.LENGTH_SHORT).show();
                                        }else{
                                            myDB.updateProjectDeadline(projectID, deadlineDate);
                                        }
                                        projectDeadLineTextView.setText(deadlineDate.toString().substring(0,9));
                                        setDaysTogo(deadlineDate);

                                    }
                                }, hour, minute, false);
                        timePickerDialog.show();
                    }
                }, year, month, day);

        datePickerDialog.show();
    }
    private void setAllProjectData() {
        projectNameTextView.setText(currentProject.getProjectName());
        stepTitleEditText.setText(currentProject.getProjectName());
        String deadline = currentProject.getProjectDeadline().toString().trim();
        String shortDeadline = deadline.substring(0, 9);
        projectDeadLineTextView.setText(shortDeadline);
        projectImportanceTextView.setText(currentProject.getProjectImportance());
        String s = currentProject.getProjectImportance();
        if(s.equals("Low")){
            projectImportanceRelativeLayout.setBackgroundResource(R.drawable.green_badge_background);
        } else if (s.equals("Medium")) {
            projectImportanceRelativeLayout.setBackgroundResource(R.drawable.medium_badge_background);
        } else if (s.equals("High")) {
            projectImportanceRelativeLayout.setBackgroundResource(R.drawable.high_badge_background);
        }else{
            projectImportanceTextView.setText("Low");
            projectImportanceRelativeLayout.setBackgroundResource(R.drawable.green_badge_background);
            if(projectAvailable){
                myDB.updateProjectImportance(projectID, "Low");
            }
        }
        int currentProgress = projectProgressTotalBar.getProgress();
        if(currentProgress<=0){
            projectProgressState.setText("Yet To Start");
            projectProgressState.setTextColor(Color.parseColor("#cfd8dc"));
        } else if (currentProgress>0 && currentProgress<=80) {
            projectProgressState.setText("IN PROGRESS");
            projectProgressState.setTextColor(Color.parseColor("#ffd699"));
        } else if (currentProgress>80 && currentProgress<=99) {
            projectProgressState.setText("Ending Stages");
            projectProgressState.setTextColor(Color.parseColor("#f4ad4a"));
        }else{
            projectProgressState.setText("COMPLETED!");
            projectProgressState.setTextColor(Color.parseColor("#81c784"));
        }

        setDaysTogo(deadline);

    }
    private void setTheProjectProgress(int projectID) {
        if(projectAvailable){
            ArrayList<Integer> phaseIDs = myDB.getPhaseIDsForProject(projectID);
            int average = 0;
            for(int i=0;i<phaseIDs.size();i++){
                average = average+myDB.getAverageStepProgress(phaseIDs.get(i), projectID);
            }

            if(phaseIDs.size() > 0) {
                int newAverage = average / phaseIDs.size();
                projectProgressTotalBar.setProgress(newAverage);
                if(newAverage == 100){
                    myDB.updateProjectCompleted(projectID, 1);
                }else{
                    myDB.updateProjectCompleted(projectID, 0);
                }
            }else{
                projectProgressTotalBar.setProgress(0);
            }

        }else{
            projectProgressTotalBar.setProgress(0);
        }
    }
    public void setDaysTogo(String Deadline){
        int daysToGo;
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy, HH.mm");
        try {
            Date date = format.parse(Deadline);
            daysToGo = calculateDaysRemaining(Deadline);
        } catch (ParseException e) {
            daysToGo = 0;
            System.out.println("The string is not in the correct format.");
        }
        String daysToGoString;
        if(daysToGo >= 100){
            isDateDueTextView.setVisibility(View.GONE);
            daysToGoString = "99+";
            daysToGoTextView.setText(daysToGoString);
            daysToGoDescriptionTextView.setText("Days to go");
            daysToGoTextView.setTextColor(Color.parseColor("#C0EEE4"));
        }else if(daysToGo<0){
            int NewdaysToGo = daysToGo* -1;
            daysToGoDescriptionTextView.setText("Days Ago");
            if(NewdaysToGo >= 100){
                daysToGoString ="99+";
                daysToGoTextView.setText(daysToGoString);

            }else{
                daysToGoString = Integer.toString(NewdaysToGo);
                daysToGoTextView.setText(daysToGoString);
            }
            isDateDueTextView.setVisibility(View.VISIBLE);
            daysToGoTextView.setTextColor(Color.parseColor("#e2725b"));


        }else{
            daysToGoTextView.setTextColor(Color.parseColor("#C0EEE4"));
            daysToGoDescriptionTextView.setText("Days to go");
            isDateDueTextView.setVisibility(View.GONE);
            daysToGoString = Integer.toString(daysToGo);
            daysToGoTextView.setText(daysToGoString);
        }
    }
    public static int calculateDaysRemaining(String deadlineStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy, HH.mm");
        LocalDateTime deadlineDateTime = LocalDateTime.parse(deadlineStr, formatter);
        LocalDate deadlineDate = deadlineDateTime.toLocalDate();
        LocalDate currentDate = LocalDate.now();
        long daysRemaining = ChronoUnit.DAYS.between(currentDate, deadlineDate);
        return (int) daysRemaining;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        phasesList = myDB.getAllPhasesForaProject(projectID);
        if(phasesList.size() <=0 || !projectAvailable){
            no_phases_panda_image.setVisibility(View.VISIBLE);
        }else{
            no_phases_panda_image.setVisibility(View.GONE);
        }
        currentProject = myDB.getAllProjectData(projectID);
        phasesForProjectAdapter = new phasesForProjectAdapter(phasesList, phasesForProjectActivity.this);
        allStepsForPhaseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        allStepsForPhaseRecyclerView.setAdapter(phasesForProjectAdapter);
        phasesForProjectAdapter.notifyDataSetChanged();
        setTheProjectProgress(projectID);
        setAllProjectData();

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
    protected void onResume() {
        super.onResume();
    }
}