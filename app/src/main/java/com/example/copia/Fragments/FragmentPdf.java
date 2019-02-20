package com.example.copia.Fragments;


import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.copia.Adapters.PDFAdapter;
import com.example.copia.Entities.PDFEntity;
import com.example.copia.MainActivity;
import com.example.copia.R;
import com.example.copia.Utilities;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPdf extends Fragment
{
    boolean finished = false;
    List<PDFEntity> pdfEntities;
    RecyclerView pdf_recyclerview;
    PDFAdapter pdfAdapter;
    public FragmentPdf() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_fragment_pdf, container, false);
        pdfEntities = new ArrayList<>();
        pdf_recyclerview = (RecyclerView)view.findViewById(R.id.pdf_recyclerview);
        pdf_recyclerview.setHasFixedSize(true);
        pdf_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        String objectId = ((MainActivity)getActivity()).getObjectId();
        new PDFRetrieveTask(objectId).execute((Void)null);
        return view;
    }

    private class PDFRetrieveTask extends AsyncTask<Void, Void, List<PDFEntity>>
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        AlertDialog dialog;
        String objectId;

        public PDFRetrieveTask(String objectId) {
            pdfEntities = new ArrayList<>();
            this.dialog = dialog;
            this.objectId = objectId;
            dialog = new SpotsDialog.Builder()
                    .setMessage("Loading PDF files")
                    .setContext(getContext())
                    .setCancelable(false)
                    .build();
        }

        @Override
        protected List<PDFEntity> doInBackground(Void... voids) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("PDFFiles");
            query.whereEqualTo("Parent", objectId);
            query.whereEqualTo("Deleted", false);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null && objects != null)
                    {
                        for(ParseObject object : objects)
                        {
                            PDFEntity pdfEntity = new PDFEntity();
                            pdfEntity.setFilename(object.getString("Name"));
                            pdfEntity.setCreatedAt(simpleDateFormat.format(object.getCreatedAt()));
                            try
                            {
                                pdfEntity.setFile(object.getParseFile("Files").getData());
                            }
                            catch (ParseException e1) {e1.printStackTrace();}
                            pdfEntities.add(pdfEntity);
                        }
                    }
                    finished = true;
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
            return pdfEntities;
        }

        @Override
        protected void onPreExecute() {dialog.show();}

        @Override
        protected void onPostExecute(List<PDFEntity> pdfEntities) {
            dialog.dismiss();
            if(pdfEntities.size() > 0)
            {
                pdfAdapter = new PDFAdapter(getContext(), pdfEntities);
                pdf_recyclerview.setAdapter(pdfAdapter);
            }
            else
                Utilities.getInstance().showAlertBox("Response", "0 Records Found", getContext());
        }
    }

}
