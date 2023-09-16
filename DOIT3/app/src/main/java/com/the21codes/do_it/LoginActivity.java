package com.the21codes.do_it;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity {


    private EditText logInEmailId, loginPassWordId;
    private Button registerNewUserButton, loginUserButton;
    TextView forgotPassWordTextView;
    CircleImageView signInWithGoogle;
    DataBaseHelper myDB;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions gso;

    SharedPreferences userLoginDetails;
    SharedPreferences.Editor userLoginDetailsEditor;

    SharedPreferences extraLoginDetails;
    SharedPreferences.Editor extraLoginDetailsEditor;
    String silly;
    Dialog ForgotPasswordDialog;
    ProgressBar progressBar;

    SharedPreferences vibrationDetails;
    SharedPreferences.Editor vibrationDetailsEditor;
    Boolean vibrationsEnabled = false;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myDB = new DataBaseHelper(this);
        userLoginDetails = getSharedPreferences("UserLoginAndNameDetails", MODE_PRIVATE);
        userLoginDetailsEditor = userLoginDetails.edit();
        extraLoginDetails = getSharedPreferences("extraLoginDetails", MODE_PRIVATE);
        extraLoginDetailsEditor = extraLoginDetails.edit();
        if(isDeviceConnected(getApplicationContext())){
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();

            if(user != null){
                openMainActivity();
            }
        }
        ForgotPasswordDialog = new Dialog(this);
        logInEmailId = (EditText) findViewById(R.id.logInEmailId);
        loginPassWordId = (EditText) findViewById(R.id.loginPassWordId);
        registerNewUserButton =(Button) findViewById(R.id.registerNewUserButton);
        loginUserButton =(Button) findViewById(R.id.loginUserButton);
        forgotPassWordTextView =(TextView) findViewById(R.id.forgotPassWordTextView);
        signInWithGoogle = findViewById(R.id.signInWithGoogle);
        progressBar = findViewById(R.id.progressBar);


        vibrationDetails = getSharedPreferences("vibrationPreferences", MODE_PRIVATE);
        vibrationDetailsEditor = vibrationDetails.edit();
        vibrationsEnabled = vibrationDetails.getBoolean("vibrations", true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);





        registerNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                openRegisterUser();
            }
        });
        loginUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                if(isDeviceConnected(getApplicationContext())) {
                    loginTheUser();
                }else {
                    Toast.makeText(LoginActivity.this, "Please connect to the Internet and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        signInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                if(isDeviceConnected(getApplicationContext())) {
                    signIn();
                }else{
                    Toast.makeText(LoginActivity.this, "Please connect to the Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        forgotPassWordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                showResetPasswordDialog();
            }
        });
    }

    private void openMainActivity() {
        Intent openMainActivity = new Intent(this, MainActivity.class);
        startActivity(openMainActivity);
    }

    private void signIn(){
        Toast.makeText(this, "sign in", Toast.LENGTH_SHORT).show();
        mAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, authRequestCode);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == authRequestCode){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{

                GoogleSignInAccount account = task.getResult(ApiException.class);
                fireBaseAuthWithGoogle(account);
            }catch (ApiException e){
                Toast.makeText(this, "SomeThing went wrong", Toast.LENGTH_SHORT).show();
            }
        }
        progressBar.setVisibility(View.GONE);
    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                // The user is already signed in, check if they exist in your database
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // If the user exists in the database, update their data
                                        if (dataSnapshot.exists()) {
                                            User user = dataSnapshot.getValue(User.class);
                                            userLoginDetailsEditor.putString("USER_NAME", currentUser.getDisplayName());
                                            userLoginDetailsEditor.putString("USER_EMAIL", currentUser.getEmail());
                                            userLoginDetailsEditor.putString("USER_LOGIN_TYPE", "GOOGLE");
                                            userLoginDetailsEditor.apply();
                                            // Update user data here
                                        } else {
                                            // If the user does not exist, create a new user with default values
                                            User newUser = new User(currentUser.getDisplayName(), currentUser.getEmail(), "Google_Login", "0");
                                            userRef.setValue(newUser);
                                            userLoginDetailsEditor.putString("USER_NAME", currentUser.getDisplayName());
                                            userLoginDetailsEditor.putString("USER_EMAIL", currentUser.getEmail());
                                            userLoginDetailsEditor.putString("USER_LOGIN_TYPE", "GOOGLE");
                                            userLoginDetailsEditor.apply();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Handle error here
                                    }
                                });

                            } else {
                                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                            extraLoginDetailsEditor.putBoolean("FirstEverLogin", true);
                            extraLoginDetailsEditor.putBoolean("UserInSession", true);
                            extraLoginDetailsEditor.apply();
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void loginTheUser() {
        String email = logInEmailId.getText().toString().trim();
        String password = loginPassWordId.getText().toString().trim();
        if(email.isEmpty()){
            logInEmailId.setError("Email Is Required");
            logInEmailId.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            logInEmailId.setError("Please provide a valid Email");
            logInEmailId.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if(password.isEmpty()){
            loginPassWordId.setError("Password is required");
            loginPassWordId.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if(password.length() < 6){
            loginPassWordId.setError("PassWord should consist at least 6 characters");
            loginPassWordId.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        try{
            if(isDeviceConnected(getApplicationContext())) {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.fetchSignInMethodsForEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                if (task.getResult().getSignInMethods().size() == 0) {
                                    logInEmailId.setError("Email Does Not Exists, Please Register!");
                                    logInEmailId.requestFocus();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {

                                                try {
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                                    String uid = mAuth.getCurrentUser().getUid();
                                                    reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.child("userName").exists() && !snapshot.child("userName").getValue().toString().isEmpty()) {
                                                                silly = snapshot.child("userName").getValue().toString();
                                                            } else {
                                                                silly = "NONE";
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            silly = "NONE";
                                                        }
                                                    });
                                                } catch (Exception e) {
                                                    silly = "NONE";
                                                }
                                                userLoginDetailsEditor.putString("USER_NAME", silly);
                                                userLoginDetailsEditor.putString("USER_EMAIL", email);
                                                userLoginDetailsEditor.putString("USER_LOGIN_TYPE", "REGULAR");
                                                userLoginDetailsEditor.apply();
                                                extraLoginDetailsEditor.putBoolean("FirstEverLogin", true);
                                                extraLoginDetailsEditor.putBoolean("UserInSession", true);
                                                extraLoginDetailsEditor.apply();
                                                progressBar.setVisibility(View.GONE);
                                                openHomePageActivity();
                                            } else {
                                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                                    loginPassWordId.setError("Invalid Password");
                                                    loginPassWordId.requestFocus();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Could Not Login, Please try again.", Toast.LENGTH_SHORT).show();
                                                }
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }else{
                Toast.makeText(this, "Please connect to the Internet", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }catch (Exception e){
            Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }
    }

    private static boolean isDeviceConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo!= null && networkInfo.isConnected()){
            return true;
        }
        return false;
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
                }
            }
        });


    }

    private void openHomePageActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void openRegisterUser() {
        Intent intent = new Intent(this, RegisterNewUserActivity.class);
        startActivity(intent);
        finish();
    }
}