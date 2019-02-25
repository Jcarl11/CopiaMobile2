package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.copia.Adapters.ClientAdapter;
import com.example.copia.Entities.ClientEntity;
import com.example.copia.Utilities;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class RestoreRecordsTask extends AsyncTask<Void, Void, Boolean>
{
    RecyclerView recyclerView;
    int pos;
    List<?> entities;
    List<ClientEntity> clientEntities;
    boolean successful = false;
    private Context context;
    private String objectId;
    private String searchClass;
    AlertDialog dialog;

    public RestoreRecordsTask(Context context, String objectId, String searchClass) {
        this.context = context;
        this.objectId = objectId;
        this.searchClass = searchClass;
        dialog = Utilities.getInstance().showLoading(context, "Restoring", false);
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(searchClass);
        try {
            ParseObject object = query.get(objectId);
            object.put("Deleted", false);
            object.save();
            successful = true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return successful;
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        dialog.dismiss();
        if(aBoolean == true)
        {
            getEntities().remove(getPos());
            //getClientEntities().remove(getPos());
            getRecyclerView().getAdapter().notifyItemRemoved(getPos());
        }
        else
            Utilities.getInstance().showAlertBox("Reponse", "Failed restoring record", context);
    }


    public List<?> getEntities() {
        return entities;
    }

    public void setEntities(List<?> entities) {
        this.entities = entities;
    }

    public List<ClientEntity> getClientEntities() {
        return clientEntities;
    }

    public void setClientEntities(List<ClientEntity> clientEntities) {
        this.clientEntities = clientEntities;
    }

    /*public ClientAdapter getClientAdapter() {
        return clientAdapter;
    }

    public void setClientAdapter(ClientAdapter clientAdapter) {
        this.clientAdapter = clientAdapter;
    }*/

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
}
