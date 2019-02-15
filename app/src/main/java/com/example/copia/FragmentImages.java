package com.example.copia;


import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.copia.Entities.ImagesEntity;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.PointerEncoder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class FragmentImages extends Fragment {

    ImagesAdapter imagesAdapter;
    List<ImagesEntity> imagesEntities;
    RecyclerView images_recyclerview;
    public FragmentImages() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_images, container, false);
        imagesEntities = new ArrayList<>();
        images_recyclerview = (RecyclerView)view.findViewById(R.id.images_recyclerview);
        images_recyclerview.setHasFixedSize(true);
        images_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        String objectId = ((MainActivity)getActivity()).getObjectId();
        new ImagesRetrieveTask(objectId).execute((Void)null);
        return view;
    }

    private class ImagesRetrieveTask extends AsyncTask<Void, Void, List<ImagesEntity>>
    {

        boolean finished = false;
        AlertDialog dialog;
        String objectId = null;
        public ImagesRetrieveTask(String objectId) {
            this.objectId = objectId;
            imagesEntities = new ArrayList<>();
            dialog = new SpotsDialog.Builder()
                    .setMessage("Loading Images")
                    .setContext(getContext())
                    .setCancelable(false)
                    .build();
        }

        @Override
        protected List<ImagesEntity> doInBackground(Void... voids) {
            try {
            ParseQuery<ParseObject> client = ParseQuery.getQuery("Client");
            client.get(objectId);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
            query.include("ClientPointer");
            query.whereMatchesQuery("ClientPointer", client);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null && objects != null)
                    {
                        for(ParseObject object : objects)
                        {
                            ImagesEntity imagesEntity = new ImagesEntity();
                            try {
                                byte[] data = object.getParseFile("Files").getData();
                                imagesEntity.setImage(data);
                                imagesEntity.setImageName(object.getString("Name"));
                                imagesEntity.setObjectId(object.getObjectId());
                                imagesEntity.setSize(String.valueOf(data.length/1024));
                                imagesEntities.add(imagesEntity);
                            } catch (ParseException e1) {e1.printStackTrace();}
                        }
                    }
                    finished = true;
                }
            });
            } catch (ParseException e) {
                e.printStackTrace();
            }
            while(finished == false)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return imagesEntities;
        }

        @Override
        protected void onPreExecute() {dialog.show();}

        @Override
        protected void onPostExecute(List<ImagesEntity> imagesEntities) {
            dialog.dismiss();
            if(imagesEntities.size() > 0)
            {
                imagesAdapter = new ImagesAdapter(getContext(), imagesEntities);
                images_recyclerview.setAdapter(imagesAdapter);
            }
            else
                Utilities.getInstance().showAlertBox("Response", "0 Records Found", getContext());
        }
    }
}
