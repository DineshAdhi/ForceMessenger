package com.example.dinesh.forcetexter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class ForgotPasswordActivity extends AppCompatActivity {

    Firebase ref;
    TextInputLayout forgotPasswordEmailLayout;
    EditText forgotPasswordEmail;
    Button forgotPasswordButton;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initialize();
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(forgotPasswordEmail.getText().toString().equals(""))
                {
                    forgotPasswordEmailLayout.setError("Please Enter the mail");
                }
                 else if(!forgotPasswordEmail.getText().toString().contains("@"))
                {
                    forgotPasswordEmailLayout.setError("Your email is invalid");
                }
                else
                {
                    progressDialog.setMessage("Generating your password");
                    progressDialog.setTitle("Wait a sec");
                    progressDialog.show();
                    ref.resetPassword(forgotPasswordEmail.getText().toString(), new Firebase.ResultHandler() {
                        @Override
                        public void onSuccess() {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(ForgotPasswordActivity.this).setTitle("Password Change Alert")
                                    .setMessage("If the entered Email is correct, you will recieve the recovery password. Please check your mail")
                                    .setPositiveButton("I got it!", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {

                        }
                    });
                }
            }
        });
    }

    private void initialize() {
        Firebase.setAndroidContext(this);
        ref=new Firebase("https://texter10c.firebaseio.com");
        forgotPasswordEmail=(EditText)findViewById(R.id.forgotPasswordEmail);
        forgotPasswordEmailLayout=(TextInputLayout)findViewById(R.id.forgotPasswordEmailLayout);
        forgotPasswordButton=(Button)findViewById(R.id.forgotPasswordButton);
        forgotPasswordEmailLayout.setHint("Recovery Mail");
        progressDialog=new ProgressDialog(ForgotPasswordActivity.this);
    }
}
