package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.example.copia.Adapters.NotesAdapter;
import com.example.copia.Entities.NotesEntity;
import com.example.copia.Utilities;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Notes_Retrieve_Deleted_Task extends AsyncTask<Void, Void, List<NotesEntity>> {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
    NotesAdapter notesAdapter;
    private Context context;
    private RecyclerView recyclerview;
    AlertDialog dialog;
    List<NotesEntity> notesEntities = new ArrayList<>();

    public Notes_Retrieve_Deleted_Task(Context context) {
        this.context = context;
        dialog = Utilities.getInstance().showLoading(context, "Please wait",false);
    }
    @Override
    protected List<NotesEntity> doInBackground(Void... voids) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Notes");
        query.whereEqualTo("Deleted", true);
        try
        {
            List<ParseObject> parseObjects = query.find();
            for(ParseObject object : parseObjects)
            {
                NotesEntity notesEntity = new NotesEntity();
                notesEntity.setObjectId(object.getObjectId());
                notesEntity.setCreatedAt(simpleDateFormat.format(object.getCreatedAt()));
                notesEntity.setRemark(object.getString("Remark"));
                notesEntities.add(notesEntity);
            }
        }
        catch (ParseException e) {e.printStackTrace();}
        return notesEntities;
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected void onPostExecute(List<NotesEntity> notesEntities) {
        dialog.dismiss();
        if(notesEntities.size() > 0)
        {
            notesAdapter = new NotesAdapter(context, notesEntities);
            recyclerview.setAdapter(notesAdapter);
        }
        else
            Utilities.getInstance().showAlertBox("Response", "0 Records found", context);
    }

    public NotesAdapter getNotesAdapter() {
        return notesAdapter;
    }

    public void setNotesAdapter(NotesAdapter notesAdapter) {
        this.notesAdapter = notesAdapter;
    }

    public RecyclerView getRecyclerview() {
        return recyclerview;
    }

    public void setRecyclerview(RecyclerView recyclerview) {
        this.recyclerview = recyclerview;
    }

    public List<NotesEntity> getNotesEntities() {
        return notesEntities;
    }

    public void setNotesEntities(List<NotesEntity> notesEntities) {
        this.notesEntities = notesEntities;
    }
}
