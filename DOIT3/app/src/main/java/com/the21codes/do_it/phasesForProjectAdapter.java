package com.the21codes.do_it;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.AlertDialog;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.allyants.notifyme.NotifyMe;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class phasesForProjectAdapter extends RecyclerView.Adapter<phasesForProjectAdapter.ViewHolder> {

    Context context;
    ArrayList<phasesModelClass> phaseList;
    DataBaseHelper myDB;
    private int selectedOptionIndex = 0;
    String thePhaseNotificationString = "NOT_SET";
    String newThePhaseNotificationString = "NOT_SET";
    String newDeadlineDate= "New DeadLine";
    private Handler mHandler = new Handler();
    private static final int DELAY = 2000; // milliseconds
    public boolean isNotificationActive = false;
    public boolean newNotificationSet = false;
    public boolean phaseAvailable;
    int thePhaseID=-1;
    int theProjectID=-1;
    int currentStepListPosition =0;

    SharedPreferences vibrationDetails;
    SharedPreferences.Editor vibrationDetailsEditor;
    Boolean vibrationsEnabled = false;
    private Vibrator vibrator;



    public phasesForProjectAdapter(ArrayList<phasesModelClass> phaseList, Context context) {
        this.context = context;
        this.phaseList = phaseList;

    }

    @NonNull
    @Override
    public phasesForProjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.new_phases_design, null, false);
        myDB = new DataBaseHelper(context);
        vibrationDetails = context.getSharedPreferences("vibrationPreferences", MODE_PRIVATE);
        vibrationDetailsEditor = vibrationDetails.edit();
        vibrationsEnabled = vibrationDetails.getBoolean("vibrations", true);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull phasesForProjectAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        thePhaseID = phaseList.get(position).getPhaseID();
        theProjectID = phaseList.get(position).getProjectID();
        currentStepListPosition = position;
        if(theProjectID>-1 && thePhaseID >-1){
            phaseAvailable = true;
        }else{
            phaseAvailable = false;
        }
        holder.theMainPhaseLinearLayout.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.slide_in_nimation));

        holder.phraseTitleEditText.setText(phaseList.get(position).getPhaseName());
        holder.PhaseDeadlineTextView.setText(phaseList.get(position).getPhaseDeadline());
        holder.phasePriorityTextView.setText(phaseList.get(position).getPhasePriority());
        holder.pencil_iconForEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               holder.phraseTitleEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(holder.phraseTitleEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        holder.pencil_iconForEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    holder.phraseTitleEditText.clearFocus();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(holder.phraseTitleEditText.getWindowToken(), 0);
                }
                return false;
            }
        });
        holder.exploreTheStepsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(10,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_popper));
                Intent intent = new Intent(context, StepsActivity.class);
                int PhaseID = phaseList.get(position).getPhaseID();
                int projectID = phaseList.get(position).getProjectID();
                intent.putExtra("phaseID", PhaseID);
                intent.putExtra("projectID", projectID);
                intent.setAction("Normal_operation");
                context.startActivity(intent);

            }
        });


        holder.PhaseDeadlineTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                if(holder.PhaseDeadlineTextView.getText().toString().trim().equals("Set Deadline")){
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
                                                    int projectID = phaseList.get(position).getProjectID();
                                                    if(projectID < 0){
                                                        Toast.makeText(context, "Could Not Update The Deadline", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        myDB.updatePhaseDeadLine(phaseList.get(position).getProjectID(), phaseList.get(position).getPhaseID(), deadlineDate);
                                                    }
                                                    int daysRemaining = calculateDaysRemaining(deadlineDate);
                                                    if(daysRemaining<0){
                                                        holder.daysToGoPhaseTextView.setText("Due "+(daysRemaining*-1)+" Days Ago");
                                                        holder.daysToGoPhaseTextView.setTextColor(Color.parseColor("#c24a44"));
                                                    }else{
                                                        holder.daysToGoPhaseTextView.setText((daysRemaining)+" Days To Go");
                                                        holder.daysToGoPhaseTextView.setTextColor(Color.parseColor("#C0EEE4"));
                                                    }
                                                    holder.PhaseDeadlineTextView.setText(deadlineDate);
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
                            int projectID = phaseList.get(position).getProjectID();
                            String deadlineString = myDB.getProjectDeadline(projectID);
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
                                                            holder.PhaseDeadlineTextView.setText(deadlineDate);
                                                            myDB.updatePhaseDeadLine(phaseList.get(position).getProjectID(), phaseList.get(position).getPhaseID(), deadlineDate);
                                                            int daysRemaining = calculateDaysRemaining(deadlineDate);
                                                            if(daysRemaining<0){
                                                                holder.daysToGoPhaseTextView.setText("Due "+(daysRemaining*-1)+" Days Ago");
                                                                holder.daysToGoPhaseTextView.setTextColor(Color.parseColor("#e2725b"));
                                                            }else{
                                                                holder.daysToGoPhaseTextView.setText((daysRemaining)+" Days To Go");
                                                                holder.daysToGoPhaseTextView.setTextColor(Color.parseColor("#C0EEE4"));
                                                            }
                                                            boolean timePassed;
                                                            if(deadlineDate.equals("Set Deadline")){
                                                                timePassed = false;
                                                            }else{
                                                                try {
                                                                    timePassed = hasTimePassed(deadlineDate);
                                                                } catch (ParseException e) {
                                                                    timePassed = false;
                                                                }
                                                            }
                                                            int average = setTheProjectProgress(thePhaseID, theProjectID);
                                                            if(average !=100) {
                                                                if (timePassed) {
                                                                    holder.PhaseProgressStateTextView.setText("DUE");
                                                                    holder.PhaseProgressStateTextView.setTextColor(Color.parseColor("#FBFBFB"));
                                                                    holder.phaseProgressRelativeLayout.setBackgroundColor(Color.parseColor("#c24a44"));
                                                                }
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
        holder.phasePriorityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_popper));
                if(holder.phasePriorityTextView.getText().toString().trim().equals("Set Phase Priority")){
                    holder.phasePriorityTextView.setText("Low");
                    holder.phasePriorityTextView.setTextColor(Color.parseColor("#55efc4"));
                    myDB.upDatePhasePriority(phaseList.get(position).getProjectID(), phaseList.get(position).getPhaseID(), "Low");
                }else if( holder.phasePriorityTextView.getText().equals("Low")){
                    holder.phasePriorityTextView.setText("Medium");
                    holder.phasePriorityTextView.setTextColor(Color.parseColor("#f5d76e"));
                    myDB.upDatePhasePriority(phaseList.get(position).getProjectID(), phaseList.get(position).getPhaseID(), "Medium");
                }else if( holder.phasePriorityTextView.getText().equals("Medium")){
                    holder.phasePriorityTextView.setText("High");
                    holder.phasePriorityTextView.setTextColor(Color.parseColor("#FFA07A"));
                    myDB.upDatePhasePriority(phaseList.get(position).getProjectID(), phaseList.get(position).getPhaseID(), "High");
                }else if( holder.phasePriorityTextView.getText().equals("High")){
                    holder.phasePriorityTextView.setText("Low");
                    holder.phasePriorityTextView.setTextColor(Color.parseColor("#55efc4"));
                    myDB.upDatePhasePriority(phaseList.get(position).getProjectID(), phaseList.get(position).getPhaseID(), "Low");
                }else{
                    holder.phasePriorityTextView.setText("Low");
                    holder.phasePriorityTextView.setTextColor(Color.parseColor("#55efc4"));
                    myDB.upDatePhasePriority(phaseList.get(position).getProjectID(), phaseList.get(position).getPhaseID(), "Low");
                }
            }
        });

        //notificationLogic
        holder.deletePhaseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(10,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_popper));
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
                deleteDialog.setTitle("Delete The Phase: "+phaseList.get(position).getPhaseName()+"?");
                deleteDialog.setMessage("This will delete the phase and all its steps. The changes can not be undone?");
                deleteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                deleteDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(phaseAvailable){
                            myDB.deletePhase(thePhaseID, theProjectID);
                            myDB.deleteEntireStepTable(thePhaseID, theProjectID);
                            phasesModelClass phaseToBeDeleted = phaseList.get(position);
                            phaseList.remove(position);
                            notifyItemRemoved(position);
                            int wasNotificationSet = phaseToBeDeleted.isNotificationSet();
                            if(wasNotificationSet == 1){
                                String notification = phaseToBeDeleted.getPhaseNotificationDetails();
                                NotifyMe.cancel(context, notification);
                            }
                        }else{
                            Toast.makeText(context, "Something Went Wrong, please try Again.", Toast.LENGTH_SHORT).show();
                        }
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog DELETEDialog = deleteDialog.create();
                DELETEDialog.show();
            }
        });

        //phase progressBar logic
        if(phaseAvailable) {
            int TotalPhasesOfProjectNumber = myDB.getStepCountForPhase(thePhaseID, theProjectID);
            int TotalCompletedProjects = myDB.getCompletedStepCountForPhase(thePhaseID, theProjectID);
            if(TotalPhasesOfProjectNumber> 0){
                int completion = Math.round(100.0f*((100.0f * TotalCompletedProjects)/(100.0f* TotalPhasesOfProjectNumber)));

                int average = setTheProjectProgress(thePhaseID, theProjectID);
                holder.totalStepsCompletedForPhase.setProgress(average);
                holder.totalStepsCompletedForPhase.invalidate();
                String s = TotalCompletedProjects +"/"+ TotalPhasesOfProjectNumber;
                holder.totalStepsCompleted.setText(s);


                String deadline = myDB.getPhaseDeadline(theProjectID, thePhaseID);
                boolean timePassed = false;
                if(deadline.equals("Set Deadline")){
                    timePassed = false;
                }else{
                    try {
                        timePassed = hasTimePassed(deadline);
                    } catch (ParseException e) {
                        timePassed = false;
                    }
                }

                if(timePassed){
                    if(average == 100){
                        holder.PhaseProgressStateTextView.setText("COMPLETED");
                        holder.phaseProgressRelativeLayout.setBackgroundColor(Color.parseColor("#81c784"));
                        holder.ediTextRelativeLayout.setAlpha(0.65f);
                        holder.deadLineLinearLayout.setAlpha(0.65f);
                        holder.phasePriorityDescription.setAlpha(0.65f);
                        holder.phasePriorityTextView.setAlpha(0.65f);
                        holder.bottomDetailsLinearLayout.setAlpha(0.65f);
                        holder.stepsProgressRelativeLayout.setAlpha(0.65f);
                    }else {
                        holder.PhaseProgressStateTextView.setText("DUE");
                        holder.PhaseProgressStateTextView.setTextColor(Color.parseColor("#FBFBFB"));
                        holder.phaseProgressRelativeLayout.setBackgroundColor(Color.parseColor("#c24a44"));
                    }
                }else {
                    if (average == 100) {
                        holder.PhaseProgressStateTextView.setText("COMPLETED");
                        holder.phaseProgressRelativeLayout.setBackgroundColor(Color.parseColor("#81c784"));
                        holder.ediTextRelativeLayout.setAlpha(0.65f);
                        holder.deadLineLinearLayout.setAlpha(0.65f);
                        holder.phasePriorityDescription.setAlpha(0.65f);
                        holder.phasePriorityTextView.setAlpha(0.65f);
                        holder.bottomDetailsLinearLayout.setAlpha(0.65f);
                        holder.stepsProgressRelativeLayout.setAlpha(0.65f);
                    } else if (average <= 99 && average > 0) {
                        holder.PhaseProgressStateTextView.setText("IN PROGRESS");
                        holder.phaseProgressRelativeLayout.setBackgroundColor(Color.parseColor("#ffd699"));
                        holder.ediTextRelativeLayout.setAlpha(1f);
                        holder.deadLineLinearLayout.setAlpha(1f);
                        holder.phasePriorityDescription.setAlpha(1f);
                        holder.phasePriorityTextView.setAlpha(1f);
                        holder.bottomDetailsLinearLayout.setAlpha(1f);
                        holder.stepsProgressRelativeLayout.setAlpha(1f);
                    } else {
                        holder.PhaseProgressStateTextView.setText("Yet To Start");
                        holder.phaseProgressRelativeLayout.setBackgroundColor(Color.parseColor("#cfd8dc"));
                        holder.ediTextRelativeLayout.setAlpha(1f);
                        holder.deadLineLinearLayout.setAlpha(1f);
                        holder.phasePriorityDescription.setAlpha(1f);
                        holder.phasePriorityTextView.setAlpha(1f);
                        holder.bottomDetailsLinearLayout.setAlpha(1f);
                        holder.stepsProgressRelativeLayout.setAlpha(1f);
                    }
                }
            }else {
                holder.totalStepsCompletedForPhase.setProgress(0);
                holder.totalStepsCompleted.setText("0");
                holder.totalStepsCompletedForPhase.invalidate();
                holder.PhaseProgressStateTextView.setText("Yet To Start");
                holder.phaseProgressRelativeLayout.setBackgroundColor(Color.parseColor("#cfd8dc"));
                holder.ediTextRelativeLayout.setAlpha(1f);
                holder.deadLineLinearLayout.setAlpha(1f);
                holder.phasePriorityDescription.setAlpha(1f);
                holder.phasePriorityTextView.setAlpha(1f);
                holder.bottomDetailsLinearLayout.setAlpha(1f);
                holder.stepsProgressRelativeLayout.setAlpha(1f);
            }
        }else{
            holder.totalStepsCompletedForPhase.setProgress(0);
            holder.totalStepsCompleted.setText("0");
            holder.totalStepsCompletedForPhase.invalidate();
            holder.PhaseProgressStateTextView.setText("Yet To Start");
            holder.phaseProgressRelativeLayout.setBackgroundColor(Color.parseColor("#cfd8dc"));
            holder.ediTextRelativeLayout.setAlpha(1f);
            holder.deadLineLinearLayout.setAlpha(1f);
            holder.phasePriorityDescription.setAlpha(1f);
            holder.phasePriorityTextView.setAlpha(1f);
            holder.bottomDetailsLinearLayout.setAlpha(1f);
            holder.stepsProgressRelativeLayout.setAlpha(1f);
        }

        //color setting for priority
        if(phaseAvailable){
            String currentPhasePriority = phaseList.get(position).getPhasePriority();
            if(currentPhasePriority.equals("Low")){
                holder.phasePriorityTextView.setTextColor(Color.parseColor("#55efc4"));
            }else if(currentPhasePriority.equals("Medium")){
                holder.phasePriorityTextView.setTextColor(Color.parseColor("#f5d76e"));
            } else if (currentPhasePriority.equals("High")) {
                holder.phasePriorityTextView.setTextColor(Color.parseColor("#FFA07A"));
            }else{
                holder.phasePriorityTextView.setText("Low");
                phaseList.get(position).setPhasePriority("Low");
                myDB.upDatePhasePriority(theProjectID, thePhaseID, "Low");
                holder.phasePriorityTextView.setTextColor(Color.parseColor("#55efc4"));
            }
        }

        // days remaining logic
        if(!phaseList.get(position).getPhaseDeadline().equals("Set Deadline")){
            int daysRemaining = calculateDaysRemaining(phaseList.get(position).getPhaseDeadline());
            if(daysRemaining<0){
                holder.daysToGoPhaseTextView.setText("Due "+(daysRemaining*-1)+" Days Ago");
                holder.daysToGoPhaseTextView.setTextColor(Color.parseColor("#C0EEE4"));
            }else{
                holder.daysToGoPhaseTextView.setText((daysRemaining)+" Days To Go");
                holder.daysToGoPhaseTextView.setTextColor(Color.parseColor("#e2725b"));
            }
        }else{
            holder.daysToGoPhaseTextView.setText("Days To Go: NA");
        }


        // phase name edit text
        holder.phraseTitleEditText.addTextChangedListener(new TextWatcher() {
            Runnable mRunnable = new Runnable() {
                @Override
                public void run() {
                    String text = holder.phraseTitleEditText.getText().toString();
                    try{
                        myDB.updatePhaseName(phaseList.get(position).getProjectID(), phaseList.get(position).getPhaseID(), text);
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



    }





    @Override
    public int getItemCount() {
        return phaseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        EditText phraseTitleEditText;
        TextView PhaseDeadlineTextView, phasePriorityTextView, totalStepsCompleted, daysToGoPhaseTextView,
                PhaseProgressStateTextView, phasePriorityDescription;
        ImageView deletePhaseImageView, pencil_iconForEditText;
        RelativeLayout exploreTheStepsLayout, phaseProgressRelativeLayout, ediTextRelativeLayout, stepsProgressRelativeLayout;
        ArcProgress totalStepsCompletedForPhase;
        LinearLayout theMainPhaseLinearLayout, deadLineLinearLayout, bottomDetailsLinearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            phraseTitleEditText = itemView.findViewById(R.id.phraseTitleEditText);
            PhaseDeadlineTextView = itemView.findViewById(R.id.PhaseDeadlineTextView);
            phasePriorityTextView = itemView.findViewById(R.id.phasePriorityTextView);
            deletePhaseImageView = itemView.findViewById(R.id.deletePhaseImageView);
            daysToGoPhaseTextView = itemView.findViewById(R.id.daysToGoPhaseTextView);
            pencil_iconForEditText = itemView.findViewById(R.id.pencil_iconForEditText);
            exploreTheStepsLayout = itemView.findViewById(R.id.exploreTheStepsLayout);
            totalStepsCompleted = itemView.findViewById(R.id.totalStepsCompleted);
            totalStepsCompletedForPhase = itemView.findViewById(R.id.totalStepsCompletedForPhase);
            phaseProgressRelativeLayout = itemView.findViewById(R.id.phaseProgressRelativeLayout);
            PhaseProgressStateTextView = itemView.findViewById(R.id.PhaseProgressStateTextView);
            theMainPhaseLinearLayout = itemView.findViewById(R.id.theMainPhaseLinearLayout);
            ediTextRelativeLayout = itemView.findViewById(R.id.ediTextRelativeLayout);
            deadLineLinearLayout = itemView.findViewById(R.id.deadLineLinearLayout);
            phasePriorityDescription = itemView.findViewById(R.id.phasePriorityDescription);
            stepsProgressRelativeLayout = itemView.findViewById(R.id.stepsProgressRelativeLayout);
            bottomDetailsLinearLayout = itemView.findViewById(R.id.bottomDetailsLinearLayout);

        }
    }


    private int setTheProjectProgress(int phaseID, int projectID) {
        int average =0;
        if(projectID>=0 && phaseID>=0){
            average = average+myDB.getAverageStepProgress(phaseID, projectID);
            if(average ==100){
                myDB.upDateIsPhaseCompleted(projectID, phaseID, 1);
            }else if(average>1 && average<=99){
                myDB.upDateIsPhaseCompleted(projectID, phaseID, 0);
            }else{
                myDB.upDateIsPhaseCompleted(projectID, phaseID, 0);
            }
        }else{
            average =0;
        }

        return average;

    }

    public static boolean hasTimePassed(String dateString) throws ParseException {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy, HH.mm");
            Date date = formatter.parse(dateString);
            return date.before(new Date());
        } catch (Exception e){
            return false;
        }
    }



    public static int calculateDaysRemaining(String deadlineStr) {
        try {
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
}
