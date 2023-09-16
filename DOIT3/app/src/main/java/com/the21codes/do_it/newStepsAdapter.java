package com.the21codes.do_it;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.allyants.notifyme.NotifyMe;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class newStepsAdapter extends RecyclerView.Adapter<newStepsAdapter.ViewHolder> {

    ArrayList<newStepsModel> stepsList;
    Context context;
    int oldProgress =0;
    boolean stepAvailable;
    int phaseID;
    int projectID;
    DataBaseHelper myDB;
    private Handler mNotesHandler = new Handler();
    private Handler mHandler = new Handler();
    private static final int DELAY = 2000;
    private static final int NOTES_DELAY = 7000; // milliseconds
    private ProgressListener progressListener;
    public boolean isNotificationActive;
    public boolean newNotificationSet = false;
    String thePhaseNotificationString = "NOT_SET";
    String newThePhaseNotificationString = "NOT_SET";
    int currentStepListPosition =0;
    boolean areTheSystemNotificationsEnabled;
    AlarmManager alarmManager;

    SharedPreferences vibrationDetails;
    SharedPreferences.Editor vibrationDetailsEditor;
    Boolean vibrationsEnabled = false;
    private Vibrator vibrator;


    public newStepsAdapter(ArrayList<newStepsModel> stepsList, Context context, int projectID, int phaseID, ProgressListener progressListener){
        this.stepsList = stepsList;
        this.context = context;
        this.projectID = projectID;
        this.phaseID = phaseID;
        this.progressListener = progressListener;

    }



    @NonNull
    @Override
    public newStepsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.new_steps_design, null, false);
        myDB = new DataBaseHelper(context);
        vibrationDetails = context.getSharedPreferences("vibrationPreferences", MODE_PRIVATE);
        vibrationDetailsEditor = vibrationDetails.edit();
        vibrationsEnabled = vibrationDetails.getBoolean("vibrations", true);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        areTheSystemNotificationsEnabled = areNotificationsEnabled(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull newStepsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int currentStepID = stepsList.get(position).getStepID();
        currentStepListPosition = position;
        if(currentStepID >= 0 && phaseID>0 && projectID>0){
           stepAvailable = true;
        }else{
            stepAvailable = false;
        }

        holder.mainStepTile.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.slide_in_nimation));


        holder.stepTitleEditText.setText(stepsList.get(position).getStepName());
        String dead = stepsList.get(position).getStepDeadline();
        if(dead.equals("Set Deadline")){
            holder.stepDeadlineTextView.setText(dead);
        }else {
            if (dead.length() > 8) {
                holder.stepDeadlineTextView.setText(dead.trim().substring(0, 9));
            }else{
                holder.stepDeadlineTextView.setText(dead);
            }
        }
        holder.stepDeadlineTextView.setText(stepsList.get(position).getStepDeadline());
        holder.theStepNotesEditText.setText(stepsList.get(position).getStepNotes());
        holder.number_progress_bar.setProgress(stepsList.get(position).getStepProgress());
        holder.completionSeekBar.setProgress(stepsList.get(position).getStepProgress());
        int isNotificationSet = stepsList.get(position).isNotificationSet();

        if(areTheSystemNotificationsEnabled) {
            if (isNotificationSet == 1) {
                holder.stepNotificationImageView.setImageResource(R.drawable.baseline_notifications_active_24);
                String notificationDetails = stepsList.get(position).getStepNotificationDetails();
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
                            NotifyMe.cancel(context, notificationDetails);
                        }
                        holder.stepNotificationImageView.setImageResource(R.drawable.baseline_notification_add_24);
                        if (stepAvailable) {
                            myDB.updateIsStepNotificationSet(phaseID, currentStepID, 0, projectID);
                            myDB.updateStepNotification(phaseID, currentStepID, "NOT_SET", projectID);
                            isNotificationActive = false;
                        }

                    } else {
                        boolean notificationExists = isStepNotificationSet(context, notificationDetails);
                        if (!notificationExists) {
                            try {
                                setTheNotification(notificationDetails);
                                isNotificationActive = true;
                                holder.stepNotificationImageView.setImageResource(R.drawable.baseline_notifications_active_24);
                                if (stepAvailable) {
                                    myDB.updateIsStepNotificationSet(phaseID, currentStepID, 1, projectID);
                                    myDB.updateStepNotification(phaseID, currentStepID, notificationDetails, projectID);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (stepAvailable) {
                                    myDB.updateIsStepNotificationSet(phaseID, currentStepID, 0, projectID);
                                    myDB.updateStepNotification(phaseID, currentStepID, "NOT_SET", projectID);
                                }

                            }
                        } else {
                            isNotificationActive = true;
                            holder.stepNotificationImageView.setImageResource(R.drawable.baseline_notifications_active_24);
                            if (stepAvailable) {
                                myDB.updateIsStepNotificationSet(phaseID, currentStepID, 1, projectID);
                                myDB.updateStepNotification(phaseID, currentStepID, notificationDetails, projectID);
                            }
                        }
                    }
                } else {
                    holder.stepNotificationImageView.setImageResource(R.drawable.baseline_notification_add_24);
                    if (stepAvailable) {
                        myDB.updateIsStepNotificationSet(phaseID, currentStepID, 0, projectID);
                        myDB.updateStepNotification(phaseID, currentStepID, "NOT_SET", projectID);
                    }
                }
            } else {
                if (stepAvailable) {
                    myDB.updateIsStepNotificationSet(phaseID, currentStepID, 0, projectID);
                    myDB.updateStepNotification(phaseID, currentStepID, "NOT_SET", projectID);
                }
                holder.stepNotificationImageView.setImageResource(R.drawable.baseline_notification_add_24);
            }
        }else{
            holder.stepNotificationImageView.setImageResource(R.drawable.baseline_notifications_off_24);
            holder.stepNotificationImageView.setAlpha(0.7f);
        }
        int isNotificationSet1 = myDB.getStepIsNotificationSet(stepsList.get(position).getStepID(), phaseID,projectID);
        isNotificationActive = isNotificationSet1 == 1;



        oldProgress = stepsList.get(position).getStepProgress();
        if(stepsList.get(position).getStepProgress() == 100){
            holder.fullyCompletedCheckBox.setChecked(true);
            holder.number_progress_bar.setAlpha(0.6f);
            holder.ediTextRelativeLayout.setAlpha(0.6f);
            holder.notesDropDownRelativeLayout.setAlpha(0.6f);
            holder.deadlineRelativeLayoutTile.setAlpha(0.6f);
            holder.theStepNotesEditText.setAlpha(0.6f);
            holder.progressTrackerRelativeLayout.setAlpha(0.6f);
            holder.stepProgressStateTextView.setText("COMPLETED");
            holder.stepProgressStateRelativeLayout.setBackgroundColor(Color.parseColor("#81c784"));
        }
        //progress and seekbar and checkBox
        holder.fullyCompletedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE));
                }

                if(b){
                    progressListener.isStepCompleted(true);
                    holder.completionSeekBar.setProgress(100);
                    holder.number_progress_bar.setProgress(100);
                    if(stepAvailable){
                        myDB.updateStepProgress(phaseID, stepsList.get(position).getStepID(), 100, projectID);
                        myDB.updateIsStepCompleted(phaseID,stepsList.get(position).getStepID(),1,projectID);
                        myDB.updateOrInsertTask(getTodayDate(),1,0);
                    }
                    myDB.updateStepProgress(phaseID, stepsList.get(position).getStepID(), 100, projectID);
                    progressListener.onProgressChanged(100);
                    holder.stepProgressStateTextView.setText("COMPLETED");
                    holder.stepProgressStateRelativeLayout.setBackgroundColor(Color.parseColor("#81c784"));
                    holder.number_progress_bar.setAlpha(0.6f);
                    holder.ediTextRelativeLayout.setAlpha(0.6f);
                    holder.notesDropDownRelativeLayout.setAlpha(0.6f);
                    holder.deadlineRelativeLayoutTile.setAlpha(0.6f);
                    holder.theStepNotesEditText.setAlpha(0.6f);
                    holder.progressTrackerRelativeLayout.setAlpha(0.6f);
                }else {
                    if(oldProgress == 100){
                        oldProgress = 0;
                    }
                    holder.completionSeekBar.setProgress(oldProgress);
                    holder.number_progress_bar.setProgress(oldProgress);
                    if(stepAvailable){
                        myDB.updateStepProgress(phaseID, stepsList.get(position).getStepID(), oldProgress, projectID);
                        myDB.updateIsStepCompleted(phaseID,stepsList.get(position).getStepID(),0,projectID);
                        myDB.updateOrInsertTask(getTodayDate(),-1,0);
                    }
                    progressListener.onProgressChanged(oldProgress);
                    if(oldProgress<=0){
                        holder.stepProgressStateTextView.setText("Yet To Start");
                        holder.stepProgressStateRelativeLayout.setBackgroundColor(Color.parseColor("#cfd8dc"));
                    } else if (oldProgress>1 && oldProgress<=99) {
                        holder.stepProgressStateTextView.setText("IN PROGRESS");
                        holder.stepProgressStateRelativeLayout.setBackgroundColor(Color.parseColor("#ffd699"));
                    }else{
                        holder.stepProgressStateTextView.setText("COMPLETED");
                        holder.stepProgressStateRelativeLayout.setBackgroundColor(Color.parseColor("#81c784"));
                    }
                    holder.number_progress_bar.setAlpha(1f);
                    holder.ediTextRelativeLayout.setAlpha(1f);
                    holder.notesDropDownRelativeLayout.setAlpha(1f);
                    holder.deadlineRelativeLayoutTile.setAlpha(1f);
                    holder.theStepNotesEditText.setAlpha(1f);
                    holder.progressTrackerRelativeLayout.setAlpha(1f);
                }
            }
        });
        holder.completionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                holder.number_progress_bar.setProgress(progressChanged);
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                holder.number_progress_bar.setProgress(progressChanged);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                progressChanged = (seekBar.getProgress() / 5) * 5;
                holder.number_progress_bar.setProgress(progressChanged);
                seekBar.setProgress(progressChanged);
                if(stepAvailable){
                    myDB.updateStepProgress(phaseID, stepsList.get(position).getStepID(), progressChanged, projectID);
                    progressListener.onProgressChanged(progressChanged);
                }
                if(progressChanged<=0){
                    holder.stepProgressStateTextView.setText("Yet To Start");
                    holder.stepProgressStateRelativeLayout.setBackgroundColor(Color.parseColor("#cfd8dc"));
                } else if (progressChanged>1 && progressChanged<=99) {
                    holder.stepProgressStateTextView.setText("IN PROGRESS");
                    holder.stepProgressStateRelativeLayout.setBackgroundColor(Color.parseColor("#ffd699"));
                }else{
                    progressListener.isStepCompleted(true);
                    holder.stepProgressStateTextView.setText("COMPLETED");
                    holder.stepProgressStateRelativeLayout.setBackgroundColor(Color.parseColor("#81c784"));
                    if(stepAvailable) {
                        myDB.updateIsStepCompleted(phaseID, stepsList.get(position).getStepID(), 1, projectID);
                        myDB.updateOrInsertTask(getTodayDate(), 1, 0);
                    }
                }


                if(progressChanged != 100){
                    holder.fullyCompletedCheckBox.setChecked(false);
                }
                if(progressChanged == 100){
                    holder.fullyCompletedCheckBox.setChecked(true);
                    holder.number_progress_bar.setAlpha(0.6f);
                    holder.ediTextRelativeLayout.setAlpha(0.6f);
                    holder.notesDropDownRelativeLayout.setAlpha(0.6f);
                    holder.deadlineRelativeLayoutTile.setAlpha(0.6f);
                    holder.theStepNotesEditText.setAlpha(0.6f);
                    holder.progressTrackerRelativeLayout.setAlpha(0.6f);
                }
            }
        });
        //drop down arrow and notes
        holder.dropDownNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_popper));
               if(holder.theStepNotesEditText.getVisibility() == View.GONE){
                   holder.dropDownNotes.animate().rotationBy(-90).setDuration(200).start();
                   holder.theStepNotesEditText.setVisibility(View.VISIBLE);
               }else{
                   holder.dropDownNotes.animate().rotationBy(90).setDuration(200).start();
                   holder.theStepNotesEditText.setVisibility(View.GONE);
               }
            }
        });
        holder.theStepNotesEditText.setMinHeight((4* holder.theStepNotesEditText.getLineHeight()));
        holder.theStepNotesEditText.addTextChangedListener(new TextWatcher() {
            Runnable mRunnable = new Runnable() {
                @Override
                public void run() {
                    String text = holder.theStepNotesEditText.getText().toString();
                    try{
                        if(stepAvailable){
                            myDB.updateStepNotes(phaseID,stepsList.get(position).getStepID(), text, projectID);
                        }
                    }catch (Exception e){
                        Toast.makeText(context, "Could Not Update the project Name", Toast.LENGTH_SHORT).show();
                    }

                }
            };

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mNotesHandler.removeCallbacks(mRunnable);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mNotesHandler.removeCallbacks(mRunnable); // remove any pending callbacks
                mNotesHandler.postDelayed(mRunnable, NOTES_DELAY); // post a new callback with delay
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int lineCount = holder.theStepNotesEditText.getLineCount();
                int newHeight = lineCount * holder.theStepNotesEditText.getLineHeight();
                int mod = newHeight % (6 * holder.theStepNotesEditText.getLineHeight());
                if(mod !=0){
                    newHeight +=(6 * holder.theStepNotesEditText.getLineHeight()) -mod;
                }
                holder.theStepNotesEditText.setMinHeight(newHeight);
            }
        });
        //Deadline and days To Go Logic
        int daysToGo;
        if(stepsList.get(position).getStepDeadline().trim().equals("Set Deadline") ||
                stepsList.get(position).getStepDeadline().trim().equals("Deadline Not Set")){
            daysToGo =0;
        }else{
            daysToGo = calculateDaysRemaining(stepsList.get(position).getStepDeadline().trim());
            if(daysToGo<0){
                holder.daysToGoHeading.setText("Due:");
                holder.daysToGoDeadline.setTextColor(Color.parseColor("#c24a44"));
                holder.daysToGoDeadline.setText((daysToGo*-1)+" Days Ago.");

            }else{
                holder.daysToGoHeading.setText("Days To Go:");
                holder.daysToGoDeadline.setText(daysToGo+" Days");
                holder.daysToGoDeadline.setTextColor(Color.parseColor("#C0EEE4"));
            }
        }

        holder.stepDeadlineTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                if(holder.theStepNotesEditText.getText().toString().trim().equals("Set Deadline")){
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                    calendar.set(Calendar.YEAR, i);
                                    calendar.set(Calendar.MONTH, i1);
                                    calendar.set(Calendar.DAY_OF_MONTH, i2);

                                    TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                                            new TimePickerDialog.OnTimeSetListener() {
                                                @Override
                                                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                                    calendar.set(Calendar.HOUR_OF_DAY, i);
                                                    calendar.set(Calendar.MINUTE, i1);
                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy, HH.mm", Locale.getDefault());
                                                    String deadlineDate = dateFormat.format(calendar.getTime());
                                                    if(deadlineDate.equals("Set Deadline")){
                                                        holder.stepDeadlineTextView.setText(deadlineDate);
                                                    }else {
                                                        if (deadlineDate.length() > 8) {
                                                            holder.stepDeadlineTextView.setText(deadlineDate.trim().substring(0, 9));
                                                        }else{
                                                            holder.stepDeadlineTextView.setText(deadlineDate);
                                                        }
                                                    }
                                                    if(!stepAvailable){
                                                        Toast.makeText(context, "Could Not Update The Deadline", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        myDB.updateStepDeadLine(phaseID, stepsList.get(position).getStepID(), deadlineDate, projectID);
                                                    }
                                                    int daysRemaining = calculateDaysRemaining(deadlineDate);
                                                    if(daysRemaining<0){
                                                        holder.daysToGoHeading.setText("Due:");
                                                        holder.daysToGoDeadline.setText((daysRemaining*-1)+" Days Ago.");
                                                        holder.daysToGoDeadline.setTextColor(Color.parseColor("#c24a44"));

                                                    }else{
                                                        holder.daysToGoHeading.setText("Days To Go:");
                                                        holder.daysToGoDeadline.setText(daysRemaining+" Days");
                                                        holder.daysToGoDeadline.setTextColor(Color.parseColor("#C0EEE4"));
                                                    }

                                                }
                                            }, hour, minute, false);
                                    timePickerDialog.show();
                                }
                            }, year, month, day);

                    datePickerDialog.show();
                }else{
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Change Phase Deadline?");
                    alertDialogBuilder.setMessage("This will change the Phase Deadline.");
                    alertDialogBuilder.setPositiveButton("Change Deadline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            String deadlineString;
                            if(stepAvailable){
                                deadlineString = myDB.getPhaseDeadline(projectID, phaseID);
                            }else{
                                deadlineString = "20-Jul-23, 12.00";
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

                            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                            calendar.set(Calendar.YEAR, i);
                                            calendar.set(Calendar.MONTH, i1);
                                            calendar.set(Calendar.DAY_OF_MONTH, i2);

                                            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                                                    new TimePickerDialog.OnTimeSetListener() {
                                                        @Override
                                                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                                            calendar.set(Calendar.HOUR_OF_DAY, i);
                                                            calendar.set(Calendar.MINUTE, i1);
                                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy, HH.mm", Locale.getDefault());
                                                            String deadlineDate = dateFormat.format(calendar.getTime());
                                                            holder.stepDeadlineTextView.setText(deadlineDate);
                                                            myDB.updateStepDeadLine(phaseID, stepsList.get(position).getStepID(), deadlineDate, projectID);
                                                            int daysRemaining = calculateDaysRemaining(deadlineDate);
                                                            if(daysRemaining<0){
                                                                holder.daysToGoHeading.setText("Due:");
                                                                holder.daysToGoDeadline.setText((daysRemaining*-1)+" Days Ago.");
                                                                holder.daysToGoDeadline.setTextColor(Color.parseColor("#c24a44"));
                                                            }else{
                                                                holder.daysToGoHeading.setText("Days To Go:");
                                                                holder.daysToGoDeadline.setText(daysRemaining+" Days");
                                                                holder.daysToGoDeadline.setTextColor(Color.parseColor("#C0EEE4"));
                                                            }
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
            }
        });
        // progressTag
        int currentProgress = holder.number_progress_bar.getProgress();
        if(currentProgress<=0){
            holder.stepProgressStateTextView.setText("Yet To Start");
            holder.stepProgressStateRelativeLayout.setBackgroundColor(Color.parseColor("#cfd8dc"));
        } else if (currentProgress>1 && currentProgress<=99) {
            holder.stepProgressStateTextView.setText("IN PROGRESS");
            holder.stepProgressStateRelativeLayout.setBackgroundColor(Color.parseColor("#ffd699"));
        }else{
            holder.stepProgressStateTextView.setText("COMPLETED");
            holder.stepProgressStateRelativeLayout.setBackgroundColor(Color.parseColor("#81c784"));
        }

        // editText - Step Name
        holder.stepTitleEditText.addTextChangedListener(new TextWatcher() {
            Runnable mRunnable = new Runnable() {
                @Override
                public void run() {
                    String text = holder.stepTitleEditText.getText().toString();
                    try{
                     myDB.updateStepName(phaseID, stepsList.get(position).getStepID(), text, projectID);
                    }catch (Exception e){
                        Toast.makeText(context, "Could Not Update the project Name", Toast.LENGTH_SHORT).show();
                    }

                }
            };

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

        //deleting an Step
        holder.deleteStepIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_popper));
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Delete The Step: "+stepsList.get(position).getStepName()+"?");
                alertDialogBuilder.setMessage("This will delete the step and the changes can not be Undone?");
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        newStepsModel stepToBeDeleted = stepsList.get(position);
                        stepsList.remove(position);
                        notifyItemRemoved(position);
                        int wasNotificationSet = stepToBeDeleted.isNotificationSet();
                        myDB.deleteStep(stepToBeDeleted.getStepID(), phaseID, projectID);
                        if(wasNotificationSet == 1){
                         String notification = stepToBeDeleted.getStepNotificationDetails();
                         NotifyMe.cancel(context, notification);
                        }
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        // settingNotification
        holder.stepNotificationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vibrationsEnabled) {
                    vibrator.vibrate(VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_popper));
                areTheSystemNotificationsEnabled = areNotificationsEnabled(context);
                if (!areTheSystemNotificationsEnabled) {
                    checkNotificationPermissions();
                } else {
                    int notification = myDB.getStepIsNotificationSet(stepsList.get(position).getStepID(), phaseID, projectID);
                    if (notification == 1) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        String currentNotificationDetails = myDB.getStepNotification(stepsList.get(position).getStepID(), phaseID, projectID);
                        String silly = "The Current Notification is Set On: " + currentNotificationDetails;
                        builder1.setTitle("Notification Controls")
                                .setMessage(silly)
                                .setPositiveButton("Cancel Notification", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                            cancelNotificationAlarm(currentNotificationDetails);
                                        }else {
                                            NotifyMe.cancel(context, currentNotificationDetails);
                                        }
                                        holder.stepNotificationImageView.setImageResource(R.drawable.baseline_notification_add_24);
                                        if (stepAvailable) {
                                            myDB.updateIsStepNotificationSet(phaseID, currentStepID, 0, projectID);
                                            myDB.updateStepNotification(phaseID, currentStepID, "NOT_SET", projectID);
                                        }
                                        dialog.dismiss();
                                        Toast.makeText(context, "Notification canceled", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Reschedule", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        AlertDialog.Builder rescheduleBuilder = new AlertDialog.Builder(context);
                                        LayoutInflater inflater = LayoutInflater.from(context);
                                        View dialogView = inflater.inflate(R.layout.dialog_notification, null);
                                        rescheduleBuilder.setView(dialogView);
                                        rescheduleBuilder.setTitle("Remind Me On");
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

                                                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                                                        new DatePickerDialog.OnDateSetListener() {
                                                            @Override
                                                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                                                calendar.set(Calendar.YEAR, i);
                                                                calendar.set(Calendar.MONTH, i1);
                                                                calendar.set(Calendar.DAY_OF_MONTH, i2);
                                                                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                                                                        new TimePickerDialog.OnTimeSetListener() {
                                                                            @Override
                                                                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                                                                calendar.set(Calendar.HOUR_OF_DAY, i);
                                                                                calendar.set(Calendar.MINUTE, i1);
                                                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy, HH.mm", Locale.getDefault());
                                                                                newThePhaseNotificationString = dateFormat.format(calendar.getTime());
                                                                                String s = "Schedule notification on: " + newThePhaseNotificationString + "?";
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
                                        rescheduleBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (newThePhaseNotificationString.equals("NOT_SET")) {
                                                    dialog.dismiss();
                                                } else {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                        cancelNotificationAlarm(currentNotificationDetails);
                                                    }else {
                                                        NotifyMe.cancel(context, currentNotificationDetails);
                                                    }
                                                    holder.stepNotificationImageView.setImageResource(R.drawable.baseline_notification_add_24);
                                                    if (stepAvailable) {
                                                        myDB.updateIsStepNotificationSet(phaseID, currentStepID, 0, projectID);
                                                        myDB.updateStepNotification(phaseID, currentStepID, "NOT_SET", projectID);
                                                    }
                                                    try {
                                                        setTheNotification(newThePhaseNotificationString);
                                                        newNotificationSet = true;
                                                        if (stepAvailable) {
                                                            myDB.updateStepNotification(phaseID, currentStepID, newThePhaseNotificationString, projectID);
                                                            myDB.updateIsStepNotificationSet(phaseID, currentStepID, 1, projectID);
                                                            stepsList.get(position).setNotificationSet(1);
                                                            stepsList.get(position).setStepNotificationDetails(newThePhaseNotificationString);
                                                            holder.stepNotificationImageView.setImageResource(R.drawable.baseline_notifications_active_24);
                                                        } else {
                                                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                                            stepsList.get(position).setNotificationSet(0);
                                                            stepsList.get(position).setStepNotificationDetails("NOT_SET");
                                                            holder.stepNotificationImageView.setImageResource(R.drawable.baseline_notification_add_24);
                                                            newNotificationSet = false;
                                                        }
                                                        dialog.dismiss();
                                                        Toast.makeText(context, "Notification rescheduled successfully", Toast.LENGTH_SHORT).show();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        newNotificationSet = false;
                                                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                    }
                                                }
                                            }
                                        });

                                        rescheduleBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                newNotificationSet = false;
                                            }
                                        });
                                        rescheduleBuilder.show();
                                    }
                                })
                                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog1 = builder1.create();
                        dialog1.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = LayoutInflater.from(context);
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

                                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                                        new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                                calendar.set(Calendar.YEAR, i);
                                                calendar.set(Calendar.MONTH, i1);
                                                calendar.set(Calendar.DAY_OF_MONTH, i2);
                                                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                                                        new TimePickerDialog.OnTimeSetListener() {
                                                            @Override
                                                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                                                calendar.set(Calendar.HOUR_OF_DAY, i);
                                                                calendar.set(Calendar.MINUTE, i1);
                                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy, HH.mm", Locale.getDefault());
                                                                String deadlineDate = dateFormat.format(calendar.getTime());
                                                                thePhaseNotificationString = deadlineDate;
                                                                String s = "Schedule notification on: " + deadlineDate + "?";
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
                                if (thePhaseNotificationString.equals("NOT_SET")) {
                                    dialog.dismiss();
                                } else {
                                    try {
                                        setTheNotification(thePhaseNotificationString);
                                        newNotificationSet = true;
                                        if (stepAvailable && !thePhaseNotificationString.equals("NOT_SET")) {
                                            myDB.updateStepNotification(phaseID, currentStepID, thePhaseNotificationString, projectID);
                                            myDB.updateIsStepNotificationSet(phaseID, currentStepID, 1, projectID);
                                            stepsList.get(position).setNotificationSet(1);
                                            stepsList.get(position).setStepNotificationDetails(thePhaseNotificationString);
                                            holder.stepNotificationImageView.setImageResource(R.drawable.baseline_notifications_active_24);
                                        } else {
                                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                            stepsList.get(position).setNotificationSet(0);
                                            stepsList.get(position).setStepNotificationDetails("NOT_SET");
                                            holder.stepNotificationImageView.setImageResource(R.drawable.baseline_notification_add_24);
                                        }
                                        dialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        newNotificationSet = false;
                                        dialog.dismiss();
                                    }
                                }
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                newNotificationSet = false;
                            }
                        });
                        builder.show();

                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return stepsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        EditText stepTitleEditText, theStepNotesEditText;
        TextView stepDeadlineTextView, daysToGoDeadline, daysToGoHeading, stepProgressStateTextView;
        ImageView editTextIcon, dropDownNotes, deleteStepIcon, stepNotificationImageView;
        RelativeLayout stepProgressStateRelativeLayout, ediTextRelativeLayout, notesDropDownRelativeLayout, progressTrackerRelativeLayout, deadlineRelativeLayoutTile;
        SeekBar completionSeekBar;
        NumberProgressBar number_progress_bar;
        CheckBox fullyCompletedCheckBox;
        LinearLayout mainStepTile, progressSeekbarLinearLayout;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            theStepNotesEditText = itemView.findViewById(R.id.theStepNotesEditText);
            stepTitleEditText = itemView.findViewById(R.id.stepTitleEditText);
            stepDeadlineTextView = itemView.findViewById(R.id.stepDeadlineTextView);
            dropDownNotes = itemView.findViewById(R.id.dropDownNotes);
            completionSeekBar = itemView.findViewById(R.id.completionSeekBar);
            number_progress_bar = itemView.findViewById(R.id.number_progress_bar);
            fullyCompletedCheckBox = itemView.findViewById(R.id.fullyCompletedCheckBox);
            daysToGoDeadline = itemView.findViewById(R.id.daysToGoDeadline);
            daysToGoHeading = itemView.findViewById(R.id.daysToGoHeading);
            stepProgressStateTextView = itemView.findViewById(R.id.stepProgressStateTextView);
            stepProgressStateRelativeLayout = itemView.findViewById(R.id.stepProgressStateRelativeLayout);
            mainStepTile = itemView.findViewById(R.id.mainStepTile);
            progressSeekbarLinearLayout = itemView.findViewById(R.id.progressSeekbarLinearLayout);
            deleteStepIcon = itemView.findViewById(R.id.deleteStepIcon);
            stepNotificationImageView = itemView.findViewById(R.id.stepNotificationImageView);
            ediTextRelativeLayout = itemView.findViewById(R.id.ediTextRelativeLayout);
            notesDropDownRelativeLayout = itemView.findViewById(R.id.notesDropDownRelativeLayout);
            progressTrackerRelativeLayout = itemView.findViewById(R.id.progressTrackerRelativeLayout);
            deadlineRelativeLayoutTile = itemView.findViewById(R.id.deadlineRelativeLayoutTile);
        }
    }

    private void checkNotificationPermissions() {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Enable Notifications");
            builder.setMessage("Notifications are currently disabled. Please enable notifications to receive reminders.");

            builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Open app settings to enable notifications
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                    context.startActivity(intent);
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


    private void setTheNotification(String thePhaseNotificationString) throws Exception {
        Calendar calendar = getCalendarObjectForString(thePhaseNotificationString);
        String stepName = stepsList.get(currentStepListPosition).getStepName();
        String content = "You have Scheduled a remainder for the Task: '"+stepName+"'. Have A look";
        String title = "DO-IT: "+stepName+" Reminder.";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                createNotificationChannel();
                setNotificationAlarm(calendar, thePhaseNotificationString, projectID, phaseID, title, content);
            }catch(Exception E){
                Toast.makeText(context, "Something went wrong while setting the notification", Toast.LENGTH_SHORT).show();
                return;
            }
        }else {
            Intent notificationIntent = new Intent(context, StepsActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationIntent.setAction("Notification_Intent");
            notificationIntent.putExtra("NOTIFICATION_PROJECT_ID", projectID);
            notificationIntent.putExtra("NOTIFICATION_PHASE_ID", phaseID);
            NotifyMe notifyMe = new NotifyMe.Builder(context)
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

    public void setNotificationAlarm(Calendar calendar, String key, int projectID, int phaseID, String title, String content) {

        try {
            Intent intent = new Intent(context, broadcast_receiver.class);
            intent.putExtra("notificationId", key.hashCode());
            intent.putExtra("NOTIFICATION_PROJECT_ID", projectID);
            intent.putExtra("NOTIFICATION_PHASE_ID", phaseID);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            intent.setAction("Notification_Intent" + key);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, key.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);

            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            long triggerTimeInMillis = calendar.getTimeInMillis();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeInMillis, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeInMillis, pendingIntent);
            }
        }catch (Exception e){
            Toast.makeText(context, "Something went wrong while setting the notification", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public static boolean areNotificationsEnabled(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        return notificationManager.areNotificationsEnabled();
    }




    public void cancelNotificationAlarm(String key) {
            try {
                Intent intent = new Intent(context, broadcast_receiver.class);
                intent.setAction("Notification_Intent" + key);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, key.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.cancel(pendingIntent);
                NotificationManager nmgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }catch (Exception e){
            Toast.makeText(context, "Something went wrong while setting the notification", Toast.LENGTH_SHORT).show();
            return;
        }
    }



    private String getTodayDate(){
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return today.format(formatter);
    }

    public static boolean hasTimePassed(String dateString) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy, HH.mm");
        Date date = formatter.parse(dateString);
        return date.before(new Date());
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

    public static int calculateDaysRemaining(String deadlineStr) {
        //todo: add try and catch to prevent exceptions
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy, HH.mm");
            LocalDateTime deadlineDateTime = LocalDateTime.parse(deadlineStr, formatter);
            LocalDate deadlineDate = deadlineDateTime.toLocalDate();
            LocalDate currentDate = LocalDate.now();
            long daysRemaining = ChronoUnit.DAYS.between(currentDate, deadlineDate);
            return (int) daysRemaining;
        }catch (Exception e){
            return 0;
        }

    }

    private Calendar getCalendarObjectForString(String thePhaseNotificationString) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy, HH.mm");
        Date date = format.parse(thePhaseNotificationString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
    public interface ProgressListener {
        void onProgressChanged(int progress);

        void isStepCompleted(boolean complete);
    }



}
