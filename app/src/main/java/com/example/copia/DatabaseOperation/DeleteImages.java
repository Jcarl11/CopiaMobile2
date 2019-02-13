package com.example.copia.DatabaseOperation;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class DeleteImages
{
    boolean finished = false;
    public List<Boolean> client_images_delete(ParseQuery<ParseObject> reference)
    {
        List<Boolean> results = new ArrayList<>();
        finished = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
        query.include("ClientPointer");
        query.whereMatchesQuery("ClientPointer", reference);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects != null)
                {
                    for(ParseObject parseObject : objects)
                    {
                        try {
                            parseObject.delete();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
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
