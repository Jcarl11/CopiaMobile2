package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.example.copia.Adapters.ContractorsAdapter;
import com.example.copia.Entities.ContractorsEntity;
import com.example.copia.Utilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ContractorsSearchTask extends AsyncTask<Void, Void, List<ContractorsEntity>> {
    private RecyclerView search_recyclerview;
    private ContractorsAdapter contractorsAdapter;
    private List<ContractorsEntity> contractorsEntities;
    private boolean finished = false;
    private Context context;
    private String keyword;
    private AlertDialog dialog;

    public ContractorsSearchTask(RecyclerView search_recyclerview, Context context, String keyword) {
        contractorsEntities = new ArrayList<>();
        this.search_recyclerview = search_recyclerview;
        this.context = context;
        this.keyword = keyword;
        dialog = Utilities.getInstance().showLoading(context, "Searching contractors", false);
    }
    @Override
    protected List<ContractorsEntity> doInBackground(Void... voids) {
        String[] parameters = keyword.split(",");
        ParseQuery<ParseObject> getRemarksQuery = ParseQuery.getQuery("Notes");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Contractors");
        query.whereContainedIn("Tags", Arrays.asList(parameters));
        query.whereEqualTo("Deleted", false);
        try {
            List<ParseObject> objects = query.find();
            if(objects != null) {
                for(ParseObject object : objects)
                {
                    ContractorsEntity contractorsEntity = new ContractorsEntity();
                    contractorsEntity.setObjectId(object.getObjectId());
                    contractorsEntity.setRepresentative(object.getString("Representative"));
                    contractorsEntity.setPosition(object.getString("Position"));
                    contractorsEntity.setCompany(object.getString("Company"));
                    contractorsEntity.setSpecialization(object.getString("Specialization"));
                    contractorsEntity.setClassification(object.getString("Classification"));
                    contractorsEntity.setIndustry(object.getString("Industry"));
                    getRemarksQuery.whereEqualTo("Deleted", false);
                    getRemarksQuery.whereEqualTo("Parent", contractorsEntity.getObjectId());
                    int remark_count = getRemarksQuery.count();
                    contractorsEntity.setRemarkCount(String.valueOf(remark_count));
                    contractorsEntities.add(contractorsEntity);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
            search_recyclerview.setAdapter(contractorsAdapter);
        }
        else
            Utilities.getInstance().showAlertBox("Reponse", "0 records found", context);
    }

    public ContractorsAdapter getContractorsAdapter() {
        return contractorsAdapter;
    }

    public List<ContractorsEntity> getContractorsEntities() {
        return contractorsEntities;
    }
}
