package com.example.copia.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.copia.MainActivity;
import com.example.copia.R;
import com.example.copia.Utilities;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotes extends Fragment {
    NotesAdapter notesAdapter;
    List<NotesEntity> notesEntities;
    RecyclerView notes_recyclerview;
    int pos = -1;
    public FragmentNotes() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_fragment_notes, container, false);
        notesEntities = new ArrayList<>();
        notes_recyclerview = (RecyclerView)view.findViewById(R.id.notes_recyclerview);
        notes_recyclerview.setHasFixedSize(true);
        notes_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        notes_recyclerview.setOnTouchListener(notes_listener());
        String objectId = ((MainActivity)getActivity()).getObjectId();
        new NotesRetrieveTask(objectId).execute((Void)null);
        return view;
    }


    private SwipeDismissRecyclerViewTouchListener notes_listener()
    {
        SwipeDismissRecyclerViewTouchListener listener = new SwipeDismissRecyclerViewTouchListener.Builder(
                notes_recyclerview,
                new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        pos = position;
                        return true;
                    }

                    @Override
                    public void onDismiss(View view) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                switch (which)
                                {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        new DeleteSingleNote(notesEntities.get(pos).getObjectId()).execute((Void)null);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("This record will be deleted").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    }
                })
                .setIsVertical(false)
                .setItemTouchCallback(
                        new SwipeDismissRecyclerViewTouchListener.OnItemTouchCallBack() {
                            @Override
                            public void onTouch(int index) {
                                // Do what you want when item be touched
                            }
                        })
                .setItemClickCallback(new SwipeDismissRecyclerViewTouchListener.OnItemClickCallBack() {
                    @Override
                    public void onClick(int i) {
                        String[] choices = new String[]{"Preview", "Edit"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Choose what to to: ");
                        builder.setCancelable(true);
                        builder.setItems(choices, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                if(which == 0) {
                                }
                                else if(which == 1) {
                                }
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                })
                .create();
        return listener;
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
                    .setMessage("Loading Notes")
                    .setContext(getContext())
                    .setCancelable(false)
                    .build();
        }

        @Override
        protected List<NotesEntity> doInBackground(Void... voids) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Notes");
            query.whereEqualTo("Parent", objectId);
            query.whereEqualTo("Deleted", false);
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
    private class DeleteSingleNote extends AsyncTask<Void, Void, Boolean>
    {
        boolean successful = false;
        boolean finished = false;
        AlertDialog dialog;
        String objectId;

        public DeleteSingleNote(String objectId) {
            this.objectId = objectId;
            dialog = new SpotsDialog.Builder()
                    .setMessage("Deleting record")
                    .setContext(getContext())
                    .setCancelable(false)
                    .build();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Notes");
            query.getInBackground(objectId, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(e == null && object != null)
                    {
                        object.put("Deleted", true);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null)
                                    successful = true;
                                finished = true;
                            }
                        });
                    }

                }
            });
            while (finished == false)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return successful;
        }

        @Override
        protected void onPreExecute() {dialog.show();}

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            dialog.dismiss();
            if(aBoolean == true)
            {
                notesEntities.remove(pos);
                notesAdapter.notifyItemRemoved(pos);
                Utilities.getInstance().showAlertBox("Response", "Record deleted successfully", getContext());
            }
            else
                Utilities.getInstance().showAlertBox("Error", "Record was not deleted successfully", getContext());
        }
    }
}
