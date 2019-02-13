package com.example.copia;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.List;

import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener;

public class FragmentSearch extends Fragment
{
    ArrayList<String> searchCategories = new ArrayList<>();
    EditText search_edittext_search;
    Spinner search_spinner_searchin;
    RecyclerView search_recyclerview;
    ClientAdapter clientAdapter;
    List<ClientEntity> clientEntities;
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
        clientEntities = new ArrayList<>();
        search_recyclerview = (RecyclerView)view.findViewById(R.id.search_recyclerview);
        search_recyclerview.setHasFixedSize(true);
        search_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        clientEntities.add(new ClientEntity("Joey", "Janitor", "Mapua", "Food and Beverage", "Government"));
        clientEntities.add(new ClientEntity("Ramon", "Janitor", "Mapua", "Food and Beverage", "Government"));
        clientEntities.add(new ClientEntity("Juan", "Janitor", "Mapua", "Food and Beverage", "Government"));
        clientEntities.add(new ClientEntity("Ana", "Janitor", "Mapua", "Food and Beverage", "Government"));
        clientEntities.add(new ClientEntity("Pedro", "Janitor", "Mapua", "Food and Beverage", "Government"));
        clientEntities.add(new ClientEntity("John", "Janitor", "Mapua", "Food and Beverage", "Government"));
        clientEntities.add(new ClientEntity("Mike", "Janitor", "Mapua", "Food and Beverage", "Government"));
        clientEntities.add(new ClientEntity("Randy", "Janitor", "Mapua", "Food and Beverage", "Government"));
        clientEntities.add(new ClientEntity("Sam", "Janitor", "Mapua", "Food and Beverage", "Government"));
        clientAdapter = new ClientAdapter(getContext(), clientEntities);
        search_recyclerview.setAdapter(clientAdapter);
        option = false;
        search_recyclerview.setOnTouchListener(listener());
        search_spinner_searchin = (Spinner)view.findViewById(R.id.search_spinner_searchin);
        search_spinner_searchin.setAdapter(dataAdapter);
        search_edittext_search = (EditText)view.findViewById(R.id.search_edittext_search);
        search_edittext_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    Utilities.getInstance().showAlertBox("Sample", "Sample", getContext());
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
                        Utilities.getInstance().showAlertBox("Touched", "Row Touched", getContext());
                    }
                })
                .create();
        return listener;
    }
}
