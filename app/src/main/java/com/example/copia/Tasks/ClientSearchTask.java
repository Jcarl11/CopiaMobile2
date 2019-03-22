package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.example.copia.Adapters.ClientAdapter;
import com.example.copia.Entities.ClientEntity;
import com.example.copia.Utilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ClientSearchTask extends AsyncTask<Void, Void, List<ClientEntity>>
{
    private RecyclerView search_recyclerview;
    private ClientAdapter clientAdapter;
    private List<ClientEntity> clientEntityList;
    private boolean finished = false;
    private Context context;
    private String keyword;
    private AlertDialog dialog;
    public ClientSearchTask(Context context, String keyword, RecyclerView search_recyclerview) {
        clientEntityList = new ArrayList<>();
        this.search_recyclerview = search_recyclerview;
        this.keyword = keyword;
        this.context = context;
        dialog = Utilities.getInstance().showLoading(context, "Searching clients", false);
    }




    @Override
    protected List<ClientEntity> doInBackground(Void... voids) {
        String[] parameters = keyword.split(",");
        ParseQuery<ParseObject> getRemarksQuery = ParseQuery.getQuery("Notes");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");
        query.whereContainedIn("Tags", Arrays.asList(parameters));
        query.whereEqualTo("Deleted", false);
        try {
            List<ParseObject> objects = query.find();
            if(objects != null) {
                for(ParseObject object : objects)
                {
                    ClientEntity clientEntity = new ClientEntity();
                    clientEntity.setObjectId(object.getObjectId());
                    clientEntity.setRepresentative(object.getString("Representative"));
                    clientEntity.setPosition(object.getString("Position"));
                    clientEntity.setCompany(object.getString("Company"));
                    clientEntity.setIndustry(object.getString("Industry"));
                    clientEntity.setType(object.getString("Type"));
                    getRemarksQuery.whereEqualTo("Deleted", false);
                    getRemarksQuery.whereEqualTo("Parent", clientEntity.getObjectId());
                    int remark_count = getRemarksQuery.count();
                    clientEntity.setRemarkCount(String.valueOf(remark_count));
                    clientEntityList.add(clientEntity);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return clientEntityList;
    }

    @Override
    protected void onPreExecute() {dialog.show();}

    @Override
    protected void onPostExecute(List<ClientEntity> clientEntities) {
        dialog.dismiss();
        if(clientEntities.size() > 0)
        {
            clientAdapter = new ClientAdapter(context, clientEntities);
            search_recyclerview.setAdapter(clientAdapter);
        }
        else
            Utilities.getInstance().showAlertBox("Reponse", "0 records found", context);
    }

    public ClientAdapter getClientAdapter() {
        return clientAdapter;
    }
    public List<ClientEntity> getClientEntities() {return clientEntityList;}
}
