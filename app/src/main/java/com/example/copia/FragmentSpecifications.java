package com.example.copia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.dpizarro.autolabel.library.Label;
import com.example.copia.DatabaseOperation.FileUpload;
import com.example.copia.DatabaseOperation.ImageUpload;
import com.example.copia.DatabaseOperation.RemarksUpload;
import com.example.copia.DatabaseOperation.UploadPrimary;
import com.parse.ParseObject;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import dmax.dialog.SpotsDialog;

public class FragmentSpecifications extends Fragment implements View.OnClickListener
{
    ParseObject reference = null;
    FileUpload fileUpload = new FileUpload();
    UploadPrimary uploadPrimary = new UploadPrimary();
    RemarksUpload remarksUpload = new RemarksUpload();
    ArrayList<NormalFile> filesList = new ArrayList<>();
    EditText specifications_edittext_document, specifications_edittext_division, specifications_edittext_section, specifications_edittext_type,
            specifications_edittext_keywords, specifications_edittext_remarks;
    Button specifications_btn_keyword, specifications_btn_remark, specifications_btn_choosefile, specifications_btn_upload;
    AutoLabelUI specifications_label_keywords, specifications_label_remarks, specifications_label_files;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_specifications, container, false);
        specifications_edittext_document = (EditText)view.findViewById(R.id.specifications_edittext_document);
        specifications_edittext_division = (EditText)view.findViewById(R.id.specifications_edittext_division);
        specifications_edittext_section = (EditText)view.findViewById(R.id.specifications_edittext_section);
        specifications_edittext_type = (EditText)view.findViewById(R.id.specifications_edittext_type);
        specifications_edittext_keywords = (EditText)view.findViewById(R.id.specifications_edittext_keywords);
        specifications_edittext_remarks = (EditText)view.findViewById(R.id.specifications_edittext_remarks);
        specifications_btn_keyword = (Button)view.findViewById(R.id.specifications_button_addkeyword);
        specifications_btn_remark = (Button)view.findViewById(R.id.specifications_button_addremark);
        specifications_btn_choosefile = (Button)view.findViewById(R.id.specifications_btn_uploadfile);
        specifications_btn_upload = (Button)view.findViewById(R.id.specifications_button_upload);
        specifications_label_keywords = (AutoLabelUI)view.findViewById(R.id.specifications_label_keywords);
        specifications_label_remarks = (AutoLabelUI)view.findViewById(R.id.specifications_label_remark);
        specifications_label_files = (AutoLabelUI)view.findViewById(R.id.specifications_label_files);
        specifications_btn_keyword.setOnClickListener(this);
        specifications_btn_remark.setOnClickListener(this);
        specifications_btn_choosefile.setOnClickListener(this);
        specifications_btn_upload.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.specifications_button_addkeyword:
                String keyword = specifications_edittext_keywords.getText().toString().trim();
                if(!TextUtils.isEmpty(keyword))
                    specifications_label_keywords.addLabel(keyword);
                else
                    Toast.makeText(getContext(), "No input detected",Toast.LENGTH_SHORT).show();
                break;
            case R.id.specifications_button_addremark:
                String remark = specifications_edittext_remarks.getText().toString().trim();
                if(!TextUtils.isEmpty(remark))
                    specifications_label_remarks.addLabel(remark);
                else
                    Toast.makeText(getContext(), "No input detected",Toast.LENGTH_SHORT).show();
                break;
            case R.id.specifications_btn_uploadfile:
                Intent intent4 = new Intent(getContext(), NormalFilePickActivity.class);
                intent4.putExtra(Constant.MAX_NUMBER, 9);
                intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[] {"pdf"});
                startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
                break;
            case R.id.specifications_button_upload:
                if(hasNullFields() == false)
                    new SpecificationsUploadTask().execute((Void)null);
                else
                    Utilities.getInstance().showAlertBox("Error", "Fill the necessary fields", getContext());
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    filesList = list;
                    for(NormalFile file : list)
                        specifications_label_files.addLabel(file.getName());
                }
                break;
        }
    }

    private boolean hasNullFields()
    {
        boolean hasNull = true;
        String document = specifications_edittext_document.getText().toString().trim();
        String division = specifications_edittext_division.getText().toString().trim();
        String section = specifications_edittext_section.getText().toString().trim();
        String type = specifications_edittext_type.getText().toString().trim();
        if(!TextUtils.isEmpty(document) && !TextUtils.isEmpty(division) && !TextUtils.isEmpty(section) && !TextUtils.isEmpty(type))
            hasNull = false;

        return hasNull;
    }

    public ArrayList<String> specifications_extractStringsToTags()
    {
        ArrayList<String> tags = new ArrayList<>();
        if(specifications_label_keywords.getLabels().size() > 0)
        {
            for(Label label : specifications_label_keywords.getLabels())
                tags.add(label.getText());
        }
        tags.add(specifications_edittext_document.getText().toString().trim().toUpperCase());
        tags.add(specifications_edittext_division.getText().toString().trim().toUpperCase());
        tags.add(specifications_edittext_section.getText().toString().trim().toUpperCase());
        tags.add(specifications_edittext_type.getText().toString().trim().toUpperCase());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(specifications_edittext_division.getText().toString().trim());
        stringBuilder.append("-");
        stringBuilder.append(specifications_edittext_section.getText().toString().trim());
        stringBuilder.append("-");
        stringBuilder.append(specifications_edittext_type.getText().toString().trim());
        tags.add(stringBuilder.toString());
        String[] titleSplit = specifications_edittext_document.getText().toString().trim().toUpperCase().split("\\s+");
        String[] divisionSplit = specifications_edittext_division.getText().toString().trim().toUpperCase().split("\\s+");
        String[] sectionSplit = specifications_edittext_section.getText().toString().trim().toUpperCase().split("\\s+");
        String[] typeSplit = specifications_edittext_type.getText().toString().trim().toUpperCase().split("\\s+");
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

    private class SpecificationsUploadTask extends AsyncTask<Void, Void, Boolean>
    {
        AlertDialog dialog;
        public SpecificationsUploadTask() {
            dialog = new SpotsDialog.Builder()
                    .setMessage("Loading data")
                    .setContext(getContext())
                    .setCancelable(false)
                    .build();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            ArrayList<Boolean> results = new ArrayList<>();
            final List<Label> labels = specifications_label_remarks.getLabels();
            HashMap<String, String> data = new HashMap<>();
            data.put("Title", specifications_edittext_document.getText().toString().trim().toUpperCase());
            data.put("Division", specifications_edittext_division.getText().toString().trim().toUpperCase());
            data.put("Section", specifications_edittext_section.getText().toString().trim().toUpperCase());
            data.put("Type", specifications_edittext_type.getText().toString().trim().toUpperCase());

            if(Utilities.API_LEVEL >= android.os.Build.VERSION_CODES.N) //If api level is 24 or above
            {
                CompletableFuture<ParseObject> REFERENCE = CompletableFuture.supplyAsync(()->uploadPrimary.specifications_upload(data, specifications_extractStringsToTags()));
                try {reference = REFERENCE.get();}
                catch (ExecutionException e) {e.printStackTrace();}
                catch (InterruptedException e) {e.printStackTrace();}

                CompletableFuture<Void> cf = CompletableFuture.supplyAsync(()->remarksUpload.specifications_remarks_upload(labels, reference))
                        .thenAccept(result->{results.add(result);})
                        .thenApplyAsync(dat->fileUpload.specifications_file_upload(reference, filesList))
                        .thenAccept(result->{results.add(result);});
                try {cf.get();}
                catch (ExecutionException e) {e.printStackTrace();}
                catch (InterruptedException e) {e.printStackTrace();}
            }
            else
            {
                final ParseObject specificationsObject = uploadPrimary.specifications_upload(data, specifications_extractStringsToTags());
                if (!remarksUpload.specifications_remarks_upload(labels, specificationsObject))
                    results.add(false);
                if (!fileUpload.specifications_file_upload(specificationsObject, filesList))
                    results.add(false);
            }
            return results.contains(false);
        }

        @Override
        protected void onPreExecute() {dialog.show();}

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            dialog.dismiss();
            if(aBoolean)
                Utilities.getInstance().showAlertBox("Status", "Successful", getContext());
            else
                Utilities.getInstance().showAlertBox("Status", "Some of the files/remarks were not uploaded properly", getContext());
        }
    }

}
