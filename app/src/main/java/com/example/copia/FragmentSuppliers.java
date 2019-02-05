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

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class FragmentSuppliers extends Fragment implements View.OnClickListener
{
    ArrayList<ImageFile> imageList = new ArrayList<>();
    ArrayList<NormalFile> filesList = new ArrayList<>();
    ArrayList<String> industryList = new ArrayList<>();
    ArrayList<String> typeList = new ArrayList<>();
    AutoLabelUI mAutoLabel_remark;
    AutoLabelUI suppliers_label_files;
    EditText suppliers_edittext_representative, suppliers_edittext_position, suppliers_edittext_company,suppliers_edittext_brand,suppliers_edittext_addremark;
    Spinner suppliers_spinner_industry,suppliers_spinner_type;
    Button suppliers_btn_addremark,suppliers_btn_upload, suppliers_btn_uploadfile, suppliers_btn_uploadimage;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suppliers, container, false);

        List<ComboboxEntity> industry = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Suppliers", "Industry");
        List<ComboboxEntity> type = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Suppliers", "Type");
        for(ComboboxEntity entity : industry)
            industryList.add(entity.getTitle());
        for(ComboboxEntity entity : type)
            typeList.add(entity.getTitle());

        suppliers_edittext_representative = (EditText)view.findViewById(R.id.suppliers_edittext_representative);
        suppliers_edittext_position = (EditText)view.findViewById(R.id.suppliers_edittext_position);
        suppliers_edittext_company = (EditText)view.findViewById(R.id.suppliers_edittext_company);
        suppliers_edittext_brand = (EditText)view.findViewById(R.id.suppliers_edittext_brand);
        suppliers_edittext_addremark = (EditText)view.findViewById(R.id.suppliers_edittext_remarks);
        suppliers_spinner_industry = (Spinner)view.findViewById(R.id.suppliers_combobox_industry);
        suppliers_spinner_type = (Spinner)view.findViewById(R.id.suppliers_combobox_type);
        suppliers_btn_addremark = (Button)view.findViewById(R.id.suppliers_button_addremark);
        suppliers_btn_upload = (Button)view.findViewById(R.id.suppliers_button_upload);
        suppliers_btn_upload.setOnClickListener(this);
        suppliers_btn_uploadfile = (Button)view.findViewById(R.id.suppliers_btn_uploadfile);
        suppliers_btn_uploadfile.setOnClickListener(this);
        suppliers_btn_uploadimage = (Button)view.findViewById(R.id.suppliers_btn_uploadimage);
        suppliers_btn_uploadimage.setOnClickListener(this);
        mAutoLabel_remark = (AutoLabelUI)view.findViewById(R.id.suppliers_label_remark);
        suppliers_label_files = (AutoLabelUI)view.findViewById(R.id.suppliers_label_files);
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, industryList);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, typeList);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suppliers_spinner_industry.setAdapter(dataAdapter1);
        suppliers_spinner_type.setAdapter(dataAdapter2);
        suppliers_spinner_industry.setPrompt("Industry");
        suppliers_spinner_type.setPrompt("Type");
        suppliers_btn_addremark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String remark = suppliers_edittext_addremark.getText().toString().trim();
                if(!TextUtils.isEmpty(remark))
                    mAutoLabel_remark.addLabel(remark);
                else
                    Toast.makeText(getContext(), "No input detected",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void onClick(View v)
    {
        String IS_NEED_CAMERA = new String();
        switch(v.getId())
        {
            case R.id.suppliers_btn_uploadfile:
                Intent intent4 = new Intent(getContext(), NormalFilePickActivity.class);
                intent4.putExtra(Constant.MAX_NUMBER, 9);
                intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[] {"pdf"});
                startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
                break;
            case R.id.suppliers_btn_uploadimage:
                Intent intent1 = new Intent(getContext(), ImagePickActivity.class);
                intent1.putExtra(IS_NEED_CAMERA, true);
                intent1.putExtra(Constant.MAX_NUMBER, 9);
                startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
                break;
            case R.id.suppliers_button_upload:
                if(hasNullFields() == false)
                    new SuppliersUploadTask().execute((Void)null);
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
                        suppliers_label_files.addLabel(file.getName());
                }
                break;

            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    filesList = list;
                    for(NormalFile file : list)
                        suppliers_label_files.addLabel(file.getName());
                }
                break;
        }
    }

    private boolean hasNullFields()
    {
        boolean hasNull = true;
        String representative = suppliers_edittext_representative.getText().toString().trim();
        String position = suppliers_edittext_position.getText().toString().trim();
        String company = suppliers_edittext_company.getText().toString().trim();
        String brand = suppliers_edittext_brand.getText().toString().trim();
        String industry = suppliers_spinner_industry.getSelectedItem().toString();
        String type = suppliers_spinner_type.getSelectedItem().toString();
        if(!TextUtils.isEmpty(representative) && !TextUtils.isEmpty(position) && !TextUtils.isEmpty(company) && !TextUtils.isEmpty(brand) && !TextUtils.isEmpty(industry) && !TextUtils.isEmpty(type))
            hasNull = false;

        return hasNull;
    }

    public ArrayList<String> suppliers_extractStringsToTags()
    {
        String representative = suppliers_edittext_representative.getText().toString().trim().toUpperCase();
        String position = suppliers_edittext_position.getText().toString().trim().toUpperCase();
        String company = suppliers_edittext_company.getText().toString().trim().toUpperCase();
        String brand = suppliers_edittext_brand.getText().toString().trim().toUpperCase();
        String industry = suppliers_spinner_industry.getSelectedItem().toString().toUpperCase();
        String type = suppliers_spinner_type.getSelectedItem().toString().toUpperCase();

        ArrayList<String> tags = new ArrayList<>();
        tags.add(representative);
        tags.add(position);
        tags.add(company);
        tags.add(brand);
        tags.add(industry);
        tags.add(type);
        String[] representativeSplit = representative.split("\\s+");
        String[] positionSplit = position.split("\\s+");
        String[] companySplit = company.split("\\s+");
        String[] brandSplit = brand.split("\\s+");
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
    private class SuppliersUploadTask extends AsyncTask<Void, Void, Boolean>
    {
        FileUpload fileUpload = new FileUpload();
        ImageUpload imageUpload = new ImageUpload();
        UploadPrimary uploadPrimary = new UploadPrimary();
        RemarksUpload remarksUpload = new RemarksUpload();
        AlertDialog dialog;
        public SuppliersUploadTask()
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
            final List<Label> labels = mAutoLabel_remark.getLabels();
            HashMap<String, String> data = new HashMap<>();
            data.put("Representative", suppliers_edittext_representative.getText().toString().trim().toUpperCase());
            data.put("Position", suppliers_edittext_position.getText().toString().trim().toUpperCase());
            data.put("Company_Name", suppliers_edittext_company.getText().toString().trim().toUpperCase());
            data.put("Brand", suppliers_edittext_brand.getText().toString().trim().toUpperCase());
            data.put("Industry", suppliers_spinner_industry.getSelectedItem().toString().toUpperCase());
            data.put("Type", suppliers_spinner_type.getSelectedItem().toString().toUpperCase());

            final ParseObject suppliersObject = uploadPrimary.suppliers_upload(data, suppliers_extractStringsToTags());
            if(!remarksUpload.suppliers_remarks_upload(labels, suppliersObject))
                results.add(false);
            if(!imageUpload.suppliers_image_upload(suppliersObject, imageList))
                results.add(false);
            if(!fileUpload.suppliers_file_upload(suppliersObject, filesList))
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
