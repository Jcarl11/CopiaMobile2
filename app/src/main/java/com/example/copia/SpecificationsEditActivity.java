package com.example.copia;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.dpizarro.autolabel.library.Label;
import com.example.copia.Entities.SpecificationsEntity;
import com.example.copia.Fragments.FragmentSearch;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;

public class SpecificationsEditActivity extends AppCompatActivity {
    SpecificationsEntity specificationsEntity;
    TextInputLayout specifications_title, specifications_division, specifications_section, specifications_type;
    EditText specifications_edit_tags;
    Button specifications_save, specifications_add;
    AutoLabelUI keywords;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specifications_edit);
        getSupportActionBar().setTitle("Specifications Edit");
        Intent intent = getIntent();
        specificationsEntity = (SpecificationsEntity) intent.getSerializableExtra(FragmentSearch.SPECIFICATIONS);
        specifications_title = (TextInputLayout)findViewById(R.id.specifications_edit_title);
        specifications_division = (TextInputLayout)findViewById(R.id.specifications_edit_division);
        specifications_section = (TextInputLayout)findViewById(R.id.specifications_edit_section);
        specifications_type = (TextInputLayout)findViewById(R.id.specifications_edit_type);
        specifications_edit_tags = (EditText)findViewById(R.id.specifications_edit_tags);
        specifications_add = (Button)findViewById(R.id.specifications_edit_add);
        specifications_save = (Button)findViewById(R.id.specifications_edit_save);
        keywords = (AutoLabelUI)findViewById(R.id.specifications_edit_taglabel);
        setDefaultValues();
    }
    public void specificationsSaveClicked(View view)
    {
        checkforNull();
    }
    public void addClick(View view)
    {
        if(!TextUtils.isEmpty(specifications_edit_tags.getText().toString()))
            keywords.addLabel(specifications_edit_tags.getText().toString());
        else
            Toast.makeText(this, "Empty fields", Toast.LENGTH_SHORT).show();
    }
    public void checkforNull()
    {
        if(!validateNull(specifications_title) | !validateNull(specifications_division) | !validateNull(specifications_section) | !validateNull(specifications_type))
            return;
        new SpecificationsUpdateTask().execute((Void)null);
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
        specifications_title.getEditText().setText(specificationsEntity.getTitle());
        specifications_division.getEditText().setText(specificationsEntity.getDivision());
        specifications_section.getEditText().setText(specificationsEntity.getSection());
        specifications_type.getEditText().setText(specificationsEntity.getType());
    }

    private class SpecificationsUpdateTask extends AsyncTask<Void, Void, Boolean>
    {
        boolean finished = false;
        boolean successfull = false;
        AlertDialog dialog = Utilities.getInstance().showLoading(SpecificationsEditActivity.this, "Updating record", false);
        @Override
        protected Boolean doInBackground(Void... voids) {
            String title = specifications_title.getEditText().getText().toString().trim().toUpperCase();
            String division = specifications_division.getEditText().getText().toString().trim().toUpperCase();
            String section = specifications_section.getEditText().getText().toString().trim().toUpperCase();
            String type = specifications_type.getEditText().getText().toString().trim().toUpperCase();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Specifications");
            query.getInBackground(specificationsEntity.getObjectid(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(e == null && object != null)
                    {
                        object.put("Title", title);
                        object.put("Division", division);
                        object.put("Section", section);
                        object.put("Type", type);
                        object.put("Tags", specifications_extractStringsToTags());
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
                specificationsEntity.setTitle(specifications_title.getEditText().getText().toString().trim().toUpperCase());
                specificationsEntity.setDivision(specifications_division.getEditText().getText().toString().trim().toUpperCase());
                specificationsEntity.setSection(specifications_section.getEditText().getText().toString().trim().toUpperCase());
                specificationsEntity.setType(specifications_type.getEditText().getText().toString().trim().toUpperCase());
                specificationsEntity.setDocument(specificationsEntity.getDivision() + " - " + specificationsEntity.getSection() + " - " + specificationsEntity.getType());
                Intent intent = new Intent();
                intent.putExtra(FragmentSearch.SPECIFICATIONS, specificationsEntity);
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
    public ArrayList<String> specifications_extractStringsToTags()
    {
        String title = specifications_title.getEditText().getText().toString().trim().toUpperCase();
        String division = specifications_division.getEditText().getText().toString().trim().toUpperCase();
        String section = specifications_section.getEditText().getText().toString().trim().toUpperCase();
        String type = specifications_type.getEditText().getText().toString().trim().toUpperCase();
        ArrayList<String> tags = new ArrayList<>();
        if(keywords.getLabels().size() > 0)
        {
            for(Label label : keywords.getLabels())
                tags.add(label.getText());
        }
        tags.add(title);
        tags.add(division);
        tags.add(section);
        tags.add(type);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(division);
        stringBuilder.append("-");
        stringBuilder.append(section);
        stringBuilder.append("-");
        stringBuilder.append(type);
        tags.add(stringBuilder.toString());
        String[] titleSplit = title.split("\\s+");
        String[] divisionSplit = division.split("\\s+");
        String[] sectionSplit = section.split("\\s+");
        String[] typeSplit = type.split("\\s+");
        for(String values : titleSplit)
            tags.add(values.toUpperCase());
        for(String values : divisionSplit)
            tags.add(values.toUpperCase());
        for(String values : sectionSplit)
            tags.add(values.toUpperCase());
        for(String values : typeSplit)
            tags.add(values.toUpperCase());

        return tags;
    }
}
