package com.the21codes.do_it;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class simpleProjectAdapter extends RecyclerView.Adapter<simpleProjectAdapter.ViewHolder> {
    ArrayList<ProjectModel> projectLists;
    Context context;
    DataBaseHelper myDB;
    SharedPreferences vibrationDetails;
    SharedPreferences.Editor vibrationDetailsEditor;
    Boolean vibrationsEnabled = false;
    private Vibrator vibrator;

    public simpleProjectAdapter(ArrayList<ProjectModel> projectLists, Context context) {
        this.projectLists = projectLists;
        this.context = context;
    }

    @NonNull
    @Override
    public simpleProjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.simple_project_listview, null, false);
        myDB = new DataBaseHelper(context);
        vibrationDetails = context.getSharedPreferences("vibrationPreferences", MODE_PRIVATE);
        vibrationDetailsEditor = vibrationDetails.edit();
        vibrationsEnabled = vibrationDetails.getBoolean("vibrations", true);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull simpleProjectAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.projectNameTextView.setText(projectLists.get(position).getProjectName());
        String shortDeadline = projectLists.get(position).getProjectDeadline().substring(0,9);
        holder.deadlineTextView.setText(shortDeadline);
        holder.mainProjectDetailsLinearLayout.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.slide_in_nimation));

        holder.goToProjectDetailsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(10,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_popper));
                Intent intent = new Intent(context, phasesForProjectActivity.class);
                int projectID = projectLists.get(position).getProjectID();
                intent.putExtra("projectID", projectID);
                intent.setAction("NORMAL_OPERATION");
                context.startActivity(intent);
            }
        });
        String importance = projectLists.get(position).getProjectImportance();
        holder.ImportanceTextView.setText(projectLists.get(position).getProjectImportance());
        if(importance.equals("Low")){
            holder.ImportanceTextView.setTextColor(Color.parseColor("#55efc4"));
        } else if (importance.equals("Medium")) {
            holder.ImportanceTextView.setTextColor(Color.parseColor("#f5d76e"));
        } else if (importance.equals("High")) {
            holder.ImportanceTextView.setTextColor(Color.parseColor("#FFA07A"));
        }else{
            holder.ImportanceTextView.setTextColor(Color.parseColor("#55efc4"));
            holder.ImportanceTextView.setText("Low");
        }


        int daysToGo = calculateDaysRemaining(projectLists.get(position).getProjectDeadline());
        if(daysToGo < 0){
            int newDays = daysToGo*-1;
            holder.daysToGoTextView.setText("Due: ");
            holder.daysRemainingTextView.setText(Integer.toString(newDays)+" days ago");
            holder.daysRemainingTextView.setTextColor(Color.parseColor("#c24a44"));
        }else{
            holder.daysRemainingTextView.setText(Integer.toString(daysToGo));
            holder.daysRemainingTextView.setTextColor(Color.parseColor("#C0EEE4"));
        }




        holder.projectProgressTotalBar.setProgress(setTheProjectProgress(projectLists.get(position).getProjectID()));

    }

    public void filteredList(ArrayList<ProjectModel> newProjectLists){
        projectLists = newProjectLists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return projectLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView projectNameTextView, daysToGoTextView, daysRemainingTextView, ImportanceTextView,
                deadlineTextView;
        RelativeLayout goToProjectDetailsRelativeLayout;
        ArcProgress projectProgressTotalBar;
        LinearLayout mainProjectDetailsLinearLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            projectNameTextView = itemView.findViewById(R.id.projectNameTextView);
            daysToGoTextView = itemView.findViewById(R.id.daysToGoTextView);
            daysRemainingTextView = itemView.findViewById(R.id.daysRemainingTextView);
            ImportanceTextView = itemView.findViewById(R.id.ImportanceTextView);
            goToProjectDetailsRelativeLayout = itemView.findViewById(R.id.goToProjectDetailsRelativeLayout);
            projectProgressTotalBar = itemView.findViewById(R.id.projectProgressTotalBar);
            deadlineTextView = itemView.findViewById(R.id.deadlineTextView);
            mainProjectDetailsLinearLayout = itemView.findViewById(R.id.mainProjectDetailsLinearLayout);

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

    private int setTheProjectProgress(int projectID) {
        int newAverage = 0;
        if(projectID>=0){
            ArrayList<Integer> phaseIDs = myDB.getPhaseIDsForProject(projectID);
            int average = 0;

            for(int i=0;i<phaseIDs.size();i++){
                average = average+myDB.getAverageStepProgress(phaseIDs.get(i), projectID);
            }

            if(phaseIDs.size() > 0) {
                newAverage = average/phaseIDs.size();
            }else{
                newAverage =0;
            }

        }else{
           newAverage =0;
        }


        return newAverage;
    }
}
