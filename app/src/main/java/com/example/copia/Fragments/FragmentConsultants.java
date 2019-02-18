package com.example.copia.Fragments;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.dpizarro.autolabel.library.Label;
import com.example.copia.Entities.ComboboxEntity;
import com.example.copia.DatabaseOperation.FileUpload;
import com.example.copia.DatabaseOperation.ImageUpload;
import com.example.copia.DatabaseOperation.RemarksUpload;
import com.example.copia.DatabaseOperation.UploadPrimary;
import com.example.copia.R;
import com.example.copia.Utilities;
import com.parse.ParseObject;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import dmax.dialog.SpotsDialog;

public class FragmentConsultants extends Fragment implements View.OnClickListener
{
    ParseObject reference = null;
    FileUpload fileUpload = new FileUpload();
    ImageUpload imageUpload = new ImageUpload(getContext());
    UploadPrimary uploadPrimary = new UploadPrimary();
    RemarksUpload remarksUpload = new RemarksUpload();
    ArrayList<ImageFile> imageList = new ArrayList<>();
    ArrayList<NormalFile> filesList = new ArrayList<>();
    ArrayList<String> industryList = new ArrayList<>();
    ArrayList<String> classificationList = new ArrayList<>();
    EditText consultants_edittext_representative, consultants_edittext_position, consultants_edittext_company, consultants_edittext_specialization,
            consultants_edittext_addremark;
    Spinner consultants_spinner_industry, consultants_spinner_classification;
    Button consultants_btn_addremark, consultants_btn_choosefile, consultants_btn_chooseimage, consultants_btn_upload;
    AutoLabelUI consultants_label_remark, consultants_label_files;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consultants, container, false);

        List<ComboboxEntity> industry = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Consultants", "Industry");
        List<ComboboxEntity> classification = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Consultants", "Classification");
        for(ComboboxEntity entity : industry)
            industryList.add(entity.getTitle());
        for(ComboboxEntity entity : classification)
            classificationList.add(entity.getTitle());

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, industryList);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, classificationList);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        consultants_edittext_representative = (EditText)view.findViewById(R.id.consultants_edittext_representative);
        consultants_edittext_position = (EditText)view.findViewById(R.id.consultants_edittext_position);
        consultants_edittext_company = (EditText)view.findViewById(R.id.consultants_edittext_company);
        consultants_edittext_specialization = (EditText)view.findViewById(R.id.consultants_edittext_specialization);
        consultants_edittext_addremark = (EditText)view.findViewById(R.id.consultants_edittext_remarks); 
        consultants_spinner_industry = (Spinner)view.findViewById(R.id.consultants_combobox_industry);
        consultants_spinner_classification = (Spinner)view.findViewById(R.id.consultants_combobox_classification);
        consultants_spinner_industry.setAdapter(dataAdapter1);
        consultants_spinner_classification.setAdapter(dataAdapter2);
        consultants_label_remark = (AutoLabelUI)view.findViewById(R.id.consultants_label_remark);
        consultants_label_files = (AutoLabelUI)view.findViewById(R.id.consultants_label_files);
        consultants_btn_addremark = (Button)view.findViewById(R.id.consultants_button_addremark);
        consultants_btn_chooseimage = (Button)view.findViewById(R.id.consultants_btn_uploadimage);
        consultants_btn_choosefile = (Button)view.findViewById(R.id.consultants_btn_uploadfile);
        consultants_btn_upload = (Button)view.findViewById(R.id.consultants_button_upload);
        consultants_btn_addremark.setOnClickListener(this);
        consultants_btn_choosefile.setOnClickListener(this);
        consultants_btn_chooseimage.setOnClickListener(this);
        consultants_btn_upload.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        String IS_NEED_CAMERA = new String();
        switch (v.getId())
        {
            case R.id.consultants_btn_uploadfile:
                Intent intent4 = new Intent(getContext(), NormalFilePickActivity.class);
                intent4.putExtra(Constant.MAX_NUMBER, 9);
                intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[] {"pdf"});
                startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
                break;
            case R.id.consultants_btn_uploadimage:
                Intent intent1 = new Intent(getContext(), ImagePickActivity.class);
                intent1.putExtra(IS_NEED_CAMERA, true);
                intent1.putExtra(Constant.MAX_NUMBER, 9);
                startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
                break;
            case R.id.consultants_button_addremark:
                String remark = consultants_edittext_addremark.getText().toString().trim();
                if(!TextUtils.isEmpty(remark))
                    consultants_label_remark.addLabel(remark);
                else
                    Toast.makeText(getContext(), "No input detected",Toast.LENGTH_SHORT).show();
                break;
            case R.id.consultants_button_upload:
                if(hasNullFields() == false)
                    new ConsultantsUploadTask().execute((Void)null);
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
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    imageList = list;
                    for (ImageFile file : list)
                        consultants_label_files.addLabel(file.getName());
                }
                break;

            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    filesList = list;
                    for(NormalFile file : list)
                        consultants_label_files.addLabel(file.getName());
                }
                break;
        }
    }

    private boolean hasNullFields()
    {
        boolean hasNull = true;
        String representative = consultants_edittext_representative.getText().toString().trim();
        String position = consultants_edittext_position.getText().toString().trim();
        String company = consultants_edittext_company.getText().toString().trim();
        String specialization = consultants_edittext_specialization.getText().toString().trim();
        String industry = consultants_spinner_industry.getSelectedItem().toString();
        String classification = consultants_spinner_classification.getSelectedItem().toString();
        if(!TextUtils.isEmpty(representative) && !TextUtils.isEmpty(position) && !TextUtils.isEmpty(company) && !TextUtils.isEmpty(specialization) && !TextUtils.isEmpty(industry) && !TextUtils.isEmpty(classification))
                hasNull = false;

        return hasNull;
    }

    public ArrayList<String> consultants_extractStringsToTags()
    {
        ArrayList<String> tags = new ArrayList<>();
        tags.add(consultants_edittext_representative.getText().toString().trim().toUpperCase());
        tags.add(consultants_edittext_position.getText().toString().trim().toUpperCase());
        tags.add(consultants_edittext_company.getText().toString().trim().toUpperCase());
        tags.add(consultants_edittext_specialization.getText().toString().trim().toUpperCase());
        tags.add(consultants_spinner_industry.getSelectedItem().toString().toUpperCase());
        tags.add(consultants_spinner_classification.getSelectedItem().toString().toUpperCase());
        String[] representativeSplit = consultants_edittext_representative.getText().toString().trim().toUpperCase().split("\\s+");
        String[] positionSplit = consultants_edittext_position.getText().toString().trim().toUpperCase().split("\\s+");
        String[] companySplit = consultants_edittext_company.getText().toString().trim().toUpperCase().split("\\s+");
        String[] specialization = consultants_edittext_specialization.getText().toString().trim().toUpperCase().split("\\s+");
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
    
    private class ConsultantsUploadTask extends AsyncTask<Void, Void, Boolean>
    {

        AlertDialog dialog;
        public ConsultantsUploadTask()
        {
            dialog = new SpotsDialog.Builder()
                    .setMessage("Loading data")
                    .setContext(getContext())
                    .setCancelable(false)
                    .build();
        }
        @Override
        protected Boolean doInBackground(Void... voids) 
        {
            ArrayList<Boolean> results = new ArrayList<>();
            final List<Label> labels = consultants_label_remark.getLabels();
            HashMap<String, String> data = new HashMap<>();
            data.put("Representative", consultants_edittext_representative.getText().toString().trim().toUpperCase());
            data.put("Position", consultants_edittext_position.getText().toString().trim().toUpperCase());
            data.put("Company", consultants_edittext_company.getText().toString().trim().toUpperCase());
            data.put("Specialization", consultants_edittext_specialization.getText().toString().trim().toUpperCase());
            data.put("Industry", consultants_spinner_industry.getSelectedItem().toString().toUpperCase());
            data.put("Classification", consultants_spinner_classification.getSelectedItem().toString().toUpperCase());

            if(Utilities.API_LEVEL >= android.os.Build.VERSION_CODES.N) //If api level is 24 or above
            {
                CompletableFuture<ParseObject> REFERENCE = CompletableFuture.supplyAsync(()->uploadPrimary.consultants_upload(data, consultants_extractStringsToTags()));
                try {reference = REFERENCE.get();}
                catch (ExecutionException e) {e.printStackTrace();}
                catch (InterruptedException e) {e.printStackTrace();}

                CompletableFuture<Void> cf = CompletableFuture.supplyAsync(()->remarksUpload.consultants_remarks_upload(labels, reference))
                        .thenAccept(result->{results.add(result);})
                        .thenApplyAsync(dat->imageUpload.consultants_image_upload(reference, imageList))
                        .thenAccept(result->{results.add(result);})
                        .thenApplyAsync(dat->fileUpload.consultants_file_upload(reference, filesList))
                        .thenAccept(result->{results.add(result);});
                try {cf.get();}
                catch (ExecutionException e) {e.printStackTrace();}
                catch (InterruptedException e) {e.printStackTrace();}
            }
            else
            {
                final ParseObject consultantsObject = uploadPrimary.consultants_upload(data, consultants_extractStringsToTags());
                if (!remarksUpload.consultants_remarks_upload(labels, consultantsObject))
                    results.add(false);
                if (!imageUpload.consultants_image_upload(consultantsObject, imageList))
                    results.add(false);
                if (!fileUpload.consultants_file_upload(consultantsObject, filesList))
                    results.add(false);
            }
            return results.contains(false);
        }

        @Override
        protected void onPreExecute()
        {dialog.show();}

        @Override
        protected void onPostExecute(Boolean aBoolean) 
        {
            dialog.dismiss();
            if(aBoolean)
                Utilities.getInstance().showAlertBox("Status", "Successful", getContext());
            else
                Utilities.getInstance().showAlertBox("Status", "Some of the files/remarks were not uploaded properly", getContext());
        }
    }
}
