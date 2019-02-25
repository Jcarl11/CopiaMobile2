package com.example.copia.DatabaseOperation;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class PermanentDeleteReference 
{
    public void factory(String searchClass, String objectId)
    {
        if(searchClass.equalsIgnoreCase("Client"))
            permanent_client_delete(objectId);
        else if(searchClass.equalsIgnoreCase("Suppliers"))
            permanent_suppliers_delete(objectId);
        else if(searchClass.equalsIgnoreCase("Contractors"))
            permanent_contractors_delete(objectId);
        else if(searchClass.equalsIgnoreCase("Consultants"))
            permanent_consultants_delete(objectId);
        else if(searchClass.equalsIgnoreCase("Specifications"))
            permanent_specifications_delete(objectId);
    }
    public void permanent_client_delete(String objectId)
    {
        boolean successful = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");
        try {
            ParseObject parseObject = query.get(objectId);
            parseObject.delete();
            successful = true;
        } catch (ParseException e) {e.printStackTrace();}
    }
    public void permanent_suppliers_delete(String objectId)
    {
        boolean successful = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Suppliers");
        try {
            ParseObject parseObject = query.get(objectId);
            parseObject.delete();
            successful = true;
        } catch (ParseException e) {e.printStackTrace();}
    }
    public void permanent_contractors_delete(String objectId)
    {
        boolean successful = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Contractors");
        try {
            ParseObject parseObject = query.get(objectId);
            parseObject.delete();
            successful = true;
        } catch (ParseException e) {e.printStackTrace();}
    }
    public void permanent_consultants_delete(String objectId)
    {
        boolean successful = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Consultants");
        try {
            ParseObject parseObject = query.get(objectId);
            parseObject.delete();
            successful = true;
        } catch (ParseException e) {e.printStackTrace();}
    }
    public void permanent_specifications_delete(String objectId)
    {
        boolean successful = false;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Specifications");
        try {
            ParseObject parseObject = query.get(objectId);
            parseObject.delete();
            successful = true;
        } catch (ParseException e) {e.printStackTrace();}
    }
}
