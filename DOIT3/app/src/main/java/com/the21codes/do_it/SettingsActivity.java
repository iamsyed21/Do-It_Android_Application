package com.the21codes.do_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    ImageView goBackImageView;
    CircleImageView googleProfilePicture, defaultPandaPictureImageView;
    TextView hiUserTextView, internetStatusTextView, userNameTextViw;
    ListView mListViewSettings;
    SettingsAdapter mSettingsAdapter;
    List<Boolean> mEnabledList;
    SharedPreferences userLoginDetails;
    SharedPreferences.Editor userLoginDetailsEditor;
    ListView aboutUsListView, openSourcesListView;
    String userName;
    String userEmail;
    String userType;
    DataBaseHelper myDB;
    Dialog aboutUsDialog;
    Dialog forgotPasswordDialog;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    List<String> settingsList;
    List<Boolean> enabledList;
    private DatabaseReference rootDatabaseReference;
    Dialog ForgotPasswordDialog;
    AdRequest adRequest;
    InterstitialAd mInterstitialAd;
    RewardedAd rewardedAd;
    String theFlattenedString;
    SharedPreferences adsCounterDetails;
    SharedPreferences.Editor adsCounterDetailsEditor;
    int adCounter;

    SharedPreferences extraLoginDetails;
    SharedPreferences.Editor extraLoginDetailsEditor;

    SharedPreferences vibrationDetails;
    SharedPreferences.Editor vibrationDetailsEditor;
    Boolean vibrationsEnabled = false;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        myDB = new DataBaseHelper(this);
        if(isDeviceConnected(getApplicationContext())){
            mAuth = FirebaseAuth.getInstance();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            rootDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        }

        settingsList = new ArrayList<>();
        enabledList = new ArrayList<>();
        ForgotPasswordDialog = new Dialog(this);
        goBackImageView = (ImageView) findViewById(R.id.goBackImageView);
        hiUserTextView = (TextView) findViewById(R.id.hiUserTextView);
        userNameTextViw = (TextView) findViewById(R.id.userNameTextViw);
        internetStatusTextView = (TextView) findViewById(R.id.internetStatusTextView);
        mListViewSettings = (ListView) findViewById(R.id.list_view_settings);
        defaultPandaPictureImageView = findViewById(R.id.defaultPandaPictureImageView);
        googleProfilePicture = findViewById(R.id.googleProfilePicture);
        aboutUsDialog = new Dialog(this);
        userLoginDetails = getSharedPreferences("UserLoginAndNameDetails", MODE_PRIVATE);
        userType = userLoginDetails.getString("USER_LOGIN_TYPE", "ALIEN");
        userName =  userLoginDetails.getString("USER_NAME", "NONE");
        userEmail =  userLoginDetails.getString("USER_EMAIL", "EMAIL");
        userLoginDetailsEditor = userLoginDetails.edit();
        adsCounterDetails = getSharedPreferences("UserLoginAndNameDetails", MODE_PRIVATE);
        adCounter = adsCounterDetails.getInt("Settings_page_ad", 0);
        adsCounterDetailsEditor = adsCounterDetails.edit();
        extraLoginDetails = getSharedPreferences("extraLoginDetails", MODE_PRIVATE);
        extraLoginDetailsEditor = extraLoginDetails.edit();

        vibrationDetails = getSharedPreferences("vibrationPreferences", MODE_PRIVATE);
        vibrationDetailsEditor = vibrationDetails.edit();
        vibrationsEnabled = vibrationDetails.getBoolean("vibrations", true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if(isDeviceConnected(getApplicationContext())) {
            currentUser = mAuth.getCurrentUser();
            internetStatusTextView.setText("Connected To the Internet. Data is synced");
        }else{
            internetStatusTextView.setText("Not connected To the Internet!");
        }
        if(currentUser == null){
            extraLoginDetailsEditor.putBoolean("UserInSession", false);
            extraLoginDetailsEditor.apply();
        }



        if(currentUser!=null && userType.equals("GOOGLE") && isDeviceConnected(getApplicationContext())) {
            String photoUrl = currentUser.getPhotoUrl().toString();
            Picasso.get().load(photoUrl).noFade().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // Assign the image to an ImageView
                    defaultPandaPictureImageView.setVisibility(View.GONE);
                    googleProfilePicture.setVisibility(View.VISIBLE);
                    googleProfilePicture.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    e.printStackTrace();
                    defaultPandaPictureImageView.setVisibility(View.VISIBLE);
                    googleProfilePicture.setVisibility(View.GONE);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });

        }

        if(userType.equals("REGULAR")){
            if(userName == null || userName.equals("NONE")){
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                String uid = currentUser.getUid();
                reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                 userName = snapshot1.child("userName").getValue(String.class);
                            }
                        }else{
                            userName = "NONE";
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userName = "NONE";
                    }
                });
            }
            settingsList.add("Enable Vibrations");
            settingsList.add("Clear All Data");
            settingsList.add("Logout User");
            settingsList.add("Delete Account");
            settingsList.add("Change Password");
            settingsList.add("Rate Us");
            settingsList.add("About DO-IT Application");
            enabledList.add(true);
            enabledList.add(true);
            enabledList.add(true);
            enabledList.add(true);
            enabledList.add(true);
            enabledList.add(true);
            enabledList.add(true);
        } else if (userType.equals("GOOGLE")) {
            if(userName==null || userName.equals("NONE")){
                if(isDeviceConnected(getApplicationContext())) {
                    userName = currentUser.getDisplayName();
                }
            }
            settingsList.add("Enable Vibrations");
            settingsList.add("Clear All Data");
            settingsList.add("Logout User");
            settingsList.add("Delete Account");
            settingsList.add("Rate Us");
            settingsList.add("About DO-IT Application");
            enabledList.add(true);
            enabledList.add(true);
            enabledList.add(true);
            enabledList.add(true);
            enabledList.add(true);
            enabledList.add(true);
        }else{
            settingsList.add("Enable Vibrations");
            settingsList.add("Clear All Data");
            settingsList.add("Logout User");
            settingsList.add("Delete Account");
            settingsList.add("Change Login Password");
            settingsList.add("About DO-IT Application");
            enabledList.add(true);
            enabledList.add(false);
            enabledList.add(false);
            enabledList.add(false);
            enabledList.add(false);
            enabledList.add(true);
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        if(!isDeviceConnected(getApplicationContext())){
            enabledList.set(1, false);
            enabledList.set(2, false);
            enabledList.set(3, false);
            enabledList.set(4, false);
        }

        if(vibrationsEnabled){
            settingsList.set(0, "Disable Vibrations");
        }


        if(isDeviceConnected(getApplicationContext())) {
            AdView adView = findViewById(R.id.adView);
            adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            setUserName(userName);
            if (adCounter <= 2) {
                adCounter++;
                adsCounterDetailsEditor.putInt("Settings_page_ad", adCounter);
                adsCounterDetailsEditor.apply();

            } else {
                adCounter = 0;
                adsCounterDetailsEditor.putInt("Settings_page_ad", 0);
                adsCounterDetailsEditor.apply();
                showInterstitialAd();
            }
        }
        hiUserTextView.setText(getGreeting());
        mEnabledList = new ArrayList<>(enabledList);
        mSettingsAdapter = new SettingsAdapter(this, settingsList, mEnabledList);
        mListViewSettings.setAdapter(mSettingsAdapter);


        // Handle click events for each list item
        mListViewSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(10,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                if (mEnabledList.get(position)) {
                    if(userType.equals("GOOGLE")){
                        if (position == 0) {
                            if(settingsList.get(0).equals("Enable Vibrations")){
                                settingsList.set(0,"Disable Vibrations");
                                mSettingsAdapter.notifyDataSetChanged();
                                vibrationDetailsEditor.putBoolean("vibrations", true);
                                vibrationDetailsEditor.apply();
                                vibrationsEnabled = false;
                            }else if(settingsList.get(0).equals("Disable Vibrations")){
                                settingsList.set(0,"Enable Vibrations");
                                mSettingsAdapter.notifyDataSetChanged();
                                vibrationDetailsEditor.putBoolean("vibrations", false);
                                vibrationDetailsEditor.apply();
                                vibrationsEnabled = true;
                            }
                        }
                        if(position == 1){
                            openClearDataDialog();
                        }
                        if(position == 2){
                            openLogoutUserDialog();
                        }
                        if(position == 3){
                            deleteAccountFromExistence();

                        }if(position == 4){
                           openRateUs();
                        }
                        if(position ==5){
                            openAboutApplication();
                        }

                    }else if(userType.equals("REGULAR")){
                        if (position == 0) {
                            if(settingsList.get(0).equals("Enable Vibrations")){
                                settingsList.set(0,"Disable Vibrations");
                                mSettingsAdapter.notifyDataSetChanged();
                                vibrationDetailsEditor.putBoolean("vibrations", true);
                                vibrationDetailsEditor.apply();
                                vibrationsEnabled = false;
                            }else if(settingsList.get(0).equals("Disable Vibrations")){
                                settingsList.set(0,"Enable Vibrations");
                                mSettingsAdapter.notifyDataSetChanged();
                                vibrationDetailsEditor.putBoolean("vibrations", false);
                                vibrationDetailsEditor.apply();
                                vibrationsEnabled = true;
                            }
                        }
                        if(position == 1){
                            openClearDataDialog();
                        }
                        if(position == 2){
                            openLogoutUserDialog();
                        }
                        if(position == 3){
                            deleteAccountFromExistence();

                        }if(position == 4){
                            showResetPasswordDialog();
                        }
                        if(position ==5){
                            openRateUs();
                        }
                        if(position ==6){
                            openAboutApplication();
                        }
                    }else{
                        if (position == 0) {
                            if(settingsList.get(0).equals("Enable Vibrations")){
                                settingsList.set(0,"Disable Vibrations");
                                mSettingsAdapter.notifyDataSetChanged();
                                vibrationDetailsEditor.putBoolean("vibrations", true);
                                vibrationDetailsEditor.apply();
                                vibrationsEnabled = false;
                            }else if(settingsList.get(0).equals("Disable Vibrations")){
                                settingsList.set(0,"Enable Vibrations");
                                mSettingsAdapter.notifyDataSetChanged();
                                vibrationDetailsEditor.putBoolean("vibrations", false);
                                vibrationDetailsEditor.apply();
                                vibrationsEnabled = true;
                            }
                        }
                        if(position == 1){
                            openClearDataDialog();
                        }
                        if(position == 2){
                            openLogoutUserDialog();
                        }
                        if(position == 3){
                            deleteAccountFromExistence();

                        }if(position == 4){
                            showResetPasswordDialog();
                        }
                        if(position ==5){
                            openAboutApplication();
                        }
                    }
                } else {
                    // handle disabled item clicked
                }
            }
        });

        goBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(10,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                goBackToMain();
            }
        });


    }

    private void goBackToMain(){
        onBackPressed();
        finish();
    }

    private void showInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-6952672354974833/8886624169", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;

                            mInterstitialAd.show(SettingsActivity.this);
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    mInterstitialAd = null;
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    mInterstitialAd = null;
                                    super.onAdFailedToShowFullScreenContent(adError);
                                }

                                @Override
                                public void onAdImpression() {
                                    super.onAdImpression();
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    super.onAdShowedFullScreenContent();
                                    mInterstitialAd = null;
                                }
                            });

                    }



                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });


    }

    private void showRewardedAd(int i){
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-6952672354974833/9531328286",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        rewardedAd = null;
                        if (i==2) {
                            openLoginActivity();
                        }else if(i==3){
                            openLoginActivity();
                        }
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        rewardedAd.show(SettingsActivity.this, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                            }
                        });
                        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                rewardedAd = null;
                                super.onAdDismissedFullScreenContent();
                                if (i==2) {
                                    openLoginActivity();
                                }else if(i==3){
                                    openLoginActivity();
                                }
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                rewardedAd = null;
                                if (i==2) {
                                    openLoginActivity();
                                }else if(i==3){
                                    openLoginActivity();
                                }
                            }

                            @Override
                            public void onAdImpression() {
                                super.onAdImpression();
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent();
                                rewardedAd = null;
                                if (i==2) {
                                    openLoginActivity();
                                }else if(i==3){
                                    openLoginActivity();
                                }
                            }
                        });
                    }
                });
    }

    private void showResetPasswordDialog() {
        ForgotPasswordDialog.setContentView(R.layout.reset_password_dialog);
        EditText email_edittext =(EditText) ForgotPasswordDialog.findViewById(R.id.email_edittext);
        Button changePasswordButton =(Button) ForgotPasswordDialog.findViewById(R.id.changePasswordButton);
        TextView email_sent_message =(TextView) ForgotPasswordDialog.findViewById(R.id.email_sent_message);
        TextView dialog_description =(TextView) ForgotPasswordDialog.findViewById(R.id.dialog_description);
        TextView dialog_title =(TextView) ForgotPasswordDialog.findViewById(R.id.dialog_title);
        ForgotPasswordDialog.show();

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (changePasswordButton.getText().toString().trim().equals("Close")) {
                    ForgotPasswordDialog.dismiss();
                    if(isDeviceConnected(getApplicationContext())) {
                        showRewardedAd(4);
                    }
                } else {
                    String email = email_edittext.getText().toString().trim();
                    if (email.isEmpty()) {
                        email_edittext.setError("Email is required!");
                        email_edittext.requestFocus();
                        return;
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        email_edittext.setError("Please enter Valid Email");
                        email_edittext.requestFocus();
                        return;
                    }
                    if (isDeviceConnected(getApplicationContext())) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.fetchSignInMethodsForEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                        if (task.getResult().getSignInMethods().size() == 0) {
                                            email_edittext.setError("Email Does Not Exists, Please Register");
                                            email_edittext.requestFocus();
                                            return;
                                        } else {
                                            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        email_sent_message.setText("An link to reset your Password been successfully sent to your email address.");
                                                        email_sent_message.setVisibility(View.VISIBLE);
                                                        dialog_description.setVisibility(View.GONE);
                                                        dialog_title.setText("Link sent!");
                                                        email_edittext.setVisibility(View.GONE);
                                                        changePasswordButton.setText("Close");
                                                    } else {
                                                        email_sent_message.setText("Something went wrong! Kindly try again");
                                                        email_sent_message.setVisibility(View.GONE);
                                                        dialog_description.setVisibility(View.VISIBLE);
                                                        dialog_title.setText("Reset Password:");
                                                        email_edittext.setVisibility(View.VISIBLE);
                                                        changePasswordButton.setText("Change Password");
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                    }else {
                        Toast.makeText(SettingsActivity.this, "Please connect to the Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private void openAboutApplication() {
        aboutUsDialog.setContentView(R.layout.about_us_dialog);
        aboutUsDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        aboutUsListView = aboutUsDialog.findViewById(R.id.aboutListView);
        List<String> list  = new ArrayList<>();
        list.add("About 21Codes(Developer Information)");
        list.add("Privacy Policy");
        list.add("Terms of services");
        list.add("Open Source Licenses");
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
        aboutUsListView.setAdapter(adapter);
        aboutUsDialog.show();

        aboutUsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                switch(i){
                    case 0:
                        goToUrl("https://the21codes.com/");
                        break;
                    case 1:
                        goToUrl("https://the21codes.com/DoIt/privacyPolicy");
                        break;
                    case 2:
                        goToUrl("https://the21codes.com/DoIt/termsAndCondition");
                        break;
                    case 3:
                        openOpenSourceLicenses();
                        break;



                }
            }
        });

    }

    private void openOpenSourceLicenses() {
        Dialog openSourcesDialog = new Dialog(this);
        openSourcesDialog.setContentView(R.layout.open_source_licenses_dialog);
        openSourcesDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        openSourcesListView = openSourcesDialog.findViewById(R.id.openSourcesLicenses);

        List<String> list = new ArrayList<>();
        list.add("Circle Progress by lzyzsd");
        list.add("MP Android Chart by PhilJay");
        list.add("NotifyMe by jakebonk");
        list.add("NumberProgressBar by daimajia");
        list.add("TapTargetView by KeepSafe");
        list.add("AppIntro by AppIntro");
        list.add("picasso by square");
        list.add("CircleImageView hdodenhof");
        list.add("smart-app-rate by codemybrainsout");
        list.add("Konfetti by DanielMartinus");

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
        openSourcesListView.setAdapter(adapter);
        openSourcesDialog.show();

        openSourcesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                openLicensesActivity(i);
            }
        });
    }

    private void openLicensesActivity(int i){
        Intent intent = new Intent(this, licenses_activity.class);
        intent.putExtra("document", i);
        startActivity(intent);
    }

    private void goToUrl(String s){
        AlertDialog.Builder externalBuilder = new AlertDialog.Builder(SettingsActivity.this);
        externalBuilder.setTitle("Are You Sure?");
        externalBuilder.setMessage("This will open the link externally");
        externalBuilder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse(s);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
        externalBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = externalBuilder.create();
        dialog.show();
    }

    private void openRateUs() {
        RatingDialog.Builder ratingDialog = new RatingDialog.Builder(this);
        ratingDialog.threshold(1)
                .icon(R.drawable.doit_logo)
                .title(R.string.experience, R.color.FOURTH_SLIDE)
                .ratingBarColor(R.color.gold, R.color.Yet_To_Start);
        RatingDialog rate = ratingDialog.build();
        rate.show();
    }

    private void deleteAccountFromExistence() {
        AlertDialog.Builder clearDataDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        clearDataDialogBuilder.setTitle("Delete Account?");
        clearDataDialogBuilder.setMessage("This will delete all your data and credentials locally and from the cloud?");
        clearDataDialogBuilder.setPositiveButton("Delete Account", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isDeviceConnected(getApplicationContext())) {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    rootDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                    rootDatabaseReference.removeValue();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            myDB.deleteTheEntireDatabase();
                                            myDB.createTablesIfNotExist();
                                            myDB.close();
                                            extraLoginDetailsEditor.putBoolean("UserInSession", false);
                                            extraLoginDetailsEditor.putBoolean("FirstEverLogin", true);
                                            extraLoginDetailsEditor.apply();
                                            Toast.makeText(SettingsActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                            showRewardedAd(3);
                                            dialogInterface.dismiss();
                                        }
                                    }
                                });
                    }else {
                        dialogInterface.dismiss();
                        Toast.makeText(SettingsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SettingsActivity.this, "Please Connect to the Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        clearDataDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog deleteAccount = clearDataDialogBuilder.create();
        deleteAccount.show();
    }

    private static boolean isDeviceConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo!= null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

    private void openLogoutUserDialog() {
        AlertDialog.Builder clearDataDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        clearDataDialogBuilder.setTitle("Logout of your account?");
        clearDataDialogBuilder.setMessage("This will log you out of the session");
        clearDataDialogBuilder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if(mAuth !=null && isDeviceConnected(getApplicationContext())){
                    uploadDataToFireBase();
                    mAuth.signOut();
                    userLoginDetailsEditor.putString("USER_NAME", "NONE");
                    userLoginDetailsEditor.putString("USER_EMAIL", "EMAIL");
                    userLoginDetailsEditor.putString("USER_LOGIN_TYPE", "ALIEN");
                    userLoginDetailsEditor.apply();
                    myDB.deleteTheEntireDatabase();
                    myDB.createTablesIfNotExist();
                    myDB.close();
                    extraLoginDetailsEditor.putBoolean("UserInSession", false);
                    extraLoginDetailsEditor.putBoolean("FirstEverLogin", true);
                    extraLoginDetailsEditor.apply();
                    showRewardedAd(3);
                    dialogInterface.dismiss();
                }else{
                    Toast.makeText(SettingsActivity.this, "Please connect to the Internet", Toast.LENGTH_SHORT).show();
                }



            }
        });
        clearDataDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog logOut = clearDataDialogBuilder.create();
        logOut.show();
    }

    private void uploadDataToFireBase() {
        theFlattenedString = flattenTheDatabase();
        uploadDatabaseToFireBase();
    }

    private void uploadDatabaseToFireBase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        if(theFlattenedString.length() == 0){
            rootDatabaseReference.child("userDatabase").setValue("0").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                }
            });
        }else{
            rootDatabaseReference.child("userDatabase").setValue(theFlattenedString).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                }
            });
        }

    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void openClearDataDialog() {
        AlertDialog.Builder clearDataDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        clearDataDialogBuilder.setTitle("Are you sure?");
        clearDataDialogBuilder.setMessage("All your projects will be deleted and all data will be cleared locally and from the cloud. " +
                "This process can't be undone");
        clearDataDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isDeviceConnected(getApplicationContext())) {
                    clearFirebaseValues();
                    myDB.deleteTheEntireDatabase();
                    myDB.createTablesIfNotExist();
                    myDB.close();
                    mEnabledList.set(1, false);
                    mSettingsAdapter.notifyDataSetChanged();
                    dialogInterface.dismiss();
                    showRewardedAd(1);
                }else{
                    Toast.makeText(SettingsActivity.this, "Please connect to the Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        clearDataDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog clearData = clearDataDialogBuilder.create();
        clearData.show();
    }

    private void clearFirebaseValues() {
        FirebaseAuth mAuth1 = FirebaseAuth.getInstance();
        try{
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            rootDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
            rootDatabaseReference.child("userDatabase").setValue("0").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(SettingsActivity.this, "Cloud Database Cleared!", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void disableSetting(int position) {
        mSettingsAdapter.setEnabled(position, false);
    }

    public void enableSetting(int position) {
        mSettingsAdapter.setEnabled(position, true);
    }

    public void setUserName(String Name){
        try {
            if (Name != null && !Name.equals("NONE") && !Name.trim().isEmpty()) {
                String[] array = Name.trim().split(" ");
                if (array.length == 1) {
                    userNameTextViw.setText(array[0]);
                } else {
                    if (array[0].length() == 1) {
                        String newName = array[0] + " " + array[1];
                        userNameTextViw.setText(newName);
                    } else {
                        userNameTextViw.setText(array[0]);
                    }
                }
            } else {
                userNameTextViw.setText("Go-getter!");
            }
        }catch (Exception e){
            userNameTextViw.setText("Go-getter!");
        }
    }

    public String getGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 4 && hour < 12) {
            return "Good Morning";
        } else if (hour >= 12 && hour < 17) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
        }
    }

    private String flattenTheDatabase(){
        try {
            ArrayList<String> allDataBase = new ArrayList<>();
            ArrayList<ProjectModel> projectsList = myDB.getAllTheProjects();

            StringBuilder projects = new StringBuilder();
            StringBuilder phases = new StringBuilder();
            StringBuilder steps = new StringBuilder();
            for(ProjectModel project: projectsList) {
                projects.append(Integer.toString(project.getProjectID())).append(":-basic-Separator-:").
                        append(project.getProjectName()).append(":-basic-Separator-:").
                        append(project.getProjectStart()).append(":-basic-Separator-:").
                        append(project.getProjectDeadline()).append(":-basic-Separator-:").
                        append(Integer.toString(project.getProjectPhases())).append(":-basic-Separator-:").
                        append(project.getProjectImportance()).append(":-basic-Separator-:").
                        append(Integer.toString(project.getProjectCompleted())).append(":-basic-Separator-:").
                        append(project.getProjectTag()).append(":-project-Separator-:");

                ArrayList<phasesModelClass> phasesList = myDB.getAllPhasesForaProject(project.getProjectID());
                for(phasesModelClass phase: phasesList) {
                    phases.append(Integer.toString(phase.getPhaseID())).append(":-basic-Separator-:").
                            append(Integer.toString(phase.getProjectID())).append(":-basic-Separator-:").
                            append(phase.getPhaseName()).append(":-basic-Separator-:").
                            append(phase.getPhaseDeadline()).append(":-basic-Separator-:").
                            append(phase.getPhasePriority()).append(":-basic-Separator-:").
                            append(phase.getPhaseNotificationDetails()).append(":-basic-Separator-:").
                            append(Integer.toString(phase.getPhaseTotalSteps())).append(":-basic-Separator-:").
                            append(phase.getPhaseType()).append(":-basic-Separator-:").
                            append(Integer.toString(phase.isDeadlineSet())).append(":-basic-Separator-:").
                            append(Integer.toString(phase.isNotificationSet())).append(":-basic-Separator-:").
                            append(Integer.toString(phase.isPhaseCompleted())).append(":-phase-Separator-:");

                    ArrayList<newStepsModel> stepsList = myDB.getAllStepsForaPhase(phase.getPhaseID(), phase.getProjectID());
                    for(newStepsModel step: stepsList) {
                        steps.append(Integer.toString(step.getStepID())).append(":-basic-Separator-:").
                                append(step.getStepName()).append(":-basic-Separator-:").
                                append(Integer.toString(step.getPhaseID())).append(":-basic-Separator-:").
                                append(step.getStepDeadline()).append(":-basic-Separator-:").
                                append(Integer.toString(step.getStepProgress())).append(":-basic-Separator-:").
                                append(step.getStepNotes()).append(":-basic-Separator-:").
                                append(step.getStepNotificationDetails()).append(":-basic-Separator-:").
                                append(Integer.toString(step.isNotificationSet())).append(":-basic-Separator-:").
                                append(Integer.toString(step.isDeadlineSet())).append(":-basic-Separator-:").
                                append(Integer.toString(step.isStepCompleted())).append(":-step-Separator-:");
                    }
                    steps.append("::PHASE--INDICATOR::");
                }
                phases.append("::PROJECT--INDICATOR::");
                steps.append("::PROJECT--INDICATOR::");
            }

            allDataBase.add(projects.toString());
            allDataBase.add(phases.toString());
            allDataBase.add(steps.toString());


            StringBuilder finalString = new StringBuilder();
            finalString.append(projects).append("~~~FIRST##SEPARATOR~~~").append(phases).append("~~~FIRST##SEPARATOR~~~").append(steps);
            return finalString.toString();

        }catch(Exception e){
            e.printStackTrace();
            return "0";
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}