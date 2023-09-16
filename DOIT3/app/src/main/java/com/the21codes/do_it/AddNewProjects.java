package com.the21codes.do_it;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddNewProjects extends AppCompatActivity {




    ArrayList<phasesModelClass> arrayList;
    RecyclerView phasesRecyclerView;
    phasesForProjectAdapter phasesAdapter;
    Button addNewPhasesButton, addAnExtraPhase;
    ProjectModel projectDetails;
    EditText projectNameEditText;
    TextView projectStartDateTextView, projectPhasesTotalTextView, projectDeadLineTextView, projectImportanceTextView, projectTagTextViewMain;
    DataBaseHelper myDB;
    LinearLayout  addExtraPhasesLinearLayout, projectDetailsLinearLayout;
    RelativeLayout addNewphasesLinearLayout, projectInfoLayout,
            theMainParentRelativeLayout;
    SharedPreferences instructionsGiven;
    SharedPreferences.Editor instructionsGivenEditor;
    boolean projectPageInstructionsGiven;



    private static final String DATABASE_NAME = "DO_IT_DATABASE";
    private static final String PROJECT_TABLE = "PROJECT_TABLE";
    private static final String PHASE_TABLE_PREFIX= "PHASE_TABLE_";
    private static final String STEPS_TABLE_PREFIX = "STEPS_TABLE_";
    private static final String PROJECT_KEY_ID  = "id";
    private static final String PHASE_KEY_ID  = "id";
    private static final String STEP_KEY_ID  = "id";
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
    private static final String STEP_NAME = "STEP_NAME";
    private static final String STEP_PHASE_ID = "PHASE_ID";
    private static final String STEP_DEADLINE = "STEP_DEADLINE";
    private static final String STEP_PROGRESS = "STEP_PROGRESS";
    private static final String STEP_NOTES = "STEP_NOTES";
    private static final String STEP_NOTIFICATION = "STEP_NOTIFICATION";
    private static final String STEP_DEADLINE_SET = "STEP_DEADLINE_SET";
    private static final String STEP_NOTIFICATION_SET = "STEP_NOTIFICATION_SET";
    private static final String STEP_IS_STEP_COMPLETED = "STEP_IS_COMPLETED";

    int theProjectID = 0;

    SharedPreferences vibrationDetails;
    SharedPreferences.Editor vibrationDetailsEditor;
    Boolean vibrationsEnabled = false;
    private Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_projects);
        myDB = new DataBaseHelper(this);
        arrayList = new ArrayList<>();
        phasesRecyclerView = (RecyclerView) findViewById(R.id.phasesRecyclerView);
        phasesAdapter = new phasesForProjectAdapter(arrayList, AddNewProjects.this);
        phasesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        phasesRecyclerView.setAdapter(phasesAdapter);
        projectNameEditText = (EditText) findViewById(R.id.projectNameEditText);
        projectStartDateTextView =(TextView) findViewById(R.id.projectStartDateTextView);
        projectPhasesTotalTextView =(TextView) findViewById(R.id.projectPhasesTotalTextView);
        projectDeadLineTextView =(TextView) findViewById(R.id.projectDeadLineTextView);
        projectImportanceTextView =(TextView) findViewById(R.id.projectImportanceTextView);
        projectTagTextViewMain =(TextView) findViewById(R.id.projectTagTextViewMain);
        addNewphasesLinearLayout = (RelativeLayout) findViewById(R.id.addNewphasesLinearLayout);
        projectInfoLayout = (RelativeLayout) findViewById(R.id.projectInfoLayout);
        theMainParentRelativeLayout = (RelativeLayout) findViewById(R.id.theMainParentRelativeLayout);
        addExtraPhasesLinearLayout = (LinearLayout) findViewById(R.id.addExtraPhasesLinearLayout);
        projectDetailsLinearLayout = (LinearLayout) findViewById(R.id.projectDetailsLinearLayout);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-YY, HH.mm", Locale.getDefault());
        String creationTime = dateFormat.format(calendar.getTime());
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        vibrationDetails = getSharedPreferences("vibrationPreferences", MODE_PRIVATE);
        vibrationDetailsEditor = vibrationDetails.edit();
        vibrationsEnabled = vibrationDetails.getBoolean("vibrations", true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        AdView adView = findViewById(R.id.adView);;
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        projectStartDateTextView.setText(creationTime);
        projectDeadLineTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddNewProjects.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                calendar.set(Calendar.YEAR, i);
                                calendar.set(Calendar.MONTH, i1);
                                calendar.set(Calendar.DAY_OF_MONTH, i2);


                                TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewProjects.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                                calendar.set(Calendar.HOUR_OF_DAY, i);
                                                calendar.set(Calendar.MINUTE, i1);
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy, HH.mm", Locale.getDefault());
                                                String deadlineDate = dateFormat.format(calendar.getTime());
                                                projectDeadLineTextView.setText(deadlineDate);
                                            }
                                        }, hour, minute, false);
                                timePickerDialog.show();
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });
        projectImportanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                if(projectImportanceTextView.getText().equals("Choose Here!")){
                    projectImportanceTextView.setText("Low");
                    projectImportanceTextView.setBackgroundColor(Color.parseColor("#55efc4"));
                }else if(projectImportanceTextView.getText().equals("Low")){
                    projectImportanceTextView.setText("Medium");
                    projectImportanceTextView.setBackgroundColor(Color.parseColor("#f5d76e"));
                }else if(projectImportanceTextView.getText().equals("Medium")){
                    projectImportanceTextView.setText("High");
                    projectImportanceTextView.setBackgroundColor(Color.parseColor("#FFA07A"));
                }else if(projectImportanceTextView.getText().equals("High")){
                    projectImportanceTextView.setText("Low");
                    projectImportanceTextView.setBackgroundColor(Color.parseColor("#55efc4"));
                }else{
                    projectImportanceTextView.setText("Low");
                    projectImportanceTextView.setBackgroundColor(Color.parseColor("#55efc4"));
                }
            }
        });
        instructionsGiven = getSharedPreferences("instructionGivenDetails", MODE_PRIVATE);
        instructionsGivenEditor = instructionsGiven.edit();
        projectPageInstructionsGiven = instructionsGiven.getBoolean("projectPageInstructions", false);
        addNewPhasesButton = (Button) findViewById(R.id.addNewPhasesButton);
        addNewPhasesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                if(projectNameEditText.getText().toString().isEmpty()){
                    projectDeadLineTextView.requestFocus();
                    Toast.makeText(AddNewProjects.this, "Please Enter the project Name to continue", Toast.LENGTH_SHORT).show();
                }else if(projectDeadLineTextView.getText().equals("Set Deadline")){
                    projectNameEditText.requestFocus();
                    Toast.makeText(AddNewProjects.this, "Please Select an Deadline to continue", Toast.LENGTH_SHORT).show();
                }else{
                    showTemplateListsDialog();
                }
            }
        });

        addAnExtraPhase = (Button) findViewById(R.id.addAnExtraPhase);
        addAnExtraPhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               addNewPhase();
            }
        });


        if(!projectPageInstructionsGiven){
            openInstructions();
        }

        projectNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                projectNameEditText.clearFocus();
              //  removeTextFocus();
            }
        });
    }

    private void openInstructions() {
        Typeface myTypeface1 = ResourcesCompat.getFont(getApplicationContext(), R.font.lato);
        Typeface myTypeface2 = ResourcesCompat.getFont(getApplicationContext(), R.font.merriweather);
        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(projectInfoLayout, "You Can set All Your basic project details Here!", "Click on the respective fields to Set deadlines and project priorities!")
                                .outerCircleColor(R.color.SECOND_SLIDE)
                                .outerCircleAlpha(0.95f)
                                .targetCircleColor(R.color.white)
                                .textColor(R.color.SECOND_SLIDE_TEXT_ONE)
                                .descriptionTextColor(R.color.SECOND_SLIDE_TEXT_TWO)
                                .descriptionTextSize(20)
                                .descriptionTextAlpha(1)
                                .titleTextSize(30)
                                .tintTarget(false)
                                .drawShadow(true)
                                .titleTypeface(myTypeface1)
                                .descriptionTypeface(myTypeface2)
                                .transparentTarget(false)
                                .cancelable(false)
                                .id(1)
                                .targetRadius(84)
                        ,
                        TapTarget.forView(addNewPhasesButton, "Project Templates", "Select your project templates and choose what kind of project you want to work one")
                                .outerCircleColor(R.color.FOURTH_SLIDE)
                                .outerCircleAlpha(0.95f)
                                .targetCircleColor(R.color.white)
                                .textColor(R.color.FOURTH_SLIDE_ONE)
                                .descriptionTextColor(R.color.FOURTH_SLIDE_TWO)
                                .descriptionTextSize(20)
                                .descriptionTextAlpha(1)
                                .titleTextSize(30)
                                .tintTarget(false)
                                .drawShadow(true)
                                .titleTypeface(myTypeface1)
                                .descriptionTypeface(myTypeface2)
                                .transparentTarget(false)
                                .cancelable(false)
                                .id(3)
                                .targetRadius(84))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        instructionsGivenEditor.putBoolean("projectPageInstructions", true);
                        instructionsGivenEditor.apply();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        if(targetClicked && lastTarget.id()==3){
                            if(projectNameEditText.getText().toString().isEmpty()){
                                Toast.makeText(AddNewProjects.this, "Please Enter Project Name to continue", Toast.LENGTH_SHORT).show();
                            } else if (projectDeadLineTextView.getText().toString().trim().equals("Set Deadline")) {
                                Toast.makeText(AddNewProjects.this, "Please Select a deadline for the project", Toast.LENGTH_SHORT).show();
                            }else{
                                showTemplateListsDialog();
                            }
                            instructionsGivenEditor.putBoolean("projectPageInstructions", true);
                            instructionsGivenEditor.apply();
                        }

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        instructionsGivenEditor.putBoolean("projectPageInstructions", true);
                        instructionsGivenEditor.apply();
                    }
                }).start();
    }

    private void showTemplateListsDialog(){
        final String[] templates = {"Subject/Exam Study Plan",
                "Job/Company Interview Prep Plan",
                "Creative Writing Plan",
                "Art/Craft Project Plan",
                "Home Management Plan",
                "Website/App Dev Plan",
                "Software Engineering Plan",
                "Event Outline Plan",
                "General Trip Plan",
                "Resume Building Plan"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //  builder.setTitle("Select An Template");
        AlertDialog dialog;
        View view = getLayoutInflater().inflate(R.layout.template_picker_activity, null);
        builder.setView(view);
        ListView ListView = view.findViewById(R.id.templatesListView);
        Button CustomButton = view.findViewById(R.id.buildYourOwnTemplate);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_list_item, templates);
        ListView.setAdapter(adapter);

        dialog = builder.create();
        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                switch (i){
                    case 0:
                        openStudyPlanTemplate();
                        break;
                    case 1:
                        openPlanInterview();
                        break;
                    case 2:
                        openArtsOrCraft();
                        break;
                    case 3:
                        openCreativeWritingPlan();
                        break;
                    case 4:
                        openHomeManagementPlan();
                        break;
                    case 5:
                        openPlanAnApplication();
                        break;
                    case 6:
                        softwareEnggPlan();
                        break;
                    case 7:
                        generalEventPlan();
                        break;
                    case 8:
                        generalTripPlan();
                        break;
                    case 9:
                        generalrESUMEPlan();
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }
        });

        CustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                openCustomTemplate();
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    private void generalrESUMEPlan() {
        swapTheLinearLayouts();
        theProjectID = insertIntoProjectTable("#Resume");
        projectPhasesTotalTextView.setText("5");
        projectTagTextViewMain.setText("#Resume");
        createThePhaseTable(theProjectID);

        //phase 1
        phasesModelClass phase1 = new phasesModelClass(
                "Planning and research", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Resume", 0,0,0);
        int phase1ID = insertNewPhase(theProjectID, phase1);
        createStepTableForEachPhase(phase1ID, theProjectID);
        newStepsModel stepsModel1 = new newStepsModel("Collecting work experience details", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel1);
        newStepsModel stepsModel2 = new newStepsModel("Listing educational qualifications and certifications", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel2);
        newStepsModel stepsModel3 = new newStepsModel("Preparing a list of relevant skills and competencies", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel3);
        newStepsModel stepsModel4 = new newStepsModel("Identifying relevant achievements and accomplishments", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel4);
        newStepsModel stepsModelx = new newStepsModel("Gathering personal information (e.g., name, contact details)", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModelx);
        phase1.setPhaseID(phase1ID);
        arrayList.add(phase1);



        //phase 2
        phasesModelClass phase2 = new phasesModelClass(
                "Structuring the Resume", theProjectID, "Set Deadline", "Medium", "NOT_SET",
                4, "#Resume", 0,0,0);
        int phase2ID = insertNewPhase(theProjectID, phase2);
        createStepTableForEachPhase(phase2ID, theProjectID);
        newStepsModel stepsModel5 = new newStepsModel("Choosing an appropriate format", phase2ID, "Set Deadline", 0," (e.g., chronological, functional, combination). Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel5);
        newStepsModel stepsModel6 = new newStepsModel("Writing a compelling summary or objective statement", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel6);
        newStepsModel stepsModel7 = new newStepsModel("Organizing the sections and subsections of the resume", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel7);
        newStepsModel stepsModel8 = new newStepsModel("Ensuring a clear and concise layout with appropriate fonts and styles", phase2ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel8);
        newStepsModel stepsModely = new newStepsModel("Including relevant keywords for search engine optimization (SEO)", phase2ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModely);
        phase2.setPhaseID(phase2ID);
        arrayList.add(phase2);


        //phase 3
        phasesModelClass phase3 = new phasesModelClass(
                "Writing the Content", theProjectID, "Set Deadline", "High", "NOT_SET",
                4, "#Resume", 0,0,0);
        int phase3ID = insertNewPhase(theProjectID, phase3);
        createStepTableForEachPhase(phase3ID, theProjectID);
        newStepsModel stepsModel9 = new newStepsModel("Crafting attention-grabbing headlines and section titles", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel9);
        newStepsModel stepsModel10 = new newStepsModel("Crafting attention-grabbing headlines and section titles", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel10);
        newStepsModel stepsModel11 = new newStepsModel("Describing job responsibilities and achievements", phase3ID, "Set Deadline", 0,"Using action verbs and quantifiable metrics. Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel11);
        newStepsModel stepsModel12 = new newStepsModel("Including relevant industry-specific jargon and terminology", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel12);
        newStepsModel stepsModelz = new newStepsModel("Ensuring consistency in language, grammar, and spelling", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModelz);
        phase3.setPhaseID(phase3ID);
        arrayList.add(phase3);

        //phase 4

        phasesModelClass phase4 = new phasesModelClass(
                "Editing and proofreading", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Resume", 0,0,0);
        int phase4ID = insertNewPhase(theProjectID, phase4);
        createStepTableForEachPhase(phase4ID, theProjectID);
        newStepsModel stepsModel13 = new newStepsModel("Proofreading the resume for spelling and grammar errors", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel13);
        newStepsModel stepsModel14 = new newStepsModel("Checking the resume for overall coherence and flow", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel14);
        newStepsModel stepsModel15 = new newStepsModel("Ensuring that the resume is tailored to the specific job or industry", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel15);
        newStepsModel stepsModel16 = new newStepsModel("Getting feedback from peers, mentors, or career counselors", phase4ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel16);
        newStepsModel stepsModela = new newStepsModel("Making necessary revisions and improvements based on feedback", phase4ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModela);
        phase4.setPhaseID(phase4ID);
        arrayList.add(phase4);

        //phase 5

        phasesModelClass phase5 = new phasesModelClass(
                "Finalizing and submitting", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Resume", 0,0,0);
        int phase5ID = insertNewPhase(theProjectID, phase5);
        createStepTableForEachPhase(phase5ID, theProjectID);
        newStepsModel stepsModel17 = new newStepsModel("Saving the resume in appropriate file formats (e.g., PDF, Word)", phase5ID, "Set Deadline", 0,"appropriate file formats (e.g., PDF, Word). Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel13);
        newStepsModel stepsModel18 = new newStepsModel("Naming the file in a clear and identifiable manner", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel14);
        newStepsModel stepsModel19 = new newStepsModel("Writing a concise and professional cover letter", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel15);
        newStepsModel stepsModel20 = new newStepsModel("Submitting the resume and cover letter", phase5ID, "Set Deadline", 0,"Through appropriate channels (e.g., email, online application, in-person). Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel16);
        phase5.setPhaseID(phase5ID);
        arrayList.add(phase5);
        phasesAdapter.notifyDataSetChanged();
    }

    private void generalTripPlan() {
        swapTheLinearLayouts();
        theProjectID = insertIntoProjectTable("#Trip");
        projectPhasesTotalTextView.setText("5");
        projectTagTextViewMain.setText("#Trip");
        createThePhaseTable(theProjectID);

        //phase 1
        phasesModelClass phase1 = new phasesModelClass(
                "Research and Planning", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Trip", 0,0,0);
        int phase1ID = insertNewPhase(theProjectID, phase1);
        createStepTableForEachPhase(phase1ID, theProjectID);
        newStepsModel stepsModel1 = new newStepsModel("Check for any necessary travel documentation", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel1);
        newStepsModel stepsModel2 = new newStepsModel("Decide on a destination", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel2);
        newStepsModel stepsModel3 = new newStepsModel("Plan the itinerary and activities for each day", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel3);
        newStepsModel stepsModel4 = new newStepsModel("Research transportation options and costs", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel4);
        phase1.setPhaseID(phase1ID);
        arrayList.add(phase1);



        //phase 2
        phasesModelClass phase2 = new phasesModelClass(
                "Booking and Reservations", theProjectID, "Set Deadline", "Medium", "NOT_SET",
                4, "#Trip", 0,0,0);
        int phase2ID = insertNewPhase(theProjectID, phase2);
        createStepTableForEachPhase(phase2ID, theProjectID);
        newStepsModel stepsModel5 = new newStepsModel("Book transportation", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel5);
        newStepsModel stepsModel6 = new newStepsModel("Reserve accommodation, such as hotels.", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel6);
        newStepsModel stepsModel7 = new newStepsModel("Book any necessary tours and activities.", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel7);
        newStepsModel stepsModel8 = new newStepsModel("Purchase travel insurance, if desired", phase2ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel8);
        phase2.setPhaseID(phase2ID);
        arrayList.add(phase2);


        //phase 3
        phasesModelClass phase3 = new phasesModelClass(
                "Pre-Trip Preparation", theProjectID, "Set Deadline", "High", "NOT_SET",
                4, "#Trip", 0,0,0);
        int phase3ID = insertNewPhase(theProjectID, phase3);
        createStepTableForEachPhase(phase3ID, theProjectID);
        newStepsModel stepsModel9 = new newStepsModel("Create a packing list.", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel9);
        newStepsModel stepsModel10 = new newStepsModel("Ensure travel documentation are up-to-date", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel10);
        newStepsModel stepsModel11 = new newStepsModel("Notify banks to avoid any issues with transactions", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel11);
        newStepsModel stepsModel12 = new newStepsModel("Confirm all bookings and reservations", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel12);
        phase3.setPhaseID(phase3ID);
        arrayList.add(phase3);

        //phase 4

        phasesModelClass phase4 = new phasesModelClass(
                "On-Trip Logistics", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Trip", 0,0,0);
        int phase4ID = insertNewPhase(theProjectID, phase4);
        createStepTableForEachPhase(phase4ID, theProjectID);
        newStepsModel stepsModel13 = new newStepsModel("Follow the planned itinerary and activities", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel13);
        newStepsModel stepsModel14 = new newStepsModel("Navigate transportation and logistics, such as rental cars or public transportation", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel14);
        newStepsModel stepsModel15 = new newStepsModel("Manage accommodation and check-in/check-out procedures", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel15);
        newStepsModel stepsModel16 = new newStepsModel("Stay safe and healthy", phase4ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel16);
        phase4.setPhaseID(phase4ID);
        arrayList.add(phase4);

        //phase 5

        phasesModelClass phase5 = new phasesModelClass(
                "Post-Trip Evaluation", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Trip", 0,0,0);
        int phase5ID = insertNewPhase(theProjectID, phase5);
        createStepTableForEachPhase(phase5ID, theProjectID);
        newStepsModel stepsModel17 = new newStepsModel("Reflect on the trip and evaluate", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel13);
        newStepsModel stepsModel18 = new newStepsModel("Review any issues consider solutions for future trips", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel14);
        newStepsModel stepsModel19 = new newStepsModel("Keep track of expenses", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel15);
        newStepsModel stepsModel20 = new newStepsModel("Share the trip experience with others", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel16);
        phase5.setPhaseID(phase5ID);
        arrayList.add(phase5);
        phasesAdapter.notifyDataSetChanged();
    }

    private void generalEventPlan() {
        swapTheLinearLayouts();
        theProjectID = insertIntoProjectTable("#Event");
        projectPhasesTotalTextView.setText("4");
        projectTagTextViewMain.setText("#Event");
        createThePhaseTable(theProjectID);

        //phase 1
        phasesModelClass phase1 = new phasesModelClass(
                "Planning", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Event", 0,0,0);
        int phase1ID = insertNewPhase(theProjectID, phase1);
        createStepTableForEachPhase(phase1ID, theProjectID);
        newStepsModel stepsModel1 = new newStepsModel("Define the event objectives and goals", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel1);
        newStepsModel stepsModel2 = new newStepsModel("Determine the event scope and budget", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel2);
        newStepsModel stepsModel3 = new newStepsModel("Develop a detailed event plan with deadlines", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel3);
        newStepsModel stepsModel4 = new newStepsModel("Select the venue and vendors", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel4);
        phase1.setPhaseID(phase1ID);
        arrayList.add(phase1);



        //phase 2
        phasesModelClass phase2 = new phasesModelClass(
                "Pre-Event", theProjectID, "Set Deadline", "Medium", "NOT_SET",
                4, "#Event", 0,0,0);
        int phase2ID = insertNewPhase(theProjectID, phase2);
        createStepTableForEachPhase(phase2ID, theProjectID);
        newStepsModel stepsModel5 = new newStepsModel("Create a detailed event schedule and agenda", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel5);
        newStepsModel stepsModel6 = new newStepsModel("Coordinate with vendors and suppliers", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel6);
        newStepsModel stepsModel7 = new newStepsModel("Arrange for speakers, entertainment, and catering", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel7);
        newStepsModel stepsModel8 = new newStepsModel("Manage event logistics and accommodations", phase2ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel8);
        phase2.setPhaseID(phase2ID);
        arrayList.add(phase2);


        //phase 3
        phasesModelClass phase3 = new phasesModelClass(
                "Event Execution", theProjectID, "Set Deadline", "High", "NOT_SET",
                4, "#Event", 0,0,0);
        int phase3ID = insertNewPhase(theProjectID, phase3);
        createStepTableForEachPhase(phase3ID, theProjectID);
        newStepsModel stepsModel9 = new newStepsModel("Set up the event venue and equipment", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel9);
        newStepsModel stepsModel10 = new newStepsModel("Monitor the event schedule", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel10);
        newStepsModel stepsModel11 = new newStepsModel("Provide assistance to attendees, vendors, and speakers", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel11);
        phase3.setPhaseID(phase3ID);
        arrayList.add(phase3);

        //phase 4

        phasesModelClass phase4 = new phasesModelClass(
                "Post-Event", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Event", 0,0,0);
        int phase4ID = insertNewPhase(theProjectID, phase4);
        createStepTableForEachPhase(phase4ID, theProjectID);
        newStepsModel stepsModel13 = new newStepsModel("Debrief with the event team and vendors", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel13);
        newStepsModel stepsModel14 = new newStepsModel("Analyze feedback from attendees", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel14);
        newStepsModel stepsModel15 = new newStepsModel("Evaluate the event success", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel15);
        newStepsModel stepsModel16 = new newStepsModel("Create a post-event report and document", phase4ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel16);
        phase4.setPhaseID(phase4ID);
        arrayList.add(phase4);
        phasesAdapter.notifyDataSetChanged();
    }

    private void openPlanAnApplication() {
        swapTheLinearLayouts();
        theProjectID = insertIntoProjectTable("#Dev");
        projectPhasesTotalTextView.setText("5");
        projectTagTextViewMain.setText("#Dev");
        createThePhaseTable(theProjectID);

        //phase 1
        phasesModelClass phase1 = new phasesModelClass(
                "Discovery and Planning", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Dev", 0,0,0);
        int phase1ID = insertNewPhase(theProjectID, phase1);
        createStepTableForEachPhase(phase1ID, theProjectID);
        newStepsModel stepsModel1 = new newStepsModel("Conduct market research and analysis", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel1);
        newStepsModel stepsModel2 = new newStepsModel("Define project goals and objectives", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel2);
        newStepsModel stepsModel3 = new newStepsModel("Develop a project scope and timeline", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel3);
        newStepsModel stepsModel4 = new newStepsModel("Determine the technology stack and budget required", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel4);
        phase1.setPhaseID(phase1ID);
        arrayList.add(phase1);



        //phase 2
        phasesModelClass phase2 = new phasesModelClass(
                "Design", theProjectID, "Set Deadline", "Medium", "NOT_SET",
                4, "#Dev", 0,0,0);
        int phase2ID = insertNewPhase(theProjectID, phase2);
        createStepTableForEachPhase(phase2ID, theProjectID);
        newStepsModel stepsModel5 = new newStepsModel("Create wireframes and mockups", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel5);
        newStepsModel stepsModel6 = new newStepsModel("Develop a visual design and branding strategy", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel6);
        newStepsModel stepsModel7 = new newStepsModel("Determine the (UI/UX) design", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel7);
        newStepsModel stepsModel8 = new newStepsModel("Obtain feedback and iterate on the design", phase2ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel8);
        phase2.setPhaseID(phase2ID);
        arrayList.add(phase2);


        //phase 3
        phasesModelClass phase3 = new phasesModelClass(
                "Development", theProjectID, "Set Deadline", "High", "NOT_SET",
                4, "#Dev", 0,0,0);
        int phase3ID = insertNewPhase(theProjectID, phase3);
        createStepTableForEachPhase(phase3ID, theProjectID);
        newStepsModel stepsModel9 = new newStepsModel("Develop the front-end and back-end functionality", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel9);
        newStepsModel stepsModel10 = new newStepsModel("Implement the design elements and UI/UX features", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel10);
        newStepsModel stepsModel11 = new newStepsModel("Incorporate any necessary third-party services", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel11);
        newStepsModel stepsModel12 = new newStepsModel("Test and debug the code as it is developed", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel12);
        phase3.setPhaseID(phase3ID);
        arrayList.add(phase3);

        //phase 4

        phasesModelClass phase4 = new phasesModelClass(
                "Testing", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Dev", 0,0,0);
        int phase4ID = insertNewPhase(theProjectID, phase4);
        createStepTableForEachPhase(phase4ID, theProjectID);
        newStepsModel stepsModel13 = new newStepsModel("Conduct extensive testing", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel13);
        newStepsModel stepsModel14 = new newStepsModel("Test on a variety of devices and browsers", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel14);
        newStepsModel stepsModel15 = new newStepsModel("Identify and address any bugs or issues", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel15);
        newStepsModel stepsModel16 = new newStepsModel("Obtain user feedback and iterate on the development", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel16);
        phase4.setPhaseID(phase4ID);
        arrayList.add(phase4);

        //phase 5

        phasesModelClass phase5 = new phasesModelClass(
                "Deployment and Launch", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Dev", 0,0,0);
        int phase5ID = insertNewPhase(theProjectID, phase5);
        createStepTableForEachPhase(phase5ID, theProjectID);
        newStepsModel stepsModel17 = new newStepsModel("Deploy the website or application", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel13);
        newStepsModel stepsModel18 = new newStepsModel("Perform final testing", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel14);
        newStepsModel stepsModel19 = new newStepsModel("Prepare for and execute a launch plan", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel15);
        newStepsModel stepsModel20 = new newStepsModel("Monitor the website or application post-launch", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel16);
        phase5.setPhaseID(phase5ID);
        arrayList.add(phase5);
        phasesAdapter.notifyDataSetChanged();
    }

    private void softwareEnggPlan() {
        swapTheLinearLayouts();
        theProjectID = insertIntoProjectTable("#Software");
        projectPhasesTotalTextView.setText("5");
        projectTagTextViewMain.setText("#Software");
        createThePhaseTable(theProjectID);

        //phase 1
        phasesModelClass phase1 = new phasesModelClass(
                "Requirements Gathering and Analysis", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Software", 0,0,0);
        int phase1ID = insertNewPhase(theProjectID, phase1);
        createStepTableForEachPhase(phase1ID, theProjectID);
        newStepsModel stepsModel1 = new newStepsModel("Identify and document the requirements", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel1);
        newStepsModel stepsModel2 = new newStepsModel("Perform feasibility analysis", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel2);
        newStepsModel stepsModel3 = new newStepsModel("Ensure that all requirements are considered", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel3);
        newStepsModel stepsModel4 = new newStepsModel("Develop use cases and functional specifications", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel4);
        phase1.setPhaseID(phase1ID);
        arrayList.add(phase1);



        //phase 2
        phasesModelClass phase2 = new phasesModelClass(
                "Design", theProjectID, "Set Deadline", "Medium", "NOT_SET",
                4, "#Software", 0,0,0);
        int phase2ID = insertNewPhase(theProjectID, phase2);
        createStepTableForEachPhase(phase2ID, theProjectID);
        newStepsModel stepsModel5 = new newStepsModel("Develop a high-level architecture for the project", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel5);
        newStepsModel stepsModel6 = new newStepsModel("Create detailed technical specifications for components", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel6);
        newStepsModel stepsModel7 = new newStepsModel("Develop data models and flow diagrams", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel7);
        newStepsModel stepsModel8 = new newStepsModel("Identify any integrations with other systems", phase2ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel8);
        phase2.setPhaseID(phase2ID);
        arrayList.add(phase2);


        //phase 3
        phasesModelClass phase3 = new phasesModelClass(
                "Implementation", theProjectID, "Set Deadline", "High", "NOT_SET",
                4, "#Software", 0,0,0);
        int phase3ID = insertNewPhase(theProjectID, phase3);
        createStepTableForEachPhase(phase3ID, theProjectID);
        newStepsModel stepsModel9 = new newStepsModel("Write and test code for each component", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel9);
        newStepsModel stepsModel10 = new newStepsModel("Develop UI and meet the specifications", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel10);
        newStepsModel stepsModel11 = new newStepsModel("Implement data models & integrate with others", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel11);
        newStepsModel stepsModel12 = new newStepsModel("Conduct code reviews", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel12);
        phase3.setPhaseID(phase3ID);
        arrayList.add(phase3);

        //phase 4

        phasesModelClass phase4 = new phasesModelClass(
                "Testing", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Software", 0,0,0);
        int phase4ID = insertNewPhase(theProjectID, phase4);
        createStepTableForEachPhase(phase4ID, theProjectID);
        newStepsModel stepsModel13 = new newStepsModel("Develop test plans and test cases", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel13);
        newStepsModel stepsModel14 = new newStepsModel("Perform functional and non-functional testing", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel14);
        newStepsModel stepsModel15 = new newStepsModel("Conduct load testing", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel15);
        newStepsModel stepsModel16 = new newStepsModel("Identify and fix any defects or issues", phase4ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel16);
        phase4.setPhaseID(phase4ID);
        arrayList.add(phase4);

        //phase 5

        phasesModelClass phase5 = new phasesModelClass(
                "Deployment and Maintenance", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Software", 0,0,0);
        int phase5ID = insertNewPhase(theProjectID, phase5);
        createStepTableForEachPhase(phase5ID, theProjectID);
        newStepsModel stepsModel17 = new newStepsModel("Deploy the system to the production environment", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel13);
        newStepsModel stepsModel18 = new newStepsModel("Monitor the system for performance and stability", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel14);
        newStepsModel stepsModel19 = new newStepsModel("Address any issues or bugs that arise", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel15);
        newStepsModel stepsModel20 = new newStepsModel("Provide ongoing maintenance and support for the system", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel16);
        phase5.setPhaseID(phase5ID);
        arrayList.add(phase5);
        phasesAdapter.notifyDataSetChanged();
    }

    private void openHomeManagementPlan() {
        swapTheLinearLayouts();
        theProjectID = insertIntoProjectTable("#Chores");
        projectPhasesTotalTextView.setText("4");
        projectTagTextViewMain.setText("#Chores");
        createThePhaseTable(theProjectID);

        //phase 1
        phasesModelClass phase1 = new phasesModelClass(
                "Assessment and Planning", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Chores", 0,0,0);
        int phase1ID = insertNewPhase(theProjectID, phase1);
        createStepTableForEachPhase(phase1ID, theProjectID);
        newStepsModel stepsModel1 = new newStepsModel("Identify areas with problems", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel1);
        newStepsModel stepsModel2 = new newStepsModel("Develop a cleaning and organization plan", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel2);
        newStepsModel stepsModel3 = new newStepsModel("Determine what equipment are necessary", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel3);
        newStepsModel stepsModel4 = new newStepsModel("Set goals for completing the plan", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel4);
        phase1.setPhaseID(phase1ID);
        arrayList.add(phase1);



        //phase 2
        phasesModelClass phase2 = new phasesModelClass(
                "Setting Priorities and Scheduling", theProjectID, "Set Deadline", "Medium", "NOT_SET",
                4, "#Chores", 0,0,0);
        int phase2ID = insertNewPhase(theProjectID, phase2);
        createStepTableForEachPhase(phase2ID, theProjectID);
        newStepsModel stepsModel5 = new newStepsModel("Prioritize tasks based on urgency and importance", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel5);
        newStepsModel stepsModel6 = new newStepsModel("Assign tasks to specific days", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel6);
        newStepsModel stepsModel7 = new newStepsModel("Create a checklist to track progress.", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel7);
        newStepsModel stepsModel8 = new newStepsModel("Ensure that tasks are evenly distributed", phase2ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel8);
        phase2.setPhaseID(phase2ID);
        arrayList.add(phase2);


        //phase 3
        phasesModelClass phase3 = new phasesModelClass(
                "Execution and Completion", theProjectID, "Set Deadline", "High", "NOT_SET",
                4, "#Chores", 0,0,0);
        int phase3ID = insertNewPhase(theProjectID, phase3);
        createStepTableForEachPhase(phase3ID, theProjectID);
        newStepsModel stepsModel9 = new newStepsModel("Follow the plan to complete tasks", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel9);
        newStepsModel stepsModel10 = new newStepsModel("Use appropriate cleaning techniques", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel10);
        newStepsModel stepsModel11 = new newStepsModel("Stay focused and efficient while completing tasks", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel11);
        newStepsModel stepsModel12 = new newStepsModel("Take breaks as needed to avoid burnout", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel12);
        phase3.setPhaseID(phase3ID);
        arrayList.add(phase3);

        //phase 4

        phasesModelClass phase4 = new phasesModelClass(
                "Maintenance and Organization", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Chores", 0,0,0);
        int phase4ID = insertNewPhase(theProjectID, phase4);
        createStepTableForEachPhase(phase4ID, theProjectID);
        newStepsModel stepsModel13 = new newStepsModel("Create a system for maintaining the cleanliness", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel13);
        newStepsModel stepsModel14 = new newStepsModel("Determine a cleaning schedule to avoid falling behind", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel14);
        newStepsModel stepsModel15 = new newStepsModel("Develop plans to keep the home clutter-free", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel15);
        newStepsModel stepsModel16 = new newStepsModel("Regularly evaluate the cleaning plan as necessary.", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel16);
        phase4.setPhaseID(phase4ID);
        arrayList.add(phase4);
        phasesAdapter.notifyDataSetChanged();
    }

    private void openCreativeWritingPlan() {
        swapTheLinearLayouts();
        theProjectID = insertIntoProjectTable("#Writing");
        projectPhasesTotalTextView.setText("5");
        projectTagTextViewMain.setText("#Writing");
        createThePhaseTable(theProjectID);

        //phase 1
        phasesModelClass phase1 = new phasesModelClass(
                "Pre-Writing and Planning", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Writing", 0,0,0);
        int phase1ID = insertNewPhase(theProjectID, phase1);
        createStepTableForEachPhase(phase1ID, theProjectID);
        newStepsModel stepsModel1 = new newStepsModel("Brainstorm and generate ideas", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel1);
        newStepsModel stepsModel2 = new newStepsModel("Create an outline or a storyboard", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel2);
        newStepsModel stepsModel3 = new newStepsModel("Develop characters and setting", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel3);
        newStepsModel stepsModel4 = new newStepsModel("Conduct research and gather background information", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel4);
        phase1.setPhaseID(phase1ID);
        arrayList.add(phase1);



        //phase 2
        phasesModelClass phase2 = new phasesModelClass(
                "Drafting and Writing", theProjectID, "Set Deadline", "Medium", "NOT_SET",
                4, "#Writing", 0,0,0);
        int phase2ID = insertNewPhase(theProjectID, phase2);
        createStepTableForEachPhase(phase2ID, theProjectID);
        newStepsModel stepsModel5 = new newStepsModel("Write a rough draft of the story or article", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel5);
        newStepsModel stepsModel6 = new newStepsModel("Stay focused and avoid distractions", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel6);
        newStepsModel stepsModel7 = new newStepsModel("Use sensory details to create vivid imagery", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel7);
        newStepsModel stepsModel8 = new newStepsModel("Write in a consistent style and tone", phase2ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel8);
        phase2.setPhaseID(phase2ID);
        arrayList.add(phase2);


        //phase 3
        phasesModelClass phase3 = new phasesModelClass(
                "Revising and Editing", theProjectID, "Set Deadline", "High", "NOT_SET",
                4, "#Writing", 0,0,0);
        int phase3ID = insertNewPhase(theProjectID, phase3);
        createStepTableForEachPhase(phase3ID, theProjectID);
        newStepsModel stepsModel9 = new newStepsModel("Read and revise the draft multiple times", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel9);
        newStepsModel stepsModel10 = new newStepsModel("Cut unnecessary parts and reorganize if necessary", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel10);
        newStepsModel stepsModel11 = new newStepsModel("Check for spelling, grammar, and punctuation errors", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel11);
        newStepsModel stepsModel12 = new newStepsModel("Solicit feedback from others and make improvements based on it", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel12);
        phase3.setPhaseID(phase3ID);
        arrayList.add(phase3);

        //phase 4

        phasesModelClass phase4 = new phasesModelClass(
                "Formatting and Polishing", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Writing", 0,0,0);
        int phase4ID = insertNewPhase(theProjectID, phase4);
        createStepTableForEachPhase(phase4ID, theProjectID);
        newStepsModel stepsModel13 = new newStepsModel("Format the story or article according to guidelines", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel13);
        newStepsModel stepsModel14 = new newStepsModel("Add finishing touches and details", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel14);
        newStepsModel stepsModel15 = new newStepsModel("Create a title and subtitle", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel15);
        newStepsModel stepsModel16 = new newStepsModel("Create a visually appealing presentation", phase4ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel16);
        phase4.setPhaseID(phase4ID);
        arrayList.add(phase4);

        //phase 5

        phasesModelClass phase5 = new phasesModelClass(
                "Publishing or Submitting", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Writing", 0,0,0);
        int phase5ID = insertNewPhase(theProjectID, phase5);
        createStepTableForEachPhase(phase5ID, theProjectID);
        newStepsModel stepsModel17 = new newStepsModel("Choose a platform to publish or submit the work", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel13);
        newStepsModel stepsModel18 = new newStepsModel("Prepare a cover letter or query letter", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel14);
        newStepsModel stepsModel19 = new newStepsModel("Follow submission guidelines and procedures", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel15);
        newStepsModel stepsModel20 = new newStepsModel("Promote and share the published work on social media", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel16);
        phase5.setPhaseID(phase5ID);
        arrayList.add(phase5);
        phasesAdapter.notifyDataSetChanged();
    }

    private void openArtsOrCraft() {
        swapTheLinearLayouts();
        theProjectID = insertIntoProjectTable("#Arts");
        projectPhasesTotalTextView.setText("5");
        projectTagTextViewMain.setText("#Arts");
        createThePhaseTable(theProjectID);


        //phase 1
        phasesModelClass phase1 = new phasesModelClass(
                "Idea and Concept Development", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Arts", 0,0,0);
        int phase1ID = insertNewPhase(theProjectID, phase1);
        createStepTableForEachPhase(phase1ID, theProjectID);
        newStepsModel stepsModel1 = new newStepsModel("Brainstorm and generate ideas for the project", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel1);
        newStepsModel stepsModel2 = new newStepsModel("Conduct research and gather reference materials", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel2);
        newStepsModel stepsModel3 = new newStepsModel("Define the medium and style of the project", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel3);
        newStepsModel stepsModel4 = new newStepsModel("Develop a concept and vision for the project", phase1ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel4);
        phase1.setPhaseID(phase1ID);
        arrayList.add(phase1);



        //phase 2
        phasesModelClass phase2 = new phasesModelClass(
                "Design and Planning", theProjectID, "Set Deadline", "Medium", "NOT_SET",
                4, "#Arts", 0,0,0);
        int phase2ID = insertNewPhase(theProjectID, phase2);
        createStepTableForEachPhase(phase2ID, theProjectID);
        newStepsModel stepsModel5 = new newStepsModel("Create sketches or models", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel5);
        newStepsModel stepsModel6 = new newStepsModel("Choose appropriate tools and materials for the project", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel6);
        newStepsModel stepsModel7 = new newStepsModel("Plan the layout and composition", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel7);
        newStepsModel stepsModel8 = new newStepsModel("Determine the timeline and budget", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel8);
        phase2.setPhaseID(phase2ID);
        arrayList.add(phase2);


        //phase 3
        phasesModelClass phase3 = new phasesModelClass(
                "Execution and Creation", theProjectID, "Set Deadline", "High", "NOT_SET",
                4, "#Arts", 0,0,0);
        int phase3ID = insertNewPhase(theProjectID, phase3);
        createStepTableForEachPhase(phase3ID, theProjectID);
        newStepsModel stepsModel9 = new newStepsModel("create working from the plan and design", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel9);
        newStepsModel stepsModel10 = new newStepsModel("plan carefully and precisely to achieve desired results", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel10);
        newStepsModel stepsModel11 = new newStepsModel("Use appropriate techniques and materials", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel11);
        newStepsModel stepsModel12 = new newStepsModel("Stay organized and on schedule to avoid delays", phase3ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel12);
        phase3.setPhaseID(phase3ID);
        arrayList.add(phase3);

        //phase 4

        phasesModelClass phase4 = new phasesModelClass(
                "Finishing and Detailing", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Arts", 0,0,0);
        int phase4ID = insertNewPhase(theProjectID, phase4);
        createStepTableForEachPhase(phase4ID, theProjectID);
        newStepsModel stepsModel13 = new newStepsModel("Refine and polish the project", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel13);
        newStepsModel stepsModel14 = new newStepsModel("Add finishing touches and details", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel14);
        newStepsModel stepsModel15 = new newStepsModel("Apply protective coatings or treatments as needed", phase4ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel15);
        newStepsModel stepsModel16 = new newStepsModel("Prepare the project for display or presentation", phase4ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel16);
        phase4.setPhaseID(phase4ID);
        arrayList.add(phase4);

        //phase 5

        phasesModelClass phase5 = new phasesModelClass(
                "Displaying or Presenting", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Arts", 0,0,0);
        int phase5ID = insertNewPhase(theProjectID, phase5);
        createStepTableForEachPhase(phase5ID, theProjectID);
        newStepsModel stepsModel17 = new newStepsModel("Choose an venue for the project display.", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel13);
        newStepsModel stepsModel18 = new newStepsModel("Create a visually appealing presentation.", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel14);
        newStepsModel stepsModel19 = new newStepsModel("Label or provide information about the project.", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel15);
        newStepsModel stepsModel20 = new newStepsModel("Promote and share the project on social media.", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel16);
        phase5.setPhaseID(phase5ID);
        arrayList.add(phase5);
        phasesAdapter.notifyDataSetChanged();
    }

    private void openPlanInterview() {
        swapTheLinearLayouts();
        theProjectID = insertIntoProjectTable("#Interview");
        projectPhasesTotalTextView.setText("5");
        projectTagTextViewMain.setText("#Interview");
        createThePhaseTable(theProjectID);

        //phase 1
        phasesModelClass phase1 = new phasesModelClass(
                "Research and Preparation", theProjectID, "Set Deadline", "Medium", "NOT_SET",
                4, "#Interview", 0,0,0);
        int phase1ID = insertNewPhase(theProjectID, phase1);
        createStepTableForEachPhase(phase1ID, theProjectID);
        newStepsModel stepsModel1 = new newStepsModel("Research the company and its culture", phase1ID, "Set Deadline", 0,"Research what companies you want to work for. Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel1);
        newStepsModel stepsModel2 = new newStepsModel("Review job description and requirements", phase1ID, "Set Deadline", 0,"Check the JD for requirements! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel2);
        newStepsModel stepsModel3 = new newStepsModel("Learn about the industry and competition", phase1ID, "Set Deadline", 0,"Understand what is expected of you from the job in the current market. Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel3);
        newStepsModel stepsModel4 = new newStepsModel("Prepare questions to ask the interviewer", phase1ID, "Set Deadline", 0,"Start preparing and anticipate the question that will be asked! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel4);
        phase1.setPhaseID(phase1ID);
        arrayList.add(phase1);



        //phase 2
        phasesModelClass phase2 = new phasesModelClass(
                "Self-Assessment and Reflection", theProjectID, "Set Deadline", "High", "NOT_SET",
                4, "#Interview", 0,0,0);
        int phase2ID = insertNewPhase(theProjectID, phase2);
        createStepTableForEachPhase(phase2ID, theProjectID);
        newStepsModel stepsModel5 = new newStepsModel("Identify strengths, weaknesses, and areas of improvement", phase2ID, "Set Deadline", 0,"Identify your forte and your weaknesses and allot time accordingly. Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel5);
        newStepsModel stepsModel6 = new newStepsModel("Identify relevant experience and skills", phase2ID, "Set Deadline", 0,"Use your relevant skills to train for the interview! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel6);
        newStepsModel stepsModel7 = new newStepsModel("Determine career goals and aspirations", phase2ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel7);
        newStepsModel stepsModel8 = new newStepsModel("Create a personal brand and elevator pitch", phase2ID, "Set Deadline", 0,"Create a persona revolving around your skills. Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel8);
        phase2.setPhaseID(phase2ID);
        arrayList.add(phase2);


        //phase 3
        phasesModelClass phase3 = new phasesModelClass(
                "Skill and Knowledge Development", theProjectID, "Set Deadline", "High", "NOT_SET",
                4, "#Interview", 0,0,0);
        int phase3ID = insertNewPhase(theProjectID, phase3);
        createStepTableForEachPhase(phase3ID, theProjectID);
        newStepsModel stepsModel9 = new newStepsModel("Improve relevant technical or soft skills", phase3ID, "Set Deadline", 0,"Practise is the key! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel9);
        newStepsModel stepsModel10 = new newStepsModel("Research and learn about the company.", phase3ID, "Set Deadline", 0,"Some questions that are asked in the interview are about the company itself. Usually you find many questions on the exam from these assignments! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel10);
        newStepsModel stepsModel11 = new newStepsModel("Attend relevant workshops or trainings", phase3ID, "Set Deadline", 0,"Tons of free resources available on the internet, get Going. Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel11);
        newStepsModel stepsModel12 = new newStepsModel("Brush up on industry trends and news", phase3ID, "Set Deadline", 0,"In this ever changing, fast paced world, be up to date. Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel12);
        phase3.setPhaseID(phase3ID);
        arrayList.add(phase3);

        //phase 4

        phasesModelClass phase4 = new phasesModelClass(
                "Practice and Mock Interviews", theProjectID, "Set Deadline", "High", "NOT_SET",
                4, "#Interview", 0,0,0);
        int phase4ID = insertNewPhase(theProjectID, phase4);
        createStepTableForEachPhase(phase4ID, theProjectID);
        newStepsModel stepsModel13 = new newStepsModel("Conduct mock interviews", phase4ID, "Set Deadline", 0,"Revision is the key! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel13);
        newStepsModel stepsModel14 = new newStepsModel("Use online resources for mock interviews and feedback", phase4ID, "Set Deadline", 0,"Tons of free resources available on the internet, get Going! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel14);
        newStepsModel stepsModel15 = new newStepsModel("Practice answering common interview questions", phase4ID, "Set Deadline", 0,"Be fluent with these questions. Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel15);
        newStepsModel stepsModel16 = new newStepsModel("Analyze and learn from feedback and mistakes", phase4ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel16);
        phase4.setPhaseID(phase4ID);
        arrayList.add(phase4);

        //phase 5

        phasesModelClass phase5 = new phasesModelClass(
                "Final Preparation and Follow-up", theProjectID, "Set Deadline", "Medium", "NOT_SET",
                4, "#Interview", 0,0,0);
        int phase5ID = insertNewPhase(theProjectID, phase5);
        createStepTableForEachPhase(phase5ID, theProjectID);
        newStepsModel stepsModel17 = new newStepsModel("Prepare interview outfit and materials", phase5ID, "Set Deadline", 0,"Dress to impress! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel13);
        newStepsModel stepsModel18 = new newStepsModel("Plan logistics and transportation for the interview", phase5ID, "Set Deadline", 0,"Be there on time Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel14);
        newStepsModel stepsModel19 = new newStepsModel("Review and practice answers to common questions", phase5ID, "Set Deadline", 0,"Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel15);
        newStepsModel stepsModel20 = new newStepsModel("Follow up with a thank-you note after the interview.", phase5ID, "Set Deadline", 0,"Prepare a thank you note as it leaves a better impression. You Got This!!","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel16);
        phase5.setPhaseID(phase5ID);
        arrayList.add(phase5);
        phasesAdapter.notifyDataSetChanged();
    }

    private void openStudyPlanTemplate() {
        swapTheLinearLayouts();
        theProjectID = insertIntoProjectTable("#Study");
        projectPhasesTotalTextView.setText("5");
        projectTagTextViewMain.setText("#Study");
        createThePhaseTable(theProjectID);

        //phase 1
        phasesModelClass phase1 = new phasesModelClass(
                "Planning and Preparation", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Study", 0,0,0);
        int phase1ID = insertNewPhase(theProjectID, phase1);
        createStepTableForEachPhase(phase1ID, theProjectID);
        newStepsModel stepsModel1 = new newStepsModel("Set goals and objectives", phase1ID, "Set Deadline", 0,"Have an clear cut definition of what is to be achieved. Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel1);
        newStepsModel stepsModel2 = new newStepsModel("Create a study schedule", phase1ID, "Set Deadline", 0,"Figure out how many hours you want to study everyday and set an deadline to help achieve this goal! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel2);
        newStepsModel stepsModel3 = new newStepsModel("Gather study materials and resources", phase1ID, "Set Deadline", 0,"Collect all the notes and resources available and that may or may not be needed to save time in the future. Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel3);
        newStepsModel stepsModel4 = new newStepsModel("Organize study space and environment", phase1ID, "Set Deadline", 0,"Organize a silent and a cool place, without any distractions that will help you focus on your goals! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase1ID, theProjectID, stepsModel4);
        phase1.setPhaseID(phase1ID);
        arrayList.add(phase1);



        //phase 2
        phasesModelClass phase2 = new phasesModelClass(
                "Research and Learning", theProjectID, "Set Deadline", "Medium", "NOT_SET",
                4, "#Study", 0,0,0);
        int phase2ID = insertNewPhase(theProjectID, phase2);
        createStepTableForEachPhase(phase2ID, theProjectID);
        newStepsModel stepsModel5 = new newStepsModel("Read and study course materials", phase2ID, "Set Deadline", 0,"Start reading the resources collected. Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel5);
        newStepsModel stepsModel6 = new newStepsModel("Take notes and highlight important points", phase2ID, "Set Deadline", 0,"Highlight and mark all the important topics that may appear on exam! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel6);
        newStepsModel stepsModel7 = new newStepsModel("Attend classes and lectures", phase2ID, "Set Deadline", 0,"Attend all lectures/ courses, if any are pending. Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel7);
        newStepsModel stepsModel8 = new newStepsModel("Participate in study groups and discussions", phase2ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase2ID, theProjectID, stepsModel8);
        phase2.setPhaseID(phase2ID);
        arrayList.add(phase2);


        //phase 3
        phasesModelClass phase3 = new phasesModelClass(
                "Practice and Application", theProjectID, "Set Deadline", "High", "NOT_SET",
                4, "#Study", 0,0,0);
        int phase3ID = insertNewPhase(theProjectID, phase3);
        createStepTableForEachPhase(phase3ID, theProjectID);
        newStepsModel stepsModel9 = new newStepsModel("Solve practice problems and exercises", phase3ID, "Set Deadline", 0,"Practise is the key! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel9);
        newStepsModel stepsModel10 = new newStepsModel("Complete assignments and projects", phase3ID, "Set Deadline", 0,"Get those assignments done. Usually you find many questions on the exam from these assignments! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel10);
        newStepsModel stepsModel11 = new newStepsModel("Participate in simulations or practical applications", phase3ID, "Set Deadline", 0,"One cool idea is to figure out how what you are studying can be implemented in our real life. Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel11);
        newStepsModel stepsModel12 = new newStepsModel("Use flashcards or other memorization techniques", phase3ID, "Set Deadline", 0,"Use any technique that helps you learn faster. Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase3ID, theProjectID, stepsModel12);
        phase3.setPhaseID(phase3ID);
        arrayList.add(phase3);

        //phase 4

        phasesModelClass phase4 = new phasesModelClass(
                "Review and Revision", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Study", 0,0,0);
        int phase4ID = insertNewPhase(theProjectID, phase4);
        createStepTableForEachPhase(phase4ID, theProjectID);
        newStepsModel stepsModel13 = new newStepsModel("Review notes and summaries", phase4ID, "Set Deadline", 0,"Revision is the key! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel13);
        newStepsModel stepsModel14 = new newStepsModel("Create study aids and mnemonic devices", phase4ID, "Set Deadline", 0,"Listen to lectures, write and practice or do whatever helps you understand the concept! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel14);
        newStepsModel stepsModel15 = new newStepsModel("Review and understand key concepts and topics", phase4ID, "Set Deadline", 0,"Summarize every chapter and take down the key objectives. Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel15);
        newStepsModel stepsModel16 = new newStepsModel("Identify areas of weakness and focus on them", phase4ID, "Set Deadline", 0,"Take your discussion notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase4ID, theProjectID, stepsModel16);
        phase4.setPhaseID(phase4ID);
        arrayList.add(phase4);

        //phase 5

        phasesModelClass phase5 = new phasesModelClass(
                "Exam Day Preparation", theProjectID, "Set Deadline", "Low", "NOT_SET",
                4, "#Study", 0,0,0);
        int phase5ID = insertNewPhase(theProjectID, phase5);
        createStepTableForEachPhase(phase5ID, theProjectID);
        newStepsModel stepsModel17 = new newStepsModel("Review exam format and requirements", phase5ID, "Set Deadline", 0,"Know when your exam is and make arrangements to get there on time! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel13);
        newStepsModel stepsModel18 = new newStepsModel("Create a checklist of items to bring to the exam", phase5ID, "Set Deadline", 0,"Make sure you bring your water bottle with you as well! Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel14);
        newStepsModel stepsModel19 = new newStepsModel("Get enough rest and eat a healthy meal before the exam", phase5ID, "Set Deadline", 0,"Rest and hydration is the key!. Take your notes here..","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel15);
        newStepsModel stepsModel20 = new newStepsModel("Manage exam anxiety and stress", phase5ID, "Set Deadline", 0,"Understand that you have given your best. Be cool and trust yourself. You Got This!!","NOT_SET",
                0,0,0);
        insertNewStep(phase5ID, theProjectID, stepsModel16);
        phase5.setPhaseID(phase5ID);
        arrayList.add(phase5);
        phasesAdapter.notifyDataSetChanged();
    }

    private void openCustomTemplate() {
        swapTheLinearLayouts();
        projectPhasesTotalTextView.setText("1");
        projectTagTextViewMain.setText("#Custom");
        theProjectID = insertIntoProjectTable("#Custom");
        createThePhaseTable(theProjectID);
        phasesModelClass newPhase = new phasesModelClass();
        newPhase.setPhaseName("Phase 1");
        newPhase.setProjectID(theProjectID);
        newPhase.setPhaseDeadline("Set Deadline");
        newPhase.setPhaseTotalSteps(1);
        newPhase.setPhasePriority("Low");
        newPhase.setPhaseNotificationDetails("NOT_SET");
        newPhase.setPhaseType("#Custom");
        newPhase.setDeadlineSet(0);
        newPhase.setNotificationSet(0);
        newPhase.setPhaseCompleted(0);
        int newPhaseID = insertNewPhase(theProjectID, newPhase);
        createStepTableForEachPhase(newPhaseID, theProjectID);
        newStepsModel stepsModel = new newStepsModel("Step 1", newPhaseID, "Set Deadline", 0,"Take your notes here!","NOT_SET",
                0,0,0);
        insertNewStep(newPhaseID, theProjectID, stepsModel);
        newPhase.setPhaseID(newPhaseID);
        arrayList.add(newPhase);
        phasesAdapter.notifyDataSetChanged();
    }



    private void addNewPhase(){
        phasesModelClass newPhase = new phasesModelClass();
        newPhase.setPhaseName("New Phase");
        newPhase.setProjectID(theProjectID);
        newPhase.setPhaseDeadline("Set Deadline");
        newPhase.setPhaseTotalSteps(1);
        newPhase.setPhasePriority("Low");
        newPhase.setPhaseNotificationDetails("NOT_SET");
        newPhase.setPhaseType("#Custom");
        newPhase.setDeadlineSet(0);
        newPhase.setNotificationSet(0);
        newPhase.setPhaseCompleted(0);
        int newPhaseID = insertNewPhase(theProjectID, newPhase);
        createStepTableForEachPhase(newPhaseID, theProjectID);
        newStepsModel stepsModel = new newStepsModel("Step 1", newPhaseID, "Set Deadline", 0,"Take your notes here!","NOT_SET",
                0,0,0);
        insertNewStep(newPhaseID, theProjectID, stepsModel);
        newPhase.setPhaseID(newPhaseID);
        arrayList.add(newPhase);
        phasesAdapter.notifyDataSetChanged();
        phasesRecyclerView.smoothScrollToPosition(arrayList.size()-1);
    }

    private int insertNewPhase(int projectID, phasesModelClass newPhase){
        return (int) myDB.insertPhaseData(projectID, newPhase.getPhaseName(),
                newPhase.getPhaseDeadline(), newPhase.getPhasePriority(),
                newPhase.getPhaseNotificationDetails(), newPhase.getPhaseTotalSteps(),
                newPhase.getPhaseType(), newPhase.isDeadlineSet(),
                newPhase.isNotificationSet(), newPhase.isPhaseCompleted());
    }

    private void insertNewStep(int newerPhaseID, int projectID, newStepsModel newStep){
        myDB.insertStepData(newerPhaseID, projectID,
                newStep.getStepName(), newStep.getStepDeadline(), newStep.getStepProgress(),
                newStep.getStepNotes(), newStep.getStepNotificationDetails(),
                newStep.isDeadlineSet(), newStep.isNotificationSet(), newStep.isStepCompleted());
    }

    private void createStepTableForEachPhase(int phaseID, int projectID){
        final String CREATE_TABLE_STEPS =
                "CREATE TABLE IF NOT EXISTS "+STEPS_TABLE_PREFIX+phaseID+PHASE_TABLE_PREFIX+projectID+"("+STEP_KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + STEP_PHASE_ID+ " INTEGER,"
                        + STEP_NAME+ " TEXT,"
                        + STEP_DEADLINE+ " TEXT,"
                        + STEP_PROGRESS+ " INTEGER,"
                        + STEP_NOTES+ " TEXT,"
                        + STEP_NOTIFICATION+ " TEXT,"
                        + STEP_DEADLINE_SET+ " INTEGER,"
                        + STEP_NOTIFICATION_SET+ " INTEGER,"
                        + STEP_IS_STEP_COMPLETED+ " INTEGER,"
                        + "FOREIGN KEY(" +STEP_KEY_ID+ ") REFERENCES "
                        + PHASE_TABLE_PREFIX+phaseID + "(" + PHASE_KEY_ID + ") ON DELETE CASCADE);";

        String dbPath = this.getDatabasePath(DATABASE_NAME).getPath();
        try(SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbPath, null)){
            db.execSQL(CREATE_TABLE_STEPS);
        }catch (SQLException E){
            E.printStackTrace();
        }
    }

    private void swapTheLinearLayouts(){
        addNewphasesLinearLayout.setVisibility(View.GONE);
        addExtraPhasesLinearLayout.setVisibility(View.VISIBLE);
        phasesRecyclerView.setVisibility(View.VISIBLE);
    }

    private int insertIntoProjectTable(String tag){
        String ProjectName = projectNameEditText.getText().toString();
        String projectStart = projectStartDateTextView.getText().toString();
        String deadline = projectDeadLineTextView.getText().toString();
        int totalPhases = Integer.parseInt(projectPhasesTotalTextView.getText().toString());
        String projectImportance = projectImportanceTextView.getText().toString();
        String projectTag = tag;
        return (int) myDB.insertProjectData(ProjectName, projectStart, deadline, totalPhases, projectImportance,0, projectTag);
    }

    private void createThePhaseTable(int projectID){
        final String CREATE_TABLE_PHASES =
                "CREATE TABLE IF NOT EXISTS " + PHASE_TABLE_PREFIX + projectID + "(" + PHASE_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + PHASE_NAME + " TEXT,"
                        + PHASE_PROJECT_ID + " INTEGER,"
                        + PHASE_DEADLINE + " TEXT,"
                        + PHASE_PRIORITY + " TEXT,"
                        + PHASE_NOTIFICATION + " TEXT,"
                        + PHASE_STEPS + " INTEGER,"
                        + PHASE_TYPE + " TEXT,"
                        + PHASE_DEADLINE_SET + " INTEGER,"
                        + PHASE_NOTIFICATION_SET + " INTEGER,"
                        + PHASE_IS_COMPLETED + " INTEGER,"
                        + "FOREIGN KEY(" + PHASE_PROJECT_ID + ") REFERENCES "
                        + PROJECT_TABLE + "(" + PROJECT_KEY_ID + ") ON DELETE CASCADE);";

        String dbPath = this.getDatabasePath(DATABASE_NAME).getPath();
        try(SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbPath, null)){
            db.execSQL(CREATE_TABLE_PHASES);
        }catch (SQLException E){
            E.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}