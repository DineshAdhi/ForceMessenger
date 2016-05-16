package com.example.dinesh.forcetexter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class ChangeProfileActivity extends AppCompatActivity implements DataHolder{

    TextInputLayout oldPasswordTextLayout,newPasswordTextLayout,newConfirmPasswordTextLayout,newEmailTextLayout;
    EditText oldPasswordField,newPasswordField,newConfirmPasswordField,newEmailField;
    Button changeButton;
    Firebase ref;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);
        initialize();
        Firebase changeRef=ref.child("UserDetails/names");
        if(dataStore.getCurrentUserID().contains("GoogleUser"))
        {
            Log.e("GoogleUserAlert",dataStore.getCurrentUserID());
            new AlertDialog.Builder(ChangeProfileActivity.this).setTitle("A Kind note")
                    .setMessage("Please kindly note that you logged in via Google, so you doesn't need to change your password or any other info")
                    .setPositiveButton("Go back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ChangeProfileActivity.this.finish();
                        }
                    }).show();
        }
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("Changing info");
                progressDialog.setMessage("Changing your password");
                progressDialog.show();
                if(newPasswordField.getText().toString().equals(newConfirmPasswordField.getText().toString()))
                {
                    ref.changePassword(dataStore.getCurrentUserMail(), oldPasswordField.getText().toString(),
                            newPasswordField.getText().toString(), new Firebase.ResultHandler() {
                                @Override
                                public void onSuccess() {
                                    progressDialog.dismiss();
                                    new AlertDialog.Builder(ChangeProfileActivity.this).setTitle("Change in Details")
                                            .setMessage("Your password has been changed, please take a note of it.Just in case if you forget your password, kindly tap 'Forgot Password' while logging")
                                            .setPositiveButton("I got it", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    ChangeProfileActivity.this.finish();
                                                }
                                            }).show();
                                }

                                @Override
                                public void onError(FirebaseError firebaseError) {
                                    Log.e("Error",firebaseError.getMessage());
                                }
                            });
                }
                else
                {
                    newConfirmPasswordField.setError("Password Mismatch");
                }
            }
        });



    }

    private void initialize() {
        Firebase.setAndroidContext(ChangeProfileActivity.this);
        ref=new Firebase("https://texter10c.firebaseio.com");
        oldPasswordTextLayout=(TextInputLayout)findViewById(R.id.oldPasswordInputLayout);
        newPasswordTextLayout=(TextInputLayout)findViewById(R.id.newPasswordLayout);
        newConfirmPasswordTextLayout=(TextInputLayout)findViewById(R.id.newConfirmPasswordLayout);
        changeButton=(Button)findViewById(R.id.changeButton);
        progressDialog=new ProgressDialog(ChangeProfileActivity.this);

        oldPasswordTextLayout.setHint("Old Password");
        newPasswordTextLayout.setHint("New Password");
        newConfirmPasswordTextLayout.setHint("Confirm Password");

        oldPasswordField=(EditText)findViewById(R.id.oldPasswordField);
        newPasswordField=(EditText)findViewById(R.id.newPasswordField);
        newConfirmPasswordField=(EditText)findViewById(R.id.newConfirmPasswordField);
    }
}
