package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.example.copia.Adapters.SuppliersAdapter;
import com.example.copia.Entities.SuppliersEntity;
import com.example.copia.Utilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class SuppliersSearchTask extends AsyncTask<Void, Void, List<SuppliersEntity>> {
    private RecyclerView search_recyclerview;
    private SuppliersAdapter suppliersAdapter;
    private List<SuppliersEntity> suppliersEntities;
    private boolean finished = false;
    private Context context;
    private String keyword;
    private AlertDialog dialog;

    public SuppliersSearchTask(RecyclerView search_recyclerview, Context context, String keyword) {
        suppliersEntities = new ArrayList<>();
        this.search_recyclerview = search_recyclerview;
        this.context = context;
        this.keyword = keyword;
        dialog = Utilities.getInstance().showLoading(context, "Searching suppliers", false);
    }

    @Override
    protected List<SuppliersEntity> doInBackground(Void... voids) {
        String[] parameters = keyword.split(",");
        ParseQuery<ParseObject> getRemarksQuery = ParseQuery.getQuery("Notes");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Suppliers");
        query.whereContainedIn("Tags", Arrays.asList(parameters));
        query.whereEqualTo("Deleted", false);
        try {
            List<ParseObject> objects = query.find();
            if(objects != null) {
                for(ParseObject object : objects)
                {
                    SuppliersEntity suppliersEntity = new SuppliersEntity();
                    suppliersEntity.setObjectId(object.getObjectId());
                    suppliersEntity.setRepresentative(object.getString("Representative"));
                    suppliersEntity.setPosition(object.getString("Position"));
                    suppliersEntity.setCompany(object.getString("Company_Name"));
                    suppliersEntity.setBrand(object.getString("Brand"));
                    suppliersEntity.setIndustry(object.getString("Industry"));
                    suppliersEntity.setType(object.getString("Type"));
                    getRemarksQuery.whereEqualTo("Deleted", false);
                    getRemarksQuery.whereEqualTo("Parent", suppliersEntity.getObjectId());
                    int remark_count = getRemarksQuery.count();
                    suppliersEntity.setRemarkCount(String.valueOf(remark_count));
                    suppliersEntities.add(suppliersEntity);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
            search_recyclerview.setAdapter(suppliersAdapter);
        }
        else
            Utilities.getInstance().showAlertBox("Reponse", "0 records found", context);
    }

    public SuppliersAdapter getSuppliersAdapter() {return suppliersAdapter;}
    public List<SuppliersEntity> getSuppliersEntities() {return suppliersEntities;}
}
