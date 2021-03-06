package com.example.copia.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.copia.Adapters.NotesAdapter;
import com.example.copia.EditActivities.NotesAddActivity;
import com.example.copia.EditActivities.NotesEditActivity;
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

import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotes extends Fragment implements View.OnClickListener {
    public static String NOTES_ENTITY = "NOTES_ENTITY";
    public static String NOTES_ENTITY_PARENT = "NOTES_ENTITY_PARENT";
    NotesAdapter notesAdapter;
    List<NotesEntity> notesEntities;
    RecyclerView notes_recyclerview;
    Button notes_add_add;
    int pos = -1;
    String objectId;
    public FragmentNotes() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_fragment_notes, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Notes");
        notesEntities = new ArrayList<>();
        notes_add_add = (Button)view.findViewById(R.id.notes_add_add);
        notes_recyclerview = (RecyclerView)view.findViewById(R.id.notes_recyclerview);
        notes_recyclerview.setHasFixedSize(true);
        notes_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        notes_recyclerview.setOnTouchListener(notes_listener());
        objectId = ((MainActivity)getActivity()).getObjectId();
        notes_add_add.setOnClickListener(this);
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
                                    Utilities.getInstance().showAlertBox(notesEntities.get(pos).getCreatedAt(), notesEntities.get(pos).getRemark(), getContext());
                                }
                                else if(which == 1) {
                                    NotesEntity note = notesEntities.get(pos);
                                    Intent intent = new Intent(getActivity(), NotesEditActivity.class);
                                    intent.putExtra(NOTES_ENTITY, note);
                                    startActivityForResult(intent, 1);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            if(resultCode == MainActivity.RESULT_OK )
            {
                notesEntities.remove(pos);
                notesAdapter.notifyItemRemoved(pos);
                notesEntities.add((NotesEntity) data.getSerializableExtra(NOTES_ENTITY));
                notesAdapter = new NotesAdapter(getContext(), notesEntities);
                notes_recyclerview.setAdapter(notesAdapter);
                Utilities.getInstance().showAlertBox("Response", "Record updated", getContext());
            }
            else
                Utilities.getInstance().showAlertBox("Response", "Update failed. Please try again", getContext());
        }
        else if(requestCode == 3)
        {
            if(resultCode == MainActivity.RESULT_OK)
            {
                notesEntities.add((NotesEntity) data.getSerializableExtra(NotesAddActivity.NOTE_ENTITY_NEWRECORD));
                notesAdapter = new NotesAdapter(getContext(), notesEntities);
                notes_recyclerview.setAdapter(notesAdapter);
            }
            else
                Utilities.getInstance().showAlertBox("Response", "No records were added", getContext());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.notes_add_add:
                Intent intent = new Intent(getActivity(), NotesAddActivity.class);
                intent.putExtra(NOTES_ENTITY_PARENT, objectId);
                startActivityForResult(intent, 3);
                break;
        }
    }

    private class NotesRetrieveTask extends AsyncTask<Void, Void, List<NotesEntity>>
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        boolean finished = false;
        AlertDialog dialog = Utilities.getInstance().showLoading(getContext(), "Loading Notes", false);
        String objectId = null;

        public NotesRetrieveTask(String objectId) {
            finished = false;
            this.objectId = objectId;
            notesEntities = new ArrayList<>();
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
        AlertDialog dialog = Utilities.getInstance().showLoading(getContext(), "Deleting record", false);
        String objectId;

        public DeleteSingleNote(String objectId) {
            this.objectId = objectId;
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
