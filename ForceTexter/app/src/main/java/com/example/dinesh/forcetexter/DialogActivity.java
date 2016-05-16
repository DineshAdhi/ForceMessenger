package com.example.dinesh.forcetexter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.CookieManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DialogActivity extends AppCompatActivity implements DataHolder{

    Firebase ref;
    ArrayList<Map<String,String>> userDialogMap;
    ListView dialogListView;
    FloatingActionButton floatingActionButton;
    final static String NEW_PREFS="New Preferences";
    int backpressedCount=0;
    ProgressBar progressBar;
    ArrayList<Map<String,String>> sharedPreferencesDialogMap;

    @Override
    public void onBackPressed() {
        backpressedCount++;
        if(backpressedCount<=1)
        {
            Toast.makeText(DialogActivity.this, "Press the back again to exit the app", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent=new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                backpressedCount=0;
            }
        };
        handler.postDelayed(runnable,3000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.changeProfileMenuID:
                Intent intent2=new Intent(DialogActivity.this,ChangeProfileActivity.class);
                startActivity(intent2);
                break;
            case R.id.LogoutMenuID:
                SharedPreferences.Editor editor=getSharedPreferences(NEW_PREFS,MODE_PRIVATE).edit();
                editor.remove("CurrentUsername");
                editor.remove("CurrentUserId");
                editor.remove("DialogMap");
                editor.commit();
                setLastSeen();
                ref.unauth();
                Intent intent=new Intent(DialogActivity.this,LoginActivity.class);
                intent.putExtra("Logout","true");
                startActivity(intent);
                break;
            case R.id.setStatusMenuID:
                Intent intent1=new Intent(DialogActivity.this,StatusActivity.class);
                startActivity(intent1);
                break;

        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        initialize();

        final Firebase userIdRef = ref.child("UserDetails/names");
        userIdRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, String> map = new HashMap<String, String>();
                map = dataSnapshot.getValue(Map.class);
                dataStore.addUserIds(map.get("UserId").toString());
                populateDialogs();
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

    public void populateDialogs()
    {
        DialogAdapter adapter;
        Firebase dialogRef=ref.child("ChatDialogs");
        dialogRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, String> map = new HashMap<String, String>();
                dataStore.generateChatId();
                Log.e("CurrrentUserId",dataStore.getCurrentUserID());
                Log.e("Generated ChatIds",dataStore.generatedChatIds.toString());
                map = dataSnapshot.getValue(Map.class);
                map.put("DialogId", dataSnapshot.getKey());
                if (dataStore.generatedChatIds.contains(map.get("DialogId")))
                    if(!userDialogMap.contains(map)) {
                        userDialogMap.add(map);
                        storeDialogMapinSharedPreferences(userDialogMap);
                    }
                final DialogAdapter adapter=new DialogAdapter(userDialogMap,DialogActivity.this,isNetworkAvailable());
                dialogListView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);

                dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dataStore.setOpponentUserName(adapter.getOpponentName(position));
                        dataStore.setOpponentUserID(adapter.getId(position));
                        Intent intent=new Intent(DialogActivity.this,ChatActivity.class);
                        startActivity(intent);
                    }
                });


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

    private void storeDialogMapinSharedPreferences(final ArrayList<Map<String,String>> map)
    {
        AsyncTask<Void,Void,ArrayList> task=new AsyncTask<Void, Void, ArrayList>() {
            @Override
            protected ArrayList doInBackground(Void... params) {
                Log.e("Storing","DialogMap in SP");
                JSONArray result=new JSONArray(map);
                SharedPreferences.Editor messageSharedPreferences=getSharedPreferences(NEW_PREFS,MODE_PRIVATE).edit();
                messageSharedPreferences.putString("DialogMap",result.toString());
                messageSharedPreferences.commit();
                return null;
            }
        };
          task.execute();
    }

    public void setLastSeen()
    {
        SimpleDateFormat sdf=new SimpleDateFormat();
        Calendar cal=Calendar.getInstance();
        Firebase lastSeenref=ref.child("LastSeen/"+dataStore.getCurrentUserID());
        Map<String,String> map=new HashMap<String,String>();
        map.put("LastSeen",sdf.format(cal.getTime()));
        lastSeenref.setValue(map);
    }


    private void initialize() {

        Firebase.setAndroidContext(this);
        ref = new Firebase("https://texter10c.firebaseio.com");
        userDialogMap = new ArrayList<Map<String, String>>();
        sharedPreferencesDialogMap=new ArrayList<Map<String, String>>();
        dialogListView = (ListView) findViewById(R.id.dialogListView);
        getSupportActionBar().setTitle("Chats");
        progressBar=(ProgressBar)findViewById(R.id.dialogProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.usersListFab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DialogActivity.this, UsersListActivity.class);
                startActivity(intent);
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences(NEW_PREFS, MODE_PRIVATE);
            if (!sharedPreferences.getString("CurrentUsername", "null").equals("null")) {
                dataStore.addCurrentUserEmail(sharedPreferences.getString("Email","null"));
                dataStore.setCurrentUserID(sharedPreferences.getString("CurrentUserId", "null"));
                dataStore.setCurrentUserName(sharedPreferences.getString("CurrentUsername", null));
        }

       if(!isNetworkAvailable())
       {
           retrieveDatafromSharedPreferences();
           Log.e("SharePreDialogs",sharedPreferencesDialogMap.toString());
       }

    }
    public void retrieveDatafromSharedPreferences()
    {
        sharedPreferencesDialogMap=new ArrayList<Map<String, String>>();
        SharedPreferences sharedPreferences = getSharedPreferences(NEW_PREFS, MODE_PRIVATE);
        if (!sharedPreferences.getString("DialogMap", "null").equals("null")) {
            Log.e("Retrieving", "Dialog Map from SP");
            try {
                JSONArray array = new JSONArray(sharedPreferences.getString("DialogMap", "null"));
                Map<String, String> item = null;
                for (int i = 0; i < array.length(); i++) {
                    String obj = array.get(i).toString();
                    JSONObject ary = new JSONObject(obj);
                    item = new HashMap<String, String>();
                    Iterator<String> it = ary.keys();
                    while (it.hasNext()) {
                        String key = it.next();
                        item.put(key, ary.get(key).toString());
                    }
                    sharedPreferencesDialogMap.add(item);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("D stored by SP", sharedPreferencesDialogMap.toString());
            if (!(sharedPreferences.getInt("UserIDSize", 0) == 0)) {
                Log.e("Log", "Retrieving Dialogs from SharedPreferences");
                Log.e("Log", "Size of userId" + sharedPreferences.getInt("UserIDSize", 0));
                for (int i = 0; i < sharedPreferences.getInt("UserIDSize", 0); i++) {
                    dataStore.addUserIds(sharedPreferences.getString("UserIDs" + i, null));
                }
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkIngo = manager.getActiveNetworkInfo();
        return networkIngo != null && networkIngo.isConnected();
    }
    }

