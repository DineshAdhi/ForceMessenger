package com.example.dinesh.forcetexter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dinesh on 03-04-2016.
 */
public class UserAdapter extends BaseAdapter implements DataHolder {

    List<Map<String,String>> mapList;
    Context context;
    LayoutInflater layoutInflater;
    Firebase ref;
    Bitmap myBitmap;

    public UserAdapter(List<Map<String,String>> map,Context context)
    {
        Firebase.setAndroidContext(context);
        ref=new Firebase("https://texter10c.firebaseio.com");
        this.mapList=map;
        this.context=context;
        layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return mapList.size();
    }

    @Override
    public Object getItem(int position) {
        return mapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder=new ViewHolder();
        convertView=layoutInflater.inflate(R.layout.xmluserslist,null);
        holder.usersName=(TextView)convertView.findViewById(R.id.usersNameID);
        holder.usersName.setText(mapList.get(position).get("Username"));
        holder.imageView=(ImageView)convertView.findViewById(R.id.imageView);
        holder.onlineStatus=(TextView)convertView.findViewById(R.id.onlineStatus);
        Firebase lastSeenref=ref.child("LastSeen/" + getOpponentId(position));
        lastSeenref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            Map<String,String> map=new HashMap<String, String>();
                map=dataSnapshot.getValue(Map.class);
                try {
                    if (map.get("LastSeen").equals("Online")) {
                        holder.onlineStatus.setText("Online");
                        holder.onlineStatus.setTextColor(Color.GREEN);
                    }
                    else {
                        holder.onlineStatus.setText("Offline");
                        holder.onlineStatus.setTextColor(Color.RED);
                    }
                }
                catch (NullPointerException e)
                {
                    holder.onlineStatus.setText("Offline");
                    holder.onlineStatus.setTextColor(Color.RED);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //try{
          //  if(!mapList.get(position).get("ImageURL").toString().equals(null))
            //{
            //    setImage(mapList.get(position).get("ImageURL").toString());
              //  holder.imageView.setImageBitmap(myBitmap);
           // }
        //}
        //catch (NullPointerException e)
       // {
          //  Log.e("ImageNullpointer",e.getMessage());
        //}

        return convertView;
    }

    private void setImage(final String imageURL) {
        AsyncTask<Void,Void,Bitmap> task=new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {

                try {
                    URL urlConnection=new URL(imageURL);
                    HttpURLConnection connection=(HttpURLConnection)urlConnection.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input=connection.getInputStream();
                     myBitmap= BitmapFactory.decodeStream(input);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
        task.execute();

    }

    public String getOpponentId(int position)
    {
        return mapList.get(position).get("UserId");
    }

    public String getOpponentName(int position)
    {
        return mapList.get(position).get("Username");
    }

    public class ViewHolder
    {
        TextView usersName;
        TextView onlineStatus;
        ImageView imageView;
        String imageString;
    }
}
