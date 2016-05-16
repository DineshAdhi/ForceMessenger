package com.example.dinesh.forcetexter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Dinesh on 04-04-2016.
 */
public class MessageAdapter extends BaseAdapter {

    ArrayList<Map<String,String>> messageListMap;
    LayoutInflater inflater;
    String senderChatId,opponentChatId;
    Context context;

    public MessageAdapter(ArrayList<Map<String,String>> messageListMap,Context context,String senderChatId,String opponentChatId)
    {
        this.messageListMap=messageListMap;
        this.senderChatId=senderChatId;
        this.opponentChatId=opponentChatId;
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return messageListMap.size();
    }

    @Override
    public Object getItem(int position) {
        return messageListMap.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=new ViewHolder();
        if(messageListMap.get(position).get("ChatId").equals(senderChatId))
        {
            convertView=inflater.inflate(R.layout.outgoing_bubble,null);
            viewHolder.messageText=(TextView)convertView.findViewById(R.id.outText);
            viewHolder.messageText.setText(messageListMap.get(position).get("Message"));
        }
        if(messageListMap.get(position).get("ChatId").equals(opponentChatId))
        {
            convertView=inflater.inflate(R.layout.incoming_bubble,null);
            viewHolder.messageText=(TextView)convertView.findViewById(R.id.inText);
            viewHolder.messageText.setText(messageListMap.get(position).get("Message"));
        }
        return convertView;
    }
    private static class ViewHolder {
        LinearLayout layout;
        RelativeLayout relativeLayout;
        TextView messageText;
    }
}


