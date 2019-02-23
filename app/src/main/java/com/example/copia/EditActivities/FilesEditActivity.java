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

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.example.copia.Entities.PDFEntity;
import com.example.copia.Fragments.FragmentPdf;
import com.example.copia.R;
import com.example.copia.Utilities;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FilesEditActivity extends AppCompatActivity {
    ArrayList<NormalFile> filesList = new ArrayList<>();
    PDFEntity pdfEntity;
    TextInputLayout file_edit_filename;
    AutoLabelUI file_edit_files;
    Button file_edit_save, file_edit_replace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_edit);
        getSupportActionBar().setTitle("Edit files");
        Intent intent = getIntent();
        pdfEntity = (PDFEntity) intent.getSerializableExtra(FragmentPdf.PDF_ENTITY);
        file_edit_filename = (TextInputLayout)findViewById(R.id.file_edit_filename);
        file_edit_files = (AutoLabelUI)findViewById(R.id.file_edit_files);
        file_edit_save = (Button)findViewById(R.id.file_edit_save);
        file_edit_replace = (Button)findViewById(R.id.file_edit_replace);
        file_edit_files.addLabel(pdfEntity.getFilename());
        file_edit_filename.getEditText().setText(pdfEntity.getFilename());
        file_edit_files.setOnRemoveLabelListener(new AutoLabelUI.OnRemoveLabelListener() {
            @Override
            public void onRemoveLabel(View view, int position) {
                filesList.clear();
            }
        });
    }

    public void saveOnClick(View view) {
        String filename = file_edit_filename.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(filename))
            file_edit_filename.setError("Please don't leave this area blank");
        else if (file_edit_files.getLabels().size() <= 0)
            Utilities.getInstance().showAlertBox("No file", "Please choose a file", this);
        else
        {
            file_edit_filename.setError(null);
            new UpdatePDFFiles().execute((Void)null);
        }

    }

    public void replaceOnClick(View view)
    {
        openFileChooser();
    }
    private void openFileChooser()
    {
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
                    filesList.clear();
                    filesList = list;
                    for(NormalFile file : list) {
                        file_edit_files.clear();
                        file_edit_files.addLabel(file.getName());
                    }
                }
                break;
        }
    }
    private class UpdatePDFFiles extends AsyncTask<Void, Void, Boolean>
    {
        AlertDialog dialog = Utilities.getInstance().showLoading(FilesEditActivity.this, "Updating record", false);
        boolean successful = false;
        boolean finished = false;
        @Override
        protected Boolean doInBackground(Void... voids) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("PDFFiles");
            query.getInBackground(pdfEntity.getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(e == null && object != null)
                    {
                        String fileName = file_edit_filename.getEditText().getText().toString().trim();
                        object.put("Name", fileName);
                        if(filesList.size() >= 0)
                        {
                            double size = filesList.get(0).getSize() / 1024;
                            object.put("Size", filesList.get(0).getSize() / 1024);
                            pdfEntity.setSize(String.valueOf(size));
                            try {
                                object.put("Files", new ParseFile(fileName + ".pdf", FileUtils.readFileToByteArray(new File(filesList.get(0).getPath()))));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null)
                                    successful = true;
                                finished = true;
                            }
                        });
                    }
                }
            });
            while (finished == false)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return successful;
        }

        @Override
        protected void onPreExecute() {dialog.show();}

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            dialog.dismiss();
            if(aBoolean)
            {
                pdfEntity.setFilename(file_edit_filename.getEditText().getText().toString().trim());
                Intent result = new Intent();
                result.putExtra(FragmentPdf.PDF_ENTITY, pdfEntity);
                setResult(RESULT_OK, result);
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
