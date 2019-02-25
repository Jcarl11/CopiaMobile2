package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.example.copia.DatabaseOperation.PermanentDeleteFiles;
import com.example.copia.DatabaseOperation.PermanentDeleteReference;
import com.example.copia.Utilities;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PermanentDeleteRecordsTask extends AsyncTask<Void, Void, Boolean> {
    int pos;
    RecyclerView recyclerView;
    List<?> entity;
    private Context context;
    private String objectId;
    private String searchClass;
    AlertDialog dialog;
    PermanentDeleteReference permanentDeleteReference = new PermanentDeleteReference();
    PermanentDeleteFiles permanentDeleteFiles = new PermanentDeleteFiles();
    public PermanentDeleteRecordsTask(Context context, String objectId, String searchClass) {
        this.context = context;
        this.objectId = objectId;
        this.searchClass = searchClass;
        dialog = Utilities.getInstance().showLoading(context, "Please wait", false);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean successful = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(()->permanentDeleteReference.factory(searchClass, objectId))
                                            .thenRunAsync(()->permanentDeleteFiles.permanent_delete_notes_withreference(objectId))
                                            .thenRunAsync(()->permanentDeleteFiles.permanent_delete_images_withreference(objectId))
                                            .thenRunAsync(()->permanentDeleteFiles.permanent_delete_file_withreference(objectId));
            try {
                completableFuture.get();
                successful = true;
            }
            catch (ExecutionException e) {e.printStackTrace();}
            catch (InterruptedException e) {e.printStackTrace();}
        }
        else
        {
            permanentDeleteReference.factory(searchClass, objectId);
            permanentDeleteFiles.permanent_delete_notes_withreference(objectId);
            permanentDeleteFiles.permanent_delete_images_withreference(objectId);
            permanentDeleteFiles.permanent_delete_file_withreference(objectId);
            successful = true;
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
        if(aBoolean == true) {
            getEntity().remove(getPos());
            getRecyclerView().getAdapter().notifyItemRemoved(getPos());
            Utilities.getInstance().showAlertBox("Response", "Record deleted", context);
        }
        else
            Utilities.getInstance().showAlertBox("Response", "Error deleting record", context);
    }

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

    public List<?> getEntity() {
        return entity;
    }

    public void setEntity(List<?> entity) {
        this.entity = entity;
    }
}
