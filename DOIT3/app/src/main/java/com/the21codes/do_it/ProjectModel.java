package com.the21codes.do_it;


public class ProjectModel {


    private String projectName, projectStart, projectDeadline, projectImportance, projectTag;
    int projectID, projectPhases, projectCompleted;


    public ProjectModel(String projectName, String projectStart,
                        String projectDeadline, int projectPhases,
                        String projectImportance, int projectCompleted, String projectTag) {
        this.projectName = projectName;
        this.projectStart = projectStart;
        this.projectDeadline = projectDeadline;
        this.projectPhases = projectPhases;
        this.projectImportance = projectImportance;
        this.projectCompleted = projectCompleted;
        this.projectTag = projectTag;
    }

    public ProjectModel(int projectID, String projectName, String projectStart,
                        String projectDeadline, int projectPhases,
                        String projectImportance, int projectCompleted, String projectTag) {
        this.projectID = projectID;
        this.projectName = projectName;
        this.projectStart = projectStart;
        this.projectDeadline = projectDeadline;
        this.projectPhases = projectPhases;
        this.projectImportance = projectImportance;
        this.projectCompleted = projectCompleted;
        this.projectTag = projectTag;
    }


    public ProjectModel() {
    }

    public int getProjectCompleted() {
        return projectCompleted;
    }

    public void setProjectCompleted(int projectCompleted) {
        this.projectCompleted = projectCompleted;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectStart() {
        return projectStart;
    }

    public void setProjectStart(String projectStart) {
        this.projectStart = projectStart;
    }

    public String getProjectDeadline() {
        return projectDeadline;
    }

    public void setProjectDeadline(String projectDeadline) {
        this.projectDeadline = projectDeadline;
    }

    public int getProjectPhases() {
        return projectPhases;
    }

    public void setProjectPhases(int projectPhases) {
        this.projectPhases = projectPhases;
    }

    public String getProjectImportance() {
        return projectImportance;
    }

    public void setProjectImportance(String projectImportance) {
        this.projectImportance = projectImportance;
    }

    public String getProjectTag() {
        return projectTag;
    }

    public void setProjectTag(String projectTag) {
        this.projectTag = projectTag;
    }
}
