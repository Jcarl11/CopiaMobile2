package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.example.copia.Adapters.ConsultantsAdapter;
import com.example.copia.Entities.ConsultantsEntity;
import com.example.copia.Entities.ContractorsEntity;
import com.example.copia.Utilities;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class Consultants_Retrieve_Deleted_Task extends AsyncTask<Void, Void, List<ConsultantsEntity>> {
    ConsultantsAdapter consultantsAdapter;
    private Context context;
    private RecyclerView recyclerview;
    AlertDialog dialog;
    List<ConsultantsEntity> consultantsEntities = new ArrayList<>();

    public Consultants_Retrieve_Deleted_Task(Context context) {
        this.context = context;
        dialog = Utilities.getInstance().showLoading(context, "Please wait",false);
    }
    @Override
    protected List<ConsultantsEntity> doInBackground(Void... voids) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Consultants");
        query.whereEqualTo("Deleted", true);
        try
        {
            List<ParseObject> parseObjects = query.find();
            for(ParseObject object : parseObjects)
            {
                ConsultantsEntity consultantsEntity = new ConsultantsEntity();
                consultantsEntity.setObjectId(object.getObjectId());
                consultantsEntity.setRepresentative(object.getString("Representative"));
                consultantsEntity.setPosition(object.getString("Position"));
                consultantsEntity.setCompany(object.getString("Company"));
                consultantsEntity.setSpecialization(object.getString("Specialization"));
                consultantsEntity.setIndustry(object.getString("Industry"));
                consultantsEntity.setClassification(object.getString("Classification"));
                consultantsEntities.add(consultantsEntity);
            }
        }
        catch (ParseException e) {e.printStackTrace();}
        return consultantsEntities;
    }

    @Override
    protected void onPreExecute() {dialog.show();}

    @Override
    protected void onPostExecute(List<ConsultantsEntity> consultantsEntities) {
        dialog.dismiss();
        if(consultantsEntities.size() > 0)
        {
            consultantsAdapter = new ConsultantsAdapter(context, consultantsEntities);
            recyclerview.setAdapter(consultantsAdapter);
        }
        else
            Utilities.getInstance().showAlertBox("Response", "0 Records found", context);
    }

    public ConsultantsAdapter getConsultantsAdapter() {
        return consultantsAdapter;
    }

    public void setConsultantsAdapter(ConsultantsAdapter consultantsAdapter) {
        this.consultantsAdapter = consultantsAdapter;
    }

    public RecyclerView getRecyclerview() {
        return recyclerview;
    }

    public void setRecyclerview(RecyclerView recyclerview) {
        this.recyclerview = recyclerview;
    }

    public List<ConsultantsEntity> getConsultantsEntities() {
        return consultantsEntities;
    }

    public void setConsultantsEntities(List<ConsultantsEntity> consultantsEntities) {
        this.consultantsEntities = consultantsEntities;
    }
}
