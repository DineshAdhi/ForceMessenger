package com.example.dinesh.forcetexter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dinesh on 07-04-2016.
 */
public class DialogAdapter extends BaseAdapter implements DataHolder{

    ArrayList<Map<String,String>> messageList;
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> generatedChatIds;
    ArrayList<String> filterUserIDs;
    Firebase ref;
    boolean isNetworkAvailable;

    DialogAdapter(ArrayList<Map<String,String>> messageList, Context cotext, boolean isNetWorkConnected)
    {
        ref=new Firebase("https://texter10c.firebaseio.com");
        this.messageList=messageList;
        layoutInflater=(LayoutInflater)cotext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dataStore.generateChatId();
        generatedChatIds=dataStore.generatedChatIds;
        filterUserIDs=new ArrayList<String>();
        this.isNetworkAvailable=isNetWorkConnected;

    }
    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ArrayList<Map<String,String>> filteredMap=new ArrayList<Map<String, String>>();
        final ViewHolder viewHolder=new ViewHolder();
        if (generatedChatIds.contains(messageList.get(position).get("DialogId")))
        {
            convertView=layoutInflater.inflate(R.layout.dialogxml,null);
            viewHolder.messageView=(TextView)convertView.findViewById(R.id.messageView);
            viewHolder.nameView=(TextView)convertView.findViewById(R.id.nameView);
            viewHolder.typingView=(TextView)convertView.findViewById(R.id.typingView);
            viewHolder.nameView.setText(messageList.get(position).get("RecieverName"));
            viewHolder.messageView.setText(messageList.get(position).get("LatestMessage"));
        }


        if(isNetworkAvailable)
        {
            Firebase messageRef=ref.child("ChatDialogs/"+messageList.get(position).get("DialogId"));
            messageRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, String> map = new HashMap<String, String>();
                    map = dataSnapshot.getValue(Map.class);
                    viewHolder.messageView.setText(map.get("LatestMessage"));
                    Log.e("MessageChange",map.get("LatestMessage")+"");
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }


        return convertView;

    }

    public String getOpponentName(int position)
    {
        return messageList.get(position).get("RecieverName");
    }

    public String getId(int position)
    {
        return messageList.get(position).get("RecieverId");
    }

    public class ViewHolder
    {
        TextView messageView,nameView,typingView;
    }
}
