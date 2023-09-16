package com.the21codes.do_it;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DatesSorter {
    public static List<ProjectModel> sortProjectsByDeadline(List<ProjectModel> projects) {
        // Create a SimpleDateFormat object to parse the project deadlines
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy, HH.mm");

        // Create a list of Date objects by parsing the project deadlines
        List<Date> deadlineList = new ArrayList<>();
        for (ProjectModel project : projects) {
            try {
                Date parsedDate = format.parse(project.getProjectDeadline());
                deadlineList.add(parsedDate);
            } catch (ParseException e) {
                System.err.println("Error parsing deadline: " + project.getProjectDeadline());
            }
        }

        // Sort the list of Date objects in ascending order
        Collections.sort(deadlineList, new Comparator<Date>() {
            @Override
            public int compare(Date d1, Date d2) {
                return d1.compareTo(d2);
            }
        });

        // Create a new list of ProjectModel objects with the sorted deadlines
        List<ProjectModel> sortedProjects = new ArrayList<>();
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-YY, hh.mm");
        for (int i = 0; i < Math.min(deadlineList.size(), 5); i++) {
            Date deadline = deadlineList.get(i);
            for (ProjectModel project : projects) {
                if (project.getProjectDeadline() != null) {
                    Date projectDeadline;
                    try {
                        projectDeadline = format.parse(project.getProjectDeadline());
                        if (projectDeadline.equals(deadline)) {
                            project.setProjectDeadline(outputFormat.format(deadline));
                            sortedProjects.add(project);
                            break;
                        }
                    } catch (ParseException e) {
                        System.err.println("Error parsing deadline: " + project.getProjectDeadline());
                    }
                }
            }
        }

        return sortedProjects;
    }

}
