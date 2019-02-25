package com.example.copia.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.copia.Adapters.ClientAdapter;
import com.example.copia.Adapters.ConsultantsAdapter;
import com.example.copia.Adapters.ContractorsAdapter;
import com.example.copia.Adapters.SpecificationsAdapter;
import com.example.copia.Adapters.SuppliersAdapter;
import com.example.copia.Entities.ClientEntity;
import com.example.copia.Entities.ComboboxEntity;
import com.example.copia.Entities.ConsultantsEntity;
import com.example.copia.Entities.ContractorsEntity;
import com.example.copia.Entities.SpecificationsEntity;
import com.example.copia.Entities.SuppliersEntity;
import com.example.copia.MainActivity;
import com.example.copia.R;
import com.example.copia.Tasks.Client_Retrieve_Deleted_Task;
import com.example.copia.Tasks.RestoreRecordsTask;
import com.example.copia.Tasks.Suppliers_Retrieve_Deleted_Task;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.util.ArrayList;
import java.util.List;

import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDeletedFiles extends Fragment implements View.OnClickListener {
    ClientAdapter clientAdapter;
    SuppliersAdapter suppliersAdapter;
    ContractorsAdapter contractorsAdapter;
    ConsultantsAdapter consultantsAdapter;
    SpecificationsAdapter specificationsAdapter;
    List<ClientEntity> clientEntities;
    List<SuppliersEntity> suppliersEntities;
    List<ContractorsEntity> contractorsEntities;
    List<ConsultantsEntity> consultantsEntities;
    List<SpecificationsEntity> specificationsEntities;
    RecyclerView deleted_recyclerview;
    BetterSpinner deleted_spinner;
    Button deleted_go;
    String selectedItem;
    int pos = -1;
    public FragmentDeletedFiles() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_deleted_files, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Deleted Files");
        deleted_spinner = (BetterSpinner) view.findViewById(R.id.deleted_spinner);
        deleted_recyclerview = (RecyclerView) view.findViewById(R.id.deleted_recyclerview);
        deleted_recyclerview.setHasFixedSize(true);
        deleted_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        deleted_recyclerview.setOnTouchListener(listener());
        deleted_spinner.setAdapter(adapterSpinner());
        deleted_go = (Button) view.findViewById(R.id.deleted_go);
        deleted_go.setOnClickListener(this);
        deleted_spinner.setOnItemClickListener(spinner_ClickListener());
        return view;
    }

    private ArrayAdapter<String> adapterSpinner() {
        ArrayList<String> list = new ArrayList<>();
        List<ComboboxEntity> comboboxEntities = ComboboxEntity.findWithQuery(ComboboxEntity.class, "Select distinct category from combobox_entity");
        for (ComboboxEntity entity : comboboxEntities)
            list.add(entity.getCategory());
        list.add("Notes");
        list.add("Images");
        list.add("PDF Files");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, list);
        return adapter;
    }

    private AdapterView.OnItemClickListener spinner_ClickListener() {
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = (String) parent.getItemAtPosition(position);
            }
        };
        return listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deleted_go:
                if (selectedItem.equalsIgnoreCase("Client")) {
                    Client_Retrieve_Deleted_Task client_retrieve_deleted_task = new Client_Retrieve_Deleted_Task(getContext());
                    client_retrieve_deleted_task.setClient_recyclerview(deleted_recyclerview);
                    client_retrieve_deleted_task.execute((Void)null);
                    clientAdapter = client_retrieve_deleted_task.getClientAdapter();
                    clientEntities = client_retrieve_deleted_task.getClientEntities();
                }
                else if(selectedItem.equalsIgnoreCase("Suppliers"))
                {
                    Suppliers_Retrieve_Deleted_Task suppliers_retrieve_deleted_task = new Suppliers_Retrieve_Deleted_Task(getContext());
                    suppliers_retrieve_deleted_task.setRecyclerview(deleted_recyclerview);
                    suppliers_retrieve_deleted_task.execute((Void)null);
                    suppliersAdapter = suppliers_retrieve_deleted_task.getSuppliersAdapter();
                    suppliersEntities = suppliers_retrieve_deleted_task.getSuppliersEntities();
                }
                break;
        }
    }

    public SwipeDismissRecyclerViewTouchListener listener()
    {
        SwipeDismissRecyclerViewTouchListener listener = new SwipeDismissRecyclerViewTouchListener.Builder(deleted_recyclerview, new SwipeDismissRecyclerViewTouchListener.DismissCallbacks()
        {
            @Override
            public boolean canDismiss(int i) {
                pos = i;
                return false;
            }

            @Override
            public void onDismiss(View view) {

            }
        })
        .setItemClickCallback(new SwipeDismissRecyclerViewTouchListener.OnItemClickCallBack() {
            @Override
            public void onClick(int i) {
                String[] choice = new String[]{"Restore", "Delete Permanently"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose");
                builder.setCancelable(true);
                builder.setItems(choice, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        if(which == 0){
                            if(selectedItem.equalsIgnoreCase("Client"))
                            {
                                RestoreRecordsTask restoreRecordsTask = new RestoreRecordsTask(getContext(),clientEntities.get(pos).getObjectId(), "Client");
                                restoreRecordsTask.setEntities(clientEntities);
                                restoreRecordsTask.setPos(pos);
                                restoreRecordsTask.setRecyclerView(deleted_recyclerview);
                                restoreRecordsTask.execute((Void)null);
                            }
                            else if(selectedItem.equalsIgnoreCase("Suppliers"))
                            {
                                RestoreRecordsTask restoreRecordsTask = new RestoreRecordsTask(getContext(),suppliersEntities.get(pos).getObjectId(), "Suppliers");
                                restoreRecordsTask.setEntities(suppliersEntities);
                                restoreRecordsTask.setPos(pos);
                                restoreRecordsTask.setRecyclerView(deleted_recyclerview);
                                restoreRecordsTask.execute((Void)null);
                            }

                        }
                        else if(which == 1){

                        }

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        })
            .setIsVertical(false)
            .create();
        return listener;
    }

}
