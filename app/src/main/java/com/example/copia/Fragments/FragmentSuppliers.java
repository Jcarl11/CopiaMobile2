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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.example.copia.MainActivity;
import com.example.copia.R;
import com.example.copia.Utilities;
import com.parse.ParseObject;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;
import com.vincent.filepicker.filter.entity.NormalFile;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import dmax.dialog.SpotsDialog;

public class FragmentSuppliers extends Fragment implements View.OnClickListener
{
    private static final String TAG = "FragmentSuppliers";
    ParseObject reference = null;
    ArrayList<ImageFile> imageList = new ArrayList<>();
    ArrayList<NormalFile> filesList = new ArrayList<>();
    ArrayList<String> discipline = new ArrayList<>();
    AutoLabelUI mAutoLabel_remark;
    AutoLabelUI suppliers_label_files;
    EditText suppliers_edittext_representative, suppliers_edittext_position, suppliers_edittext_company,suppliers_edittext_brand,suppliers_edittext_addremark
            ,suppliers_edittext_others;
    BetterSpinner suppliers_discipline;
    Button suppliers_btn_addremark,suppliers_btn_upload, suppliers_btn_uploadfile, suppliers_btn_uploadimage;
    String selectedDiscipline = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suppliers, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Suppliers");
        List<ComboboxEntity> disciplineList = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Suppliers", "Discipline");
        Log.d(TAG, "onCreateView: disciplineList: " + disciplineList.size());
        for(ComboboxEntity entity : disciplineList)
            discipline.add(entity.getTitle());
        discipline.add("Others");
        suppliers_edittext_representative = (EditText)view.findViewById(R.id.suppliers_edittext_representative);
        suppliers_edittext_position = (EditText)view.findViewById(R.id.suppliers_edittext_position);
        suppliers_edittext_company = (EditText)view.findViewById(R.id.suppliers_edittext_company);
        suppliers_edittext_brand = (EditText)view.findViewById(R.id.suppliers_edittext_brand);
        suppliers_edittext_addremark = (EditText)view.findViewById(R.id.suppliers_edittext_remarks);
        suppliers_edittext_others = (EditText)view.findViewById(R.id.suppliers_edittext_others);
        suppliers_btn_addremark = (Button)view.findViewById(R.id.suppliers_button_addremark);
        suppliers_btn_upload = (Button)view.findViewById(R.id.suppliers_button_upload);
        suppliers_btn_upload.setOnClickListener(this);
        suppliers_btn_uploadfile = (Button)view.findViewById(R.id.suppliers_btn_uploadfile);
        suppliers_btn_uploadfile.setOnClickListener(this);
        suppliers_btn_uploadimage = (Button)view.findViewById(R.id.suppliers_btn_uploadimage);
        suppliers_btn_uploadimage.setOnClickListener(this);
        mAutoLabel_remark = (AutoLabelUI)view.findViewById(R.id.suppliers_label_remark);
        suppliers_label_files = (AutoLabelUI)view.findViewById(R.id.suppliers_label_files);
        suppliers_discipline = (BetterSpinner) view.findViewById(R.id.suppliers_discipline);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, discipline);
        suppliers_discipline.setAdapter(myAdapter);
        suppliers_discipline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDiscipline = String.valueOf(parent.getItemAtPosition(position));
                Log.d(TAG, "onItemClick: selectedDiscipline: " + selectedDiscipline);
                suppliers_edittext_others.setVisibility(View.GONE);
                if(selectedDiscipline.equalsIgnoreCase("Others")) {
                    suppliers_edittext_others.setVisibility(View.VISIBLE);
                }
            }
        });
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
        String representative = suppliers_edittext_representative.getText().toString().trim();
        String position = suppliers_edittext_position.getText().toString().trim();
        String company = suppliers_edittext_company.getText().toString().trim();
        String brand = suppliers_edittext_brand.getText().toString().trim();
        String others = selectedDiscipline.equalsIgnoreCase("Others") ?
                suppliers_edittext_others.getText().toString().trim() : null;
        if (!TextUtils.isEmpty(representative) &&
                !TextUtils.isEmpty(position) &&
                !TextUtils.isEmpty(company) &&
                !TextUtils.isEmpty(brand) &&
                selectedDiscipline != null ) {
            if(selectedDiscipline.equalsIgnoreCase("Others")) {
                if(!TextUtils.isEmpty(others)) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    public ArrayList<String> suppliers_extractStringsToTags()
    {
        String representative = suppliers_edittext_representative.getText().toString().trim().toUpperCase();
        String position = suppliers_edittext_position.getText().toString().trim().toUpperCase();
        String company = suppliers_edittext_company.getText().toString().trim().toUpperCase();
        String brand = suppliers_edittext_brand.getText().toString().trim().toUpperCase();
        String discipline = selectedDiscipline.toUpperCase();
        String others = selectedDiscipline.equalsIgnoreCase("Others") == true ?
                suppliers_edittext_others.getText().toString().trim().toUpperCase() : null;

        ArrayList<String> tags = new ArrayList<>();
        tags.add(representative);
        tags.add(position);
        tags.add(company);
        tags.add(brand);
        tags.add(discipline);
        String[] representativeSplit = representative.split("\\s+");
        String[] positionSplit = position.split("\\s+");
        String[] companySplit = company.split("\\s+");
        String[] brandSplit = brand.split("\\s+");
        String[] othersSplit = others != null ? others.split("\\s+") : null;
        for(String values : representativeSplit)
            tags.add(values.toUpperCase());
        for(String values : positionSplit)
            tags.add(values.toUpperCase());
        for(String values : companySplit)
            tags.add(values.toUpperCase());
        for(String values : brandSplit)
            tags.add(values.toUpperCase());
        if(othersSplit != null) {
            for (String values : othersSplit)
                tags.add(values.toUpperCase());
        }
        return tags;
    }
    private class SuppliersUploadTask extends AsyncTask<Void, Void, Boolean>
    {

        UploadPrimary uploadPrimary = new UploadPrimary();
        RemarksUpload remarksUpload = new RemarksUpload();
        ImageUpload imageUpload = new ImageUpload(getContext());
        FileUpload fileUpload = new FileUpload();
        AlertDialog dialog;
        public SuppliersUploadTask() {
            dialog = Utilities.getInstance().showLoading(getContext(), "Please wait", false);
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
            data.put("Discipline", selectedDiscipline.equalsIgnoreCase("Others") == true ?
                    suppliers_edittext_others.getText().toString().trim().toUpperCase() :
                    selectedDiscipline.toUpperCase());
            Log.d(TAG, "doInBackground: Discipline: "  + selectedDiscipline);


            if(Utilities.API_LEVEL >= android.os.Build.VERSION_CODES.N) //If api level is 24 or above
            {
                CompletableFuture<ParseObject> REFERENCE = CompletableFuture.supplyAsync(()->uploadPrimary.suppliers_upload(data, suppliers_extractStringsToTags()));
                try {reference = REFERENCE.get();}
                catch (ExecutionException e) {e.printStackTrace();}
                catch (InterruptedException e) {e.printStackTrace();}

                CompletableFuture<Void> cf = CompletableFuture.supplyAsync(()->remarksUpload.suppliers_remarks_upload(labels, reference))
                        .thenAccept(result->{results.add(result);})
                        .thenApplyAsync(dat->imageUpload.suppliers_image_upload(reference, imageList))
                        .thenAccept(result->{results.add(result);})
                        .thenApplyAsync(dat->fileUpload.suppliers_file_upload(reference, filesList))
                        .thenAccept(result->{results.add(result);});
                try {cf.get();}
                catch (ExecutionException e) {e.printStackTrace();}
                catch (InterruptedException e) {e.printStackTrace();}
            }
            else
            {
                final ParseObject suppliersObject = uploadPrimary.suppliers_upload(data, suppliers_extractStringsToTags());
                if (!remarksUpload.suppliers_remarks_upload(labels, suppliersObject))
                    results.add(false);
                if (!imageUpload.suppliers_image_upload(suppliersObject, imageList))
                    results.add(false);
                if (!fileUpload.suppliers_file_upload(suppliersObject, filesList))
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
