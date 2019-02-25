package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.example.copia.Adapters.SpecificationsAdapter;
import com.example.copia.Entities.SpecificationsEntity;
import com.example.copia.Utilities;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class Specifications_Retrieve_Deleted_Task extends AsyncTask<Void, Void, List<SpecificationsEntity>> {
    SpecificationsAdapter specificationsAdapter;
    private Context context;
    private RecyclerView recyclerview;
    AlertDialog dialog;
    List<SpecificationsEntity> specificationsEntities = new ArrayList<>();

    public Specifications_Retrieve_Deleted_Task(Context context) {
        this.context = context;
        dialog = Utilities.getInstance().showLoading(context, "Please wait",false);
    }
    @Override
    protected List<SpecificationsEntity> doInBackground(Void... voids) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Specifications");
        query.whereEqualTo("Deleted", true);
        try
        {
            List<ParseObject> parseObjects = query.find();
            for(ParseObject object : parseObjects)
            {
                SpecificationsEntity specificationsEntity = new SpecificationsEntity();
                specificationsEntity.setObjectid(object.getObjectId());
                specificationsEntity.setTitle(object.getString("Title"));
                specificationsEntity.setDivision(object.getString("Division"));
                specificationsEntity.setSection(object.getString("Section"));
                specificationsEntity.setType(object.getString("Type"));
                specificationsEntity.setDocument(specificationsEntity.getDivision() + " - " + specificationsEntity.getSection() + " - " + specificationsEntity.getType());
                specificationsEntities.add(specificationsEntity);
            }
        }
        catch (ParseException e) {e.printStackTrace();}
        return specificationsEntities;
    }

    @Override
    protected void onPreExecute() {dialog.show();}

    @Override
    protected void onPostExecute(List<SpecificationsEntity> specificationsEntities) {
        dialog.dismiss();
        if(specificationsEntities.size() > 0)
        {
            specificationsAdapter = new SpecificationsAdapter(context, specificationsEntities);
            recyclerview.setAdapter(specificationsAdapter);
        }
        else
            Utilities.getInstance().showAlertBox("Response", "0 Records found", context);
    }

    public SpecificationsAdapter getSpecificationsAdapter() {
        return specificationsAdapter;
    }

    public void setSpecificationsAdapter(SpecificationsAdapter specificationsAdapter) {
        this.specificationsAdapter = specificationsAdapter;
    }

    public RecyclerView getRecyclerview() {
        return recyclerview;
    }

    public void setRecyclerview(RecyclerView recyclerview) {
        this.recyclerview = recyclerview;
    }

    public List<SpecificationsEntity> getSpecificationsEntities() {
        return specificationsEntities;
    }

    public void setSpecificationsEntities(List<SpecificationsEntity> specificationsEntities) {
        this.specificationsEntities = specificationsEntities;
    }
}
