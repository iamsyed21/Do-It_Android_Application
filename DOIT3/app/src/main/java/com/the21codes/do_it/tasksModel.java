package com.the21codes.do_it;

public class tasksModel {

    private String date;
    private int steps, phases;


    public tasksModel(String date, int steps, int phases) {
        this.date = date;
        this.steps = steps;
        this.phases = phases;
    }

    public tasksModel(){

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getPhases() {
        return phases;
    }

    public void setPhases(int phases) {
        this.phases = phases;
    }
}
