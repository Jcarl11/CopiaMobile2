package com.example.copia;

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
import com.example.copia.Entities.ContractorsEntity;
import com.example.copia.Fragments.FragmentSearch;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ContractorsEditActivity extends AppCompatActivity
{
    ContractorsEntity contractorsEntity;
    TextInputLayout contractors_representative, contractors_position, contractors_company, contractors_specialization;
    Spinner contractors_industry, contractors_classification;
    Button save;
    ArrayList<String> industryLists = new ArrayList<>();
    ArrayList<String> classificationLists = new ArrayList<>();
    ArrayAdapter<String> dataAdapter1;
    ArrayAdapter<String> dataAdapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractors_edit);
        getSupportActionBar().setTitle("Contractors Edit");
        Intent intent = getIntent();
        contractorsEntity = (ContractorsEntity) intent.getSerializableExtra(FragmentSearch.CONTRACTORS);
        List<ComboboxEntity> industryList = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Contractors", "Industry");
        List<ComboboxEntity> classificationsList = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Contractors", "Classification");
        for(ComboboxEntity entity : industryList)
            industryLists.add(entity.getTitle().toUpperCase());
        for(ComboboxEntity entity : classificationsList)
            classificationLists.add(entity.getTitle().toUpperCase());
        dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, industryLists);
        dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, classificationLists);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contractors_representative = (TextInputLayout)findViewById(R.id.contractors_edit_representative);
        contractors_position = (TextInputLayout)findViewById(R.id.contractors_edit_position);
        contractors_company = (TextInputLayout)findViewById(R.id.contractors_edit_company);
        contractors_specialization = (TextInputLayout)findViewById(R.id.contractors_edit_specialization);
        contractors_industry = (Spinner)findViewById(R.id.contractors_edit_industry);
        contractors_classification = (Spinner)findViewById(R.id.contractors_edit_classification);
        save = (Button)findViewById(R.id.contractors_edit_save);
        contractors_industry.setAdapter(dataAdapter1);
        contractors_classification.setAdapter(dataAdapter2);
        setDefaultValues();
    }
    public void contractorsSaveClicked(View view)
    {
        checkforNull();
    }
    public void checkforNull()
    {
        if(!validateNull(contractors_representative) | !validateNull(contractors_position) | !validateNull(contractors_company) | !validateNull(contractors_specialization))
            return;
        new ContractorsUpdateTask().execute((Void)null);
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
        contractors_representative.getEditText().setText(contractorsEntity.getRepresentative());
        contractors_position.getEditText().setText(contractorsEntity.getPosition());
        contractors_company.getEditText().setText(contractorsEntity.getCompany());
        contractors_specialization.getEditText().setText(contractorsEntity.getSpecialization());
        contractors_industry.setSelection(dataAdapter1.getPosition(contractorsEntity.getIndustry()));
        contractors_classification.setSelection(dataAdapter2.getPosition(contractorsEntity.getClassification()));
    }
    private class ContractorsUpdateTask extends AsyncTask<Void, Void, Boolean>
    {
        boolean finished = false;
        boolean successfull = false;
        AlertDialog dialog = Utilities.getInstance().showLoading(ContractorsEditActivity.this, "Updating record", false);
        @Override
        protected Boolean doInBackground(Void... voids) {
            String representative = contractors_representative.getEditText().getText().toString().trim();
            String position = contractors_position.getEditText().getText().toString().trim();
            String company = contractors_company.getEditText().getText().toString().trim();
            String specialization = contractors_specialization.getEditText().getText().toString().trim();
            String industry = contractors_industry.getSelectedItem().toString();
            String classification = contractors_classification.getSelectedItem().toString();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Contractors");
            query.getInBackground(contractorsEntity.getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(e == null && object != null)
                    {
                        object.put("Representative", representative.toUpperCase());
                        object.put("Position", position.toUpperCase());
                        object.put("Company", company.toUpperCase());
                        object.put("Specialization", specialization.toUpperCase());
                        object.put("Industry", industry.toUpperCase());
                        object.put("Classification", classification.toUpperCase());
                        object.put("Tags", contractors_extractStringsToTags());
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
                contractorsEntity.setRepresentative(contractors_representative.getEditText().getText().toString().trim().toUpperCase());
                contractorsEntity.setPosition(contractors_position.getEditText().getText().toString().trim().toUpperCase());
                contractorsEntity.setCompany(contractors_company.getEditText().getText().toString().trim().toUpperCase());
                contractorsEntity.setSpecialization(contractors_specialization.getEditText().getText().toString().trim().toUpperCase());
                contractorsEntity.setIndustry(contractors_industry.getSelectedItem().toString().toUpperCase());
                contractorsEntity.setClassification(contractors_classification.getSelectedItem().toString().toUpperCase());
                Intent intent = new Intent();
                intent.putExtra(FragmentSearch.CONTRACTORS, contractorsEntity);
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
    public ArrayList<String> contractors_extractStringsToTags()
    {
        String representative = contractors_representative.getEditText().getText().toString().trim().toUpperCase();
        String position = contractors_position.getEditText().getText().toString().trim().toUpperCase();
        String company = contractors_company.getEditText().getText().toString().trim().toUpperCase();
        String specializations = contractors_specialization.getEditText().getText().toString().trim().toUpperCase();
        String industry = contractors_industry.getSelectedItem().toString().toUpperCase();
        String classification = contractors_classification.getSelectedItem().toString().toUpperCase();

        ArrayList<String> tags = new ArrayList<>();
        tags.add(representative);
        tags.add(position);
        tags.add(company);
        tags.add(specializations);
        tags.add(industry);
        tags.add(classification);
        String[] representativeSplit = representative.split("\\s+");
        String[] positionSplit = position.split("\\s+");
        String[] companySplit = company.split("\\s+");
        String[] specialization = specializations.split("\\s+");
        for(String values : representativeSplit)
        {
            tags.add(values.toUpperCase());
        }
        for(String values : positionSplit)
        {
            tags.add(values.toUpperCase());
        }
        for(String values : companySplit)
        {
            tags.add(values.toUpperCase());
        }
        for(String values : specialization)
        {
            tags.add(values.toUpperCase());
        }
        return tags;
    }


}
