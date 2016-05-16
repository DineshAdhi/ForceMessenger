package com.example.dinesh.forcetexter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity implements DataHolder{

    TextView userProfileStatusField;
    Firebase ref;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initialize();
        Firebase statusRef=ref.child("Status/"+dataStore.getOpponentUserID());
        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Status Change",dataSnapshot.toString());
               String status=dataSnapshot.getValue(String.class);
                try {
                    if (status != null) {
                        userProfileStatusField.setText("  "+ status);
                    } else {
                        userProfileStatusField.setText("   Hi I am using Force texter");
                    }
                }
                catch (NullPointerException e)
                {
                    userProfileStatusField.setText("Hi I am using Force texter");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void initialize() {
        Firebase.setAndroidContext(this);
        getSupportActionBar().setTitle(dataStore.getOpponentUserName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ref=new Firebase("https://texter10c.firebaseio.com");
        userProfileStatusField=(TextView)findViewById(R.id.userProfileStatusField);
    }
}
