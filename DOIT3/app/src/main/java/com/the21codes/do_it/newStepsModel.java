package com.the21codes.do_it;

public class newStepsModel {
   private String stepName,stepDeadline, stepNotes, stepNotificationDetails;
   private int isNotificationSet, isDeadlineSet, isStepCompleted, stepID, phaseID, stepProgress, projectID;

    public newStepsModel(String stepName, int phaseID, String stepDeadline, int stepProgress, String stepNotes, String stepNotificationDetails, int isNotificationSet, int isDeadlineSet, int isStepCompleted) {
        this.stepName = stepName;
        this.stepDeadline = stepDeadline;
        this.stepProgress = stepProgress;
        this.stepNotes = stepNotes;
        this.stepNotificationDetails = stepNotificationDetails;
        this.isNotificationSet = isNotificationSet;
        this.isDeadlineSet = isDeadlineSet;
        this.isStepCompleted = isStepCompleted;
        this.phaseID = phaseID;
    }

    public newStepsModel(int stepID,  int phaseID, int projectID, String stepName, String stepDeadline, int stepProgress, String stepNotes, String stepNotificationDetails, int isNotificationSet, int isDeadlineSet, int isStepCompleted) {
        this.stepID = stepID;
        this.stepName = stepName;
        this.stepDeadline = stepDeadline;
        this.stepProgress = stepProgress;
        this.stepNotes = stepNotes;
        this.stepNotificationDetails = stepNotificationDetails;
        this.isNotificationSet = isNotificationSet;
        this.isDeadlineSet = isDeadlineSet;
        this.isStepCompleted = isStepCompleted;
        this.phaseID = phaseID;
        this.projectID = projectID;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public newStepsModel() {
    }

    public int getStepID() {
        return stepID;
    }

    public void setStepID(int stepID) {
        this.stepID = stepID;
    }

    public int getPhaseID() {
        return phaseID;
    }

    public void setPhaseID(int phaseID) {
        this.phaseID = phaseID;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepDeadline() {
        return stepDeadline;
    }

    public void setStepDeadline(String stepDeadline) {
        this.stepDeadline = stepDeadline;
    }

    public int getStepProgress() {
        return stepProgress;
    }

    public void setStepProgress(int stepProgress) {
        this.stepProgress = stepProgress;
    }

    public String getStepNotes() {
        return stepNotes;
    }

    public void setStepNotes(String stepNotes) {
        this.stepNotes = stepNotes;
    }

    public String getStepNotificationDetails() {
        return stepNotificationDetails;
    }

    public void setStepNotificationDetails(String stepNotificationDetails) {
        this.stepNotificationDetails = stepNotificationDetails;
    }

    public int isNotificationSet() {
        return isNotificationSet;
    }

    public void setNotificationSet(int notificationSet) {
        isNotificationSet = notificationSet;
    }

    public int isDeadlineSet() {
        return isDeadlineSet;
    }

    public void setDeadlineSet(int deadlineSet) {
        isDeadlineSet = deadlineSet;
    }

    public int isStepCompleted() {
        return isStepCompleted;
    }

    public void setStepCompleted(int stepCompleted) {
        isStepCompleted = stepCompleted;
    }
}
