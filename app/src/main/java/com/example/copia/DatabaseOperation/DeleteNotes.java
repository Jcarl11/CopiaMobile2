package com.example.copia.DatabaseOperation;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class DeleteNotes
{
    boolean finished = false;
    public List<Boolean> client_notes_delete(String reference, List<Boolean> results)
    {
        finished = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Notes");
        query.whereEqualTo("Parent", reference);
        query.whereEqualTo("Deleted", false);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects != null)
                {
                    for(ParseObject parseObject : objects)
                    {
                        parseObject.put("Deleted", true);
                        parseObject.saveInBackground();
                    }
                    results.add(true);
                }
                else
                    results.add(false);
                finished = true;
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
        return results;
    }
    public List<Boolean> suppliers_notes_delete(String reference, List<Boolean> results)
    {
        finished = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Notes");
        query.whereEqualTo("Parent", reference);
        query.whereEqualTo("Deleted", false);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects != null)
                {
                    for(ParseObject parseObject : objects)
                    {
                        parseObject.put("Deleted", true);
                        parseObject.saveInBackground();
                    }
                    results.add(true);
                }
                else
                    results.add(false);
                finished = true;
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
        return results;
    }
}
