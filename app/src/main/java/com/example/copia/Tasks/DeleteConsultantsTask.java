package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.copia.Adapters.ConsultantsAdapter;
import com.example.copia.DatabaseOperation.DeleteFiles;
import com.example.copia.DatabaseOperation.DeleteImages;
import com.example.copia.DatabaseOperation.DeleteNotes;
import com.example.copia.DatabaseOperation.DeleteReference;
import com.example.copia.Entities.ConsultantsEntity;
import com.example.copia.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import dmax.dialog.SpotsDialog;

public class DeleteConsultantsTask extends AsyncTask<Void, Void, Boolean>
{
    int pos;
    List<ConsultantsEntity> consultantsEntities;
    ConsultantsAdapter consultantsAdapter;
    DeleteReference deleteReference = new DeleteReference();
    DeleteImages deleteImages = new DeleteImages();
    DeleteNotes deleteNotes = new DeleteNotes();
    DeleteFiles deleteFiles = new DeleteFiles();
    String objID;
    Context context;
    AlertDialog dialog;

    public DeleteConsultantsTask(String objID, Context context) {
        this.objID = objID;
        this.context = context;
        dialog = new SpotsDialog.Builder()
                .setMessage("Deleting references")
                .setContext(context)
                .setCancelable(false)
                .build();
    }
    @Override
    protected Boolean doInBackground(Void... voids) {
        List<Boolean> results = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            CompletableFuture<List<Boolean>> cf = CompletableFuture.runAsync(()->deleteReference.consultants_delete(objID))
                    .supplyAsync(()->deleteImages.consultants_images_delete(objID))
                    .thenApplyAsync(data->deleteNotes.consultants_notes_delete(objID,data))
                    .thenApplyAsync(data->deleteFiles.consultants_files_delete(objID,data));
            try {
                results = cf.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else
        {
            deleteReference.consultants_delete(objID);
            List<Boolean> first = deleteImages.consultants_images_delete(objID);
            List<Boolean> second = deleteNotes.consultants_notes_delete(objID, first);
            results = deleteFiles.consultants_files_delete(objID,second);
        }

        return results.contains(false);
    }

    @Override
    protected void onPreExecute() {dialog.show();}

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        dialog.dismiss();
        if(aBoolean == false)
        {
            consultantsEntities.remove(pos);
            consultantsAdapter.notifyItemRemoved(pos);
            Utilities.getInstance().showAlertBox("Successful", "Record deleted", context);
        }
        else
            Utilities.getInstance().showAlertBox("Error", "Some of the references were not\ndeleted properly. Please retry the deletion process", context);
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setConsultantsEntities(List<ConsultantsEntity> consultantsEntities) {
        this.consultantsEntities = consultantsEntities;
    }

    public void setConsultantsAdapter(ConsultantsAdapter consultantsAdapter) {
        this.consultantsAdapter = consultantsAdapter;
    }
}
