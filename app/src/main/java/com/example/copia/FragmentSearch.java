package com.example.copia;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.copia.Entities.ClientEntity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener;

public class FragmentSearch extends Fragment
{
    ClientAdapter clientAdapter;
    List<ClientEntity> clientEntities;
    ArrayList<String> searchCategories = new ArrayList<>();
    EditText search_edittext_search;
    Spinner search_spinner_searchin;
    RecyclerView search_recyclerview;
    boolean option;
    int pos = -1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        List<ComboboxEntity> comboboxEntities = ComboboxEntity.findWithQuery(ComboboxEntity.class, "Select distinct category from combobox_entity");
        for(ComboboxEntity entity : comboboxEntities)
            searchCategories.add(entity.getCategory());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, searchCategories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        search_recyclerview = (RecyclerView)view.findViewById(R.id.search_recyclerview);
        search_recyclerview.setHasFixedSize(true);
        search_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        clientEntities = new ArrayList<>();
        option = false;
        search_recyclerview.setOnTouchListener(listener());
        search_spinner_searchin = (Spinner)view.findViewById(R.id.search_spinner_searchin);
        search_spinner_searchin.setAdapter(dataAdapter);
        search_edittext_search = (EditText)view.findViewById(R.id.search_edittext_search);
        search_edittext_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    new SearchTask().execute((Void)null);
                return false;
            }
        });
        return view;
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
                                        search_recyclerview.removeView(view);
                                        clientEntities.remove(pos);
                                        clientAdapter.notifyItemRemoved(pos);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("This record will be deleted permanently").setPositiveButton("Yes", dialogClickListener)
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
                        Utilities.getInstance().showAlertBox("Touched", clientEntities.get(i).getObjectId(), getContext());
                    }
                })
                .create();
        return listener;
    }
    private class SearchTask extends AsyncTask<Void, Void, List<ClientEntity>>
    {

        boolean finished = false;
        AlertDialog dialog;
        public SearchTask() {
            clientEntities = new ArrayList<>();
            dialog = new SpotsDialog.Builder()
                    .setMessage("Searching clients")
                    .setContext(getContext())
                    .setCancelable(false)
                    .build();
        }

        @Override
        protected List<ClientEntity> doInBackground(Void... voids) {
            String searchIn = search_spinner_searchin.getSelectedItem().toString();
            String searchKeywords = search_edittext_search.getText().toString().trim().toUpperCase();
            String[] parameters = searchKeywords.split(",");
            ParseQuery<ParseObject> query = ParseQuery.getQuery(searchIn);
            query.whereContainedIn("Tags", Arrays.asList(parameters));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null && objects != null)
                    {
                        for(ParseObject object : objects) {
                            ClientEntity clientEntity = new ClientEntity();
                            clientEntity.setObjectId(object.getObjectId());
                            clientEntity.setRepresentative(object.getString("Representative"));
                            clientEntity.setPosition(object.getString("Position"));
                            clientEntity.setCompany(object.getString("Company"));
                            clientEntity.setIndustry(object.getString("Industry"));
                            clientEntity.setType(object.getString("Type"));
                            clientEntities.add(clientEntity);
                        }
                        finished = true;
                    }
                    else
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
            return clientEntities;
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(List<ClientEntity> clientEntities) {
            dialog.dismiss();
            if(clientEntities.size() > 0)
            {
                clientAdapter = new ClientAdapter(getContext(), clientEntities);
                search_recyclerview.setAdapter(clientAdapter);
            }
            else
                Utilities.getInstance().showAlertBox("Response", "0 records found", getContext());
        }
    }
}
