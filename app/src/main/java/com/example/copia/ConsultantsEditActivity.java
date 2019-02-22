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
import com.example.copia.Entities.ConsultantsEntity;
import com.example.copia.Fragments.FragmentSearch;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ConsultantsEditActivity extends AppCompatActivity {
    ConsultantsEntity consultantsEntity;
    TextInputLayout consultants_representative, consultants_position, consultants_company, consultants_specialization;
    Spinner consultants_industry, consultants_classification;
    Button save;
    ArrayList<String> industryLists = new ArrayList<>();
    ArrayList<String> classificationLists = new ArrayList<>();
    ArrayAdapter<String> dataAdapter1;
    ArrayAdapter<String> dataAdapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultants_edit);
        getSupportActionBar().setTitle("Consultants Edit");
        Intent intent = getIntent();
        consultantsEntity = (ConsultantsEntity) intent.getSerializableExtra(FragmentSearch.CONSULTANTS);
        List<ComboboxEntity> industryList = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Consultants", "Industry");
        List<ComboboxEntity> classificationsList = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Consultants", "Classification");
        for(ComboboxEntity entity : industryList)
            industryLists.add(entity.getTitle().toUpperCase());
        for(ComboboxEntity entity : classificationsList)
            classificationLists.add(entity.getTitle().toUpperCase());
        dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, industryLists);
        dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, classificationLists);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        consultants_representative = (TextInputLayout)findViewById(R.id.consultants_edit_representative);
        consultants_position = (TextInputLayout)findViewById(R.id.consultants_edit_position);
        consultants_company = (TextInputLayout)findViewById(R.id.consultants_edit_company);
        consultants_specialization = (TextInputLayout)findViewById(R.id.consultants_edit_specialization);
        consultants_industry = (Spinner)findViewById(R.id.consultants_edit_industry);
        consultants_classification = (Spinner)findViewById(R.id.consultants_edit_classification);
        save = (Button)findViewById(R.id.consultants_edit_save);
        consultants_industry.setAdapter(dataAdapter1);
        consultants_classification.setAdapter(dataAdapter2);
        setDefaultValues();
    }
    public void consultantsSaveClicked(View view)
    {
        checkforNull();
    }
    public void checkforNull()
    {
        if(!validateNull(consultants_representative) | !validateNull(consultants_position) | !validateNull(consultants_company) | !validateNull(consultants_specialization))
            return;
        new ConsultantsUpdateTask().execute((Void)null);
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
        consultants_representative.getEditText().setText(consultantsEntity.getRepresentative());
        consultants_position.getEditText().setText(consultantsEntity.getPosition());
        consultants_company.getEditText().setText(consultantsEntity.getCompany());
        consultants_specialization.getEditText().setText(consultantsEntity.getSpecialization());
        consultants_industry.setSelection(dataAdapter1.getPosition(consultantsEntity.getIndustry()));
        consultants_classification.setSelection(dataAdapter2.getPosition(consultantsEntity.getClassification()));
    }
    private class ConsultantsUpdateTask extends AsyncTask<Void, Void, Boolean>
    {
        boolean finished = false;
        boolean successfull = false;
        AlertDialog dialog = Utilities.getInstance().showLoading(ConsultantsEditActivity.this, "Updating record", false);
        @Override
        protected Boolean doInBackground(Void... voids) {
            String representative = consultants_representative.getEditText().getText().toString().trim();
            String position = consultants_position.getEditText().getText().toString().trim();
            String company = consultants_company.getEditText().getText().toString().trim();
            String specialization = consultants_specialization.getEditText().getText().toString().trim();
            String industry = consultants_industry.getSelectedItem().toString();
            String classification = consultants_classification.getSelectedItem().toString();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Consultants");
            query.getInBackground(consultantsEntity.getObjectId(), new GetCallback<ParseObject>() {
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
                        object.put("Tags", consultants_extractStringsToTags());
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
                consultantsEntity.setRepresentative(consultants_representative.getEditText().getText().toString().trim().toUpperCase());
                consultantsEntity.setPosition(consultants_position.getEditText().getText().toString().trim().toUpperCase());
                consultantsEntity.setCompany(consultants_company.getEditText().getText().toString().trim().toUpperCase());
                consultantsEntity.setSpecialization(consultants_specialization.getEditText().getText().toString().trim().toUpperCase());
                consultantsEntity.setIndustry(consultants_industry.getSelectedItem().toString().toUpperCase());
                consultantsEntity.setClassification(consultants_classification.getSelectedItem().toString().toUpperCase());
                Intent intent = new Intent();
                intent.putExtra(FragmentSearch.CONSULTANTS, consultantsEntity);
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
    public ArrayList<String> consultants_extractStringsToTags()
    {
        String representative = consultants_representative.getEditText().getText().toString().trim().toUpperCase();
        String position = consultants_position.getEditText().getText().toString().trim().toUpperCase();
        String company = consultants_company.getEditText().getText().toString().trim().toUpperCase();
        String specializations = consultants_specialization.getEditText().getText().toString().trim().toUpperCase();
        String industry = consultants_industry.getSelectedItem().toString().toUpperCase();
        String classification = consultants_classification.getSelectedItem().toString().toUpperCase();

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
