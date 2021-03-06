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

import com.example.copia.Entities.ClientEntity;
import com.example.copia.Entities.ComboboxEntity;
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

public class ClientEditActivity extends AppCompatActivity {
    ClientEntity clientEntity;
    TextInputLayout representative, position, company;
    Spinner industry, type;
    Button save;
    ArrayList<String> industryLists = new ArrayList<>();
    ArrayList<String> typeLists = new ArrayList<>();
    ArrayAdapter<String> dataAdapter1;
    ArrayAdapter<String> dataAdapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_edit);
        getSupportActionBar().setTitle("Client Edit");
        Intent intent = getIntent();
        clientEntity = (ClientEntity) intent.getSerializableExtra(FragmentSearch.CLIENT);
        List<ComboboxEntity> industryList = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Client", "Industry");
        List<ComboboxEntity> typeList = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Client", "Type");
        for(ComboboxEntity entity : industryList)
            industryLists.add(entity.getTitle().toUpperCase());
        for(ComboboxEntity entity : typeList)
            typeLists.add(entity.getTitle().toUpperCase());
        dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, industryLists);
        dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeLists);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        representative = (TextInputLayout)findViewById(R.id.client_edit_representative);
        position = (TextInputLayout)findViewById(R.id.client_edit_position);
        company = (TextInputLayout)findViewById(R.id.client_edit_company);
        industry = (Spinner)findViewById(R.id.client_edit_industry);
        type = (Spinner)findViewById(R.id.client_edit_type);
        save = (Button)findViewById(R.id.client_edit_save);
        industry.setAdapter(dataAdapter1);
        type.setAdapter(dataAdapter2);
        setDefaultValues();
    }
    public void clientSaveClicked(View view)
    {
        checkforNull();
    }
    public void checkforNull()
    {
        if(!validateNull(representative) | !validateNull(position) | !validateNull(company))
            return;
        new ClientUpdateTask().execute((Void)null);
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
        representative.getEditText().setText(clientEntity.getRepresentative());
        position.getEditText().setText(clientEntity.getPosition());
        company.getEditText().setText(clientEntity.getCompany());
        industry.setSelection(dataAdapter1.getPosition(clientEntity.getIndustry()));
        type.setSelection(dataAdapter2.getPosition(clientEntity.getType()));
    }

    private class ClientUpdateTask extends AsyncTask<Void, Void, Boolean>
    {
        boolean finished = false;
        boolean successfull = false;
        AlertDialog dialog = Utilities.getInstance().showLoading(ClientEditActivity.this, "Updating record", false);
        @Override
        protected Boolean doInBackground(Void... voids) {
            String rep = representative.getEditText().getText().toString().trim();
            String pos = position.getEditText().getText().toString().trim();
            String comp = company.getEditText().getText().toString().trim();
            String ind = industry.getSelectedItem().toString();
            String typ = type.getSelectedItem().toString();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");
            query.getInBackground(clientEntity.getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(e == null && object != null)
                    {
                        object.put("Representative", rep.toUpperCase());
                        object.put("Position", pos.toUpperCase());
                        object.put("Company", comp.toUpperCase());
                        object.put("Industry", ind.toUpperCase());
                        object.put("Type", typ.toUpperCase());
                        object.put("Tags", client_extractStringsToTags());
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
                clientEntity.setRepresentative(representative.getEditText().getText().toString().trim());
                clientEntity.setPosition(position.getEditText().getText().toString().trim());
                clientEntity.setCompany(company.getEditText().getText().toString().trim());
                clientEntity.setIndustry(industry.getSelectedItem().toString());
                clientEntity.setType(type.getSelectedItem().toString());
                Intent intent = new Intent();
                intent.putExtra(FragmentSearch.CLIENT, clientEntity);
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

    public ArrayList<String> client_extractStringsToTags()
    {
        String representatives = representative.getEditText().getText().toString().trim().toUpperCase();
        String positions = position.getEditText().getText().toString().trim().toUpperCase();
        String companys = company.getEditText().getText().toString().trim().toUpperCase();
        String industrys = industry.getSelectedItem().toString().toUpperCase();
        String types = type.getSelectedItem().toString().toUpperCase();

        ArrayList<String> tags = new ArrayList<>();
        tags.add(representatives);
        tags.add(positions);
        tags.add(companys);
        tags.add(industrys);
        tags.add(types);
        String[] representativeSplit = representatives.split("\\s+");
        String[] positionSplit = positions.split("\\s+");
        String[] companySplit = companys.split("\\s+");
        for(String values : representativeSplit)
            tags.add(values.toUpperCase());
        for(String values : positionSplit)
            tags.add(values.toUpperCase());
        for(String values : companySplit)
            tags.add(values.toUpperCase());
        return tags;
    }
}
