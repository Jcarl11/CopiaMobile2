package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.example.copia.Adapters.SuppliersAdapter;
import com.example.copia.Entities.SuppliersEntity;
import com.example.copia.Utilities;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class Suppliers_Retrieve_Deleted_Task extends AsyncTask<Void, Void, List<SuppliersEntity>> {
    SuppliersAdapter suppliersAdapter;
    private Context context;
    private RecyclerView recyclerview;
    AlertDialog dialog;
    List<SuppliersEntity> suppliersEntities = new ArrayList<>();

    public Suppliers_Retrieve_Deleted_Task(Context context) {
        this.context = context;
        dialog = Utilities.getInstance().showLoading(context, "Please wait",false);
    }
    @Override
    protected List<SuppliersEntity> doInBackground(Void... voids) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Suppliers");
        query.whereEqualTo("Deleted", true);
        try
        {
            List<ParseObject> parseObjects = query.find();
            for(ParseObject object : parseObjects)
            {
                SuppliersEntity suppliersEntity = new SuppliersEntity();
                suppliersEntity.setObjectId(object.getObjectId());
                suppliersEntity.setRepresentative(object.getString("Representative"));
                suppliersEntity.setPosition(object.getString("Position"));
                suppliersEntity.setCompany(object.getString("Company_Name"));
                suppliersEntity.setBrand(object.getString("Brand"));
                suppliersEntity.setDiscipline(object.getString("Discipline"));
                suppliersEntities.add(suppliersEntity);
            }
        }
        catch (ParseException e) {e.printStackTrace();}
        return suppliersEntities;
    }

    @Override
    protected void onPreExecute() {dialog.show();}

    @Override
    protected void onPostExecute(List<SuppliersEntity> suppliersEntities) {
        dialog.dismiss();
        if(suppliersEntities.size() > 0)
        {
            suppliersAdapter = new SuppliersAdapter(context, suppliersEntities);
            recyclerview.setAdapter(suppliersAdapter);
        }
        else
            Utilities.getInstance().showAlertBox("Response", "0 Records found", context);
    }

    public SuppliersAdapter getSuppliersAdapter() {
        return suppliersAdapter;
    }

    public void setSuppliersAdapter(SuppliersAdapter suppliersAdapter) {
        this.suppliersAdapter = suppliersAdapter;
    }

    public RecyclerView getRecyclerview() {
        return recyclerview;
    }

    public void setRecyclerview(RecyclerView recyclerview) {
        this.recyclerview = recyclerview;
    }

    public List<SuppliersEntity> getSuppliersEntities() {
        return suppliersEntities;
    }

    public void setSuppliersEntities(List<SuppliersEntity> suppliersEntities) {
        this.suppliersEntities = suppliersEntities;
    }
}
