package com.the21codes.do_it;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterNewUserActivity extends AppCompatActivity {

    EditText newUserNameID, newUserEmailId, newUserPassWordID;
    Button registerNewUserButton;
    SharedPreferences userLoginDetails;
    SharedPreferences.Editor userLoginDetailsEditor;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions gso;
    CircleImageView signInWithGoogle;

    SharedPreferences extraLoginDetails;
    SharedPreferences.Editor extraLoginDetailsEditor;
    RelativeLayout termsAndConditionsRelativeLayout;
    ProgressBar progressBar;
    FloatingActionButton goBackButton;
    SharedPreferences vibrationDetails;
    SharedPreferences.Editor vibrationDetailsEditor;
    Boolean vibrationsEnabled = false;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);
        if(isDeviceConnected(getApplicationContext())){
            mAuth = FirebaseAuth.getInstance();
        }
        newUserNameID = (EditText) findViewById(R.id.newUserNameID);
        newUserEmailId = (EditText) findViewById(R.id.newUserEmailId);
        newUserPassWordID = (EditText) findViewById(R.id.newUserPassWordID);
        registerNewUserButton = (Button) findViewById(R.id.registerNewUserButton);
        userLoginDetails = getSharedPreferences("UserLoginAndNameDetails", MODE_PRIVATE);
        userLoginDetailsEditor = userLoginDetails.edit();
        extraLoginDetails = getSharedPreferences("extraLoginDetails", MODE_PRIVATE);
        extraLoginDetailsEditor = extraLoginDetails.edit();
        signInWithGoogle = findViewById(R.id.signInWithGoogle);
        goBackButton = findViewById(R.id.goBackButton);
        termsAndConditionsRelativeLayout = findViewById(R.id.termsAndConditionsRelativeLayout);
        progressBar = findViewById(R.id.progressBar);
        vibrationDetails = getSharedPreferences("vibrationPreferences", MODE_PRIVATE);
        vibrationDetailsEditor = vibrationDetails.edit();
        vibrationsEnabled = vibrationDetails.getBoolean("vibrations", true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        signInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));

                if(isDeviceConnected(getApplicationContext())) {
                    signIn();
                }else{
                    Toast.makeText(RegisterNewUserActivity.this, "Please connect to the Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });


        registerNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDeviceConnected(getApplicationContext())){
                    mAuth = FirebaseAuth.getInstance();
                    validateUserCredentials();
                }else{
                    Toast.makeText(RegisterNewUserActivity.this, "Please connect to the Internet", Toast.LENGTH_SHORT).show();
                }


            }
        });

        termsAndConditionsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_popper));
                goToUrl("https://the21codes.com/DoIt/termsAndCondition");
            }
        });

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrationsEnabled){
                    vibrator.vibrate(VibrationEffect.createOneShot(5,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                goBack();
            }
        });

    }

    private void goBack() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToUrl(String s){
        AlertDialog.Builder externalBuilder = new AlertDialog.Builder(RegisterNewUserActivity.this);
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

    private void signIn(){
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
                                Toast.makeText(RegisterNewUserActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                            extraLoginDetailsEditor.putBoolean("FirstEverLogin", true);
                            extraLoginDetailsEditor.putBoolean("UserInSession", true);
                            extraLoginDetailsEditor.apply();
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterNewUserActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private static boolean isDeviceConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo!= null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

    private void validateUserCredentials() {
        String email = newUserEmailId.getText().toString().trim();
        String password = newUserPassWordID.getText().toString().trim();
        String name = newUserNameID.getText().toString().trim();

        if(name.isEmpty()){
            newUserNameID.setError("Name is required");
            newUserNameID.requestFocus();
            return;
        }



        if(email.isEmpty()){
            newUserEmailId.setError("Email Is Required");
            newUserEmailId.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            newUserEmailId.setError("Please provide a valid Email");
            newUserEmailId.requestFocus();
            return;
        }

        if(password.isEmpty()){
            newUserPassWordID.setError("Password is required");
            newUserPassWordID.requestFocus();
            return;
        }

        if(password.length() < 6){
            newUserPassWordID.setError("PassWord should consist at least 6 characters");
            newUserPassWordID.requestFocus();
            return;
        }

        if(isDeviceConnected(getApplicationContext())) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User user = new User(name, email, password, "0");

                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        userLoginDetailsEditor.putString("USER_NAME", name);
                                                        userLoginDetailsEditor.putString("USER_EMAIL", email);
                                                        userLoginDetailsEditor.putString("USER_LOGIN_TYPE", "REGULAR");
                                                        userLoginDetailsEditor.apply();
                                                        extraLoginDetailsEditor.putBoolean("FirstEverLogin", true);
                                                        extraLoginDetailsEditor.putBoolean("UserInSession", true);
                                                        extraLoginDetailsEditor.apply();
                                                        openMainActivity();
                                                    } else {

                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(RegisterNewUserActivity.this, "NOT Successful ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        else{
            Toast.makeText(this, "Please connect to the Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}