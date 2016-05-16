package com.example.dinesh.forcetexter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SignupActivity extends AppCompatActivity {

    TextInputLayout signupEmailInputLayout,signupUsernameInputLayout,signupInputPasswordLayout,signupInputMobileLayout,confirmPasswordLayout;
    EditText signupEmailField,signupUsernameField,signupPasswordField,signupMobileField,confirmPasswordField;
    Button registerButton;
    ProgressDialog progressDialog;
    ImageView profileImageView;
    Firebase ref;
    String imageFile;
    final static int SELECT_PHOTO=100;
    final static int RESULT_CROP=101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        intialize();
        IDGenerate();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Registering you..");
                progressDialog.setTitle("Hang On!");
                IDGenerate();
                if (!(signupPasswordField.getText().toString().equals(confirmPasswordField.getText().toString()))) {
                    signupInputPasswordLayout.setError("Password Mismatch");
                    confirmPasswordLayout.setError("Please check your password");
                } else {
                    signupInputPasswordLayout.setError(null);
                    confirmPasswordLayout.setError(null);
                    progressDialog.show();
                    ref.createUser(signupEmailField.getText().toString(), signupPasswordField.getText().toString(), new Firebase.ResultHandler() {
                        @Override
                        public void onSuccess() {
                            progressDialog.dismiss();
                            finish();
                            Toast.makeText(SignupActivity.this, "Account Successfully created, Sign in now !", Toast.LENGTH_LONG).show();
                            registerUser();
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            Toast.makeText(SignupActivity.this, firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,SELECT_PHOTO);
            }
        });



    }

    private int IDGenerate() {
        Random random=new Random();
        int ran=random.nextInt((30000000+1)-100000)+100000;
        return ran;
    }

    private void registerUser() {
        Firebase registerFirebaseRef=ref.child("UserDetails/names");
        Map<String,String> registerUserMap=new HashMap<String,String>();
        registerUserMap.put("Email",signupEmailField.getText().toString());
        registerUserMap.put("Password",signupPasswordField.getText().toString());
        registerUserMap.put("Username",signupUsernameField.getText().toString());
        registerUserMap.put("Mobile Number",signupMobileField.getText().toString());
        registerUserMap.put("UserId",signupUsernameField.getText().toString()+Integer.toString(IDGenerate()));
        registerFirebaseRef.push().setValue(registerUserMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Log.e("Completed",firebase.toString());


            }
        });
    }

    private void intialize() {
        Firebase.setAndroidContext(this);
        ref=new Firebase("https://texter10c.firebaseio.com");
        progressDialog=new ProgressDialog(SignupActivity.this);
        profileImageView=(ImageView)findViewById(R.id.profileImageView);
        signupEmailInputLayout=(TextInputLayout)findViewById(R.id.signupEmailInputLayout);
        signupUsernameInputLayout=(TextInputLayout)findViewById(R.id.signupUsernameInputLayout);
        signupInputPasswordLayout=(TextInputLayout)findViewById(R.id.signupPasswordInputLayout);
        signupInputMobileLayout=(TextInputLayout)findViewById(R.id.signupMobileInputLayout);
        confirmPasswordLayout=(TextInputLayout)findViewById(R.id.confirmPasswordLayout);
        signupEmailField=(EditText)findViewById(R.id.signupEmailField);
        signupUsernameField=(EditText)findViewById(R.id.signupUsernameField);
        signupPasswordField=(EditText)findViewById(R.id.signupPasswordField);
        signupMobileField=(EditText)findViewById(R.id.signupMobileField);
        confirmPasswordField=(EditText)findViewById(R.id.confirmPasswordField);
        registerButton=(Button)findViewById(R.id.registerButton);
        signupEmailInputLayout.setHint("Email");
        signupUsernameInputLayout.setHint("Username");
        signupInputPasswordLayout.setHint("Password");
        signupInputMobileLayout.setHint("Mobile Number");
        confirmPasswordLayout.setHint("Confirm Password");

    }
}
