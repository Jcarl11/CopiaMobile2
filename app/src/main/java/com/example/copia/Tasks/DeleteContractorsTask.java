package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.copia.Adapters.ContractorsAdapter;
import com.example.copia.DatabaseOperation.DeleteFiles;
import com.example.copia.DatabaseOperation.DeleteImages;
import com.example.copia.DatabaseOperation.DeleteNotes;
import com.example.copia.DatabaseOperation.DeleteReference;
import com.example.copia.Entities.ContractorsEntity;
import com.example.copia.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import dmax.dialog.SpotsDialog;

public class DeleteContractorsTask extends AsyncTask<Void, Void, Boolean> {
    int pos;
    List<ContractorsEntity> contractorsEntities;
    ContractorsAdapter contractorsAdapter;
    DeleteReference deleteReference = new DeleteReference();
    DeleteImages deleteImages = new DeleteImages();
    DeleteNotes deleteNotes = new DeleteNotes();
    DeleteFiles deleteFiles = new DeleteFiles();
    String objID;
    Context context;
    AlertDialog dialog;

    public DeleteContractorsTask(String objID, Context context) {
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
            CompletableFuture<List<Boolean>> cf = CompletableFuture.runAsync(()->deleteReference.contractors_delete(objID))
                    .supplyAsync(()->deleteImages.contractors_images_delete(objID))
                    .thenApplyAsync(data->deleteNotes.contractors_notes_delete(objID,data))
                    .thenApplyAsync(data->deleteFiles.contractors_files_delete(objID,data));
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
            deleteReference.contractors_delete(objID);
            List<Boolean> first = deleteImages.contractors_images_delete(objID);
            List<Boolean> second = deleteNotes.contractors_notes_delete(objID, first);
            results = deleteFiles.contractors_files_delete(objID,second);
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
            contractorsEntities.remove(pos);
            contractorsAdapter.notifyItemRemoved(pos);
            Utilities.getInstance().showAlertBox("Successful", "Record deleted", context);
        }
        else
            Utilities.getInstance().showAlertBox("Error", "Some of the references were not\ndeleted properly. Please retry the deletion process", context);
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setContractorsEntities(List<ContractorsEntity> contractorsEntities) {
        this.contractorsEntities = contractorsEntities;
    }

    public void setContractorsAdapter(ContractorsAdapter contractorsAdapter) {
        this.contractorsAdapter = contractorsAdapter;
    }
}
