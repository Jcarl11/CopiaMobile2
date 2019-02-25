package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.example.copia.Adapters.ContractorsAdapter;
import com.example.copia.Entities.ContractorsEntity;
import com.example.copia.Utilities;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class Contractors_Retrieve_Deleted_Task extends AsyncTask<Void, Void, List<ContractorsEntity>> {
    ContractorsAdapter contractorsAdapter;
    private Context context;
    private RecyclerView recyclerview;
    AlertDialog dialog;
    List<ContractorsEntity> contractorsEntities = new ArrayList<>();

    public Contractors_Retrieve_Deleted_Task(Context context) {
        this.context = context;
        dialog = Utilities.getInstance().showLoading(context, "Please wait",false);
    }
    @Override
    protected List<ContractorsEntity> doInBackground(Void... voids) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Contractors");
        query.whereEqualTo("Deleted", true);
        try
        {
            List<ParseObject> parseObjects = query.find();
            for(ParseObject object : parseObjects)
            {
                ContractorsEntity contractorsEntity = new ContractorsEntity();
                contractorsEntity.setObjectId(object.getObjectId());
                contractorsEntity.setRepresentative(object.getString("Representative"));
                contractorsEntity.setPosition(object.getString("Position"));
                contractorsEntity.setCompany(object.getString("Company"));
                contractorsEntity.setSpecialization(object.getString("Specialization"));
                contractorsEntity.setIndustry(object.getString("Industry"));
                contractorsEntity.setClassification(object.getString("Classification"));
                contractorsEntities.add(contractorsEntity);
            }
        }
        catch (ParseException e) {e.printStackTrace();}
        return contractorsEntities;
    }

    @Override
    protected void onPreExecute() {dialog.show();}

    @Override
    protected void onPostExecute(List<ContractorsEntity> contractorsEntities) {
        dialog.dismiss();
        if(contractorsEntities.size() > 0)
        {
            contractorsAdapter = new ContractorsAdapter(context, contractorsEntities);
            recyclerview.setAdapter(contractorsAdapter);
        }
        else
            Utilities.getInstance().showAlertBox("Response", "0 Records found", context);
    }

    public ContractorsAdapter getContractorsAdapter() {
        return contractorsAdapter;
    }

    public void setContractorsAdapter(ContractorsAdapter contractorsAdapter) {
        this.contractorsAdapter = contractorsAdapter;
    }

    public RecyclerView getRecyclerview() {
        return recyclerview;
    }

    public void setRecyclerview(RecyclerView recyclerview) {
        this.recyclerview = recyclerview;
    }

    public List<ContractorsEntity> getContractorsEntities() {
        return contractorsEntities;
    }

    public void setContractorsEntities(List<ContractorsEntity> contractorsEntities) {
        this.contractorsEntities = contractorsEntities;
    }
}
