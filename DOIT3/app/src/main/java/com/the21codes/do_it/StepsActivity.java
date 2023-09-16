package com.the21codes.do_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import com.allyants.notifyme.Notification;
import com.allyants.notifyme.NotifyMe;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.github.lzyzsd.circleprogress.ArcProgress;
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
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.Spread;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class StepsActivity extends AppCompatActivity implements newStepsAdapter.ProgressListener {


    ArrayList<newStepsModel> newStepsList;
    Button  addNewStepsId;
    RecyclerView newStepsRecyclerView;
    newStepsAdapter newStepsAdapter;
    DataBaseHelper myDB;
    int phaseID =0;
    int projectID =0;
    TextView phaseNameTextView, PhaseDeadlineTextView, phasePriorityTextView, PhaseProgressStateTextView;
    RelativeLayout editPhaseNameRelativeLayout, detailsDropRelativeLayout, phaseProgressRelativeLayout;
    EditText phaseTitleEditText;
    ImageView showDetailsDropDownArrow, setPhaseNotification, nothingYetImage;
    LinearLayout phaseDetailsDropDown;
    ArcProgress phaseProgressTotalBar;
    phasesModelClass currentPhase;
    boolean phaseAvailable;
    private Handler mHandler = new Handler();
    private static final int DELAY = 2000; // milliseconds
    CoordinatorLayout layout;
    boolean fromNotification = false;
    public int recyclerPosition = -1;
    String thePhaseNotificationString = "NOT_SET";
    String theNewPhaseNotificationString = "NOT_SET";
    String currentNotificationDetails = "NOT_SET";
    private KonfettiView konfettiView = null;
    private Shape.DrawableShape drawableShape = null;
    AlarmManager alarmManager;
    boolean areTheSystemNotificationsEnabled;
    SharedPreferences instructionsGiven;
    SharedPreferences.Editor instructionsGivenEditor;
    boolean stepsPageInstructionsGiven;
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
        setContentView(R.layout.activity_new_steps);
        myDB = new DataBaseHelper(this);
        Intent intent = getIntent();
        String action = intent.getAction();
        instructionsGiven = getSharedPreferences("instructionGivenDetails", MODE_PRIVATE);
        instructionsGivenEditor = instructionsGiven.edit();
        stepsPageInstructionsGiven = instructionsGiven.getBoolean("stepsPageInstructionsGiven", false);
        final Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_heart);
        drawableShape = new Shape.DrawableShape(drawable, true);

        if(action.equals("Notification_Intent")){
            phaseID = intent.getIntExtra("NOTIFICATION_PHASE_ID", -1);
            projectID = intent.getIntExtra("NOTIFICATION_PROJECT_ID", -1);
            fromNotification = true;
            recyclerPosition = intent.getIntExtra("stepPosition", -1);
        }else{
            phaseID = intent.getIntExtra("phaseID", -1);
            projectID = intent.getIntExtra("projectID", -1);
            fromNotification = true;
        }
        if(phaseID == -1 || projectID == -1){
            phaseAvailable = false;
            newStepsList = new ArrayList<>();
            currentPhase = new phasesModelClass("Project Phase",-1,-1,"Set Deadline", "Set Priority","Set Notification",
                    0, "#Custom", 0,0,0);
        }else{
            try {
                phaseAvailable = true;
                newStepsList = myDB.getAllStepsForaPhase(phaseID, projectID);
                currentPhase = myDB.getAllPhasesData(phaseID, projectID);
            }catch (Exception e){
                Intent newIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(newIntent);
            }
        }
        vibrationDetails = getSharedPreferences("vibrationPreferences", MODE_PRIVATE);
        vibrationDetailsEditor = vibrationDetails.edit();
        vibrationsEnabled = vibrationDetails.getBoolean("vibrations", true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        adsCounterDetails = getSharedPreferences("UserLoginAndNameDetails", MODE_PRIVATE);
        adCounter = adsCounterDetails.getInt("Steps_page_ad", 0);
        adsCounterDetailsEditor = adsCounterDetails.edit();
        areTheSystemNotificationsEnabled = areNotificationsEnabled(getApplicationContext());


        if(isDeviceConnected(getApplicationContext())) {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            adRequest = new AdRequest.Builder().build();
            if (adCounter <= 3) {
                adCounter++;
                adsCounterDetailsEditor.putInt("Steps_page_ad", adCounter);
                adsCounterDetailsEditor.apply();

            } else {
                adCounter = 0;
                adsCounterDetailsEditor.putInt("Steps_page_ad", 0);
                adsCounterDetailsEditor.apply();
                showInterstitialAd();
            }
        }

        phaseNameTextView = (TextView) findViewById(R.id.phaseNameTextView);
        PhaseDeadlineTextView = (TextView) findViewById(R.id.PhaseDeadlineTextView);
        phasePriorityTextView = (TextView) findViewById(R.id.phasePriorityTextView);
        PhaseProgressStateTextView = (TextView) findViewById(R.id.PhaseProgressStateTextView);
        phaseTitleEditText = (EditText) findViewById(R.id.phaseTitleEditText);
        showDetailsDropDownArrow = (ImageView) findViewById(R.id.showDetailsDropDownArrow);
        setPhaseNotification = (ImageView) findViewById(R.id.setPhaseNotification);
        nothingYetImage = (ImageView) findViewById(R.id.nothingYetImage);
        editPhaseNameRelativeLayout = (RelativeLayout) findViewById(R.id.editPhaseNameRelativeLayout);
        detailsDropRelativeLayout = (RelativeLayout) findViewById(R.id.detailsDropRelativeLayout);
        phaseProgressRelativeLayout = (RelativeLayout) findViewById(R.id.phaseProgressRelativeLayout);
        phaseDetailsDropDown = (LinearLayout) findViewById(R.id.phaseDetailsDropDown);
        layout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        phaseProgressTotalBar = findViewById(R.id.phaseProgressTotalBar);
        setTheProjectProgress(phaseID,projectID);
        newStepsRecyclerView = findViewById(R.id.newStepsRecyclerView);
        konfettiView = findViewById(R.id.konfettiView);
        if(phaseAvailable){
            int isNotificationThere = myDB.getPhaseNotificationSet(projectID, phaseID);
            if(areTheSystemNotificationsEnabled) {
                if (isNotificationThere == 1) {
                    setPhaseNotification.setImageResource(R.drawable.baseline_notifications_active_24);
                    String notificationDetails = currentPhase.getPhaseNotificationDetails();
                    if (!notificationDetails.isEmpty() && !notificationDetails.equals("NOT_SET") && !notificationDetails.equals("No Notification Set")
                            && !notificationDetails.equals("none") && !notificationDetails.equals("Not Set")) {
                        boolean oldNotification;
                        try {
                            oldNotification = hasTimePassed(notificationDetails);
                        } catch (Exception e) {
                            oldNotification = false;
                        }

                        if (oldNotification) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                cancelNotificationAlarm(notificationDetails);
                            }else {
                                NotifyMe.cancel(this, notificationDetails);
                            }
                            setPhaseNotification.setImageResource(R.drawable.baseline_notification_add_24);
                            myDB.upDateIsPhaseNotificationSet(projectID, phaseID, 0);
                            myDB.upDatePhaseNotifications(projectID, phaseID, "NOT_SET");
                            currentPhase.setPhaseNotificationDetails("NOT_SET");
                            currentPhase.setNotificationSet(0);
                        } else {
                            boolean notificationExists = isStepNotificationSet(this, notificationDetails);
                            if (!notificationExists) {
                                try {
                                    setTheNotification(notificationDetails);
                                    setPhaseNotification.setImageResource(R.drawable.baseline_notifications_active_24);
                                    myDB.upDateIsPhaseNotificationSet(projectID, phaseID, 1);
                                    myDB.upDatePhaseNotifications(projectID, phaseID, notificationDetails);
                                    currentPhase.setNotificationSet(1);
                                    currentPhase.setPhaseNotificationDetails(notificationDetails);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    myDB.upDateIsPhaseNotificationSet(projectID, phaseID, 0);
                                    myDB.upDatePhaseNotifications(projectID, phaseID, "NOT_SET");
                                    currentPhase.setPhaseNotificationDetails("NOT_SET");
                                    currentPhase.setNotificationSet(0);

                                }
                            } else {
                                setPhaseNotification.setImageResource(R.drawable.baseline_notifications_active_24);
                                myDB.upDateIsPhaseNotificationSet(projectID, phaseID, 1);
                                myDB.upDatePhaseNotifications(projectID, phaseID, notificationDetails);
                                currentPhase.setNotificationSet(1);
                                currentPhase.setPhaseNotificationDetails(notificationDetails);
                            }
                        }
                    } else {
                        setPhaseNotification.setImageResource(R.drawable.baseline_notification_add_24);
                        myDB.upDateIsPhaseNotificationSet(projectID, phaseID, 0);
                        myDB.upDatePhaseNotifications(projectID, phaseID, "NOT_SET");
                        currentPhase.setPhaseNotificationDetails("NOT_SET");
                        currentPhase.setNotificationSet(0);
                    }
                } else {
                    setPhaseNotification.setImageResource(R.drawable.baseline_notification_add_24);
                    myDB.upDateIsPhaseNotificationSet(projectID, phaseID, 0);
                    myDB.upDatePhaseNotifications(projectID, phaseID, "NOT_SET");
                    currentPhase.setPhaseNotificationDetails("NOT_SET");
                    currentPhase.setNotificationSet(0);
                }
            }else{
                setPhaseNotification.setImageResource(R.drawable.baseline_notifications_off_24);
                setPhaseNotification.setAlpha(0.7f);
            }
        }else{
            setPhaseNotification.setImageResource(R.drawable.baseline_notifications_off_24);
            setPhaseNotification.setAlpha(0.7f);
        }

        if(newStepsList.size()>0) {
            if(nothingYetImage.getVisibility() == View.VISIBLE){
                nothingYetImage.setVisibility(View.GONE);
            }
            if(newStepsRecyclerView.getVisibility() == View.GONE){
                newStepsRecyclerView.setVisibility(View.VISIBLE);
            }
            newStepsAdapter = new newStepsAdapter(newStepsList, StepsActivity.this, projectID, phaseID, this);
            newStepsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            newStepsRecyclerView.setAdapter(newStepsAdapter);
            newStepsAdapter.notifyDataSetChanged();

            if(fromNotification && recyclerPosition>-1 && recyclerPosition <newStepsList.size()){
                newStepsRecyclerView.smoothScrollToPosition(recyclerPosition);
            }
        }else{
            if(newStepsRecyclerView.getVisibility() == View.VISIBLE){
                newStepsRecyclerView.setVisibility(View.GONE);
            }
            if(nothingYetImage.getVisibility() == View.GONE){
                nothingYetImage.setVisibility(View.VISIBLE);
            }
        }

        addNewStepsId = (Button) findViewById(R.id.addNewStepsId);
        addNewStepsId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                if(nothingYetImage.getVisibility() == View.VISIBLE){
                    nothingYetImage.setVisibility(View.GONE);
                }
                if(newStepsRecyclerView.getVisibility() == View.GONE){
                    newStepsRecyclerView.setVisibility(View.VISIBLE);
                }
                long newStep = myDB.insertStepData(phaseID, projectID, "New Step",
                        "Set Deadline", 0,"Notes",
                        "none", 0,0,0);
                newStepsList.add(new newStepsModel( "New Step", phaseID,
                        "Set Deadline", 0,"Notes",
                        "none", 0,0,0));
                loadTheStepDataAgain();
            }
        });
        setAllPhaseData();
        Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.accordion_drop_down);
        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.accordion_drop_up);
        showDetailsDropDownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                if(phaseDetailsDropDown.getVisibility() == View.GONE){
                    phaseDetailsDropDown.setVisibility(View.VISIBLE);
                    phaseNameTextView.setVisibility(View.GONE);
                    editPhaseNameRelativeLayout.setVisibility(View.VISIBLE);
                    phaseDetailsDropDown.startAnimation(slideDown);
                    showDetailsDropDownArrow.animate().rotationBy(-90).setDuration(200).start();


                }else{
                    phaseDetailsDropDown.startAnimation(slideUp);
                    phaseDetailsDropDown.setVisibility(View.GONE);
                    phaseNameTextView.setVisibility(View.VISIBLE);
                    editPhaseNameRelativeLayout.setVisibility(View.GONE);
                    showDetailsDropDownArrow.animate().rotationBy(90).setDuration(200).start();

                }
            }
        });
        phasePriorityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                String s = phasePriorityTextView.getText().toString().trim();
                if(s.equals("Set Phase Priority")){
                    phasePriorityTextView.setText("Low");
                    phasePriorityTextView.setTextColor(Color.parseColor("#55efc4"));
                    if(phaseAvailable){
                        myDB.upDatePhasePriority(projectID, phaseID, "Low");
                    }
                }else if (s.equals("Low")) {
                    phasePriorityTextView.setText("Medium");
                    phasePriorityTextView.setTextColor(Color.parseColor("#f5d76e"));
                    if(phaseAvailable){
                        myDB.upDatePhasePriority(projectID, phaseID, "Medium");
                    }
                }else if (s.equals("Medium")) {
                    phasePriorityTextView.setText("High");
                    phasePriorityTextView.setTextColor(Color.parseColor("#FFA07A"));
                    if(phaseAvailable){
                        myDB.upDatePhasePriority(projectID, phaseID, "High");
                    }
                } else if (s.equals("High")) {
                    phasePriorityTextView.setText("Low");
                    phasePriorityTextView.setTextColor(Color.parseColor("#55efc4"));
                    if(phaseAvailable){
                        myDB.upDatePhasePriority(projectID, phaseID, "Low");
                    }
                }else {
                    phasePriorityTextView.setText("Low");
                    phasePriorityTextView.setTextColor(Color.parseColor("#55efc4"));
                    if(phaseAvailable){
                        myDB.upDatePhasePriority(projectID, phaseID, "Low");
                    }
                }
            }
        });
        phaseTitleEditText.addTextChangedListener(new TextWatcher() {
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
        PhaseDeadlineTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StepsActivity.this);
                alertDialogBuilder.setTitle("Change Phase Deadline?");
                alertDialogBuilder.setMessage("This will change the Phase Deadline.");
                alertDialogBuilder.setPositiveButton("Change Deadline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        String deadlineString;
                        if(phaseAvailable){
                            deadlineString =   myDB.getProjectDeadline(projectID);
                        }else {
                            deadlineString = "20-Jul-2023, 12.00";
                        }
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy, HH.mm", Locale.getDefault());
                        Date ultimateDeadline;
                        try {
                            ultimateDeadline = dateFormat.parse(deadlineString);
                        } catch (Exception e) {
                            try {
                                ultimateDeadline = new SimpleDateFormat("dd-MMM-yy, HH.mm", Locale.getDefault()).parse("01-Jan-50, 00.00");
                            } catch (ParseException ex) {
                                throw new RuntimeException(ex);
                            }
                        }

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(ultimateDeadline);

                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(StepsActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                        calendar.set(Calendar.YEAR, i);
                                        calendar.set(Calendar.MONTH, i1);
                                        calendar.set(Calendar.DAY_OF_MONTH, i2);

                                        TimePickerDialog timePickerDialog = new TimePickerDialog(StepsActivity.this,
                                                new TimePickerDialog.OnTimeSetListener() {
                                                    @Override
                                                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                                        calendar.set(Calendar.HOUR_OF_DAY, i);
                                                        calendar.set(Calendar.MINUTE, i1);
                                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy, HH.mm", Locale.getDefault());
                                                        String deadlineDate = dateFormat.format(calendar.getTime());
                                                        PhaseDeadlineTextView.setText(deadlineDate);
                                                        if(phaseAvailable){
                                                            myDB.updatePhaseDeadLine(projectID, phaseID, deadlineDate);
                                                        }else{
                                                            Toast.makeText(StepsActivity.this, "Could Note Update The Deadline", Toast.LENGTH_SHORT).show();
                                                        }
                                                        setTheProjectProgress(projectID, phaseID);
                                                    }
                                                }, hour, minute, true);
                                        timePickerDialog.show();
                                    }
                                }, year, month, day);

                        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                        datePickerDialog.show();
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
        });
        setPhaseNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(10,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
               areTheSystemNotificationsEnabled = areNotificationsEnabled(getApplicationContext());
                if(areTheSystemNotificationsEnabled) {
                    int isNotificationSet = myDB.getPhaseNotificationSet(projectID, phaseID);
                    if (isNotificationSet == 1) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(StepsActivity.this);
                        currentNotificationDetails = myDB.getPhaseNotification(projectID, phaseID);
                        String silly = "The Current Notification is Set On: " + currentNotificationDetails;
                        builder1.setTitle("Notification Controls")
                                .setMessage(silly)
                                .setPositiveButton("Cancel Notification", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                            cancelNotificationAlarm(currentNotificationDetails);
                                        }else {
                                            NotifyMe.cancel(StepsActivity.this, currentNotificationDetails);
                                        }
                                        setPhaseNotification.setImageResource(R.drawable.baseline_notification_add_24);
                                        if (phaseAvailable) {
                                            myDB.upDateIsPhaseNotificationSet(projectID, phaseID, 0);
                                            myDB.upDatePhaseNotifications(projectID, phaseID, "NOT_SET");
                                        }
                                        setPhaseNotification.setImageResource(R.drawable.baseline_notification_add_24);
                                        currentPhase.setPhaseNotificationDetails("NOT_SET");
                                        currentPhase.setNotificationSet(0);
                                        dialog.dismiss();
                                        Toast.makeText(StepsActivity.this, "Notification canceled", Toast.LENGTH_SHORT).show();
                                    }
                                }).setNegativeButton("Reschedule", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        RescheduleNotificationDialog();
                                    }
                                }).setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog1 = builder1.create();
                        dialog1.show();

                    } else {
                        notificationSettingDialog();
                    }
                }else{
                    checkNotificationPermissions();
                }
            }
        });



    }

    private void checkNotificationPermissions() {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enable Notifications");
            builder.setMessage("Notifications are currently disabled. Please enable notifications to receive reminders.");

            builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Open app settings to enable notifications
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivity(intent);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Dismiss dialog and do nothing
                    dialog.dismiss();
                }
            });

            builder.setCancelable(false);
            builder.show();
        }
    }


    public void RescheduleNotificationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_notification, null);
        builder.setView(dialogView);
        builder.setTitle("Remind Me On");
        Button selectNotificationTime = dialogView.findViewById(R.id.selectNotificationTime);
        TextView notifyTextview = dialogView.findViewById(R.id.notifyTextview);
        selectNotificationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(StepsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                calendar.set(Calendar.YEAR, i);
                                calendar.set(Calendar.MONTH, i1);
                                calendar.set(Calendar.DAY_OF_MONTH, i2);
                                TimePickerDialog timePickerDialog = new TimePickerDialog(StepsActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                                calendar.set(Calendar.HOUR_OF_DAY, i);
                                                calendar.set(Calendar.MINUTE, i1);
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy, HH.mm", Locale.getDefault());
                                                String deadlineDate = dateFormat.format(calendar.getTime());
                                                theNewPhaseNotificationString = deadlineDate;
                                                String s = "Schedule notification on: "+deadlineDate+ "?";
                                                notifyTextview.setVisibility(View.VISIBLE);
                                                notifyTextview.setText(s);
                                            }
                                        }, hour, minute, true);
                                timePickerDialog.show();
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }

        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(theNewPhaseNotificationString.equals("NOT_SET")){
                    dialog.dismiss();
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            cancelNotificationAlarm(currentNotificationDetails);

                    }else {
                        NotifyMe.cancel(StepsActivity.this, currentNotificationDetails);
                    }
                    setPhaseNotification.setImageResource(R.drawable.baseline_notification_add_24);
                    try {
                        setTheNotification(theNewPhaseNotificationString);
                        setPhaseNotification.setImageResource(R.drawable.baseline_notifications_active_24);
                        if(phaseAvailable){
                            setPhaseNotification.setImageResource(R.drawable.baseline_notifications_active_24);
                            myDB.upDateIsPhaseNotificationSet(projectID, phaseID, 1);
                            myDB.upDatePhaseNotifications(projectID, phaseID, theNewPhaseNotificationString);
                            currentPhase.setNotificationSet(1);
                            currentPhase.setPhaseNotificationDetails(theNewPhaseNotificationString);
                        }else{
                            Toast.makeText(StepsActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            setPhaseNotification.setImageResource(R.drawable.baseline_notification_add_24);
                            myDB.upDateIsPhaseNotificationSet(projectID, phaseID, 0);
                            myDB.upDatePhaseNotifications(projectID, phaseID, "NOT_SET");
                            currentPhase.setPhaseNotificationDetails("NOT_SET");
                            currentPhase.setNotificationSet(0);
                        }
                        dialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(StepsActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        setPhaseNotification.setImageResource(R.drawable.baseline_notification_add_24);
                        myDB.upDateIsPhaseNotificationSet(projectID, phaseID, 0);
                        myDB.upDatePhaseNotifications(projectID, phaseID, "NOT_SET");
                        currentPhase.setPhaseNotificationDetails("NOT_SET");
                        currentPhase.setNotificationSet(0);
                        dialog.dismiss();
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();

    }

    public void notificationSettingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_notification, null);
        builder.setView(dialogView);
        builder.setTitle("Remind Me On");
        Button selectNotificationTime = dialogView.findViewById(R.id.selectNotificationTime);
        TextView notifyTextview = dialogView.findViewById(R.id.notifyTextview);
        selectNotificationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(StepsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                calendar.set(Calendar.YEAR, i);
                                calendar.set(Calendar.MONTH, i1);
                                calendar.set(Calendar.DAY_OF_MONTH, i2);
                                TimePickerDialog timePickerDialog = new TimePickerDialog(StepsActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                                calendar.set(Calendar.HOUR_OF_DAY, i);
                                                calendar.set(Calendar.MINUTE, i1);
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy, HH.mm", Locale.getDefault());
                                                String deadlineDate = dateFormat.format(calendar.getTime());
                                                thePhaseNotificationString = deadlineDate;
                                                String s = "Schedule notification on: "+deadlineDate+ "?";
                                                notifyTextview.setVisibility(View.VISIBLE);
                                                notifyTextview.setText(s);
                                            }
                                        }, hour, minute, true);
                                timePickerDialog.show();
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }

        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(thePhaseNotificationString.equals("NOT_SET")){
                    Toast.makeText(StepsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else {
                    try {
                        setTheNotification(thePhaseNotificationString);
                        setPhaseNotification.setImageResource(R.drawable.baseline_notifications_active_24);
                        if(phaseAvailable){
                            setPhaseNotification.setImageResource(R.drawable.baseline_notifications_active_24);
                            myDB.upDateIsPhaseNotificationSet(projectID, phaseID, 1);
                            myDB.upDatePhaseNotifications(projectID, phaseID, thePhaseNotificationString);
                            currentPhase.setNotificationSet(1);
                            currentPhase.setPhaseNotificationDetails(thePhaseNotificationString);
                        }else{
                            Toast.makeText(StepsActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            setPhaseNotification.setImageResource(R.drawable.baseline_notification_add_24);
                            myDB.upDateIsPhaseNotificationSet(projectID, phaseID, 0);
                            myDB.upDatePhaseNotifications(projectID, phaseID, "NOT_SET");
                            currentPhase.setPhaseNotificationDetails("NOT_SET");
                            currentPhase.setNotificationSet(0);
                        }
                        dialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                        dialog.dismiss();
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();

    }

    private void showInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-6952672354974833/4835698511", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;

                        mInterstitialAd.show(StepsActivity.this);
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

    private void setTheNotification(String thePhaseNotificationString) throws Exception {
        Calendar calendar = getCalendarObjectForString(thePhaseNotificationString);
        String stepName = currentPhase.getPhaseName();
        String title = "DO-IT: " + stepName + " Reminder.";
        String content = "You have Scheduled a remainder for the phase: " + stepName + ". Have A look";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                createNotificationChannel();
                setNotificationAlarm(calendar, thePhaseNotificationString, projectID, phaseID, title, content);
            }catch(Exception E){
                Toast.makeText(this, "Something went wrong while setting the notification", Toast.LENGTH_SHORT).show();
                return;
            }
        }else {
            Intent notificationIntent = new Intent(getApplicationContext(), StepsActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationIntent.setAction("Notification_Intent");
            notificationIntent.putExtra("NOTIFICATION_PROJECT_ID", projectID);
            notificationIntent.putExtra("NOTIFICATION_PHASE_ID", phaseID);
            NotifyMe notifyMe = new NotifyMe.Builder(this)
                    .title(title)
                    .content(content)
                    .large_icon(R.drawable.doit_logo)
                    .color(54, 59, 78, 1)
                    .led_color(215, 127, 161, 1)
                    .time(calendar)
                    .key(thePhaseNotificationString)
                    .addAction(new Intent(), "Ignore", false)
                    .addAction(notificationIntent, "Open App", true)
                    .build();
        }
    }

    public void setNotificationAlarm(Calendar calendar, String key, int theprojectID, int thephaseID, String title, String content) {

        try {
            Intent intent = new Intent(this, broadcast_receiver.class);
            intent.putExtra("notificationId", key.hashCode());
            intent.putExtra("NOTIFICATION_PROJECT_ID", theprojectID);
            intent.putExtra("NOTIFICATION_PHASE_ID", thephaseID);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            intent.setAction("Notification_Intent" + key);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, key.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);

            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            long triggerTimeInMillis = calendar.getTimeInMillis();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeInMillis, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeInMillis, pendingIntent);
            }
        }catch (Exception e){
            Toast.makeText(this, "Something went wrong while setting the notification", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public static boolean areNotificationsEnabled(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        return notificationManager.areNotificationsEnabled();
    }




    public void cancelNotificationAlarm(String key) {
            try {
                Intent intent = new Intent(this, broadcast_receiver.class);
                intent.setAction("Notification_Intent" + key);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, key.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.cancel(pendingIntent);
                NotificationManager nmgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                nmgr.cancel(key.hashCode());
            } catch (Exception r) {
                r.printStackTrace();
            }
        }


    private void createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Do It";
                String description = "Plan Anything, Track Everything";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("DO_IT:NOTIFICATION", name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }catch (Exception e){
            Toast.makeText(this, "Something went wrong while setting the notification", Toast.LENGTH_SHORT).show();
            return;
        }
    }


    private Calendar getCalendarObjectForString(String thePhaseNotificationString) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy, HH.mm");
        Date date = format.parse(thePhaseNotificationString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private boolean isStepNotificationSet(Context context, String notificationKey) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();
                for (StatusBarNotification statusBarNotification : activeNotifications) {
                    if (statusBarNotification.getId() == notificationKey.hashCode()) {
                        // A notification with the specified key already exists
                        return true;
                    }
                }
                return false;
            } else {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();
                for (StatusBarNotification statusBarNotification : activeNotifications) {
                    if (statusBarNotification.getTag() != null && statusBarNotification.getTag().equals(notificationKey)) {
                        // A notification with the specified key already exists
                        return true;
                    }
                }
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }


    public static boolean hasTimePassed(String dateString) throws ParseException {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy, HH.mm");
            Date date = formatter.parse(dateString);
            return date.before(new Date());
        }catch (Exception e){
            return false;
        }

    }

    private void loadTheStepDataAgain() {
        setTheProjectProgress(phaseID,projectID);
        ArrayList<newStepsModel> newList = myDB.getAllStepsForaPhase(phaseID, projectID);
        if(newList.size()>0){
            if(nothingYetImage.getVisibility() == View.VISIBLE){
                nothingYetImage.setVisibility(View.GONE);
            }
            if(newStepsRecyclerView.getVisibility() == View.GONE){
                newStepsRecyclerView.setVisibility(View.VISIBLE);
            }
            newStepsAdapter = new newStepsAdapter(newList, StepsActivity.this, projectID, phaseID, this);
            newStepsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            newStepsRecyclerView.setAdapter(newStepsAdapter);
            newStepsAdapter.notifyDataSetChanged();
            newStepsRecyclerView.smoothScrollToPosition(newStepsList.size()-1);
        }else{
            if(newStepsRecyclerView.getVisibility() == View.VISIBLE){
                newStepsRecyclerView.setVisibility(View.GONE);
            }
            if(nothingYetImage.getVisibility() == View.GONE){
                nothingYetImage.setVisibility(View.VISIBLE);
            }
        }

    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            String text = phaseTitleEditText.getText().toString();
            phaseNameTextView.setText(text);
            try{
                if(phaseAvailable){
                    myDB.updatePhaseName(projectID,phaseID, text);
                }
            }catch (Exception e){
                Toast.makeText(StepsActivity.this, "Could Not Update the project Name", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private void setAllPhaseData() {
        phaseNameTextView.setText(currentPhase.getPhaseName());
        phaseTitleEditText.setText(currentPhase.getPhaseName());
        PhaseDeadlineTextView.setText(currentPhase.getPhaseDeadline());
        phasePriorityTextView.setText(currentPhase.getPhasePriority());
        String phasePriority = currentPhase.getPhasePriority();
        if (phasePriority.equals("Low")) {
            phasePriorityTextView.setTextColor(Color.parseColor("#55efc4"));
        }else if (phasePriority.equals("Medium")) {
            phasePriorityTextView.setTextColor(Color.parseColor("#f5d76e"));
        }else if (phasePriority.equals("High")) {
            phasePriorityTextView.setTextColor(Color.parseColor("#FFA07A"));
        }else{
            phasePriorityTextView.setText("Low");
            phasePriorityTextView.setTextColor(Color.parseColor("#55efc4"));
            if(phaseAvailable){
                myDB.upDatePhasePriority(projectID, phaseID, "Low");
                currentPhase.setPhasePriority("Low");
            }
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
    
    private void setTheProjectProgress(int phaseID, int projectID) {
        int average = 0;
        if(projectID>=0 && phaseID>=0){
                average = average+myDB.getAverageStepProgress(phaseID, projectID);
            phaseProgressTotalBar.setProgress(average);
            if(average ==100){
                myDB.upDateIsPhaseCompleted(projectID, phaseID, 1);
            }else if(average>1 && average<=99){
                myDB.upDateIsPhaseCompleted(projectID, phaseID, 0);
            }else{
                myDB.upDateIsPhaseCompleted(projectID, phaseID, 0);
            }
            boolean timePassed;
            String deadline = myDB.getPhaseDeadline(projectID, phaseID);
            if(!deadline.equals("Set Deadline")){
                try {
                    timePassed = hasTimePassed(deadline);
                }catch (Exception e){
                    timePassed = false;
                }

                if(timePassed){
                    if(average == 100){
                        PhaseProgressStateTextView.setText("COMPLETED");
                        PhaseProgressStateTextView.setTextColor(Color.parseColor("#0A0A0A"));
                        phaseProgressRelativeLayout.setBackgroundColor(Color.parseColor("#81c784"));
                    }else {
                        PhaseProgressStateTextView.setText("DUE");
                        PhaseProgressStateTextView.setTextColor(Color.parseColor("#FBFBFB"));
                        phaseProgressRelativeLayout.setBackgroundColor(Color.parseColor("#c24a44"));
                    }
                }else{
                    if(average ==100){
                        PhaseProgressStateTextView.setText("COMPLETED");
                        PhaseProgressStateTextView.setTextColor(Color.parseColor("#0A0A0A"));
                        phaseProgressRelativeLayout.setBackgroundColor(Color.parseColor("#81c784"));
                    }else if(average>1 && average<=99){
                        PhaseProgressStateTextView.setText("IN PROGRESS");
                        PhaseProgressStateTextView.setTextColor(Color.parseColor("#0A0A0A"));
                        phaseProgressRelativeLayout.setBackgroundColor(Color.parseColor("#ffd699"));
                    }else{
                        PhaseProgressStateTextView.setText("Yet To Start");
                        PhaseProgressStateTextView.setTextColor(Color.parseColor("#0A0A0A"));
                        phaseProgressRelativeLayout.setBackgroundColor(Color.parseColor("#cfd8dc"));
                    }
                }
            }

        }else{
            phaseProgressTotalBar.setProgress(0);
            PhaseProgressStateTextView.setText("Yet To Start");
            phaseProgressRelativeLayout.setBackgroundColor(Color.parseColor("#cfd8dc"));
        }
    }

    @Override
    public void onProgressChanged(int progress) {
        setTheProjectProgress(phaseID,projectID);
    }

    @Override
    public void isStepCompleted(boolean complete) {
        if(complete){
            explode();
            Toast.makeText(this, "Yayyy! Task completed!", Toast.LENGTH_LONG).show();
        }
    }

    public void explode() {
        EmitterConfig emitterConfig = new Emitter(1L, TimeUnit.SECONDS).perSecond(200);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .spread(360)
                        .setSpeed(10f)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE, drawableShape))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(0f, 30f)
                        .position(new Position.Relative(0.5, 0.3))
                        .build()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action.equals("Notification_Intent")){
            Intent newIntent = new Intent(this, MainActivity.class);
            startActivity(newIntent);
            finish();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action.equals("Notification_Intent")){
            Intent newIntent = new Intent(this, MainActivity.class);
            startActivity(newIntent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}