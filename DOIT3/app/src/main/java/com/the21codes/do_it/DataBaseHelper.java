package com.the21codes.do_it;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION =1;
    private static final String DATABASE_NAME = "DO_IT_DATABASE";

    //TABLE NAME

    private static final String PROJECT_TABLE = "PROJECT_TABLE";
    private static final String PHASE_TABLE_PREFIX= "PHASE_TABLE_";
    private static final String STEPS_TABLE_PREFIX = "STEPS_TABLE_";

    //TABLE MAIN ID
    private static final String PROJECT_KEY_ID  = "id";
    private static final String PHASE_KEY_ID  = "id";
    private static final String STEP_KEY_ID  = "id";

    //PROJECT TABLE
    private static final String PROJECT_NAME = "PROJECT_NAME";
    private static final String PROJECT_START = "PROJECT_START";
    private static final String PROJECT_DEADLINE = "PROJECT_DEADLINE";
    private static final String PROJECT_PHASES = "PROJECT_PHASES";
    private static final String PROJECT_IMPORTANCE = "PROJECT_IMPORTANCE";
    private static final String PROJECT_TAG = "PROJECT_TAG";
    private static final String PROJECT_COMPLETED = "PROJECT_COMPLETED";

    //PHASES TABLE

    private static final String PHASE_NAME = "PHASES_NAME";
    private static final String PHASE_PROJECT_ID = "PROJECT_ID";
    private static final String PHASE_DEADLINE = "PHASES_DEADLINE";
    private static final String PHASE_PRIORITY = "PHASES_PRIORITY";
    private static final String PHASE_NOTIFICATION = "PHASES_NOTIFICATION";
    private static final String PHASE_STEPS = "PHASES_STEPS";
    private static final String PHASE_TYPE = "PHASES_TYPE";
    private static final String PHASE_DEADLINE_SET = "PHASE_DEADLINE_SET";
    private static final String PHASE_NOTIFICATION_SET = "PHASES_NOTIFICATION_SET";
    private static final String PHASE_IS_COMPLETED = "PHASES_IS_COMPLETED";


    //STEPS TABLE

    private static final String STEP_NAME = "STEP_NAME";
    private static final String STEP_PHASE_ID = "PHASE_ID";
    private static final String STEP_DEADLINE = "STEP_DEADLINE";
    private static final String STEP_PROGRESS = "STEP_PROGRESS";
    private static final String STEP_NOTES = "STEP_NOTES";
    private static final String STEP_NOTIFICATION = "STEP_NOTIFICATION";
    private static final String STEP_DEADLINE_SET = "STEP_DEADLINE_SET";
    private static final String STEP_NOTIFICATION_SET = "STEP_NOTIFICATION_SET";
    private static final String STEP_IS_STEP_COMPLETED = "STEP_IS_COMPLETED";


    private static final String CREATE_TABLE_PROJECT =
            "CREATE TABLE "+PROJECT_TABLE+"("+PROJECT_KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PROJECT_NAME+ " TEXT,"
            + PROJECT_START+ " TEXT,"
            + PROJECT_DEADLINE+ " TEXT,"
            + PROJECT_PHASES+ " INTEGER,"
            + PROJECT_IMPORTANCE+ " TEXT,"
            + PROJECT_COMPLETED+ " INTEGER,"
            + PROJECT_TAG+ " TEXT" + ")";


    public static final String CREATE_TABLE_TASKS_TRACKER =
            "CREATE TABLE tasksTracker (" +
                    "date TEXT PRIMARY KEY," +
                    "steps INTEGER," +
                    "phases INTEGER" +
                    ");";




    public DataBaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //CREATE REQUIRED TABLES
        db.execSQL(CREATE_TABLE_PROJECT);
        db.execSQL(CREATE_TABLE_TASKS_TRACKER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+ STEPS_TABLE_PREFIX);
        db.execSQL("DROP TABLE IF EXISTS "+ PHASE_TABLE_PREFIX);
        db.execSQL("DROP TABLE IF EXISTS "+ PROJECT_TABLE);
    }

    public void createTablesIfNotExist() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"+PROJECT_TABLE+"'", null);

        // Check if the PROJECT_TABLE exists in the database
        if (cursor.getCount() == 0) {
            // If the PROJECT_TABLE does not exist, create it
            db.execSQL(CREATE_TABLE_PROJECT);
        }

        // Close the cursor
        cursor.close();

        // Check if the tasksTracker table exists in the database
        cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='tasksTracker'", null);

        if (cursor.getCount() == 0) {
            // If the tasksTracker table does not exist, create it
            db.execSQL(CREATE_TABLE_TASKS_TRACKER);
        }

        // Close the cursor and database connection
        cursor.close();
        db.close();
    }


    public void deleteTheEntireDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tables = new ArrayList<>();
        while (c.moveToNext()) {
            String tableName = c.getString(0);
            if (!tableName.equals("android_metadata") && !tableName.equals("sqlite_sequence")) {
                tables.add(tableName);
            }
        }
        c.close();

        for (String table : tables) {
            String dropQuery = "DROP TABLE IF EXISTS " + table;
            db.execSQL(dropQuery);
        }

        db.close();

    }



    public void deleteProject(int projectID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PROJECT_TABLE, PROJECT_KEY_ID+ " =?", new String[]{String.valueOf(projectID)});
        db.close();

    }

    public void deletePhase(int phaseID, int projectID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PHASE_TABLE_PREFIX+projectID, PHASE_KEY_ID+ " =?", new String[]{String.valueOf(phaseID)});
        db.close();
    }

    public void deleteEntirePhaseTable(int projectID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + PHASE_TABLE_PREFIX+projectID);
        db.close();
    }

    public void deleteStep(int stepID, int phaseID, int projectID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID, STEP_KEY_ID+ " =?", new String[]{String.valueOf(stepID)});
        db.close();
    }

    public void deleteEntireStepTable(int phaseID, int projectID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID);
        db.close();
    }



    public long insertProjectData(String projectName, String projectStart, String projectDeadline,
                                  int projectPhases, String projectImportance, int projectCompleted, String projectTag){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{PROJECT_TABLE});

        if (cursor.getCount() == 0) {
            // PROJECT_TABLE does not exist, create it
            db.execSQL(CREATE_TABLE_PROJECT);
        }

        cursor.close();


        ContentValues values = new ContentValues();
        values.put(PROJECT_NAME, projectName);
        values.put(PROJECT_START, projectStart);
        values.put(PROJECT_DEADLINE,
                projectDeadline);
        values.put(PROJECT_PHASES, projectPhases);
        values.put(PROJECT_IMPORTANCE, projectImportance);
        values.put(PROJECT_COMPLETED, projectCompleted);
        values.put(PROJECT_TAG, projectTag);

        long newRowID = db.insert(PROJECT_TABLE,null, values);
        db.close();
        return newRowID;
    }

    public long insertPhaseData(long projectID,String phaseName, String phaseDeadline, String phasePriority,
                                  String phaseNotification, int phaseSteps, String phaseType,
                                int isPhaseDeadlineSet, int isPhaseNotificationSet, int isPhaseCompleted){


        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PHASE_PROJECT_ID, projectID);
        values.put(PHASE_NAME, phaseName);
        values.put(PHASE_DEADLINE, phaseDeadline);
        values.put(PHASE_PRIORITY, phasePriority);
        values.put(PHASE_NOTIFICATION, phaseNotification);
        values.put(PHASE_STEPS, phaseSteps);
        values.put(PHASE_TYPE, phaseType);
        values.put(PHASE_DEADLINE_SET, isPhaseDeadlineSet);
        values.put(PHASE_NOTIFICATION_SET, isPhaseNotificationSet);
        values.put(PHASE_IS_COMPLETED, isPhaseCompleted);


        int intProjectID = (int) projectID;
        long newRowID = db.insert(PHASE_TABLE_PREFIX + intProjectID,null, values);
        db.close();
        return newRowID;
    }

    public long insertStepData(long phaseID, int projectID, String stepName, String stepDeadLine, int stepProgress,
                                String stepNotes, String stepNotification, int isStepDeadLineSet,
                                int isStepNotificationSet, int isStepCompleted){


        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STEP_PHASE_ID, phaseID);
        values.put(STEP_NAME, stepName);
        values.put(STEP_DEADLINE, stepDeadLine);
        values.put(STEP_PROGRESS, stepProgress);
        values.put(STEP_NOTES, stepNotes);
        values.put(STEP_NOTIFICATION, stepNotification);
        values.put(STEP_DEADLINE_SET, isStepDeadLineSet);
        values.put(STEP_NOTIFICATION_SET, isStepNotificationSet);
        values.put(STEP_IS_STEP_COMPLETED, isStepCompleted);

        int intPhaseID = (int) phaseID;

        long newRowID = db.insert(STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID,null, values);
        db.close();
        return newRowID;
    }



    //update and insert tasks
    public void updateOrInsertTask(String date, int steps, int phases) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor1 = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{"tasksTracker"});

        if (cursor1.getCount() == 0) {
            // PROJECT_TABLE does not exist, create it
            db.execSQL(CREATE_TABLE_TASKS_TRACKER);
        }

        cursor1.close();



        ContentValues values = new ContentValues();
        values.put("steps", steps);
        values.put("phases", phases);
        values.put("date", date);

        // Check if an entry for the given date already exists
        String selectQuery = "SELECT * FROM tasksTracker WHERE date = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[] { date });

        if (cursor.moveToFirst()) {
            // An entry for the date already exists, so update it
            int stepsColumnIndex = cursor.getColumnIndex("steps");
            int phasesColumnIndex = cursor.getColumnIndex("phases");
            int existingSteps=0;
            int existingPhases =0;
            if(stepsColumnIndex >=0){
                existingSteps = cursor.getInt(stepsColumnIndex);
            }
            if(phasesColumnIndex >= 0){
                existingPhases = cursor.getInt(phasesColumnIndex);
            }
            values.put("steps", existingSteps + steps);
            values.put("phases", existingPhases + phases);
            db.update("tasksTracker", values, "date = ?", new String[] { date });
        } else {
            // No entry for the date exists, so insert a new row
            db.insert("tasksTracker", null, values);
        }

        cursor.close();
        db.close();
    }



    public ArrayList<tasksModel> getLastSevenDaysTasks() {
        ArrayList<tasksModel> tasksList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String todayDate = dateFormat.format(Calendar.getInstance().getTime());
        Cursor todayCursor = db.rawQuery("SELECT * FROM tasksTracker WHERE date=?", new String[] { todayDate });
        if (todayCursor.moveToFirst()) {
            int stepsColumnIndex = todayCursor.getColumnIndex("steps");
            int phasesColumnIndex = todayCursor.getColumnIndex("phases");
            int steps =0;
            int phases =0;
            if(stepsColumnIndex >=0){
                steps = todayCursor.getInt(stepsColumnIndex);
            }
            if(phasesColumnIndex >= 0){
                phases = todayCursor.getInt(phasesColumnIndex);
            }
            tasksList.add(new tasksModel(todayDate, steps, phases));
        } else {
            tasksList.add(new tasksModel(todayDate, 0, 0));
        }
        todayCursor.close();

        for (int i = 0; i < 6; i++) {
            calendar.add(Calendar.DATE, -1);
            String date = dateFormat.format(calendar.getTime());
            Cursor cursor = db.rawQuery("SELECT * FROM tasksTracker WHERE date=?", new String[] { date });
            if (cursor.moveToFirst()) {
                int stepsColumnIndex = cursor.getColumnIndex("steps");
                int phasesColumnIndex = cursor.getColumnIndex("phases");
                int steps =0;
                int phases =0;
                if(stepsColumnIndex >=0){
                   steps = cursor.getInt(stepsColumnIndex);
                }
                if(phasesColumnIndex >= 0){
                    phases = cursor.getInt(phasesColumnIndex);
                }
                tasksList.add(new tasksModel(date, steps, phases));
            } else {
                tasksList.add(new tasksModel(date, 0, 0));
            }
            cursor.close();
        }



        db.close();
        return tasksList;
    }



    //update and get project details
    public void updateAllColumnsInProject(int projectID, ProjectModel projectModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PROJECT_NAME, projectModel.getProjectName());
        values.put(PROJECT_START, projectModel.getProjectStart());
        values.put(PROJECT_DEADLINE, projectModel.getProjectDeadline());
        values.put(PROJECT_PHASES, projectModel.getProjectPhases());
        values.put(PROJECT_IMPORTANCE, projectModel.getProjectImportance());
        values.put(PROJECT_COMPLETED, projectModel.getProjectCompleted());
        values.put(PROJECT_TAG, projectModel.getProjectTag());


        db.update(PROJECT_TABLE, values, PROJECT_KEY_ID + " = ?",
                new String []{ String.valueOf(projectID)});
        db.close();
    }

    public void updateProjectName(int projectID, String newProjectName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PROJECT_NAME, newProjectName);
        db.update(PROJECT_TABLE, values, PROJECT_KEY_ID + " = ?",
                new String []{ String.valueOf(projectID)});
        db.close();
    }

    public void updateProjectDeadline(int projectID, String newProjectDeadline){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PROJECT_DEADLINE, newProjectDeadline);
        db.update(PROJECT_TABLE, values, PROJECT_KEY_ID + " = ?",
                new String []{ String.valueOf(projectID)});
        db.close();
    }

    public void updateProjectPhases(int projectID, String newProjectPhases){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PROJECT_PHASES, newProjectPhases);
        db.update(PROJECT_TABLE, values, PROJECT_KEY_ID + " = ?",
                new String []{ String.valueOf(projectID)});
        db.close();
    }

    public void updateProjectImportance(int projectID, String newProjectImportance){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PROJECT_IMPORTANCE, newProjectImportance);
        db.update(PROJECT_TABLE, values, PROJECT_KEY_ID + " = ?",
                new String []{ String.valueOf(projectID)});
        db.close();
    }

    public void updateProjectTag(int projectID, String newProjectTag){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PROJECT_TAG, newProjectTag);
        db.update(PROJECT_TABLE, values, PROJECT_KEY_ID + " = ?",
                new String []{ String.valueOf(projectID)});
        db.close();
    }

    public void updateProjectCompleted(int projectID, int isProjectCompleted){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PROJECT_COMPLETED, isProjectCompleted);
        db.update(PROJECT_TABLE, values, PROJECT_KEY_ID + " = ?",
                new String []{ String.valueOf(projectID)});
        db.close();
    }

    public ProjectModel getAllProjectData(int ProjectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] cols =
                {PROJECT_KEY_ID, PROJECT_NAME, PROJECT_START, PROJECT_DEADLINE, PROJECT_PHASES,
                        PROJECT_IMPORTANCE, PROJECT_COMPLETED, PROJECT_TAG};

        String selection = PROJECT_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(ProjectID)};

        ProjectModel projectModel = new ProjectModel();

        Cursor cursor =
                db.query(PROJECT_TABLE, cols, selection,selectionArgs,null,null,null);

        if(cursor!=null && cursor.moveToFirst()){
            int projectIDColumnIndex = cursor.getColumnIndex(PROJECT_KEY_ID);
            int projectNameColumnIndex = cursor.getColumnIndex(PROJECT_NAME);
            int projectStartColumnIndex = cursor.getColumnIndex(PROJECT_START);
            int projectDeadlineColumnIndex = cursor.getColumnIndex(PROJECT_DEADLINE);
            int projectPhasesColumnIndex = cursor.getColumnIndex(PROJECT_PHASES);
            int projectImportanceColumnIndex = cursor.getColumnIndex(PROJECT_IMPORTANCE);
            int projectCompletedColumnIndex = cursor.getColumnIndex(PROJECT_COMPLETED);
            int projectTagColumnIndex = cursor.getColumnIndex(PROJECT_TAG);

            if(projectIDColumnIndex >=0) {
                projectModel.setProjectID(cursor.getInt(projectIDColumnIndex));
            }

            if(projectNameColumnIndex >=0) {
                projectModel.setProjectName(cursor.getString(projectNameColumnIndex));
            }

            if(projectStartColumnIndex >=0) {
                projectModel.setProjectStart(cursor.getString(projectStartColumnIndex));
            }

            if(projectDeadlineColumnIndex >=0) {
                projectModel.setProjectDeadline(cursor.getString(projectDeadlineColumnIndex));
            }
            if(projectPhasesColumnIndex >=0) {
                projectModel.setProjectPhases(cursor.getInt(projectPhasesColumnIndex));
            }

            if(projectImportanceColumnIndex >=0) {
                projectModel.setProjectImportance(cursor.getString(projectImportanceColumnIndex));
            }

            if(projectCompletedColumnIndex >=0) {
                projectModel.setProjectCompleted(cursor.getInt(projectCompletedColumnIndex));
            }

            if(projectTagColumnIndex >=0) {
                projectModel.setProjectTag(cursor.getString(projectTagColumnIndex));
            }

            cursor.close();
        }


        return projectModel;
    }

    public ArrayList<ProjectModel> getAllTheProjects(){
        ArrayList<ProjectModel> allProjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+PROJECT_TABLE;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{

                ProjectModel projectModel = new ProjectModel();
                int projectIDColumnIndex = cursor.getColumnIndex(PROJECT_KEY_ID);
                int projectNameColumnIndex = cursor.getColumnIndex(PROJECT_NAME);
                int projectStartColumnIndex = cursor.getColumnIndex(PROJECT_START);
                int projectDeadlineColumnIndex = cursor.getColumnIndex(PROJECT_DEADLINE);
                int projectPhasesColumnIndex = cursor.getColumnIndex(PROJECT_PHASES);
                int projectImportanceColumnIndex = cursor.getColumnIndex(PROJECT_IMPORTANCE);
                int projectCompletedColumnIndex = cursor.getColumnIndex(PROJECT_COMPLETED);
                int projectTagColumnIndex = cursor.getColumnIndex(PROJECT_TAG);

                if(projectIDColumnIndex >=0) {
                    projectModel.setProjectID(cursor.getInt(projectIDColumnIndex));
                }

                if(projectNameColumnIndex >=0) {
                    projectModel.setProjectName(cursor.getString(projectNameColumnIndex));
                }

                if(projectStartColumnIndex >=0) {
                    projectModel.setProjectStart(cursor.getString(projectStartColumnIndex));
                }

                if(projectDeadlineColumnIndex >=0) {
                    projectModel.setProjectDeadline(cursor.getString(projectDeadlineColumnIndex));
                }
                if(projectPhasesColumnIndex >=0) {
                    projectModel.setProjectPhases(cursor.getInt(projectPhasesColumnIndex));
                }

                if(projectImportanceColumnIndex >=0) {
                    projectModel.setProjectImportance(cursor.getString(projectImportanceColumnIndex));
                }

                if(projectCompletedColumnIndex >=0) {
                    projectModel.setProjectCompleted(cursor.getInt(projectCompletedColumnIndex));
                }

                if(projectTagColumnIndex >=0) {
                    projectModel.setProjectTag(cursor.getString(projectTagColumnIndex));
                }

                allProjects.add(projectModel);

            }while(cursor.moveToNext());
        }
        cursor.close();

        return allProjects;
    }

    public ArrayList<ProjectModel> getAllPendingProjects(){
        ArrayList<ProjectModel> allProjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+PROJECT_TABLE+ " WHERE "+PROJECT_COMPLETED+" =0";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{

                ProjectModel projectModel = new ProjectModel();
                int projectIDColumnIndex = cursor.getColumnIndex(PROJECT_KEY_ID);
                int projectNameColumnIndex = cursor.getColumnIndex(PROJECT_NAME);
                int projectStartColumnIndex = cursor.getColumnIndex(PROJECT_START);
                int projectDeadlineColumnIndex = cursor.getColumnIndex(PROJECT_DEADLINE);
                int projectPhasesColumnIndex = cursor.getColumnIndex(PROJECT_PHASES);
                int projectImportanceColumnIndex = cursor.getColumnIndex(PROJECT_IMPORTANCE);
                int projectCompletedColumnIndex = cursor.getColumnIndex(PROJECT_COMPLETED);
                int projectTagColumnIndex = cursor.getColumnIndex(PROJECT_TAG);

                if(projectIDColumnIndex >=0) {
                    projectModel.setProjectID(cursor.getInt(projectIDColumnIndex));
                }

                if(projectNameColumnIndex >=0) {
                    projectModel.setProjectName(cursor.getString(projectNameColumnIndex));
                }

                if(projectStartColumnIndex >=0) {
                    projectModel.setProjectStart(cursor.getString(projectStartColumnIndex));
                }

                if(projectDeadlineColumnIndex >=0) {
                    projectModel.setProjectDeadline(cursor.getString(projectDeadlineColumnIndex));
                }
                if(projectPhasesColumnIndex >=0) {
                    projectModel.setProjectPhases(cursor.getInt(projectPhasesColumnIndex));
                }

                if(projectImportanceColumnIndex >=0) {
                    projectModel.setProjectImportance(cursor.getString(projectImportanceColumnIndex));
                }

                if(projectCompletedColumnIndex >=0) {
                    projectModel.setProjectCompleted(cursor.getInt(projectCompletedColumnIndex));
                }

                if(projectTagColumnIndex >=0) {
                    projectModel.setProjectTag(cursor.getString(projectTagColumnIndex));
                }

                allProjects.add(projectModel);

            }while(cursor.moveToNext());
        }
        cursor.close();

        return allProjects;
    }

    public ArrayList<ProjectModel> getAllCompletedProjects(){
        ArrayList<ProjectModel> allProjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+PROJECT_TABLE+ " WHERE "+PROJECT_COMPLETED+" =1";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{

                ProjectModel projectModel = new ProjectModel();
                int projectIDColumnIndex = cursor.getColumnIndex(PROJECT_KEY_ID);
                int projectNameColumnIndex = cursor.getColumnIndex(PROJECT_NAME);
                int projectStartColumnIndex = cursor.getColumnIndex(PROJECT_START);
                int projectDeadlineColumnIndex = cursor.getColumnIndex(PROJECT_DEADLINE);
                int projectPhasesColumnIndex = cursor.getColumnIndex(PROJECT_PHASES);
                int projectImportanceColumnIndex = cursor.getColumnIndex(PROJECT_IMPORTANCE);
                int projectCompletedColumnIndex = cursor.getColumnIndex(PROJECT_COMPLETED);
                int projectTagColumnIndex = cursor.getColumnIndex(PROJECT_TAG);

                if(projectIDColumnIndex >=0) {
                    projectModel.setProjectID(cursor.getInt(projectIDColumnIndex));
                }

                if(projectNameColumnIndex >=0) {
                    projectModel.setProjectName(cursor.getString(projectNameColumnIndex));
                }

                if(projectStartColumnIndex >=0) {
                    projectModel.setProjectStart(cursor.getString(projectStartColumnIndex));
                }

                if(projectDeadlineColumnIndex >=0) {
                    projectModel.setProjectDeadline(cursor.getString(projectDeadlineColumnIndex));
                }
                if(projectPhasesColumnIndex >=0) {
                    projectModel.setProjectPhases(cursor.getInt(projectPhasesColumnIndex));
                }

                if(projectImportanceColumnIndex >=0) {
                    projectModel.setProjectImportance(cursor.getString(projectImportanceColumnIndex));
                }

                if(projectCompletedColumnIndex >=0) {
                    projectModel.setProjectCompleted(cursor.getInt(projectCompletedColumnIndex));
                }

                if(projectTagColumnIndex >=0) {
                    projectModel.setProjectTag(cursor.getString(projectTagColumnIndex));
                }

                allProjects.add(projectModel);

            }while(cursor.moveToNext());
        }
        cursor.close();

        return allProjects;
    }


    public ArrayList<ProjectModel> getTopFiveHighestImportantProjects(){
        ArrayList<ProjectModel> allProjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + PROJECT_TABLE + " WHERE " + PROJECT_COMPLETED + " = 0 AND (" + PROJECT_IMPORTANCE + " = 'High' OR " + PROJECT_IMPORTANCE + " = 'Medium') ORDER BY CASE " + PROJECT_IMPORTANCE + " WHEN 'High' THEN 0 WHEN 'Medium' THEN 1 END, " + PROJECT_KEY_ID + " ASC LIMIT 5";


        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{

                ProjectModel projectModel = new ProjectModel();
                int projectIDColumnIndex = cursor.getColumnIndex(PROJECT_KEY_ID);
                int projectNameColumnIndex = cursor.getColumnIndex(PROJECT_NAME);
                int projectStartColumnIndex = cursor.getColumnIndex(PROJECT_START);
                int projectDeadlineColumnIndex = cursor.getColumnIndex(PROJECT_DEADLINE);
                int projectPhasesColumnIndex = cursor.getColumnIndex(PROJECT_PHASES);
                int projectImportanceColumnIndex = cursor.getColumnIndex(PROJECT_IMPORTANCE);
                int projectCompletedColumnIndex = cursor.getColumnIndex(PROJECT_COMPLETED);
                int projectTagColumnIndex = cursor.getColumnIndex(PROJECT_TAG);

                if(projectIDColumnIndex >=0) {
                    projectModel.setProjectID(cursor.getInt(projectIDColumnIndex));
                }

                if(projectNameColumnIndex >=0) {
                    projectModel.setProjectName(cursor.getString(projectNameColumnIndex));
                }

                if(projectStartColumnIndex >=0) {
                    projectModel.setProjectStart(cursor.getString(projectStartColumnIndex));
                }

                if(projectDeadlineColumnIndex >=0) {
                    projectModel.setProjectDeadline(cursor.getString(projectDeadlineColumnIndex));
                }
                if(projectPhasesColumnIndex >=0) {
                    projectModel.setProjectPhases(cursor.getInt(projectPhasesColumnIndex));
                }

                if(projectImportanceColumnIndex >=0) {
                    projectModel.setProjectImportance(cursor.getString(projectImportanceColumnIndex));
                }

                if(projectCompletedColumnIndex >=0) {
                    projectModel.setProjectCompleted(cursor.getInt(projectCompletedColumnIndex));
                }

                if(projectTagColumnIndex >=0) {
                    projectModel.setProjectTag(cursor.getString(projectTagColumnIndex));
                }

                allProjects.add(projectModel);

            }while(cursor.moveToNext());
        }
        cursor.close();

        return allProjects;
    }

    public String getProjectName(int ProjectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {PROJECT_NAME};
        String selection = PROJECT_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(ProjectID)};

        Cursor cursor = db.query(PROJECT_TABLE,
                columns,selection,selectionArgs,null,null,null);

        String name = null;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(PROJECT_NAME);
            if(columnIndex >=0) {
                name = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public String getProjectStart(int ProjectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {PROJECT_START};
        String selection = PROJECT_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(ProjectID)};

        Cursor cursor = db.query(PROJECT_TABLE,
                columns,selection,selectionArgs,null,null,null);

        String name = null;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(PROJECT_START);
            if(columnIndex >=0) {
                name = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public String getProjectDeadline(int ProjectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {PROJECT_DEADLINE};
        String selection = PROJECT_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(ProjectID)};

        Cursor cursor = db.query(PROJECT_TABLE,
                columns,selection,selectionArgs,null,null,null);

        String name = null;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(PROJECT_DEADLINE);
            if(columnIndex >=0) {
                name = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public int getProjectPhases(int ProjectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {PROJECT_PHASES};
        String selection = PROJECT_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(ProjectID)};

        Cursor cursor = db.query(PROJECT_TABLE,
                columns,selection,selectionArgs,null,null,null);

        int name = -1;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(PROJECT_PHASES);
            if(columnIndex >=0) {
                name = cursor.getInt(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public String getProjectImportance(int ProjectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {PROJECT_IMPORTANCE};
        String selection = PROJECT_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(ProjectID)};

        Cursor cursor = db.query(PROJECT_TABLE,
                columns,selection,selectionArgs,null,null,null);

        String name = null;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(PROJECT_IMPORTANCE);
            if(columnIndex >=0) {
                name = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public String getProjectTag(int ProjectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {PROJECT_TAG};
        String selection = PROJECT_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(ProjectID)};

        Cursor cursor = db.query(PROJECT_TABLE,
                columns,selection,selectionArgs,null,null,null);

        String name = null;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(PROJECT_TAG);
            if(columnIndex >=0) {
                name = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public int getProjectComplete(int ProjectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {PROJECT_COMPLETED};
        String selection = PROJECT_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(ProjectID)};

        Cursor cursor = db.query(PROJECT_TABLE,
                columns,selection,selectionArgs,null,null,null);

        int name = 0;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(PROJECT_COMPLETED);
            if(columnIndex >=0) {
                name = cursor.getInt(columnIndex);
            }
            cursor.close();
        }

        return name;
    }


    //update and get phases methods
    public void updateAllPhaseValues(int projectID, int phaseID, phasesModelClass phaseModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHASE_NAME, phaseModel.getPhaseName());
        values.put(PHASE_PROJECT_ID, phaseModel.getProjectID());
        values.put(PHASE_DEADLINE, phaseModel.getPhaseDeadline());
        values.put(PHASE_PRIORITY, phaseModel.getPhasePriority());
        values.put(PHASE_NOTIFICATION, phaseModel.getPhaseNotificationDetails());
        values.put(PHASE_STEPS, phaseModel.getPhaseTotalSteps());
        values.put(PHASE_TYPE, phaseModel.getPhaseType());
        values.put(PHASE_DEADLINE_SET, phaseModel.isDeadlineSet());
        values.put(PHASE_NOTIFICATION_SET, phaseModel.isNotificationSet());
        values.put(PHASE_IS_COMPLETED, phaseModel.isPhaseCompleted());
        db.update(PHASE_TABLE_PREFIX+projectID, values, PHASE_KEY_ID+ " = ?",
                new String[]{String.valueOf(phaseID)});
        db.close();
    }

    public void updatePhaseName(int projectID, int phaseID, String newPhaseName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHASE_NAME, newPhaseName);
        db.update(PHASE_TABLE_PREFIX+projectID, values, PHASE_KEY_ID+ " = ?",
                new String[]{String.valueOf(phaseID)});
        db.close();
    }

    public void updatePhaseDeadLine(int projectID, int phaseID, String newPhaseDeadline){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHASE_DEADLINE, newPhaseDeadline);
        db.update(PHASE_TABLE_PREFIX+projectID, values, PHASE_KEY_ID+ " = ?",
                new String[]{String.valueOf(phaseID)});
        db.close();
    }

    public void upDatePhasePriority(int projectID, int phaseID, String newPhasePriority){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHASE_PRIORITY, newPhasePriority);
        db.update(PHASE_TABLE_PREFIX+projectID, values, PHASE_KEY_ID+ " = ?",
                new String[]{String.valueOf(phaseID)});
        db.close();
    }

    public void upDatePhaseNotifications(int projectID, int phaseID, String newPhaseNotification){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHASE_NOTIFICATION, newPhaseNotification);
        db.update(PHASE_TABLE_PREFIX+projectID, values, PHASE_KEY_ID+ " = ?",
                new String[]{String.valueOf(phaseID)});
        db.close();
    }

    public void upDatePhaseSteps(int projectID, int phaseID, int newPhaseSteps){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHASE_STEPS, newPhaseSteps);
        db.update(PHASE_TABLE_PREFIX+projectID, values, PHASE_KEY_ID+ " = ?",
                new String[]{String.valueOf(phaseID)});
        db.close();
    }

    public void upDatePhaseType(int projectID, int phaseID, String newPhaseType){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHASE_STEPS, newPhaseType);
        db.update(PHASE_TABLE_PREFIX+projectID, values, PHASE_KEY_ID+ " = ?",
                new String[]{String.valueOf(phaseID)});
        db.close();
    }

    public void upDateIsPhaseDeadlineSet(int projectID, int phaseID, int isPhaseDeadlineSet){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHASE_DEADLINE_SET, isPhaseDeadlineSet);
        db.update(PHASE_TABLE_PREFIX+projectID, values, PHASE_KEY_ID+ " = ?",
                new String[]{String.valueOf(phaseID)});
        db.close();
    }

    public void upDateIsPhaseNotificationSet(int projectID, int phaseID, int isNotificationSet){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHASE_NOTIFICATION_SET, isNotificationSet);
        db.update(PHASE_TABLE_PREFIX+projectID, values, PHASE_KEY_ID+ " = ?",
                new String[]{String.valueOf(phaseID)});
        db.close();
    }

    public void upDateIsPhaseCompleted(int projectID, int phaseID, int isPhaseCompleted){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHASE_IS_COMPLETED, isPhaseCompleted);
        db.update(PHASE_TABLE_PREFIX+projectID, values, PHASE_KEY_ID+ " = ?",
                new String[]{String.valueOf(phaseID)});
        db.close();
    }

    public phasesModelClass getAllPhasesData(int phaseID, int ProjectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] cols =
                {PHASE_KEY_ID, PHASE_PROJECT_ID, PHASE_NAME, PHASE_DEADLINE, PHASE_PRIORITY,
                        PHASE_NOTIFICATION, PHASE_STEPS, PHASE_TYPE, PHASE_DEADLINE_SET,
                        PHASE_NOTIFICATION_SET, PHASE_IS_COMPLETED};

        String selection = PHASE_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(phaseID)};

      phasesModelClass phaseModel = new phasesModelClass();
        Cursor cursor =
                db.query(PHASE_TABLE_PREFIX+ProjectID, cols, selection,selectionArgs,null,null,null);

        if(cursor!=null && cursor.moveToFirst()){
            int phaseIDColumnIndex = cursor.getColumnIndex(PHASE_KEY_ID);
            int phaseProjectIDColumnIndex = cursor.getColumnIndex(PHASE_PROJECT_ID);
            int PhaseNameColumnIndex = cursor.getColumnIndex(PHASE_NAME);
            int phaseDeadLineColumnIndex = cursor.getColumnIndex(PHASE_DEADLINE);
            int phasePriorityColumnIndex = cursor.getColumnIndex(PHASE_PRIORITY);
            int phaseNotificationColumnIndex = cursor.getColumnIndex(PHASE_NOTIFICATION);
            int phaseStepsColumnIndex = cursor.getColumnIndex(PHASE_STEPS);
            int phaseTypeColumnIndex = cursor.getColumnIndex(PHASE_TYPE);
            int phaseDeadlineSetColumnIndex = cursor.getColumnIndex(PHASE_DEADLINE_SET);
            int phaseNotificationSetColumnIndex = cursor.getColumnIndex(PHASE_NOTIFICATION_SET);
            int phaseIsCompletedColumnIndex = cursor.getColumnIndex(PHASE_IS_COMPLETED);

            if(phaseIDColumnIndex >=0) {
                phaseModel.setPhaseID(cursor.getInt(phaseIDColumnIndex));
            }

            if(phaseProjectIDColumnIndex >=0) {
                phaseModel.setProjectID(cursor.getInt(phaseProjectIDColumnIndex));
            }

            if(PhaseNameColumnIndex >=0) {
                phaseModel.setPhaseName(cursor.getString(PhaseNameColumnIndex));
            }

            if(phaseDeadLineColumnIndex >=0) {
                phaseModel.setPhaseDeadline(cursor.getString(phaseDeadLineColumnIndex));
            }
            if(phasePriorityColumnIndex >=0) {
                phaseModel.setPhasePriority(cursor.getString(phasePriorityColumnIndex));
            }

            if(phaseNotificationColumnIndex >=0) {
                phaseModel.setPhaseNotificationDetails(cursor.getString(phaseNotificationColumnIndex));
            }
            if(phaseStepsColumnIndex >=0) {
                phaseModel.setPhaseTotalSteps(cursor.getInt(phaseStepsColumnIndex));
            }

            if(phaseTypeColumnIndex >=0) {
                phaseModel.setPhaseType(cursor.getString(phaseTypeColumnIndex));
            }
            if(phaseDeadlineSetColumnIndex >=0) {
                phaseModel.setDeadlineSet(cursor.getInt(phaseDeadlineSetColumnIndex));
            }
            if(phaseNotificationSetColumnIndex >=0) {
                phaseModel.setNotificationSet(cursor.getInt(phaseNotificationSetColumnIndex));
            }
            if(phaseIsCompletedColumnIndex >=0) {
                phaseModel.setPhaseCompleted(cursor.getInt(phaseIsCompletedColumnIndex));
            }

            cursor.close();


        }


        return phaseModel;
    }

    public ArrayList<phasesModelClass> getAllPhasesForaProject(int ProjectID){
        ArrayList<phasesModelClass> AllPhases = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+ PHASE_TABLE_PREFIX+ProjectID;
        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do{
               phasesModelClass phaseModel = new phasesModelClass();
                int phaseIDColumnIndex = cursor.getColumnIndex(PHASE_KEY_ID);
                int phaseProjectIDColumnIndex = cursor.getColumnIndex(PHASE_PROJECT_ID);
                int PhaseNameColumnIndex = cursor.getColumnIndex(PHASE_NAME);
                int phaseDeadLineColumnIndex = cursor.getColumnIndex(PHASE_DEADLINE);
                int phasePriorityColumnIndex = cursor.getColumnIndex(PHASE_PRIORITY);
                int phaseNotificationColumnIndex = cursor.getColumnIndex(PHASE_NOTIFICATION);
                int phaseStepsColumnIndex = cursor.getColumnIndex(PHASE_STEPS);
                int phaseTypeColumnIndex = cursor.getColumnIndex(PHASE_TYPE);
                int phaseDeadlineSetColumnIndex = cursor.getColumnIndex(PHASE_DEADLINE_SET);
                int phaseNotificationSetColumnIndex = cursor.getColumnIndex(PHASE_NOTIFICATION_SET);
                int phaseIsCompletedColumnIndex = cursor.getColumnIndex(PHASE_IS_COMPLETED);

                if(phaseIDColumnIndex >=0) {
                    phaseModel.setPhaseID(cursor.getInt(phaseIDColumnIndex));
                }

                if(phaseProjectIDColumnIndex >=0) {
                    phaseModel.setProjectID(cursor.getInt(phaseProjectIDColumnIndex));
                }

                if(PhaseNameColumnIndex >=0) {
                    phaseModel.setPhaseName(cursor.getString(PhaseNameColumnIndex));
                }

                if(phaseDeadLineColumnIndex >=0) {
                    phaseModel.setPhaseDeadline(cursor.getString(phaseDeadLineColumnIndex));
                }
                if(phasePriorityColumnIndex >=0) {
                    phaseModel.setPhasePriority(cursor.getString(phasePriorityColumnIndex));
                }

                if(phaseNotificationColumnIndex >=0) {
                    phaseModel.setPhaseNotificationDetails(cursor.getString(phaseNotificationColumnIndex));
                }
                if(phaseStepsColumnIndex >=0) {
                    phaseModel.setPhaseTotalSteps(cursor.getInt(phaseStepsColumnIndex));
                }

                if(phaseTypeColumnIndex >=0) {
                    phaseModel.setPhaseType(cursor.getString(phaseTypeColumnIndex));
                }
                if(phaseDeadlineSetColumnIndex >=0) {
                    phaseModel.setDeadlineSet(cursor.getInt(phaseDeadlineSetColumnIndex));
                }
                if(phaseNotificationSetColumnIndex >=0) {
                    phaseModel.setNotificationSet(cursor.getInt(phaseNotificationSetColumnIndex));
                }
                if(phaseIsCompletedColumnIndex >=0) {
                    phaseModel.setPhaseCompleted(cursor.getInt(phaseIsCompletedColumnIndex));
                }

                AllPhases.add(phaseModel);



            }while(cursor.moveToNext());
        }

        cursor.close();

        return AllPhases;

    }

    public ArrayList<Integer> getPhaseIDsForProject(int projectID) {
        ArrayList<Integer> phaseIDs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + PHASE_KEY_ID + " FROM " + PHASE_TABLE_PREFIX + projectID;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int phaseIDColumnIndex = cursor.getColumnIndex(PHASE_KEY_ID);
                if (phaseIDColumnIndex >= 0) {
                    phaseIDs.add(cursor.getInt(phaseIDColumnIndex));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();

        return phaseIDs;
    }



    public int getPhaseCountForProject(int ProjectID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + PHASE_TABLE_PREFIX + ProjectID;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getCompletedPhasesCountForProject(int ProjectID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + PHASE_TABLE_PREFIX + ProjectID + " WHERE " + PHASE_IS_COMPLETED + "=1";
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }



    public String getPhaseName(int ProjectID, int phaseID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {PHASE_NAME};
        String selection = PHASE_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(phaseID)};

        Cursor cursor = db.query(PHASE_TABLE_PREFIX+ProjectID,
                columns,selection,selectionArgs,null,null,null);

        String name = null;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(PHASE_NAME);
            if(columnIndex >=0) {
                name = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public String getPhaseDeadline(int ProjectID, int phaseID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {PHASE_DEADLINE};
        String selection = PHASE_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(phaseID)};

        Cursor cursor = db.query(PHASE_TABLE_PREFIX+ProjectID,
                columns,selection,selectionArgs,null,null,null);

        String name = null;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(PHASE_DEADLINE);
            if(columnIndex >=0) {
                name = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public String getPhasePriority(int ProjectID, int phaseID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {PHASE_PRIORITY};
        String selection = PHASE_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(phaseID)};

        Cursor cursor = db.query(PHASE_TABLE_PREFIX+ProjectID,
                columns,selection,selectionArgs,null,null,null);

        String name = null;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(PHASE_PRIORITY);
            if(columnIndex >=0) {
                name = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public String getPhaseNotification(int ProjectID, int phaseID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {PHASE_NOTIFICATION};
        String selection = PHASE_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(phaseID)};

        Cursor cursor = db.query(PHASE_TABLE_PREFIX+ProjectID,
                columns,selection,selectionArgs,null,null,null);

        String name = null;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(PHASE_NOTIFICATION);
            if(columnIndex >=0) {
                name = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public int getPhaseSteps(int ProjectID, int phaseID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {PHASE_STEPS};
        String selection = PHASE_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(phaseID)};

        Cursor cursor = db.query(PHASE_TABLE_PREFIX+ProjectID,
                columns,selection,selectionArgs,null,null,null);

        int name = -1;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(PHASE_STEPS);
            if(columnIndex >=0) {
                name = cursor.getInt(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public String getPhaseType(int ProjectID, int phaseID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {PHASE_TYPE};
        String selection = PHASE_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(phaseID)};

        Cursor cursor = db.query(PHASE_TABLE_PREFIX+ProjectID,
                columns,selection,selectionArgs,null,null,null);

        String name = null;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(PHASE_TYPE);
            if(columnIndex >=0) {
                name = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public int getPhaseNotificationSet(int ProjectID, int phaseID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {PHASE_NOTIFICATION_SET};
        String selection = PHASE_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(phaseID)};

        Cursor cursor = db.query(PHASE_TABLE_PREFIX+ProjectID,
                columns,selection,selectionArgs,null,null,null);

        int name = -1;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(PHASE_NOTIFICATION_SET);
            if(columnIndex >=0) {
                name = cursor.getInt(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public int getPhaseDeadlineSet(int ProjectID, int phaseID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {PHASE_DEADLINE_SET};
        String selection = PHASE_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(phaseID)};

        Cursor cursor = db.query(PHASE_TABLE_PREFIX+ProjectID,
                columns,selection,selectionArgs,null,null,null);

        int name = -1;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(PHASE_DEADLINE_SET);
            if(columnIndex >=0) {
                name = cursor.getInt(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public int getPhaseIsCompleted(int ProjectID, int phaseID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {PHASE_IS_COMPLETED};
        String selection = PHASE_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(phaseID)};

        Cursor cursor = db.query(PHASE_TABLE_PREFIX+ProjectID,
                columns,selection,selectionArgs,null,null,null);

        int name = -1;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(PHASE_IS_COMPLETED);
            if(columnIndex >=0) {
                name = cursor.getInt(columnIndex);
            }
            cursor.close();
        }

        return name;
    }


    //update and get Step Methods
    public void updateAllStepDetails(int phaseID, int StepID, newStepsModel stepModel, int projectID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STEP_NAME, stepModel.getStepName());
        values.put(STEP_PHASE_ID, stepModel.getPhaseID());
        values.put(STEP_DEADLINE, stepModel.getStepDeadline());
        values.put(STEP_PROGRESS, stepModel.getStepProgress());
        values.put(STEP_NOTES, stepModel.getStepNotes());
        values.put(STEP_NOTIFICATION, stepModel.getStepNotificationDetails());
        values.put(STEP_DEADLINE_SET, stepModel.isDeadlineSet());
        values.put(STEP_NOTIFICATION_SET, stepModel.isNotificationSet());
        values.put(STEP_IS_STEP_COMPLETED, stepModel.isStepCompleted());

        db.update(STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID, values, STEP_KEY_ID+ " = ?",
                new String[]{String.valueOf(StepID)});
        db.close();
    }




    public void updateStepName(int phaseID, int StepID, String newStepName, int projectID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STEP_NAME, newStepName);
        db.update(STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID, values, STEP_KEY_ID+ " = ?",
                new String[]{String.valueOf(StepID)});
        db.close();
    }

    public void updateStepDeadLine(int phaseID, int StepID, String newStepDeadLine, int projectID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STEP_DEADLINE, newStepDeadLine);
        db.update(STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID, values, STEP_KEY_ID+ " = ?",
                new String[]{String.valueOf(StepID)});
        db.close();
    }

    public void updateStepProgress(int phaseID, int StepID, int newStepProgress, int projectID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STEP_PROGRESS, newStepProgress);
        db.update(STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID, values, STEP_KEY_ID+ " = ?",
                new String[]{String.valueOf(StepID)});
        db.close();
    }

    public void updateStepNotes(int phaseID, int StepID, String newStepNote, int projectID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STEP_NOTES, newStepNote);
        db.update(STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID, values, STEP_KEY_ID+ " = ?",
                new String[]{String.valueOf(StepID)});
        db.close();
    }

    public void updateStepNotification(int phaseID, int StepID, String newStepNotification, int projectID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STEP_NOTIFICATION, newStepNotification);
        db.update(STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID, values, STEP_KEY_ID+ " = ?",
                new String[]{String.valueOf(StepID)});
        db.close();
    }

    public void updateIsStepDeaLineSet(int phaseID, int StepID, int isStepDeadLineSet, int projectID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STEP_DEADLINE_SET, isStepDeadLineSet);
        db.update(STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID, values, STEP_KEY_ID+ " = ?",
                new String[]{String.valueOf(StepID)});
        db.close();
    }

    public void updateIsStepNotificationSet(int phaseID, int StepID, int isStepNotificationSet, int projectID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STEP_NOTIFICATION_SET, isStepNotificationSet);
        db.update(STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID, values, STEP_KEY_ID+ " = ?",
                new String[]{String.valueOf(StepID)});
        db.close();
    }

    public void updateIsStepCompleted(int phaseID, int StepID, int isStepCompleted, int projectID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STEP_IS_STEP_COMPLETED, isStepCompleted);
        db.update(STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID, values, STEP_KEY_ID+ " = ?",
                new String[]{String.valueOf(StepID)});
        db.close();
    }

    public int getStepCountForPhase(int phaseID, int projectID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getCompletedStepCountForPhase(int phaseID, int projectID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID + " WHERE " + STEP_IS_STEP_COMPLETED + "=1";
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }


    public String getStepName(int stepID, int PhaseID, int projectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {STEP_NAME};
        String selection = STEP_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(stepID)};

        Cursor cursor = db.query(STEPS_TABLE_PREFIX+PhaseID+PHASE_TABLE_PREFIX+projectID,
                columns,selection,selectionArgs,null,null,null);

        String name = null;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(STEP_NAME);
            if(columnIndex >=0) {
                name = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public String getStepDeadline(int stepID, int PhaseID, int projectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {STEP_DEADLINE};
        String selection = STEP_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(stepID)};

        Cursor cursor = db.query(STEPS_TABLE_PREFIX+PhaseID+PHASE_TABLE_PREFIX+projectID,
                columns,selection,selectionArgs,null,null,null);

        String name = null;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(STEP_DEADLINE);
            if(columnIndex >=0) {
                name = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public int getStepProgress(int stepID, int PhaseID, int projectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {STEP_PROGRESS};
        String selection = STEP_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(stepID)};

        Cursor cursor = db.query(STEPS_TABLE_PREFIX+PhaseID+PHASE_TABLE_PREFIX+projectID,
                columns,selection,selectionArgs,null,null,null);

        int progress = -1;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(STEP_PROGRESS);
            if(columnIndex >=0) {
                progress = cursor.getInt(columnIndex);
            }
            cursor.close();
        }

        return progress;
    }

    public String getStepNotes(int stepID, int PhaseID, int projectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {STEP_NOTES};
        String selection = STEP_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(stepID)};

        Cursor cursor = db.query(STEPS_TABLE_PREFIX+PhaseID+PHASE_TABLE_PREFIX+projectID,
                columns,selection,selectionArgs,null,null,null);

        String name = null;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(STEP_NOTES);
            if(columnIndex >=0) {
                name = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public String getStepNotification(int stepID, int PhaseID, int projectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {STEP_NOTIFICATION};
        String selection = STEP_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(stepID)};

        Cursor cursor = db.query(STEPS_TABLE_PREFIX+PhaseID+PHASE_TABLE_PREFIX+projectID,
                columns,selection,selectionArgs,null,null,null);

        String name = null;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(STEP_NOTIFICATION);
            if(columnIndex >=0) {
                name = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return name;
    }

    public int getStepIsDeadLineSet(int stepID, int PhaseID, int projectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {STEP_DEADLINE_SET};
        String selection = STEP_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(stepID)};

        Cursor cursor = db.query(STEPS_TABLE_PREFIX+PhaseID+PHASE_TABLE_PREFIX+projectID,
                columns,selection,selectionArgs,null,null,null);

        int progress = -1;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(STEP_DEADLINE_SET);
            if(columnIndex >=0) {
                progress = cursor.getInt(columnIndex);
            }
            cursor.close();
        }

        return progress;
    }

    public int getStepIsNotificationSet(int stepID, int PhaseID, int projectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {STEP_NOTIFICATION_SET};
        String selection = STEP_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(stepID)};

        Cursor cursor = db.query(STEPS_TABLE_PREFIX+PhaseID+PHASE_TABLE_PREFIX+projectID,
                columns,selection,selectionArgs,null,null,null);

        int progress = -1;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(STEP_NOTIFICATION_SET);
            if(columnIndex >=0) {
                progress = cursor.getInt(columnIndex);
            }
            cursor.close();
        }

        return progress;
    }

    public int getStepIsCompleted(int stepID, int phaseID, int projectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {STEP_IS_STEP_COMPLETED};
        String selection = STEP_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(stepID)};

        Cursor cursor = db.query(STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID,
                columns,selection,selectionArgs,null,null,null);

        int progress = -1;
        if(cursor !=null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(STEP_IS_STEP_COMPLETED);
            if(columnIndex >=0) {
                progress = cursor.getInt(columnIndex);
            }
            cursor.close();
        }

        return progress;
    }

    public ArrayList<Integer> getStepIDsForPhase(int phaseID, int projectID) {
        ArrayList<Integer> phaseIDs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + STEP_KEY_ID + " FROM " + STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int phaseIDColumnIndex = cursor.getColumnIndex(PHASE_KEY_ID);
                if (phaseIDColumnIndex >= 0) {
                    phaseIDs.add(cursor.getInt(phaseIDColumnIndex));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();

        return phaseIDs;
    }

    public newStepsModel getAllStepsData(int stepID, int phaseID, int projectID){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] cols =
                {STEP_KEY_ID, STEP_PHASE_ID, STEP_NAME, STEP_DEADLINE, STEP_PROGRESS,
                        STEP_NOTES, STEP_NOTIFICATION, STEP_DEADLINE_SET, STEP_NOTIFICATION_SET,
                        STEP_IS_STEP_COMPLETED};
        String selection = STEP_KEY_ID+ " = ?";
        String[] selectionArgs = {String.valueOf(stepID)};

        newStepsModel stepModel = new newStepsModel();
        Cursor cursor =
                db.query(STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID, cols, selection,selectionArgs,null,null,null);

        if(cursor!=null && cursor.moveToFirst()){
            int StepNameColumnIndex = cursor.getColumnIndex(STEP_NAME);
            int StepIDColumnIndex = cursor.getColumnIndex(STEP_KEY_ID);
            int StepPhaseIDColumnIndex = cursor.getColumnIndex(STEP_PHASE_ID);
            int StepDeadLineColumnIndex = cursor.getColumnIndex(STEP_DEADLINE);
            int StepProgressColumnIndex = cursor.getColumnIndex(STEP_PROGRESS);
            int StepNotesColumnIndex = cursor.getColumnIndex(STEP_NOTES);
            int StepNotificationColumnIndex = cursor.getColumnIndex(STEP_NOTIFICATION);
            int StepIsDeadLineSetColumnIndex = cursor.getColumnIndex(STEP_DEADLINE_SET);
            int StepIsNotificationSetColumnIndex = cursor.getColumnIndex(STEP_NOTIFICATION_SET);
            int StepIsStepCompleted = cursor.getColumnIndex(STEP_IS_STEP_COMPLETED);

            if(StepNameColumnIndex >=0) {
                    stepModel.setStepName(cursor.getString(StepNameColumnIndex));
            }

            if(StepIDColumnIndex >=0) {
                    stepModel.setStepID(cursor.getInt(StepIDColumnIndex));
                }

            if(StepPhaseIDColumnIndex >=0) {
                stepModel.setPhaseID(cursor.getInt(StepPhaseIDColumnIndex));
            }

            if(StepDeadLineColumnIndex >=0) {
                stepModel.setStepDeadline(cursor.getString(StepDeadLineColumnIndex));
            }
            if(StepProgressColumnIndex >=0) {
                stepModel.setStepProgress(cursor.getInt(StepProgressColumnIndex));
            }

            if(StepNotesColumnIndex >=0) {
                stepModel.setStepNotes(cursor.getString(StepNotesColumnIndex));
            }
            if(StepNotificationColumnIndex >=0) {
                stepModel.setStepNotificationDetails(cursor.getString(StepNotificationColumnIndex));
            }

            if(StepIsDeadLineSetColumnIndex >=0) {
                stepModel.setDeadlineSet(cursor.getInt(StepIsDeadLineSetColumnIndex));
            }
            if(StepIsNotificationSetColumnIndex >=0) {
                stepModel.setNotificationSet(cursor.getInt(StepIsNotificationSetColumnIndex));
            }
            if(StepIsStepCompleted >=0) {
                stepModel.setStepCompleted(cursor.getInt(StepIsStepCompleted));
            }

            cursor.close();


        }


        return stepModel;
    }

    public int getAverageStepProgress(int phaseID, int projectID){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] cols = {STEP_PROGRESS};
        double average = 0;
        int count = 0;
        Cursor cursor = db.query(STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID, cols, null, null, null, null, null);

        if(cursor!=null && cursor.moveToFirst()){
            int StepProgressColumnIndex = cursor.getColumnIndex(STEP_PROGRESS);

            do{
                if(StepProgressColumnIndex >=0) {
                    average += cursor.getInt(StepProgressColumnIndex);
                    count++;
                }
            }while(cursor.moveToNext());

            cursor.close();
        }

        if(count > 0){
            average = average/count;
        }

        return Double.valueOf(average).intValue();
    }

    public ArrayList<newStepsModel> getAllStepsForaPhase(int phaseID, int projectID){
        ArrayList<newStepsModel> AllSteps = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+ STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID;
        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do{
                int StepNameColumnIndex = cursor.getColumnIndex(STEP_NAME);
                int StepIDColumnIndex = cursor.getColumnIndex(STEP_KEY_ID);
                int StepPhaseIDColumnIndex = cursor.getColumnIndex(STEP_PHASE_ID);
                int StepDeadLineColumnIndex = cursor.getColumnIndex(STEP_DEADLINE);
                int StepProgressColumnIndex = cursor.getColumnIndex(STEP_PROGRESS);
                int StepNotesColumnIndex = cursor.getColumnIndex(STEP_NOTES);
                int StepNotificationColumnIndex = cursor.getColumnIndex(STEP_NOTIFICATION);
                int StepIsDeadLineSetColumnIndex = cursor.getColumnIndex(STEP_DEADLINE_SET);
                int StepIsNotificationSetColumnIndex = cursor.getColumnIndex(STEP_NOTIFICATION_SET);
                int StepIsStepCompleted = cursor.getColumnIndex(STEP_IS_STEP_COMPLETED);

                newStepsModel stepModel = new newStepsModel();

                if(StepNameColumnIndex >=0) {
                    stepModel.setStepName(cursor.getString(StepNameColumnIndex));
                }

                if(StepIDColumnIndex >=0) {
                    stepModel.setStepID(cursor.getInt(StepIDColumnIndex));
                }

                if(StepPhaseIDColumnIndex >=0) {
                    stepModel.setPhaseID(cursor.getInt(StepPhaseIDColumnIndex));
                }

                if(StepDeadLineColumnIndex >=0) {
                    stepModel.setStepDeadline(cursor.getString(StepDeadLineColumnIndex));
                }
                if(StepProgressColumnIndex >=0) {
                    stepModel.setStepProgress(cursor.getInt(StepProgressColumnIndex));
                }

                if(StepNotesColumnIndex >=0) {
                    stepModel.setStepNotes(cursor.getString(StepNotesColumnIndex));
                }
                if(StepNotificationColumnIndex >=0) {
                    stepModel.setStepNotificationDetails(cursor.getString(StepNotificationColumnIndex));
                }

                if(StepIsDeadLineSetColumnIndex >=0) {
                    stepModel.setDeadlineSet(cursor.getInt(StepIsDeadLineSetColumnIndex));
                }
                if(StepIsNotificationSetColumnIndex >=0) {
                    stepModel.setNotificationSet(cursor.getInt(StepIsNotificationSetColumnIndex));
                }
                if(StepIsStepCompleted >=0) {
                    stepModel.setStepCompleted(cursor.getInt(StepIsStepCompleted));
                }

                AllSteps.add(stepModel);

            }while(cursor.moveToNext());
        }

        cursor.close();

        return AllSteps;

    }


}
