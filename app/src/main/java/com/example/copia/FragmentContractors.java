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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.dpizarro.autolabel.library.Label;
import com.example.copia.DatabaseOperation.FileUpload;
import com.example.copia.DatabaseOperation.ImageUpload;
import com.example.copia.DatabaseOperation.RemarksUpload;
import com.example.copia.DatabaseOperation.UploadPrimary;
import com.parse.ParseObject;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class FragmentContractors extends Fragment implements View.OnClickListener
{
    ArrayList<ImageFile> imageList = new ArrayList<>();
    ArrayList<NormalFile> filesList = new ArrayList<>();
    ArrayList<String> industryList = new ArrayList<>();
    ArrayList<String> classificationList = new ArrayList<>();
    EditText contractors_edittext_representative, contractors_edittext_position, contractors_edittext_company, contractors_edittext_specialization,
            contractors_edittext_addremark;
    Spinner contractors_spinner_industry, contractors_spinner_classification;
    Button contractors_btn_addremark, contractors_btn_choosefile, contractors_btn_chooseimage, contractors_btn_upload;
    AutoLabelUI contractors_label_remark, contractors_label_files;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contractors, container, false);

        List<ComboboxEntity> industry = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Contractors", "Industry");
        List<ComboboxEntity> classification = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Contractors", "Classification");
        for(ComboboxEntity entity : industry)
            industryList.add(entity.getTitle());
        for(ComboboxEntity entity : classification)
            classificationList.add(entity.getTitle());

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, industryList);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, classificationList);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        contractors_edittext_representative = (EditText) view.findViewById(R.id.contractors_edittext_representative);
        contractors_edittext_position = (EditText)view.findViewById(R.id.contractors_edittext_position);
        contractors_edittext_company = (EditText) view.findViewById(R.id.contractors_edittext_company);
        contractors_edittext_specialization = (EditText) view.findViewById(R.id.contractors_edittext_specialization);
        contractors_edittext_addremark = (EditText)view.findViewById(R.id.contractors_edittext_remarks);
        contractors_spinner_industry = (Spinner) view.findViewById(R.id.contractors_combobox_industry);
        contractors_spinner_classification = (Spinner) view.findViewById(R.id.contractors_combobox_classification);
        contractors_spinner_industry.setAdapter(dataAdapter1);
        contractors_spinner_classification.setAdapter(dataAdapter2);
        contractors_label_remark = (AutoLabelUI)view.findViewById(R.id.contractors_label_remark);
        contractors_label_files = (AutoLabelUI)view.findViewById(R.id.contractors_label_files);
        contractors_btn_addremark = (Button)view.findViewById(R.id.contractors_btn_addremark);
        contractors_btn_choosefile = (Button)view.findViewById(R.id.contractors_btn_uploadfile);
        contractors_btn_chooseimage = (Button)view.findViewById(R.id.contractors_btn_uploadimage);
        contractors_btn_upload = (Button)view.findViewById(R.id.contractors_btn_upload);
        contractors_btn_addremark.setOnClickListener(this);
        contractors_btn_choosefile.setOnClickListener(this);
        contractors_btn_chooseimage.setOnClickListener(this);
        contractors_btn_upload.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v)
    {
        String IS_NEED_CAMERA = new String();
        switch (v.getId())
        {
            case R.id.contractors_btn_addremark:
                String remark = contractors_edittext_addremark.getText().toString().trim();
                if(!TextUtils.isEmpty(remark))
                    contractors_label_remark.addLabel(remark);
                else
                    Toast.makeText(getContext(), "No input detected",Toast.LENGTH_SHORT).show();
                break;
            case R.id.contractors_btn_uploadfile:
                Intent intent4 = new Intent(getContext(), NormalFilePickActivity.class);
                intent4.putExtra(Constant.MAX_NUMBER, 9);
                intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[] {"pdf"});
                startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
                break;
            case R.id.contractors_btn_uploadimage:
                Intent intent1 = new Intent(getContext(), ImagePickActivity.class);
                intent1.putExtra(IS_NEED_CAMERA, true);
                intent1.putExtra(Constant.MAX_NUMBER, 9);
                startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
                break;
            case R.id.contractors_btn_upload:
                if(hasNullFields() == false)
                    new ContractorsUploadTask().execute((Void)null);
                else
                    Utilities.getInstance().showAlertBox("Error", "Fill the necessary fields", getContext());
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    imageList = list;
                    for (ImageFile file : list)
                        contractors_label_files.addLabel(file.getName());
                }
                break;

            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    filesList = list;
                    for(NormalFile file : list)
                        contractors_label_files.addLabel(file.getName());
                }
                break;
        }
    }

    private boolean hasNullFields()
    {
        boolean hasNull = true;
        String representative = contractors_edittext_representative.getText().toString().trim();
        String position = contractors_edittext_position.getText().toString().trim();
        String company = contractors_edittext_company.getText().toString().trim();
        String specialization = contractors_edittext_specialization.getText().toString().trim();
        String industry = contractors_spinner_industry.getSelectedItem().toString();
        String classification = contractors_spinner_classification.getSelectedItem().toString();
        if(!TextUtils.isEmpty(representative) && !TextUtils.isEmpty(position) && !TextUtils.isEmpty(company) && !TextUtils.isEmpty(specialization) && !TextUtils.isEmpty(industry) && !TextUtils.isEmpty(classification))
            hasNull = false;

        return hasNull;
    }

    public ArrayList<String> contractors_extractStringsToTags()
    {
        ArrayList<String> tags = new ArrayList<>();
        tags.add(contractors_edittext_representative.getText().toString().trim().toUpperCase());
        tags.add(contractors_edittext_position.getText().toString().trim().toUpperCase());
        tags.add(contractors_edittext_company.getText().toString().trim().toUpperCase());
        tags.add(contractors_edittext_specialization.getText().toString().trim().toUpperCase());
        tags.add(contractors_spinner_industry.getSelectedItem().toString().toUpperCase());
        tags.add(contractors_spinner_classification.getSelectedItem().toString().toUpperCase());
        String[] representativeSplit = contractors_edittext_representative.getText().toString().trim().toUpperCase().split("\\s+");
        String[] positionSplit = contractors_edittext_position.getText().toString().trim().toUpperCase().split("\\s+");
        String[] companySplit = contractors_edittext_company.getText().toString().trim().toUpperCase().split("\\s+");
        String[] specialization = contractors_edittext_specialization.getText().toString().trim().toUpperCase().split("\\s+");
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


    private class ContractorsUploadTask extends AsyncTask<Void, Void, Boolean>
    {
        FileUpload fileUpload = new FileUpload();
        ImageUpload imageUpload = new ImageUpload();
        UploadPrimary uploadPrimary = new UploadPrimary();
        RemarksUpload remarksUpload = new RemarksUpload();
        AlertDialog dialog;
        public ContractorsUploadTask()
        {
            dialog = new SpotsDialog.Builder()
                    .setMessage("Loading data")
                    .setContext(getContext())
                    .setCancelable(false)
                    .build();
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            ArrayList<Boolean> results = new ArrayList<>();
            final List<Label> labels = contractors_label_remark.getLabels();
            HashMap<String, String> data = new HashMap<>();
            data.put("Representative", contractors_edittext_representative.getText().toString().trim().toUpperCase());
            data.put("Position", contractors_edittext_position.getText().toString().trim().toUpperCase());
            data.put("Company", contractors_edittext_company.getText().toString().trim().toUpperCase());
            data.put("Specialization", contractors_edittext_specialization.getText().toString().trim().toUpperCase());
            data.put("Industry", contractors_spinner_industry.getSelectedItem().toString().toUpperCase());
            data.put("Classification", contractors_spinner_classification.getSelectedItem().toString().toUpperCase());

            final ParseObject contractorsObject = uploadPrimary.contractors_upload(data, contractors_extractStringsToTags());
            if(!remarksUpload.contractors_remarks_upload(labels, contractorsObject))
                results.add(false);
            if(!imageUpload.contractors_image_upload(contractorsObject, imageList))
                results.add(false);
            if(!fileUpload.contractors_file_upload(contractorsObject, filesList))
                results.add(false);
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
