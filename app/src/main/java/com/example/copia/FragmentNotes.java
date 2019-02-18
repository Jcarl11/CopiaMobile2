package com.example.copia;


import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.copia.Adapters.NotesAdapter;
import com.example.copia.Entities.NotesEntity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotes extends Fragment {
    NotesAdapter notesAdapter;
    List<NotesEntity> notesEntities;
    RecyclerView notes_recyclerview;
    public FragmentNotes() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_fragment_notes, container, false);
        notesEntities = new ArrayList<>();
        notes_recyclerview = (RecyclerView)view.findViewById(R.id.notes_recyclerview);
        notes_recyclerview.setHasFixedSize(true);
        notes_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        String objectId = ((MainActivity)getActivity()).getObjectId();
        new NotesRetrieveTask(objectId).execute((Void)null);
        return view;
    }

    private class NotesRetrieveTask extends AsyncTask<Void, Void, List<NotesEntity>>
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        boolean finished = false;
        AlertDialog dialog;
        String objectId = null;

        public NotesRetrieveTask(String objectId) {
            finished = false;
            this.objectId = objectId;
            notesEntities = new ArrayList<>();
            dialog = new SpotsDialog.Builder()
                    .setMessage("Loading Images")
                    .setContext(getContext())
                    .setCancelable(false)
                    .build();
        }

        @Override
        protected List<NotesEntity> doInBackground(Void... voids) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Notes");
            query.whereEqualTo("Parent", objectId);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null && objects != null)
                    {
                        for(ParseObject object : objects)
                        {
                            NotesEntity notesEntity = new NotesEntity();
                            notesEntity.setObjectId(object.getObjectId());
                            notesEntity.setCreatedAt(simpleDateFormat.format(object.getCreatedAt()));
                            notesEntity.setRemark(object.getString("Remark"));
                            notesEntities.add(notesEntity);
                        }
                    }
                    finished = true;
                }
            });
            while(finished == false)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return notesEntities;
        }

        @Override
        protected void onPreExecute() {dialog.show();}

        @Override
        protected void onPostExecute(List<NotesEntity> notesEntities) {
            dialog.dismiss();
            if(notesEntities.size() > 0)
            {
                notesAdapter = new NotesAdapter(getContext(), notesEntities);
                notes_recyclerview.setAdapter(notesAdapter);
            }
            else
                Utilities.getInstance().showAlertBox("Response", "0 Records Found", getContext());
        }
    }

}
