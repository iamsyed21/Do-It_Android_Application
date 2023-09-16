package com.the21codes.do_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    VideoView LoadingLogo;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    SharedPreferences sharedFirstTimeOpeningPreferences;
    private boolean isFirstTimeAppOpening;
    private boolean InternetWorking = false;
    private boolean userInSession = false;
    SharedPreferences extraLoginDetails;
    SharedPreferences.Editor extraLoginDetailsEditor;
    boolean gotTheAuth = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sharedFirstTimeOpeningPreferences = getSharedPreferences("FirstTimeAppOpening", MODE_PRIVATE);
        isFirstTimeAppOpening = sharedFirstTimeOpeningPreferences.getBoolean("IsFirstTimeAppOpening", true);
        LoadingLogo = (VideoView) findViewById(R.id.loadingScreenAnimationId);
        Uri video = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.loading_screen_animation);
        LoadingLogo.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE);
        LoadingLogo.setVideoURI(video);
        InternetWorking = isDeviceConnected(getApplicationContext());
        if(InternetWorking){
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            gotTheAuth = true;
        }else {
            gotTheAuth = false;
        }


        // Change 3000 to the duration you want in milliseconds

        extraLoginDetails = getSharedPreferences("extraLoginDetails", MODE_PRIVATE);
        extraLoginDetailsEditor = extraLoginDetails.edit();
        userInSession = extraLoginDetails.getBoolean("UserInSession", false);


        LoadingLogo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(isFirstTimeAppOpening){
                    openOnBoardingActivity();
                }else if(isDeviceConnected(getApplicationContext())){
                    if(user !=null && gotTheAuth){
                        openMainActivity();
                    }else if(userInSession){
                        openMainActivity();
                    }else{
                        openLoginActivity();
                    }
                }else{
                    openLoginActivity();
                }


            }
        });
        LoadingLogo.start();

    }


    private static boolean isDeviceConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo!= null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

    private void openOnBoardingActivity() {
        if(isFinishing()) return;
        Intent intent = new Intent(this, OnBoardingActivity.class);
        startActivity(intent);
    }

    private void openMainActivity() {
        if(isFinishing()) return;
        Intent openMainActivity = new Intent(this, MainActivity.class);
        startActivity(openMainActivity);
    }

    public void openLoginActivity(){
        if(isFinishing()) return;
        Intent openLogin = new Intent(this, LoginActivity.class);
        startActivity(openLogin);
    }


}