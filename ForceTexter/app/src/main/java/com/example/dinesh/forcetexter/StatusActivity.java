package com.example.dinesh.forcetexter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class StatusActivity extends AppCompatActivity implements DataHolder{

    EditText statusField;
    Button statusUpdateButton;
    ListView statusListView;
    Firebase ref;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        initialize();
        statusUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statusField.getText().toString().equals(""))
                {
                    new AlertDialog.Builder(StatusActivity.this).setTitle("Empty Status")
                            .setMessage("Looks like you haven't typed in the status? Do you really want to set your status empty?").setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setStatus();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
                else
                {
                    setStatus();
                }


            }
        });
    }

    public void setStatus()
    {
        progressDialog.setMessage("Updating your Status");
        progressDialog.show();
        Firebase statusRef=ref.child("Status/"+dataStore.getCurrentUserID());
        statusRef.setValue(statusField.getText().toString(), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                progressDialog.dismiss();
                Snackbar.make(findViewById(android.R.id.content),"Updated your status",Snackbar.LENGTH_LONG)
                        .setAction("Go back", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void initialize() {
        Firebase.setAndroidContext(this);
        ref=new Firebase("https://texter10c.firebaseio.com");
        statusField=(EditText)findViewById(R.id.statusField);
        statusUpdateButton=(Button)findViewById(R.id.updateStatusbutton);
        statusListView=(ListView)findViewById(R.id.statusListView);
        final String[] status={"Busy","Having fun texting my friends","Available","At the movie",
                "Saw a beautiful girl today","Love is waste of time","Getting Drunk","I am awesome","Got placed in ZOHO",
                "Insanely romantic night I had today"};
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(StatusActivity.this,android.R.layout.simple_list_item_1,status);
        statusListView.setAdapter(adapter);
        statusListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                statusField.setText(adapter.getItem(position).toString());
            }
        });
        progressDialog=new ProgressDialog(this);


    }
}
