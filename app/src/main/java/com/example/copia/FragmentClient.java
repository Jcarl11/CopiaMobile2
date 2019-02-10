package com.example.copia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import com.dpizarro.autolabel.library.AutoLabelUISettings;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import dmax.dialog.SpotsDialog;


public class FragmentClient extends Fragment implements View.OnClickListener
{
    ArrayList<ImageFile> imageList = new ArrayList<>();
    ArrayList<NormalFile> filesList = new ArrayList<>();
    ArrayList<String> industryList = new ArrayList<>();
    ArrayList<String> typeList = new ArrayList<>();
    AutoLabelUI mAutoLabel_remark;
    AutoLabelUI label_files;
    Button client_button_upload,client_btn_addremark,client_btn_uploadfile, client_btn_uploadimage;
    EditText client_edittext_remark,client_edittext_representative,client_edittext_position,client_edittext_company;
    Spinner spinner_industry, spinner_type;
    ParseObject reference = null;
    UploadPrimary uploadPrimary = new UploadPrimary();
    RemarksUpload remarksUpload = new RemarksUpload();
    ImageUpload imageUpload = new ImageUpload();
    FileUpload fileUpload = new FileUpload();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client,container,false);
        List<ComboboxEntity> industry = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Client", "Industry");
        List<ComboboxEntity> type = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Client", "Type");
        for(ComboboxEntity entity : industry)
            industryList.add(entity.getTitle());
        for(ComboboxEntity entity : type)
            typeList.add(entity.getTitle());
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, industryList);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, typeList);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        client_button_upload = (Button)view.findViewById(R.id.client_button_upload);
        client_button_upload.setOnClickListener(this);
        client_edittext_representative = (EditText)view.findViewById(R.id.client_edittext_representative);
        client_edittext_position = (EditText)view.findViewById(R.id.client_edittext_position);
        client_edittext_company = (EditText)view.findViewById(R.id.client_edittext_company);
        client_btn_addremark = (Button)view.findViewById(R.id.client_button_addremark);
        client_btn_uploadimage = (Button)view.findViewById(R.id.client_btn_uploadimage);
        client_btn_uploadimage.setOnClickListener(this);
        client_btn_uploadfile = (Button)view.findViewById(R.id.client_btn_uploadfile);
        client_btn_uploadfile.setOnClickListener(this);
        client_edittext_remark = (EditText)view.findViewById(R.id.client_edittext_remarks);
        spinner_industry = (Spinner)view.findViewById(R.id.combobox_industry);
        spinner_type = (Spinner)view.findViewById(R.id.combobox_type);
        spinner_industry.setAdapter(dataAdapter1);
        spinner_type.setAdapter(dataAdapter2);
        mAutoLabel_remark = (AutoLabelUI)view.findViewById(R.id.label_remark);
        label_files = (AutoLabelUI)view.findViewById(R.id.label_files);
        AutoLabelUISettings autoLabelUISettings = new AutoLabelUISettings.Builder().build();
        client_btn_addremark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String remark = client_edittext_remark.getText().toString().trim();
                if(!TextUtils.isEmpty(remark))
                    mAutoLabel_remark.addLabel(remark);
                else
                    Toast.makeText(getContext(), "No input detected",Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    public ArrayList<String> convertTags(ArrayList<Label> tags)
    {
        ArrayList<String> tagsList = new ArrayList<>();
        for(Label labels : tags)
        {
            tagsList.add(labels.getText().trim());
        }
        return tagsList;
    }


    @Override
    public void onClick(View v) {
        String IS_NEED_CAMERA = new String();
        switch (v.getId())
        {
            case R.id.client_btn_uploadfile:
                Intent intent4 = new Intent(getContext(), NormalFilePickActivity.class);
                intent4.putExtra(Constant.MAX_NUMBER, 9);
                intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[] {"pdf"});
                startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
                break;
            case R.id.client_btn_uploadimage:
                Intent intent1 = new Intent(getContext(), ImagePickActivity.class);
                intent1.putExtra(IS_NEED_CAMERA, true);
                intent1.putExtra(Constant.MAX_NUMBER, 9);
                startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
                break;
            case R.id.client_button_upload:
                if(hasNullFields() == false)
                {
                    new ClientUploadTask().execute((Void)null);
                }
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
                        label_files.addLabel(file.getName());
                }
                break;

            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    filesList = list;
                    for(NormalFile file : list)
                        label_files.addLabel(file.getName());
                }
                break;
        }
    }

    private boolean hasNullFields()
    {
        boolean hasNull = true;
        String representative = client_edittext_representative.getText().toString().trim();
        String position = client_edittext_position.getText().toString().trim();
        String company = client_edittext_company.getText().toString().trim();
        String industry = spinner_industry.getSelectedItem().toString();
        String type = spinner_type.getSelectedItem().toString();
        if(!TextUtils.isEmpty(representative) && !TextUtils.isEmpty(position) && !TextUtils.isEmpty(company) && !TextUtils.isEmpty(industry) && !TextUtils.isEmpty(type))
            hasNull = false;

        return hasNull;
    }

    public ArrayList<String> client_extractStringsToTags()
    {
        String representative = client_edittext_representative.getText().toString().trim().toUpperCase();
        String position = client_edittext_position.getText().toString().trim().toUpperCase();
        String company = client_edittext_company.getText().toString().trim().toUpperCase();
        String industry = spinner_industry.getSelectedItem().toString().toUpperCase();
        String type = spinner_type.getSelectedItem().toString().toUpperCase();

        ArrayList<String> tags = new ArrayList<>();
        tags.add(representative);
        tags.add(position);
        tags.add(company);
        tags.add(industry);
        tags.add(type);
        String[] representativeSplit = representative.split("\\s+");
        String[] positionSplit = position.split("\\s+");
        String[] companySplit = company.split("\\s+");
        for(String values : representativeSplit)
            tags.add(values.toUpperCase());
        for(String values : positionSplit)
            tags.add(values.toUpperCase());
        for(String values : companySplit)
            tags.add(values.toUpperCase());
        return tags;
    }

    private class ClientUploadTask extends AsyncTask<Void, Void, Boolean>
    {
        AlertDialog dialog;
        public ClientUploadTask()
        {
            dialog = new SpotsDialog.Builder()
                    .setMessage("Loading data")
                    .setContext(getContext())
                    .setCancelable(false)
                    .build();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Boolean doInBackground(Void... voids) {
            ArrayList<Boolean> results = new ArrayList<>();
            final List<Label> labels = mAutoLabel_remark.getLabels();
            HashMap<String, String> data = new HashMap<>();
            data.put("Representative", client_edittext_representative.getText().toString().trim().toUpperCase());
            data.put("Position", client_edittext_position.getText().toString().trim().toUpperCase());
            data.put("Company", client_edittext_company.getText().toString().trim().toUpperCase());
            data.put("Industry", spinner_industry.getSelectedItem().toString().toUpperCase());
            data.put("Type", spinner_type.getSelectedItem().toString().toUpperCase());

            if(Utilities.API_LEVEL >= android.os.Build.VERSION_CODES.N) //If api level is 24 or above
            {
                CompletableFuture<ParseObject> REFERENCE = CompletableFuture.supplyAsync(()->uploadPrimary.client_upload(data, client_extractStringsToTags()));
                try {reference = REFERENCE.get();}
                catch (ExecutionException e) {e.printStackTrace();}
                catch (InterruptedException e) {e.printStackTrace();}

                CompletableFuture<Void> cf = CompletableFuture.supplyAsync(()->remarksUpload.client_remarks_upload(labels, reference))
                                                    .thenAccept(result->{results.add(result);})
                                                    .thenApplyAsync(dat->imageUpload.client_image_upload(reference, imageList))
                                                    .thenAccept(result->{results.add(result);})
                                                    .thenApplyAsync(dat->fileUpload.client_file_upload(reference, filesList))
                                                    .thenAccept(result->{results.add(result);});
                try {cf.get();}
                catch (ExecutionException e) {e.printStackTrace();}
                catch (InterruptedException e) {e.printStackTrace();}
            }
            else
            {
                final ParseObject clientObject = uploadPrimary.client_upload(data, client_extractStringsToTags());
                if(!remarksUpload.client_remarks_upload(labels, clientObject))
                    results.add(false);
                if(!imageUpload.client_image_upload(clientObject, imageList))
                    results.add(false);
                if(!fileUpload.client_file_upload(clientObject, filesList))
                    results.add(false);
            }
            return results.contains(false);
        }

        @Override
        protected void onPreExecute() {dialog.show();}

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
