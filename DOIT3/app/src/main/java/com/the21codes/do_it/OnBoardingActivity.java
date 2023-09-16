package com.the21codes.do_it;


import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.widget.Button;

import com.github.appintro.AppIntro2;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;

public class OnBoardingActivity extends AppIntro2 {

    Button goToLoginButton;
    SharedPreferences sharedFirstTimeOpeningPreferences;
    SharedPreferences.Editor sharedFirstTimeOpeningPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      //  goToLoginButton = (Button) findViewById(R.id.goToLoginButton);
        sharedFirstTimeOpeningPreferences = getSharedPreferences("FirstTimeAppOpening", MODE_PRIVATE);
        sharedFirstTimeOpeningPreferencesEditor = sharedFirstTimeOpeningPreferences.edit();

        addSlide(AppIntroFragment.createInstance(
                "Welcome to DO-IT!",
                "Plan Anything and Track Everything!",
                R.drawable.welcome_slide_logo,
                R.color.FIRST_SLIDE,
                R.color.FIRST_SLIDE_TEXT_ONE,
                R.color.FIRST_SLIDE_TEXT_TWO,
                R.font.lato,
                R.font.merriweather));



        // Add the second slide
        addSlide(AppIntroFragment.createInstance(
                "Streamline project's complexity!",
                "Manage your workload by dividing your project into smaller and logical pieces!",
                R.drawable.slide_one,
                R.color.SECOND_SLIDE,
                R.color.SECOND_SLIDE_TEXT_ONE,
                R.color.SECOND_SLIDE_TEXT_TWO,
                R.font.lato,
                R.font.merriweather));

        // Add the third slide
        addSlide(AppIntroFragment.createInstance(
                "Keep track of your progress!",
                "Finish those pending projects by breaking them down into logical phases and steps, and track the progress for each, individually!",
                R.drawable.slide_two,
                R.color.THIRD_SLIDE,
                R.color.THIRD_SLIDE_TEXT_ONE,
                R.color.THIRD_SLIDE_TEXT_TWO,
                R.font.lato,
                R.font.merriweather));

        // Add the fourth slide
        addSlide(AppIntroFragment.createInstance(
                "The Ultimate Planner!",
                "Set deadlines, prioritize important tasks, take notes, and more \n\n let's get started!",
                R.drawable.slide_three,
                R.color.FOURTH_SLIDE,
                R.color.FOURTH_SLIDE_ONE,
                R.color.FOURTH_SLIDE_TWO,
                R.font.lato,
                R.font.merriweather));

        // Customize the color of the status bar
        setTransformer(new AppIntroPageTransformerType.Parallax(1,-1,2));
        showStatusBar(false);
        setScrollDurationFactor(2);
        setImmersiveMode();
        setSystemBackButtonLocked(true);
        setColorTransitionsEnabled(true);
        setIndicatorEnabled(true);
        setButtonsEnabled(true);
        setStatusBarColorRes(R.color.FIRST_SLIDE);
        setVibrate(true);
        setColorTransitionsEnabled(true);
        setSkipButtonEnabled(true);
        setIndicatorEnabled(true);
        setProgressIndicator();

    }






    @Override
    protected void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
                sharedFirstTimeOpeningPreferencesEditor.putBoolean("IsFirstTimeAppOpening", false);
                sharedFirstTimeOpeningPreferencesEditor.apply();
                openLoginActivity();
    }

    @Override
    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        sharedFirstTimeOpeningPreferencesEditor.putBoolean("IsFirstTimeAppOpening", false);
        sharedFirstTimeOpeningPreferencesEditor.apply();
        openLoginActivity();
    }


    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}