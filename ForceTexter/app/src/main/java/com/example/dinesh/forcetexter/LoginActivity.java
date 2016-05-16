package com.example.dinesh.forcetexter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.UserRecoverableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class LoginActivity extends AppCompatActivity implements DataHolder, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    TextInputLayout emailInputLayout,passwordInputLayout;
    EditText emailField,passwordField;
    Button loginButton;
    TextView signupButton,forgotPasswordButton;
    Firebase ref;
    ProgressDialog progressDialog;
    Intent intent;
    GoogleApiClient mGoogleApiClient;
    SignInButton googleSignInButton;
    String googleAccessToken;
    Snackbar snackbar;
    final static String NEW_PREFS="New Preferences";
    GoogleAuthUtil googleAuthUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initialize();
        Firebase.setAndroidContext(this);
        ref=new Firebase("https://texter10c.firebaseio.com");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Logging in !");
                progressDialog.setTitle("Hang on!");
                progressDialog.show();

                ref.authWithPassword(emailField.getText().toString(), passwordField.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        Log.e("Authenticated","Authenticated");
                        getUserIdandLogin();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Contacting Google");
                progressDialog.setMessage("Logging you in");
                progressDialog.show();
                if(!mGoogleApiClient.isConnected())
                mGoogleApiClient.connect();
            }
        });

    }

    private void getGoogleToken(){

        AsyncTask<Void,Void,String> task=new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                final String scopes="oauth2:"+"https://www.googleapis.com/auth/plus.login"+" "+"https://www.googleapis.com/auth/plus.me";
                try {
                    if(!mGoogleApiClient.isConnected())
                    {
                        mGoogleApiClient.connect();
                    }
                    googleAccessToken= GoogleAuthUtil.getToken(LoginActivity.this,Plus.AccountApi.getAccountName(mGoogleApiClient),scopes);
                    Log.e("AccessToken",googleAccessToken+"");
                }
                catch (IOException e)
                {
                    Log.e("IOException",e.getMessage());
                    e.printStackTrace();
                }
               catch (UserRecoverableAuthException e)
               {
                   startActivity(e.getIntent(),null);
               } catch (GoogleAuthException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    ref.authWithOAuthToken("google", googleAccessToken, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(final AuthData authData) {
                            try {
                                Log.e("Firebase", "Google Authentication Success");
                                Log.e("Username", authData.getProviderData().get("displayName").toString());
                                Log.e("Id", authData.getProviderData().get("id").toString());


                                Firebase googleUserRef = ref.child("UserDetails/names/" + authData.getProviderData().get("id").toString());
                                Map<String, String> googleUserMap = new HashMap<String, String>();
                                googleUserMap.put("Username", authData.getProviderData().get("displayName").toString());
                                final String UserID = "GoogleUser" + authData.getProviderData().get("displayName") + authData.getProviderData().get("id");
                                googleUserMap.put("UserId", UserID);

                                googleUserRef.setValue(googleUserMap, new Firebase.CompletionListener() {
                                    @Override
                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                        progressDialog.dismiss();
                                        dataStore.setCurrentUserName(authData.getProviderData().get("displayName").toString());
                                        dataStore.setCurrentUserID(UserID);
                                        storeDatainSharedPreferences();
                                        Log.e("Disconnecting","Disconnecting");
                                        Intent intent = new Intent(LoginActivity.this, DialogActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                            catch (NullPointerException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            Log.e("GogoleAuthFailed", firebaseError.getMessage());

                        }
                    });
                }
                catch (NullPointerException e)
                {
                    Log.e("Accesstoken problem",e.getMessage());
                }
            }
        };
        task.execute();
    }

    public void getUserIdandLogin()
    {
        dataStore.userDialogMap=new ArrayList<Map<String,String>>();
        dataStore.generatedChatIds=new ArrayList<>();
        Firebase refUser=ref.child("UserDetails/names");
        refUser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, String> map = new HashMap<String, String>();
                map = dataSnapshot.getValue(Map.class);
                try{
                    if(!map.get("UserId").contains("GoogleUser"))
                    if (map.get("Email").equals(emailField.getText().toString()))
                    {
                        Toast.makeText(LoginActivity.this, "Successfilly Logged in", Toast.LENGTH_SHORT).show();
                        dataStore.addCurrentUserEmail(emailField.getText().toString());
                        Log.e("DataStore",dataStore.getCurrentUserMail());
                        dataStore.setCurrentUserName(map.get("Username"));
                        dataStore.setCurrentUserID(map.get("UserId"));
                        intent=new Intent(LoginActivity.this,DialogActivity.class);
                        startActivity(intent);
                        storeDatainSharedPreferences();
                        progressDialog.dismiss();
                    }

                }
                catch (NullPointerException e)
                {
                    Log.e("NullPointerGUser",e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void storeDatainSharedPreferences() {
        try {
            SharedPreferences.Editor editor = getSharedPreferences(NEW_PREFS, MODE_PRIVATE).edit();
            editor.putString("Email",dataStore.getCurrentUserMail());
            editor.putString("CurrentUsername", dataStore.getCurrentUserName());
            editor.putString("CurrentUserId", dataStore.getCurrentUserID());
            editor.commit();
            if(mGoogleApiClient!=null)
                mGoogleApiClient.disconnect();

        }
        catch (NoSuchElementException e)
        {
            new AlertDialog.Builder(LoginActivity.this).setMessage("There was an error whil logging in")
                    .setTitle("Little Problem here!").setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent=new Intent(LoginActivity.this,LoginActivity.class);
                    removeDatainSharedPreferences();
                    mGoogleApiClient.disconnect();
                    startActivity(intent);
                }
            }).show();
        }
    }

    private void removeDatainSharedPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences(NEW_PREFS, MODE_PRIVATE).edit();
        editor.remove("CurrentUsername");
        editor.remove("CurrentUserId");
        editor.commit();
    }


    private void initialize() {
        emailInputLayout=(TextInputLayout)findViewById(R.id.emailInputLayout);
        emailField=(EditText)findViewById(R.id.emailField);
        passwordField=(EditText)findViewById(R.id.passwordField);
        passwordInputLayout=(TextInputLayout)findViewById(R.id.passwordInputLayout);
        loginButton=(Button)findViewById(R.id.loginButton);
        progressDialog=new ProgressDialog(LoginActivity.this);
        signupButton=(TextView)findViewById(R.id.signupButton);
        emailInputLayout.setHint("Email");
        passwordInputLayout.setHint("Password");
        forgotPasswordButton=(TextView)findViewById(R.id.forgotPasswordField);
        googleSignInButton=(SignInButton)findViewById(R.id.googleSignInButton);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharePreferences=getSharedPreferences(NEW_PREFS,MODE_PRIVATE);
        if(!sharePreferences.getString("CurrentUsername","null").equals("null")) {
            Log.e("SharedPreferences", sharePreferences.getString("CurrentUsername", "null"));
            Log.e("SharedPreferences",sharePreferences.getString("CurrentUserId",null));
            Intent intent=new Intent(LoginActivity.this,DialogActivity.class);
            startActivity(intent);
        }

        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();


       if(!isNetworkAvailable())
       {
          snackbar=Snackbar.make(findViewById(android.R.id.content),"You are offline",Snackbar.LENGTH_INDEFINITE)
                  .setAction("Retry", new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          if(!isNetworkAvailable())
                              dismissSnackBar();
                          else
                              snackbar.show();
                      }

                  });
           snackbar.show();
       }

    }

    private void dismissSnackBar() {
        snackbar.dismiss();
    }


    private boolean isNetworkAvailable()
    {
        ConnectivityManager manager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkIngo=manager.getActiveNetworkInfo();
        return networkIngo!=null&& networkIngo.isConnected();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
            Log.e("GoogleApi","Connected");
            getGoogleToken();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("GoogleApi",connectionResult.toString());
        if(!connectionResult.hasResolution())
        {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(),LoginActivity.this,0).show();
        }
        if(connectionResult.isSuccess())
        {
            getGoogleToken();
        }
        try
        {
            connectionResult.startResolutionForResult(this,100);
        }
        catch (IntentSender.SendIntentException e)
        {
            e.printStackTrace();
        }
    }




}
