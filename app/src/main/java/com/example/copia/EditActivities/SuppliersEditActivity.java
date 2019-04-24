package com.example.copia.EditActivities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.copia.Entities.ComboboxEntity;
import com.example.copia.Entities.SuppliersEntity;
import com.example.copia.Fragments.FragmentSearch;
import com.example.copia.R;
import com.example.copia.Utilities;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class SuppliersEditActivity extends AppCompatActivity {
    SuppliersEntity suppliersEntity;
    TextInputLayout representative, position, company, brand;
    Spinner discipline_spinner;
    Button save;
    ArrayList<String> disciplineLists = new ArrayList<>();
    ArrayList<String> typeLists = new ArrayList<>();
    ArrayAdapter<String> dataAdapter1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppliers_edit);
        getSupportActionBar().setTitle("Suppliers Edit");
        Intent intent = getIntent();
        suppliersEntity = (SuppliersEntity) intent.getSerializableExtra(FragmentSearch.SUPPLIERS);
        List<ComboboxEntity> disciplineList = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Suppliers", "Discipline");
        for(ComboboxEntity entity : disciplineList)
            disciplineLists.add(entity.getTitle().toUpperCase());
        dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, disciplineLists);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        representative = (TextInputLayout)findViewById(R.id.suppliers_edit_representative);
        position = (TextInputLayout)findViewById(R.id.suppliers_edit_position);
        company = (TextInputLayout)findViewById(R.id.suppliers_edit_company);
        brand = (TextInputLayout)findViewById(R.id.suppliers_edit_brand);
        discipline_spinner = (Spinner)findViewById(R.id.suppliers_edit_discipline);
        save = (Button)findViewById(R.id.suppliers_edit_save);
        discipline_spinner.setAdapter(dataAdapter1);
        setDefaultValues();
    }
    
    public void suppliersSaveClicked(View view)
    {
        checkforNull();
    }
    public void checkforNull()
    {
        if(!validateNull(representative) | !validateNull(position) | !validateNull(company) | !validateNull(brand))
            return;
        new SuppliersUpdateTask().execute((Void)null);
    }

    private boolean validateNull(TextInputLayout textInputLayout) {
        String input = textInputLayout.getEditText().getText().toString().trim();

        if (input.isEmpty()) {
            textInputLayout.setError("Please don't leave this field blank");
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }
    private void setDefaultValues()
    {
        representative.getEditText().setText(suppliersEntity.getRepresentative());
        position.getEditText().setText(suppliersEntity.getPosition());
        company.getEditText().setText(suppliersEntity.getCompany());
        brand.getEditText().setText(suppliersEntity.getBrand());
        discipline_spinner.setSelection(dataAdapter1.getPosition(suppliersEntity.getDiscipline()));
    }


    private class SuppliersUpdateTask extends AsyncTask<Void, Void, Boolean>
    {
        boolean finished = false;
        boolean successfull = false;
        AlertDialog dialog = Utilities.getInstance().showLoading(SuppliersEditActivity.this, "Updating record", false);
        @Override
        protected Boolean doInBackground(Void... voids) {
            String rep = representative.getEditText().getText().toString().trim();
            String pos = position.getEditText().getText().toString().trim();
            String comp = company.getEditText().getText().toString().trim();
            String brands = brand.getEditText().getText().toString().trim();
            String disc = discipline_spinner.getSelectedItem().toString();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Suppliers");
            query.getInBackground(suppliersEntity.getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(e == null && object != null)
                    {
                        object.put("Representative", rep.toUpperCase());
                        object.put("Position", pos.toUpperCase());
                        object.put("Company_Name", comp.toUpperCase());
                        object.put("Brand", brands.toUpperCase());
                        object.put("Discipline", disc.toUpperCase());
                        object.put("Tags", suppliers_extractStringsToTags());
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null)
                                    successfull = true;
                                finished = true;
                            }
                        });
                    }
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
            return successfull;
        }

        @Override
        protected void onPreExecute() {dialog.show();}

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            dialog.dismiss();
            if(aBoolean)
            {
                suppliersEntity.setRepresentative(representative.getEditText().getText().toString().trim().toUpperCase());
                suppliersEntity.setPosition(position.getEditText().getText().toString().trim().toUpperCase());
                suppliersEntity.setCompany(company.getEditText().getText().toString().trim().toUpperCase());
                suppliersEntity.setBrand(brand.getEditText().getText().toString().trim().toUpperCase());
                suppliersEntity.setDiscipline(discipline_spinner.getSelectedItem().toString().toUpperCase());
                Intent intent = new Intent();
                intent.putExtra(FragmentSearch.SUPPLIERS, suppliersEntity);
                setResult(RESULT_OK, intent);
                finish();
            }
            else
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }
    public ArrayList<String> suppliers_extractStringsToTags()
    {
        String rep = representative.getEditText().getText().toString().trim().toUpperCase();
        String pos = position.getEditText().getText().toString().trim().toUpperCase();
        String comp = company.getEditText().getText().toString().trim().toUpperCase();
        String brands = brand.getEditText().getText().toString().trim().toUpperCase();
        String disc = discipline_spinner.getSelectedItem().toString().toUpperCase();

        ArrayList<String> tags = new ArrayList<>();
        tags.add(rep);
        tags.add(pos);
        tags.add(comp);
        tags.add(brands);
        tags.add(disc);
        String[] representativeSplit = rep.split("\\s+");
        String[] positionSplit = pos.split("\\s+");
        String[] companySplit = comp.split("\\s+");
        String[] brandSplit = brands.split("\\s+");
        for(String values : representativeSplit)
            tags.add(values.toUpperCase());
        for(String values : positionSplit)
            tags.add(values.toUpperCase());
        for(String values : companySplit)
            tags.add(values.toUpperCase());
        for(String values : brandSplit)
            tags.add(values.toUpperCase());
        return tags;
    }
}
