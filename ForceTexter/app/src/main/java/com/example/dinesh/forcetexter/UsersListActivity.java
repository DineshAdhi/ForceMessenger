package com.example.dinesh.forcetexter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UsersListActivity extends AppCompatActivity implements DataHolder {

    List<String> usersList;
    ArrayList<Map<String, String>> userDetailsList;
    Firebase ref;
    UserAdapter adapter;
    String id;
    String senderName;
    ListView usersListView;
    ProgressBar progressBar;
    FloatingActionButton floatingActionButton;
    final static String MY_PREFS = "1213234";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
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
        setContentView(R.layout.activity_users_list);
        initialize();

        Firebase userRef = ref.child("UserDetails/names");
        userRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, String> map = new HashMap<String, String>();
                map = dataSnapshot.getValue(Map.class);
                if (!(map.get("UserId").equals(id)))
                    userDetailsList.add(map);
                else senderName = map.get("Username").toString();
                populateView(userDetailsList);
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

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String OpponentId = adapter.getOpponentId(position);
                Intent intent = new Intent(UsersListActivity.this, ChatActivity.class);
                dataStore.setOpponentUserID(OpponentId);
                dataStore.setOpponentUserName(adapter.getOpponentName(position));
                Log.e("CurrentUser", dataStore.getCurrentUserName());
//                Log.e("OpponeneUser", dataStore.getOpponentUserName());
                startActivity(intent);
            }
        });
    }

    public void populateView(ArrayList<Map<String, String>> userDetails) {
        Set<Map<String, String>> userDetailSet = new HashSet<Map<String, String>>();
        userDetailSet.addAll(userDetails);
        storeinSharedPreferences();
        adapter = new UserAdapter(userDetails, UsersListActivity.this);
        usersListView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    private void storeinSharedPreferences() {
        JSONArray result = new JSONArray(userDetailsList);
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS, MODE_PRIVATE).edit();
        editor.putString("UserDetailsList", result.toString());
        editor.commit();
    }


    private void initialize() {
        Firebase.setAndroidContext(this);
        ref = new Firebase("https://texter10c.firebaseio.com");
        usersList = new ArrayList<String>();
        usersListView = (ListView) findViewById(R.id.usersListVIew);
        id = dataStore.getCurrentUserID();
        progressBar=(ProgressBar)findViewById(R.id.userListProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        String friends = "Friends";
        Log.e("IDinUSersList", id);
        userDetailsList = new ArrayList<Map<String, String>>();
        ArrayList<Map<String, String>> sharedprefencesMap = new ArrayList<Map<String, String>>();
       getSupportActionBar().setTitle("Friends");

    }
}