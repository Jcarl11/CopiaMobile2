package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.example.copia.Adapters.SpecificationsAdapter;
import com.example.copia.Entities.SpecificationsEntity;
import com.example.copia.Utilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class SpecificationsSearchTask extends AsyncTask<Void, Void, List<SpecificationsEntity>> {
    private RecyclerView search_recyclerview;
    private SpecificationsAdapter specificationsAdapter;
    private List<SpecificationsEntity> specificationsEntities;
    private boolean finished = false;
    private Context context;
    private String keyword;
    private AlertDialog dialog;

    public SpecificationsSearchTask(RecyclerView search_recyclerview, Context context, String keyword) {
        specificationsEntities = new ArrayList<>();
        this.search_recyclerview = search_recyclerview;
        this.context = context;
        this.keyword = keyword;
        dialog = new SpotsDialog.Builder()
                .setMessage("Searching specifications")
                .setContext(context)
                .setCancelable(false)
                .build();
    }
    @Override
    protected List<SpecificationsEntity> doInBackground(Void... voids) {
        String[] parameters = keyword.split(",");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Specifications");
        query.whereContainedIn("Tags", Arrays.asList(parameters));
        query.whereEqualTo("Deleted", false);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(objects != null && e == null)
                {
                    for(ParseObject object : objects)
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
            search_recyclerview.setAdapter(specificationsAdapter);
        }
        else
            Utilities.getInstance().showAlertBox("Reponse", "0 records found", context);
    }

    public SpecificationsAdapter getSpecificationsAdapter() {
        return specificationsAdapter;
    }

    public List<SpecificationsEntity> getSpecificationsEntities() {
        return specificationsEntities;
    }
}
