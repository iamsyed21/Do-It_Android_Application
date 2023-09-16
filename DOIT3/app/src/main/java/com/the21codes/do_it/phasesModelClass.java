package com.the21codes.do_it;

public class phasesModelClass {


   private String phaseName, phaseDeadline, phasePriority, phaseNotificationDetails, phaseType;
   private int isDeadlineSet, isNotificationSet, isPhaseCompleted, phaseID,projectID, PhaseTotalSteps;



    public phasesModelClass(String phaseName, int projectID, String phaseDeadline,
                            String phasePriority, String phaseNotificationDetails,
                            int phaseTotalSteps, String phaseType,
                            int isDeadlineSet, int isNotificationSet,
                            int isPhaseCompleted) {
        this.projectID = projectID;
        this.phaseName = phaseName;
        this.phaseDeadline = phaseDeadline;
        this.phasePriority = phasePriority;
        this.phaseNotificationDetails = phaseNotificationDetails;
        this.PhaseTotalSteps = phaseTotalSteps;
        this.phaseType = phaseType;
        this.isDeadlineSet = isDeadlineSet;
        this.isNotificationSet = isNotificationSet;
        this.isPhaseCompleted = isPhaseCompleted;
    }

    public phasesModelClass(String phaseName, int phaseID, int projectID, String phaseDeadline,
                            String phasePriority, String phaseNotificationDetails,
                            int phaseTotalSteps, String phaseType,
                            int isDeadlineSet, int isNotificationSet,
                            int isPhaseCompleted) {
        this.phaseID = phaseID;
        this.projectID = projectID;
        this.phaseName = phaseName;
        this.phaseDeadline = phaseDeadline;
        this.phasePriority = phasePriority;
        this.phaseNotificationDetails = phaseNotificationDetails;
        this.PhaseTotalSteps = phaseTotalSteps;
        this.phaseType = phaseType;
        this.isDeadlineSet = isDeadlineSet;
        this.isNotificationSet = isNotificationSet;
        this.isPhaseCompleted = isPhaseCompleted;
    }

    public phasesModelClass() {
    }

    public int getPhaseID() {
        return phaseID;
    }

    public void setPhaseID(int phaseID) {
        this.phaseID = phaseID;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public int getPhaseTotalSteps() {
        return PhaseTotalSteps;
    }

    public void setPhaseTotalSteps(int phaseTotalSteps) {
        PhaseTotalSteps = phaseTotalSteps;
    }

    public String getPhaseType() {
        return phaseType;
    }

    public void setPhaseType(String phaseType) {
        this.phaseType = phaseType;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public String getPhaseDeadline() {
        return phaseDeadline;
    }

    public void setPhaseDeadline(String phaseDeadline) {
        this.phaseDeadline = phaseDeadline;
    }

    public String getPhasePriority() {
        return phasePriority;
    }

    public void setPhasePriority(String phasePriority) {
        this.phasePriority = phasePriority;
    }

    public String getPhaseNotificationDetails() {
        return phaseNotificationDetails;
    }

    public void setPhaseNotificationDetails(String phaseNotificationDetails) {
        this.phaseNotificationDetails = phaseNotificationDetails;
    }

    public int isDeadlineSet() {
        return isDeadlineSet;
    }

    public void setDeadlineSet(int deadlineSet) {
        isDeadlineSet = deadlineSet;
    }

    public int isNotificationSet() {
        return isNotificationSet;
    }

    public void setNotificationSet(int notificationSet) {
        isNotificationSet = notificationSet;
    }

    public int isPhaseCompleted() {
        return isPhaseCompleted;
    }

    public void setPhaseCompleted(int phaseCompleted) {
        isPhaseCompleted = phaseCompleted;
    }
}
