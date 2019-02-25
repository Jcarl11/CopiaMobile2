package com.example.copia.EditActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.example.copia.Entities.PDFEntity;
import com.example.copia.Fragments.FragmentPdf;
import com.example.copia.R;
import com.example.copia.Utilities;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FileAddActivity extends AppCompatActivity {
    public static String PDF_ENTITY_NEWRECORD = "PDF_ENTITY_NEWRECORD";
    String objectId;
    TextInputLayout file_add_filename;
    ImageView file_add_imageview;
    AutoLabelUI file_add_files;
    Button file_add_choosefilebtn, file_add_addbtn;
    String filePath = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_add);
        getSupportActionBar().setTitle("New file");
        Intent object = getIntent();
        objectId = object.getStringExtra(FragmentPdf.PDF_ENTITY_PARENT);
        file_add_filename = (TextInputLayout)findViewById(R.id.file_add_filename);
        file_add_imageview = (ImageView)findViewById(R.id.file_add_imageview);
        file_add_files = (AutoLabelUI)findViewById(R.id.file_add_files);
        file_add_choosefilebtn = (Button)findViewById(R.id.file_add_choosefilebtn);
        file_add_addbtn = (Button)findViewById(R.id.file_add_addbtn);
        file_add_files.setOnRemoveLabelListener(new AutoLabelUI.OnRemoveLabelListener() {
            @Override
            public void onRemoveLabel(View view, int position) {
                file_add_imageview.setImageDrawable(null);
                filePath = null;
            }
        });
    }

    public void file_chooseClicked(View view) {
        Intent intent4 = new Intent(this, NormalFilePickActivity.class);
        intent4.putExtra(Constant.MAX_NUMBER, 1);
        intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[] {"pdf"});
        startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    for(NormalFile file : list) {
                        file_add_files.clear();
                        file_add_imageview.setImageDrawable(getDrawable(R.drawable.ic_file));
                        file_add_files.addLabel(file.getName());
                        filePath = list.get(0).getPath();
                    }
                }
                break;
        }
    }

    public void file_addClicked(View view) {
        String fileName = file_add_filename.getEditText().getText().toString().trim();
        if(TextUtils.isEmpty(fileName))
            file_add_filename.setError("Please don't leave empty fields");
        else if(file_add_imageview.getDrawable() == null)
            Utilities.getInstance().showAlertBox("","Choose a file", this);
        else {
            file_add_filename.setError(null);
            new AddNewFileTask().execute((Void)null);
        }
    }

    private class AddNewFileTask extends AsyncTask<Void, Void, PDFEntity>
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        PDFEntity pdfEntity = null;
        AlertDialog dialog = Utilities.getInstance().showLoading(FileAddActivity.this, "Adding file", false);
        String filename = file_add_filename.getEditText().getText().toString().trim();
        File fileLocation = new File(filePath);
        byte[] file;
        {
            try {
                file = FileUtils.readFileToByteArray(fileLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected PDFEntity doInBackground(Void... voids) {

            ParseObject query = new ParseObject("PDFFiles");
            query.put("Files", new ParseFile(filename + ".pdf", file));
            query.put("Name", filename);
            query.put("Parent", objectId);
            query.put("Size", file.length / 1024);
            query.put("Deleted", false);
            try
            {
                query.save();
                pdfEntity = new PDFEntity();
                pdfEntity.setFilename(filename);
                pdfEntity.setSize(String.valueOf(file.length / 1024));
                pdfEntity.setUrl(query.getParseFile("Files").getUrl());
                pdfEntity.setObjectId(query.getObjectId());
                pdfEntity.setCreatedAt(simpleDateFormat.format(query.getCreatedAt()));
            }
            catch (ParseException e) {e.printStackTrace();}
            return pdfEntity;
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(PDFEntity pdfEntity) {
            dialog.dismiss();
            if(pdfEntity != null)
            {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(PDF_ENTITY_NEWRECORD, pdfEntity);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
            else
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }
}
