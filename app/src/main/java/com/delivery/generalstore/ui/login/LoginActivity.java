package com.delivery.generalstore.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.delivery.generalstore.R;
import com.delivery.generalstore.ui.MainActivity;
import com.delivery.generalstore.ui.login.LoginViewModel;
import com.delivery.generalstore.ui.login.LoginViewModelFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private String mVerificationId;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText phonenumberET = findViewById(R.id.phonenumber);
        final EditText nameET = findViewById(R.id.name);
        final EditText emailET = findViewById(R.id.email);
        final EditText otpET = findViewById(R.id.otp);
        final Button signinBT = findViewById(R.id.login);
        final Button generateOtpBT = findViewById(R.id.generate_otp);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        mAuth = FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                signinBT.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getNameError() != null) {
                    nameET.setError(getString(loginFormState.getNameError()));
                }
                if (loginFormState.getEmailError() != null) {
                    emailET.setError(getString(loginFormState.getEmailError()));
                }
                if (loginFormState.getPhoneError() != null) {
                    phonenumberET.setError(getString(loginFormState.getPhoneError()));
                }
                if (loginFormState.getOtpError() != null) {
                    otpET.setError(getString(loginFormState.getOtpError()));
                }
                if(loginFormState.getPhoneError() == null && loginFormState.getNameError() == null && loginFormState.getEmailError() == null){
                    generateOtpBT.setEnabled(true);
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }
            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(nameET.getText().toString(),emailET.getText().toString(),phonenumberET.getText().toString(),
                        otpET.getText().toString());
            }
        };
        phonenumberET.addTextChangedListener(afterTextChangedListener);
        otpET.addTextChangedListener(afterTextChangedListener);
        otpET.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                }
                return false;
            }
        });


        final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                credential.getSmsCode();
                Log.d("On", "In Verification Completed"+credential.getSmsCode());
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // Show a message and update the UI
                // ...
                Log.d("On", "In Verification Failed" + e);
            }

            @Override
            public void onCodeSent(@NonNull final String verificationId,
                                   @NonNull final PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("On", "onCodeSent:" + verificationId + token);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                otpET.setVisibility(View.VISIBLE);
                generateOtpBT.setVisibility(View.INVISIBLE);
                signinBT.setVisibility(View.VISIBLE);
                nameET.setEnabled(false);
                emailET.setEnabled(false);
                phonenumberET.setEnabled(false);
                loadingProgressBar.setVisibility(View.INVISIBLE);
            }
        };

        generateOtpBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+phonenumberET.getText().toString(),  //send a request to firesbase to send an OTP.
                        120,
                        TimeUnit.SECONDS,
                        LoginActivity.this,
                        mCallbacks);
                Log.d("On", "genenrateOtpBT");
            }
        });

        signinBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                AuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId,otpET.getText().toString());
                mAuth.signInWithCredential(phoneAuthCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser userdetails=mAuth.getCurrentUser();
                        createuser_in_cloud_db(nameET.getText().toString(),emailET.getText().toString(),userdetails);
                        Log.d("on","Auth success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("on","Auth Failure");
                        showLoginFailed("Auth Failure");
                    }
                });
            }
        });
    }

    private void updateUiWithUser(FirebaseUser user) {
        // TODO : initiate successful logged in experience
        if(user != null) {
            String welcome = getString(R.string.welcome) +user.getUid()+ user.getEmail();
            // TODO : initiate successful logged in experience
            Snackbar.make(findViewById(R.id.login), welcome, Snackbar.LENGTH_LONG).show();
            Intent movetoMainPage=new Intent(getApplicationContext(), MainActivity.class);
            movetoMainPage.putExtra("UId",user.getUid());
            movetoMainPage.putExtra("Email",user.getEmail());
            finish();
            startActivity(movetoMainPage);
        }
        else{
            Toast.makeText(getApplicationContext(), "Please enter your details to Login",Toast.LENGTH_LONG).show();
        }
    }

    private void createuser_in_cloud_db(final String name, String email, final FirebaseUser user){

        // Creating data(Map variable) to store in firebase
        Map<String, Object> data=new HashMap<>();
        data.put("Email",email);
        data.put("Phone", user.getPhoneNumber());
        data.put("Name", name);
        data.put("User ID",user.getUid());

        if(db==null){
            Log.e("In create user", "db is getting null");
        }
        else {
            Log.e("In Create User", user.getPhoneNumber()+"@generalstore");
            DocumentReference user_doc_ref = db.collection("users").document(user.getPhoneNumber()+"@generalstore");
            user_doc_ref.set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("In Create user", "Added user details");
                            updateUiWithUser(user);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("In Create user", "Failed"+e);
                        }
                    });
        }
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUiWithUser(currentUser);

    }
}