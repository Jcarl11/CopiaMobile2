package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.example.copia.Adapters.PDFAdapter;
import com.example.copia.Entities.PDFEntity;
import com.example.copia.Utilities;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Files_Retrieve_Deleted_Task extends AsyncTask<Void, Void, List<PDFEntity>> {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
    PDFAdapter pdfAdapter;
    private Context context;
    private RecyclerView recyclerview;
    AlertDialog dialog;
    List<PDFEntity> pdfEntities = new ArrayList<>();

    public Files_Retrieve_Deleted_Task(Context context) {
        this.context = context;
        dialog = Utilities.getInstance().showLoading(context, "Please wait",false);
    }
    @Override
    protected List<PDFEntity> doInBackground(Void... voids) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("PDFFiles");
        query.whereEqualTo("Deleted", true);
        try
        {
            List<ParseObject> parseObjects = query.find();
            for(ParseObject object : parseObjects)
            {
                PDFEntity pdfEntity = new PDFEntity();
                pdfEntity.setObjectId(object.getObjectId());
                pdfEntity.setSize(String.valueOf(object.getNumber("Size")));
                pdfEntity.setFilename(object.getString("Name"));
                pdfEntity.setCreatedAt(simpleDateFormat.format(object.getCreatedAt()));
                pdfEntity.setUrl(object.getParseFile("Files").getUrl());
                pdfEntities.add(pdfEntity);
            }
        }
        catch (ParseException e) {e.printStackTrace();}
        return pdfEntities;
    }

    @Override
    protected void onPreExecute() {dialog.show();}

    @Override
    protected void onPostExecute(List<PDFEntity> pdfEntities) {
        dialog.dismiss();
        if(pdfEntities.size() > 0)
        {
            pdfAdapter = new PDFAdapter(context, pdfEntities);
            recyclerview.setAdapter(pdfAdapter);
        }
        else
            Utilities.getInstance().showAlertBox("Response", "0 Records found", context);
    }

    public PDFAdapter getPdfAdapter() {
        return pdfAdapter;
    }

    public void setPdfAdapter(PDFAdapter pdfAdapter) {
        this.pdfAdapter = pdfAdapter;
    }

    public RecyclerView getRecyclerview() {
        return recyclerview;
    }

    public void setRecyclerview(RecyclerView recyclerview) {
        this.recyclerview = recyclerview;
    }

    public List<PDFEntity> getPdfEntities() {
        return pdfEntities;
    }

    public void setPdfEntities(List<PDFEntity> pdfEntities) {
        this.pdfEntities = pdfEntities;
    }
}
