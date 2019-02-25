package com.example.copia.Tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.example.copia.Adapters.ImagesAdapter;
import com.example.copia.Entities.ImagesEntity;
import com.example.copia.Utilities;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Images_Retrieve_Deleted_Task extends AsyncTask<Void, Void, List<ImagesEntity>> {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
    ImagesAdapter imagesAdapter;
    private Context context;
    private RecyclerView recyclerview;
    AlertDialog dialog;
    List<ImagesEntity> imagesEntities = new ArrayList<>();

    public Images_Retrieve_Deleted_Task(Context context) {
        this.context = context;
        dialog = Utilities.getInstance().showLoading(context, "Please wait",false);
    }
    @Override
    protected List<ImagesEntity> doInBackground(Void... voids) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
        query.whereEqualTo("Deleted", true);
        try
        {
            List<ParseObject> parseObjects = query.find();
            for(ParseObject object : parseObjects)
            {
                ImagesEntity imagesEntity = new ImagesEntity();
                imagesEntity.setObjectId(object.getObjectId());
                imagesEntity.setSize(String.valueOf(object.getNumber("Size")));
                imagesEntity.setImageName(object.getString("Name"));
                imagesEntity.setUrl(object.getParseFile("Files").getUrl());
                imagesEntities.add(imagesEntity);
            }
        }
        catch (ParseException e) {e.printStackTrace();}
        return imagesEntities;
    }

    @Override
    protected void onPreExecute() {dialog.show();}

    @Override
    protected void onPostExecute(List<ImagesEntity> imagesEntities) {
        dialog.dismiss();
        if(imagesEntities.size() > 0)
        {
            imagesAdapter = new ImagesAdapter(context, imagesEntities);
            recyclerview.setAdapter(imagesAdapter);
        }
        else
            Utilities.getInstance().showAlertBox("Response", "0 Records found", context);
    }

    public ImagesAdapter getImagesAdapter() {
        return imagesAdapter;
    }

    public void setImagesAdapter(ImagesAdapter imagesAdapter) {
        this.imagesAdapter = imagesAdapter;
    }

    public RecyclerView getRecyclerview() {
        return recyclerview;
    }

    public void setRecyclerview(RecyclerView recyclerview) {
        this.recyclerview = recyclerview;
    }

    public List<ImagesEntity> getImagesEntities() {
        return imagesEntities;
    }

    public void setImagesEntities(List<ImagesEntity> imagesEntities) {
        this.imagesEntities = imagesEntities;
    }
}
