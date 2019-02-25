package com.example.copia.DatabaseOperation;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.example.copia.Utilities;

import java.util.ArrayList;
import java.util.List;

public class PermanentDeleteFilesTask extends AsyncTask<Void, Void, Boolean> {
    RecyclerView recyclerView;
    int pos;
    List<?> entity;
    ArrayList<Boolean> results = new ArrayList<>();
    private Context context;
    private String objectId;
    private String searchClass;
    AlertDialog dialog;
    PermanentDeleteFiles permanentDeleteFiles = new PermanentDeleteFiles();
    public PermanentDeleteFilesTask(Context context, String objectId, String searchClass) {
        this.context = context;
        this.objectId = objectId;
        this.searchClass = searchClass;
        dialog = Utilities.getInstance().showLoading(context, "Please wait", false);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if(searchClass.equalsIgnoreCase("Notes"))
            results.add(permanentDeleteFiles.permanent_delete_notes(objectId));
        else if(searchClass.equalsIgnoreCase("Images"))
            results.add(permanentDeleteFiles.permanent_delete_images(objectId));
        else if(searchClass.equalsIgnoreCase("PDFFiles"))
            results.add(permanentDeleteFiles.permanent_delete_file(objectId));
        return results.contains(false);
    }

    @Override
    protected void onPreExecute() {dialog.show();}

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        dialog.dismiss();
        if(aBoolean == false) {
            getEntity().remove(getPos());
            getRecyclerView().getAdapter().notifyItemRemoved(getPos());
            Utilities.getInstance().showAlertBox("Response", "Record deleted", context);
        }
        else
            Utilities.getInstance().showAlertBox("Response", "Error deleting record", context);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public List<?> getEntity() {
        return entity;
    }

    public void setEntity(List<?> entity) {
        this.entity = entity;
    }
}
