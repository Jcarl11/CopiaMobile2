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
import com.example.copia.Adapters.ImagesAdapter;
import com.example.copia.Adapters.NotesAdapter;
import com.example.copia.Adapters.PDFAdapter;
import com.example.copia.Adapters.SpecificationsAdapter;
import com.example.copia.Adapters.SuppliersAdapter;
import com.example.copia.DatabaseOperation.PermanentDeleteFiles;
import com.example.copia.DatabaseOperation.PermanentDeleteFilesTask;
import com.example.copia.Entities.ClientEntity;
import com.example.copia.Entities.ComboboxEntity;
import com.example.copia.Entities.ConsultantsEntity;
import com.example.copia.Entities.ContractorsEntity;
import com.example.copia.Entities.ImagesEntity;
import com.example.copia.Entities.NotesEntity;
import com.example.copia.Entities.PDFEntity;
import com.example.copia.Entities.SpecificationsEntity;
import com.example.copia.Entities.SuppliersEntity;
import com.example.copia.MainActivity;
import com.example.copia.R;
import com.example.copia.Tasks.Client_Retrieve_Deleted_Task;
import com.example.copia.Tasks.Consultants_Retrieve_Deleted_Task;
import com.example.copia.Tasks.Contractors_Retrieve_Deleted_Task;
import com.example.copia.Tasks.Files_Retrieve_Deleted_Task;
import com.example.copia.Tasks.Images_Retrieve_Deleted_Task;
import com.example.copia.Tasks.Notes_Retrieve_Deleted_Task;
import com.example.copia.Tasks.PermanentDeleteRecordsTask;
import com.example.copia.Tasks.RestoreRecordsTask;
import com.example.copia.Tasks.Specifications_Retrieve_Deleted_Task;
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
    NotesAdapter notesAdapter;
    ImagesAdapter imagesAdapter;
    PDFAdapter pdfAdapter;

    List<ClientEntity> clientEntities;
    List<SuppliersEntity> suppliersEntities;
    List<ContractorsEntity> contractorsEntities;
    List<ConsultantsEntity> consultantsEntities;
    List<SpecificationsEntity> specificationsEntities;
    List<NotesEntity> notesEntities;
    List<ImagesEntity> imagesEntities;
    List<PDFEntity> pdfEntities;
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
                else if(selectedItem.equalsIgnoreCase("Contractors"))
                {
                    Contractors_Retrieve_Deleted_Task contractors_retrieve_deleted_task = new Contractors_Retrieve_Deleted_Task(getContext());
                    contractors_retrieve_deleted_task.setRecyclerview(deleted_recyclerview);
                    contractors_retrieve_deleted_task.execute((Void)null);
                    contractorsAdapter = contractors_retrieve_deleted_task.getContractorsAdapter();
                    contractorsEntities = contractors_retrieve_deleted_task.getContractorsEntities();
                }
                else if(selectedItem.equalsIgnoreCase("Consultants"))
                {
                    Consultants_Retrieve_Deleted_Task consultants_retrieve_deleted_task = new Consultants_Retrieve_Deleted_Task(getContext());
                    consultants_retrieve_deleted_task.setRecyclerview(deleted_recyclerview);
                    consultants_retrieve_deleted_task.execute((Void)null);
                    consultantsAdapter = consultants_retrieve_deleted_task.getConsultantsAdapter();
                    consultantsEntities = consultants_retrieve_deleted_task.getConsultantsEntities();
                }
                else if(selectedItem.equalsIgnoreCase("Specifications"))
                {
                    Specifications_Retrieve_Deleted_Task specifications_retrieve_deleted_task = new Specifications_Retrieve_Deleted_Task(getContext());
                    specifications_retrieve_deleted_task.setRecyclerview(deleted_recyclerview);
                    specifications_retrieve_deleted_task.execute((Void)null);
                    specificationsAdapter = specifications_retrieve_deleted_task.getSpecificationsAdapter();
                    specificationsEntities = specifications_retrieve_deleted_task.getSpecificationsEntities();
                }
                else if(selectedItem.equalsIgnoreCase("Notes"))
                {
                    Notes_Retrieve_Deleted_Task notes_retrieve_deleted_task = new Notes_Retrieve_Deleted_Task(getContext());
                    notes_retrieve_deleted_task.setRecyclerview(deleted_recyclerview);
                    notes_retrieve_deleted_task.execute((Void)null);
                    notesAdapter = notes_retrieve_deleted_task.getNotesAdapter();
                    notesEntities = notes_retrieve_deleted_task.getNotesEntities();
                }
                else if(selectedItem.equalsIgnoreCase("Images"))
                {
                    Images_Retrieve_Deleted_Task images_retrieve_deleted_task = new Images_Retrieve_Deleted_Task(getContext());
                    images_retrieve_deleted_task.setRecyclerview(deleted_recyclerview);
                    images_retrieve_deleted_task.execute((Void)null);
                    imagesAdapter = images_retrieve_deleted_task.getImagesAdapter();
                    imagesEntities = images_retrieve_deleted_task.getImagesEntities();
                }
                else if(selectedItem.equalsIgnoreCase("PDF Files"))
                {
                    Files_Retrieve_Deleted_Task files_retrieve_deleted_task = new Files_Retrieve_Deleted_Task(getContext());
                    files_retrieve_deleted_task.setRecyclerview(deleted_recyclerview);
                    files_retrieve_deleted_task.execute((Void)null);
                    pdfAdapter = files_retrieve_deleted_task.getPdfAdapter();
                    pdfEntities = files_retrieve_deleted_task.getPdfEntities();
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
                            restore();
                        }
                        else if(which == 1){
                            deletePermanently();
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
    private void restore()
    {
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
        else if(selectedItem.equalsIgnoreCase("Contractors"))
        {
            RestoreRecordsTask restoreRecordsTask = new RestoreRecordsTask(getContext(),contractorsEntities.get(pos).getObjectId(), "Contractors");
            restoreRecordsTask.setEntities(contractorsEntities);
            restoreRecordsTask.setPos(pos);
            restoreRecordsTask.setRecyclerView(deleted_recyclerview);
            restoreRecordsTask.execute((Void)null);
        }
        else if(selectedItem.equalsIgnoreCase("Consultants"))
        {
            RestoreRecordsTask restoreRecordsTask = new RestoreRecordsTask(getContext(),consultantsEntities.get(pos).getObjectId(), "Consultants");
            restoreRecordsTask.setEntities(consultantsEntities);
            restoreRecordsTask.setPos(pos);
            restoreRecordsTask.setRecyclerView(deleted_recyclerview);
            restoreRecordsTask.execute((Void)null);
        }
        else if(selectedItem.equalsIgnoreCase("Specifications"))
        {
            RestoreRecordsTask restoreRecordsTask = new RestoreRecordsTask(getContext(),specificationsEntities.get(pos).getObjectid(), "Specifications");
            restoreRecordsTask.setEntities(specificationsEntities);
            restoreRecordsTask.setPos(pos);
            restoreRecordsTask.setRecyclerView(deleted_recyclerview);
            restoreRecordsTask.execute((Void)null);
        }
        else if(selectedItem.equalsIgnoreCase("Notes"))
        {
            RestoreRecordsTask restoreRecordsTask = new RestoreRecordsTask(getContext(),notesEntities.get(pos).getObjectId(), "Notes");
            restoreRecordsTask.setEntities(notesEntities);
            restoreRecordsTask.setPos(pos);
            restoreRecordsTask.setRecyclerView(deleted_recyclerview);
            restoreRecordsTask.execute((Void)null);
        }
        else if(selectedItem.equalsIgnoreCase("Images"))
        {
            RestoreRecordsTask restoreRecordsTask = new RestoreRecordsTask(getContext(),imagesEntities.get(pos).getObjectId(), "Images");
            restoreRecordsTask.setEntities(imagesEntities);
            restoreRecordsTask.setPos(pos);
            restoreRecordsTask.setRecyclerView(deleted_recyclerview);
            restoreRecordsTask.execute((Void)null);
        }
        else if(selectedItem.equalsIgnoreCase("PDF Files"))
        {
            RestoreRecordsTask restoreRecordsTask = new RestoreRecordsTask(getContext(),pdfEntities.get(pos).getObjectId(), "PDFFiles");
            restoreRecordsTask.setEntities(pdfEntities);
            restoreRecordsTask.setPos(pos);
            restoreRecordsTask.setRecyclerView(deleted_recyclerview);
            restoreRecordsTask.execute((Void)null);
        }
    }

    private void deletePermanently()
    {
        if(!selectedItem.equalsIgnoreCase("Notes") && !selectedItem.equalsIgnoreCase("Images") && !selectedItem.equalsIgnoreCase("PDF Files"))
        {
            if (selectedItem.equalsIgnoreCase("Client")) {
                PermanentDeleteRecordsTask permanentDeleteRecordsTask = new PermanentDeleteRecordsTask(getContext(), clientEntities.get(pos).getObjectId(), "Client");
                permanentDeleteRecordsTask.setEntity(clientEntities);
                permanentDeleteRecordsTask.setPos(pos);
                permanentDeleteRecordsTask.setRecyclerView(deleted_recyclerview);
                permanentDeleteRecordsTask.execute((Void) null);
            }
            else if (selectedItem.equalsIgnoreCase("Suppliers")) {
                PermanentDeleteRecordsTask permanentDeleteRecordsTask = new PermanentDeleteRecordsTask(getContext(), suppliersEntities.get(pos).getObjectId(), "Suppliers");
                permanentDeleteRecordsTask.setEntity(suppliersEntities);
                permanentDeleteRecordsTask.setPos(pos);
                permanentDeleteRecordsTask.setRecyclerView(deleted_recyclerview);
                permanentDeleteRecordsTask.execute((Void) null);
            }
            else if (selectedItem.equalsIgnoreCase("Contractors")) {
                PermanentDeleteRecordsTask permanentDeleteRecordsTask = new PermanentDeleteRecordsTask(getContext(), contractorsEntities.get(pos).getObjectId(), "Contractors");
                permanentDeleteRecordsTask.setEntity(contractorsEntities);
                permanentDeleteRecordsTask.setPos(pos);
                permanentDeleteRecordsTask.setRecyclerView(deleted_recyclerview);
                permanentDeleteRecordsTask.execute((Void) null);
            }
            else if (selectedItem.equalsIgnoreCase("Consultants")) {
                PermanentDeleteRecordsTask permanentDeleteRecordsTask = new PermanentDeleteRecordsTask(getContext(), consultantsEntities.get(pos).getObjectId(), "Consultants");
                permanentDeleteRecordsTask.setEntity(consultantsEntities);
                permanentDeleteRecordsTask.setPos(pos);
                permanentDeleteRecordsTask.setRecyclerView(deleted_recyclerview);
                permanentDeleteRecordsTask.execute((Void) null);
            }
            else if (selectedItem.equalsIgnoreCase("Specifications")) {
                PermanentDeleteRecordsTask permanentDeleteRecordsTask = new PermanentDeleteRecordsTask(getContext(), consultantsEntities.get(pos).getObjectId(), "Specifications");
                permanentDeleteRecordsTask.setEntity(specificationsEntities);
                permanentDeleteRecordsTask.setPos(pos);
                permanentDeleteRecordsTask.setRecyclerView(deleted_recyclerview);
                permanentDeleteRecordsTask.execute((Void) null);
            }

        }
        else
        {
            if (selectedItem.equalsIgnoreCase("Notes")) {
                PermanentDeleteFilesTask permanentDeleteFilesTask = new PermanentDeleteFilesTask(getContext(), notesEntities.get(pos).getObjectId(), "Notes");
                permanentDeleteFilesTask.setEntity(notesEntities);
                permanentDeleteFilesTask.setPos(pos);
                permanentDeleteFilesTask.setRecyclerView(deleted_recyclerview);
                permanentDeleteFilesTask.execute((Void)null);
            }
            else if (selectedItem.equalsIgnoreCase("Images")) {
                PermanentDeleteFilesTask permanentDeleteFilesTask = new PermanentDeleteFilesTask(getContext(), imagesEntities.get(pos).getObjectId(), "Images");
                permanentDeleteFilesTask.setEntity(imagesEntities);
                permanentDeleteFilesTask.setPos(pos);
                permanentDeleteFilesTask.setRecyclerView(deleted_recyclerview);
                permanentDeleteFilesTask.execute((Void)null);
            }
            else if (selectedItem.equalsIgnoreCase("PDF Files")) {
                PermanentDeleteFilesTask permanentDeleteFilesTask = new PermanentDeleteFilesTask(getContext(), pdfEntities.get(pos).getObjectId(), "PDFFiles");
                permanentDeleteFilesTask.setEntity(pdfEntities);
                permanentDeleteFilesTask.setPos(pos);
                permanentDeleteFilesTask.setRecyclerView(deleted_recyclerview);
                permanentDeleteFilesTask.execute((Void)null);
            }
        }


    }

}
