package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.copia.Adapters.ClientAdapter;
import com.example.copia.Entities.ClientEntity;
import com.example.copia.Fragments.FragmentDeletedFiles;
import com.example.copia.Utilities;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class Client_Retrieve_Deleted_Task extends AsyncTask<Void, Void, List<ClientEntity>> {
    ClientAdapter clientAdapter;
    private Context context;
    private RecyclerView client_recyclerview;
    AlertDialog dialog;
    List<ClientEntity> clientEntities = new ArrayList<>();

    public Client_Retrieve_Deleted_Task(Context context) {
        this.context = context;
        dialog = Utilities.getInstance().showLoading(context, "Please wait",false);
    }

    @Override
    protected List<ClientEntity> doInBackground(Void... voids) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");
        query.whereEqualTo("Deleted", true);
        try
        {
            List<ParseObject> parseObjects = query.find();
            for(ParseObject object : parseObjects)
            {
                ClientEntity clientEntity = new ClientEntity();
                clientEntity.setObjectId(object.getObjectId());
                clientEntity.setRepresentative(object.getString("Representative"));
                clientEntity.setPosition(object.getString("Position"));
                clientEntity.setCompany(object.getString("Company"));
                clientEntity.setIndustry(object.getString("Industry"));
                clientEntity.setType(object.getString("Type"));
                clientEntities.add(clientEntity);
            }
        }
        catch (ParseException e) {e.printStackTrace();}
        return clientEntities;
    }

    @Override
    protected void onPreExecute() {dialog.show();}

    @Override
    protected void onPostExecute(List<ClientEntity> clientEntities) {
        dialog.dismiss();
        if(clientEntities.size() > 0)
        {
            clientAdapter = new ClientAdapter(context, clientEntities);
            client_recyclerview.setAdapter(clientAdapter);
        }
        else
            Utilities.getInstance().showAlertBox("Response", "0 Records found", context);
    }

    public ClientAdapter getClientAdapter() {
        return clientAdapter;
    }

    public void setClientAdapter(ClientAdapter clientAdapter) {
        this.clientAdapter = clientAdapter;
    }

    public List<ClientEntity> getClientEntities() {
        return clientEntities;
    }

    public void setClientEntities(List<ClientEntity> clientEntities) {
        this.clientEntities = clientEntities;
    }

    public RecyclerView getClient_recyclerview() {
        return client_recyclerview;
    }

    public void setClient_recyclerview(RecyclerView client_recyclerview) {
        this.client_recyclerview = client_recyclerview;
    }
}
