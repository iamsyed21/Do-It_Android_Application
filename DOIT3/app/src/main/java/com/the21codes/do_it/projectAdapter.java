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
import android.widget.ImageView;
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

public class projectAdapter extends RecyclerView.Adapter<projectAdapter.ViewHolder> {
    ArrayList<ProjectModel> projectLists;
    Context context;
    boolean projectAvailable;
    DataBaseHelper myDB;

    SharedPreferences vibrationDetails;
    SharedPreferences.Editor vibrationDetailsEditor;
    Boolean vibrationsEnabled = false;
    private Vibrator vibrator;

    public projectAdapter(ArrayList<ProjectModel> projectLists, Context context) {
        this.projectLists = projectLists;
        this.context = context;
    }

    @NonNull
    @Override
    public projectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.project_list_view,null, false);
        myDB = new DataBaseHelper(context);
        vibrationDetails = context.getSharedPreferences("vibrationPreferences", MODE_PRIVATE);
        vibrationDetailsEditor = vibrationDetails.edit();
        vibrationsEnabled = vibrationDetails.getBoolean("vibrations", true);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull projectAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(projectLists.get(position).getProjectID() >= 0){
            projectAvailable = true;
        }else{
            projectAvailable = false;
        }
        holder.theProjectTileView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.slide_in_nimation));

        holder.projectNameEditText.setText(projectLists.get(position).getProjectName());
        holder.projectDeadLineTextView.setText(projectLists.get(position).getProjectDeadline());
        holder.projectImportanceTextView.setText(projectLists.get(position).getProjectImportance());
        holder.projectTagTextView.setText(projectLists.get(position).getProjectTag());

        String importance = projectLists.get(position).getProjectImportance();
        if(importance.equals("Low")){
            holder.importanceRelativeLayout.setBackgroundResource(R.drawable.green_badge_background);
        } else if (importance.equals("Medium")) {
            holder.importanceRelativeLayout.setBackgroundResource(R.drawable.medium_badge_background);
        } else if (importance.equals("High")) {
            holder.importanceRelativeLayout.setBackgroundResource(R.drawable.high_badge_background);
        }else{
            holder.importanceRelativeLayout.setBackgroundResource(R.drawable.green_badge_background);
        }


        int daysToGo = calculateDaysRemaining(projectLists.get(position).getProjectDeadline().toString().trim());
        String daysToGoString;
        if(daysToGo >= 100){
            holder.isDateDueTextView.setVisibility(View.INVISIBLE);
            daysToGoString = "99+";
            holder.daysToGoTextView.setText(daysToGoString);
            holder.daysToGoTextView.setTextColor(Color.parseColor("#C0EEE4"));
            holder.daysToGoDescriptionTextView.setText("Days to go");
        }else if(daysToGo<0){
            daysToGo = daysToGo* -1;
            holder.daysToGoDescriptionTextView.setText("Days Ago");
            if(daysToGo >= 100){
                daysToGoString ="99+";
                holder.daysToGoTextView.setText(daysToGoString);
            }else{
                daysToGoString = Integer.toString(daysToGo);
                holder.daysToGoTextView.setText(daysToGoString);
            }
            holder.daysToGoTextView.setTextColor(Color.parseColor("#e2725b"));
            holder.isDateDueTextView.setVisibility(View.VISIBLE);
        }else{
            holder.daysToGoDescriptionTextView.setText("Days to go");
            holder.daysToGoTextView.setTextColor(Color.parseColor("#C0EEE4"));
            daysToGoString = Integer.toString(daysToGo);
            holder.daysToGoTextView.setText(daysToGoString);
            holder.isDateDueTextView.setVisibility(View.INVISIBLE);
        }
        holder.exploreProjectRelativeLayout.setOnClickListener(new View.OnClickListener() {
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
        int projectProgress = setTheProjectProgress(projectLists.get(position).getProjectID());
        if(projectProgress<=0){
            holder.projectProgressState.setText("Yet To Start");
            holder.projectProgressState.setTextColor(context.getColor(R.color.Yet_To_Start));
        }else if(projectProgress>0 && projectProgress<=99){
            holder.projectProgressState.setText("In Progress");
            holder.projectProgressState.setTextColor(context.getColor(R.color.IN_PROGRESS));
        }else{
            holder.projectProgressState.setText("Completed");
            holder.projectProgressState.setTextColor(context.getColor(R.color.Completed));
        }
        holder.projectProgressTotalBar.setProgress(projectProgress);

    }

    private int setTheProjectProgress(int projectID) {
        if(projectAvailable){
            ArrayList<Integer> phaseIDs = myDB.getPhaseIDsForProject(projectID);
            int average = 0;
            for(int i=0;i<phaseIDs.size();i++){
                average = average+myDB.getAverageStepProgress(phaseIDs.get(i), projectID);
            }

            if(phaseIDs.size() > 0) {
                int newAverage = average / phaseIDs.size();
                if(newAverage == 100){
                    myDB.updateProjectCompleted(projectID, 1);
                }else{
                    myDB.updateProjectCompleted(projectID, 0);
                }
                return newAverage;
            }else{
               return 0;
            }

        }else{
            return 0;
        }
    }

    public void filteredList(ArrayList<ProjectModel> newProjectLists){
        projectLists = newProjectLists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return projectLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView projectDeadLineTextView,
                projectImportanceTextView, projectTagTextView, projectProgressState, daysToGoTextView,
                daysToGoDescriptionTextView, projectNameEditText, isDateDueTextView;
        RelativeLayout exploreProjectRelativeLayout, importanceRelativeLayout;
        ImageView pencil_iconForEditText;
        ArcProgress projectProgressTotalBar;
        LinearLayout theProjectTileView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            projectNameEditText = itemView.findViewById(R.id.projectNameEditText);
            projectDeadLineTextView = itemView.findViewById(R.id.projectDeadLineTextView);
            projectImportanceTextView = itemView.findViewById(R.id.projectImportanceTextView);
            projectTagTextView = itemView.findViewById(R.id.projectTagTextView);
            exploreProjectRelativeLayout = itemView.findViewById(R.id.exploreProjectRelativeLayout);
            projectProgressState = itemView.findViewById(R.id.projectProgressState);
            daysToGoTextView = itemView.findViewById(R.id.daysToGoTextView);
            pencil_iconForEditText = itemView.findViewById(R.id.pencil_iconForEditText);
            daysToGoDescriptionTextView = itemView.findViewById(R.id.daysToGoDescriptionTextView);
            projectProgressTotalBar = itemView.findViewById(R.id.projectProgressTotalBar);
            isDateDueTextView = itemView.findViewById(R.id.isDateDueTextView);
            importanceRelativeLayout = itemView.findViewById(R.id.importanceRelativeLayout);
            theProjectTileView = itemView.findViewById(R.id.theProjectTileView);
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



}
