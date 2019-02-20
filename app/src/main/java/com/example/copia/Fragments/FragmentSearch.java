package com.example.copia.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.copia.Adapters.ClientAdapter;
import com.example.copia.Entities.ComboboxEntity;
import com.example.copia.DatabaseOperation.DeleteFiles;
import com.example.copia.DatabaseOperation.DeleteImages;
import com.example.copia.DatabaseOperation.DeleteNotes;
import com.example.copia.DatabaseOperation.RetreiveReference;
import com.example.copia.Entities.ClientEntity;
import com.example.copia.MainActivity;
import com.example.copia.R;
import com.example.copia.SearchTasks.ClientSearchTask;
import com.example.copia.Utilities;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import dmax.dialog.SpotsDialog;
import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener;


public class FragmentSearch extends Fragment
{
    private ClientAdapter clientAdapter;
    private List<ClientEntity> clientEntities;
    EditText search_edittext_search;
    Spinner search_spinner_searchin;
    RecyclerView search_recyclerview;
    boolean option;
    int pos = -1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ArrayList<String> searchCategories = new ArrayList<>();
        List<ComboboxEntity> comboboxEntities = ComboboxEntity.findWithQuery(ComboboxEntity.class, "Select distinct category from combobox_entity");
        for(ComboboxEntity entity : comboboxEntities)
            searchCategories.add(entity.getCategory());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, searchCategories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        search_recyclerview = (RecyclerView)view.findViewById(R.id.search_recyclerview);
        search_recyclerview.setHasFixedSize(true);
        search_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        option = false;
        search_recyclerview.setOnTouchListener(listener());
        search_spinner_searchin = (Spinner)view.findViewById(R.id.search_spinner_searchin);
        search_spinner_searchin.setAdapter(dataAdapter);
        search_edittext_search = (EditText)view.findViewById(R.id.search_edittext_search);
        search_edittext_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String keyword = search_edittext_search.getText().toString().trim().toUpperCase();
                    String searchin = search_spinner_searchin.getSelectedItem().toString();
                    if(searchin.equalsIgnoreCase("Client"))
                    {
                        ClientSearchTask clientSearchTask = new ClientSearchTask(getContext(), keyword, search_recyclerview);
                        clientSearchTask.execute((Void)null);
                        clientAdapter = new ClientAdapter(getContext(), clientSearchTask.getClientEntities());
                        clientEntities = clientSearchTask.getClientEntities();
                        //clientAdapter = clientSearchTask.getClientAdapter();
                    }

                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        search_recyclerview.setAdapter(clientAdapter);
    }

    private SwipeDismissRecyclerViewTouchListener listener()
    {
        SwipeDismissRecyclerViewTouchListener listener = new SwipeDismissRecyclerViewTouchListener.Builder(
                search_recyclerview,
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
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        new DeleteClientTask(clientEntities.get(pos).getObjectId()).execute((Void)null);
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
                        String[] choices = new String[]{"Notes", "Image Files", "PDF Files"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Choose what to show: ");
                        builder.setCancelable(true);
                        builder.setItems(choices, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0) {
                                    dialog.dismiss();
                                    ((MainActivity)getActivity()).setObjectId(clientEntities.get(pos).getObjectId());
                                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentNotes()).addToBackStack(null).commit();
                                }
                                else if(which == 1) {
                                    dialog.dismiss();
                                    ((MainActivity)getActivity()).setObjectId(clientEntities.get(pos).getObjectId());
                                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentImages()).addToBackStack(null).commit();
                                }
                                else if(which == 2) {
                                    dialog.dismiss();
                                    ((MainActivity)getActivity()).setObjectId(clientEntities.get(pos).getObjectId());
                                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentPdf()).addToBackStack(null).commit();
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
    private class DeleteClientTask extends AsyncTask<Void, Void, Boolean>
    {
        RetreiveReference retreiveReference = new RetreiveReference();
        DeleteImages deleteImages = new DeleteImages();
        DeleteNotes deleteNotes = new DeleteNotes();
        DeleteFiles deleteFiles = new DeleteFiles();
        String objID;
        AlertDialog dialog;
        public DeleteClientTask(String objID) {
            this.objID = objID;
            dialog = new SpotsDialog.Builder()
                    .setMessage("Deleting references")
                    .setContext(getContext())
                    .setCancelable(false)
                    .build();
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            List<Boolean> results = new ArrayList<>();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                CompletableFuture<List<Boolean>> cf = CompletableFuture.runAsync(()->retreiveReference.client_retrieve(objID))
                                            .supplyAsync(()->deleteImages.client_images_delete(objID))
                                            .thenApplyAsync(data->deleteNotes.client_notes_delete(objID,data))
                                            .thenApplyAsync(data->deleteFiles.client_files_delete(objID,data));
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
                List<Boolean> first = deleteImages.client_images_delete(objID);
                List<Boolean> second = deleteNotes.client_notes_delete(objID, first);
                results = deleteFiles.client_files_delete(objID,second);
            }

            return results.contains(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            dialog.dismiss();
            if(aBoolean == false)
            {
                clientEntities.remove(pos);
                clientAdapter.notifyItemRemoved(pos);
                Utilities.getInstance().showAlertBox("Successful", "Record deleted", getContext());
            }
            else
                Utilities.getInstance().showAlertBox("Error", "Some of the references were not\ndeleted properly. Please retry the deletion process", getContext());
        }
    }
}
