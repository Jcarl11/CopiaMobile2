package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.example.copia.Adapters.ConsultantsAdapter;
import com.example.copia.Entities.ConsultantsEntity;
import com.example.copia.Utilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ConsultantsSearchTask extends AsyncTask<Void, Void, List<ConsultantsEntity>> {
    private RecyclerView search_recyclerview;
    private ConsultantsAdapter consultantsAdapter;
    private List<ConsultantsEntity> consultantsEntities;
    private boolean finished = false;
    private Context context;
    private String keyword;
    private AlertDialog dialog;

    public ConsultantsSearchTask(RecyclerView search_recyclerview, Context context, String keyword) {
        consultantsEntities = new ArrayList<>();
        this.search_recyclerview = search_recyclerview;
        this.context = context;
        this.keyword = keyword;
        dialog = Utilities.getInstance().showLoading(context, "Searching consultants", false);
    }

    @Override
    protected List<ConsultantsEntity> doInBackground(Void... voids) {
        String[] parameters = keyword.split(",");
        ParseQuery<ParseObject> getRemarksQuery = ParseQuery.getQuery("Notes");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Consultants");
        query.whereContainedIn("Tags", Arrays.asList(parameters));
        query.whereEqualTo("Deleted", false);
        try {
            List<ParseObject> objects = query.find();
            if(objects != null) {
                for(ParseObject object : objects)
                {
                    ConsultantsEntity consultantsEntity = new ConsultantsEntity();
                    consultantsEntity.setObjectId(object.getObjectId());
                    consultantsEntity.setRepresentative(object.getString("Representative"));
                    consultantsEntity.setPosition(object.getString("Position"));
                    consultantsEntity.setCompany(object.getString("Company"));
                    consultantsEntity.setSpecialization(object.getString("Specialization"));
                    consultantsEntity.setClassification(object.getString("Classification"));
                    consultantsEntity.setIndustry(object.getString("Industry"));
                    getRemarksQuery.whereEqualTo("Deleted", false);
                    getRemarksQuery.whereEqualTo("Parent", consultantsEntity.getObjectId());
                    int remark_count = getRemarksQuery.count();
                    consultantsEntity.setRemarkCount(String.valueOf(remark_count));
                    consultantsEntities.add(consultantsEntity);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
            search_recyclerview.setAdapter(consultantsAdapter);
        }
        else
            Utilities.getInstance().showAlertBox("Reponse", "0 records found", context);
    }

    public ConsultantsAdapter getConsultantsAdapter() {
        return consultantsAdapter;
    }

    public List<ConsultantsEntity> getConsultantsEntities() {
        return consultantsEntities;
    }
}
