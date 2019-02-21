package com.example.copia.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.copia.Adapters.PDFAdapter;
import com.example.copia.Entities.PDFEntity;
import com.example.copia.MainActivity;
import com.example.copia.R;
import com.example.copia.Utilities;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPdf extends Fragment
{
    int pos = -1;
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
        pdf_recyclerview.setOnTouchListener(listener());
        String objectId = ((MainActivity)getActivity()).getObjectId();
        new PDFRetrieveTask(objectId).execute((Void)null);
        return view;
    }

    private SwipeDismissRecyclerViewTouchListener listener()
    {
        SwipeDismissRecyclerViewTouchListener listener = new SwipeDismissRecyclerViewTouchListener.Builder(
                pdf_recyclerview,
                new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        pos = position;
                        return true;
                    }

                    @Override
                    public void onDismiss(View view) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                switch (which)
                                {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        new DeleteSingleFile(pdfEntities.get(pos).getObjectId()).execute((Void)null);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("This record will be deleted").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                    }
                })
                .setIsVertical(false)
                .setItemTouchCallback(
                        new SwipeDismissRecyclerViewTouchListener.OnItemTouchCallBack() {
                            @Override
                            public void onTouch(int index) {
                                // Do what you want when item be touched
                            }
                        })
                .setItemClickCallback(new SwipeDismissRecyclerViewTouchListener.OnItemClickCallBack() {
                    @Override
                    public void onClick(int i) {
                        String[] choices = new String[]{"Edit", "Download"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Choose what to to: ");
                        builder.setCancelable(true);
                        builder.setItems(choices, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                if(which == 0) {
                                }
                                else if(which == 1) {
                                    String filename = pdfEntities.get(pos).getFilename() + ".pdf";
                                    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
                                    try {
                                        FileOutputStream fileOutputStream = new FileOutputStream(dir);
                                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                                        bufferedOutputStream.write(pdfEntities.get(pos).getFile());
                                        bufferedOutputStream.flush();
                                        bufferedOutputStream.close();
                                        fileOutputStream.flush();
                                        fileOutputStream.close();
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    finally {
                                        Utilities.getInstance().showAlertBox("Result", "File saved", getContext());
                                    }
                                }
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                })
                .create();
        return listener;
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
                            pdfEntity.setObjectId(object.getObjectId());
                            pdfEntity.setFilename(object.getString("Name"));
                            pdfEntity.setUrl(object.getParseFile("Files").getUrl());
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

    private class DeleteSingleFile extends AsyncTask<Void, Void, Boolean>
    {
        boolean successful = false;
        boolean finished = false;
        AlertDialog dialog;
        String objectId;

        public DeleteSingleFile(String objectId) {
            this.objectId = objectId;
            dialog = new SpotsDialog.Builder()
                    .setMessage("Deleting record")
                    .setContext(getContext())
                    .setCancelable(false)
                    .build();
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("PDFFiles");
            query.getInBackground(objectId, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(e == null && object != null)
                    {
                        object.put("Deleted", true);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null)
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
            if(aBoolean == true)
            {
                pdfEntities.remove(pos);
                pdfAdapter.notifyItemRemoved(pos);
                Utilities.getInstance().showAlertBox("Response", "Record deleted successfully", getContext());
            }
            else
                Utilities.getInstance().showAlertBox("Error", "Record was not deleted successfully", getContext());
        }
    }
}
