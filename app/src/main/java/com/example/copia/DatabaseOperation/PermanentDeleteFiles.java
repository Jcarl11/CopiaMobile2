package com.example.copia.DatabaseOperation;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class PermanentDeleteFiles
{
    public void permanent_delete_notes_withreference(String objectId)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Notes");
        query.whereEqualTo("Parent", objectId);
        query.whereEqualTo("Deleted", true);
        try {
            List<ParseObject> parseObjects = query.find();
            for(ParseObject parseObject : parseObjects)
                parseObject.delete();
        } catch (ParseException e) {e.printStackTrace();}
    }
    public boolean permanent_delete_notes(String objectId)
    {
        boolean successful = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Notes");
        try {
            ParseObject parseObject = query.get(objectId);
            parseObject.delete();
            successful = true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return successful;
    }
    public void permanent_delete_images_withreference(String objectId)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
        query.whereEqualTo("Parent", objectId);
        query.whereEqualTo("Deleted", true);
        try {
            List<ParseObject> parseObjects = query.find();
            for(ParseObject parseObject : parseObjects)
                parseObject.delete();
        } catch (ParseException e) {e.printStackTrace();}
    }
    public boolean permanent_delete_images(String objectId)
    {
        boolean successful = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
        try {
            ParseObject object = query.get(objectId);
            object.delete();
            successful = true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return successful;
    }
    public void permanent_delete_file_withreference(String objectId)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("PDFFiles");
        query.whereEqualTo("Parent", objectId);
        query.whereEqualTo("Deleted", true);
        try {
            List<ParseObject> parseObjects = query.find();
            for(ParseObject parseObject : parseObjects)
                parseObject.delete();
        } catch (ParseException e) {e.printStackTrace();}
    }
    public boolean permanent_delete_file(String objectId)
    {
        boolean successful = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("PDFFiles");
        try {
            ParseObject parseObject = query.get(objectId);
            parseObject.delete();
            successful = true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return successful;
    }
}
