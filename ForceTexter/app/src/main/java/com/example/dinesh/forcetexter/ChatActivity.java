package com.example.dinesh.forcetexter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements DataHolder {

    EditText messageBox;
    ImageButton sendButton;
    Firebase ref;
    String senderChatID,recieverChatID;
    ArrayList<Map<String,String>> messageMapList;
    ListView chatBox;
    String senderName,recieverName,latestMessage;
    TextView toolbarOnlineStatus;

    @Override
    protected void onResume() {
        super.onResume();
        SimpleDateFormat sdf=new SimpleDateFormat();
        Calendar cal=Calendar.getInstance();
        Firebase lastSeenref=ref.child("LastSeen/"+dataStore.getCurrentUserID());
        Map<String,String> map=new HashMap<String,String>();
        map.put("LastSeen","Online");
        lastSeenref.setValue(map);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setLastSeen();
    }

    @Override
    protected void onStop() {
        super.onStop();
        setLastSeen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setLastSeen();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
            case android.R.id.title:
                Toast.makeText(ChatActivity.this,"You clicked",Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initialize();
        senderName=dataStore.getCurrentUserName();
        recieverName=dataStore.getOpponentUserName();
        senderChatID=dataStore.ChatIDSender();
        recieverChatID=dataStore.ChatIDReceiver();
        String OpponentName=dataStore.getOpponentUserName();
        sendButton.setColorFilter(Color.RED);
        messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendButton.setColorFilter(Color.GREEN);
                if (messageBox.getText().toString().equals("")) {
                    sendButton.setColorFilter(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (messageBox.getText().toString().equals("")) {
                    sendButton.setColorFilter(Color.RED);
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase chatRef = ref.child("Messages/textMessages");
                Map<String, String> chatMap = new HashMap<String, String>();
                if (!messageBox.getText().toString().equals("")) {
                    latestMessage = messageBox.getText().toString();
                    chatMap.put("ChatId", senderChatID);
                    chatMap.put("Message", messageBox.getText().toString());
                    chatRef.push().setValue(chatMap);
                    messageBox.setText("");
                    sendButton.setColorFilter(Color.RED);
                }

                Firebase dialogRef = ref.child("ChatDialogs/" + recieverChatID);
                Map<String, String> dialogMap = new HashMap<String, String>();
                dialogMap.put("RecieverName", dataStore.getCurrentUserName());
                dialogMap.put("LatestMessage", latestMessage);
                dialogMap.put("RecieverId", dataStore.getCurrentUserID());
                dialogRef.setValue(dialogMap);

                Firebase newdialogRef = ref.child("ChatDialogs/" + senderChatID);
                Map<String, String> newdialogMap = new HashMap<String, String>();
                newdialogMap.put("RecieverName", dataStore.getOpponentUserName());
                newdialogMap.put("LatestMessage", latestMessage);
                newdialogMap.put("RecieverId", dataStore.getOpponentUserID());
                newdialogRef.setValue(newdialogMap);
            }
        });

        Firebase messageRef=ref.child("Messages/textMessages");
        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, String> messageMap = new HashMap<String, String>();
                messageMap = dataSnapshot.getValue(Map.class);
                if (messageMap.get("ChatId").equals(senderChatID) || messageMap.get("ChatId").equals(recieverChatID)) {
                    messageMapList.add(messageMap);
                    MessageAdapter adapter = new MessageAdapter(messageMapList, ChatActivity.this, senderChatID, recieverChatID);
                    chatBox.setAdapter(adapter);
                    chatBox.setSelection(adapter.getCount());
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e("Child Changed", dataSnapshot.getValue().toString());
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

        Firebase Seenref=ref.child("LastSeen/"+dataStore.getCurrentUserID());
        Map<String,String> map=new HashMap<String,String>();
        map.put("LastSeen", "Online");
        Seenref.setValue(map);

        messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Firebase Seenref=ref.child("LastSeen/"+dataStore.getCurrentUserID());
                Map<String,String> map=new HashMap<String,String>();
                map.put("LastSeen", "typing....");
                Seenref.setValue(map);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Runnable runnable=new Runnable() {
                    @Override
                    public void run() {
                        Firebase Seenref=ref.child("LastSeen/"+dataStore.getCurrentUserID());
                        Map<String,String> map=new HashMap<String,String>();
                        map.put("LastSeen", "Online");
                        Seenref.setValue(map);
                    }
                };
                Handler handler=new Handler();
                handler.postDelayed(runnable,2000);
            }
        });

        Firebase lastSeenref=ref.child("LastSeen/"+dataStore.getOpponentUserID());
        lastSeenref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,String> map=new HashMap<String, String>();
                map=dataSnapshot.getValue(Map.class);
                try {
                    Log.e("DataChange",map.toString());
                    toolbarOnlineStatus.setText(map.get("LastSeen"));

                }
                catch (NullPointerException e)
                {
                    Log.e("NullPointerException",e.getMessage());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

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
    public void initialize()
    {
        Firebase.setAndroidContext(this);
        ref=new Firebase("https://texter10c.firebaseio.com");
        messageBox=(EditText)findViewById(R.id.messsageBox);
        sendButton=(ImageButton)findViewById(R.id.sendButton);
        messageMapList=new ArrayList<Map<String, String>>();
        chatBox=(ListView)findViewById(R.id.chatBox);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.getNavigationIcon();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolbatUserName=(TextView)findViewById(R.id.toolbarUserName);
        toolbarOnlineStatus=(TextView)findViewById(R.id.toolbarOnlineStatus);
        toolbatUserName.setText(dataStore.getOpponentUserName());
        toolbatUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChatActivity.this,UserProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
