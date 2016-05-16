package com.example.dinesh.forcetexter;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Dinesh on 05-04-2016.
 */
public interface DataHolder
{

    DataStore dataStore=new DataStore();

    class DataStore
    {
        String currentUserMail;
        String currentUserID,OpponentUserID;
        String currentUserName;
        String OpponentUserName;
        ArrayList<String> generatedChatIds;
        ArrayList<String> userIDs;
        ArrayList<Map<String,String>> userDialogMap;
        ArrayList<Map<String,String>> userDetailsList;

        DataStore()
        {
            userIDs=new ArrayList<String>();
            generatedChatIds=new ArrayList<String>();
            userDialogMap=new ArrayList<Map<String,String>>();
            userDetailsList=new ArrayList<Map<String,String>>();
        }
        public void addCurrentUserEmail(String currentUserMail)
        {
            this.currentUserMail=currentUserMail;
        }
        public String getCurrentUserMail()
        {
            return this.currentUserMail;
        }
        public void addUserIds(String userId)
        {
            userIDs.add(userId);
        }
        public ArrayList<String> getUserIDs()
        {
            return userIDs;
        }
        public void setCurrentUserID(String currentUser)
        {
            this.currentUserID=currentUser;
        }

        public String getCurrentUserID()
        {
            return currentUserID;
        }
        public void setOpponentUserID(String currentUser)
        {
            this.OpponentUserID=currentUser;
        }

        public String getOpponentUserID()
        {
            return OpponentUserID;
        }
        public void setCurrentUserName(String name)
        {
           this.currentUserName=name;
        }
        public String getCurrentUserName()
        {
            return currentUserName;
        }
        public void setOpponentUserName(String name)
        {
            this.OpponentUserName=name;
        }
        public String getOpponentUserName()
        {
            return OpponentUserName;
        }
        public String ChatIDSender()
        {
            return currentUserID+OpponentUserID;
        }
        public String ChatIDReceiver()
        {
            return OpponentUserID+currentUserID;
        }
        public void generateChatId()
        {
            generatedChatIds=new ArrayList<String>();

            for (int i=0;i<userIDs.size();i++)
            {
                if(!generatedChatIds.contains(getCurrentUserID()+getUserIDs().get(i))) {
                    generatedChatIds.add(getCurrentUserID() + getUserIDs().get(i));
                }
            }
        }

    }

}
