package com.example.copia.DatabaseOperation;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class DeleteReference
{
    boolean finished = false;
    public void client_delete(String objectId)
    {
        finished = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null && object != null)
                {
                    object.put("Deleted", true);
                    object.saveInBackground();
                }
            }
        });
        while(finished == false)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void suppliers_delete(String objectId)
    {
        finished = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Suppliers");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null && object != null)
                {
                    object.put("Deleted", true);
                    object.saveInBackground();
                }
            }
        });
        while(finished == false)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void contractors_delete(String objectId)
    {
        finished = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Contractors");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null && object != null)
                {
                    object.put("Deleted", true);
                    object.saveInBackground();
                }
            }
        });
        while(finished == false)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
