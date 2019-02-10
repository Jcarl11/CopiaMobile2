package com.example.copia;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class FragmentSearch extends Fragment
{
    private static final String[][] SPACESHIPS = { { "Casini", "Chemical", "NASA", "Jupiter" },
            { "Spitzer", "Nuclear", "NASA", "Alpha Centauri" } };
    private static String[] header = {"header 1", "header 2", "header 3", "header 4"};
    TableView tableView;
    ArrayList<String> searchCategories = new ArrayList<>();
    EditText search_edittext_search;
    Spinner search_spinner_searchin;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        List<ComboboxEntity> comboboxEntities = ComboboxEntity.findWithQuery(ComboboxEntity.class, "Select distinct category from combobox_entity");
        for(ComboboxEntity entity : comboboxEntities)
            searchCategories.add(entity.getCategory());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, searchCategories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableView = (TableView)view.findViewById(R.id.tableView);
        tableView.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(getContext(), header));
        tableView.setDataAdapter(new SimpleTableDataAdapter(getContext(), SPACESHIPS));
        tableView.addDataClickListener(new TableDataClickListener() {
            @Override
            public void onDataClicked(int rowIndex, Object clickedData) {
                Toast.makeText(getContext(), ((String[])clickedData)[2], Toast.LENGTH_SHORT).show();
            }
        });
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
}
