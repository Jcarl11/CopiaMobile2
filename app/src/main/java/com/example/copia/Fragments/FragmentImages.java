package com.example.copia.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.copia.Adapters.ImagesAdapter;
import com.example.copia.Entities.ImagesEntity;
import com.example.copia.MainActivity;
import com.example.copia.R;
import com.example.copia.Utilities;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener;

public class FragmentImages extends Fragment {

    int pos = -1;
    ImagesAdapter imagesAdapter;
    List<ImagesEntity> imagesEntities;
    RecyclerView images_recyclerview;
    public FragmentImages() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_fragment_images, container, false);
        imagesEntities = new ArrayList<>();
        images_recyclerview = (RecyclerView)view.findViewById(R.id.images_recyclerview);
        images_recyclerview.setHasFixedSize(true);
        images_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        images_recyclerview.setOnTouchListener(listener());
        String objectId = ((MainActivity)getActivity()).getObjectId();
        new ImagesRetrieveTask(objectId).execute((Void)null);
        return view;
    }


    private SwipeDismissRecyclerViewTouchListener listener()
    {
        SwipeDismissRecyclerViewTouchListener listener = new SwipeDismissRecyclerViewTouchListener.Builder(
                images_recyclerview,
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
                                        new DeleteSingleImage(imagesEntities.get(pos).getObjectId()).execute((Void)null);
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
                        String[] choices = new String[]{"Preview", "Edit", "Download"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Choose what to to: ");
                        builder.setCancelable(true);
                        builder.setItems(choices, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                if(which == 0) {
                                    new ImageViewer.Builder<ImagesEntity>(getContext(), imagesEntities)
                                            .setFormatter(new ImageViewer.Formatter<ImagesEntity>() {
                                                @Override
                                                public String format(ImagesEntity custom) {
                                                    return custom.getUrl();
                                                }
                                            })
                                            .show();
                                }
                                else if(which == 1) {
                                }
                                else if(which == 2) {
                                    Dexter.withActivity(getActivity())
                                            .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                                            .withListener(new MultiplePermissionsListener() {
                                                @Override
                                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                                    if(report.areAllPermissionsGranted())
                                                        download();
                                                    else
                                                    {
                                                        List<PermissionDeniedResponse> deniedPermissionResponses = report.getDeniedPermissionResponses();
                                                        Utilities.getInstance().showAlertBox("Important", "Permission is need to be able to download files.", getContext());
                                                    }
                                                }

                                                @Override
                                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                                    token.continuePermissionRequest();
                                                }
                                            }).check();

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


    private void download()
    {

        Picasso.get()
                .load(imagesEntities.get(pos).getUrl())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        try {
                            String name = imagesEntities.get(pos).getImageName() + ".jpg";
                            File newDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), name);
                            FileOutputStream out = new FileOutputStream(newDir);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
                            out.flush();
                            out.close();
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                        finally{
                            Utilities.getInstance().showAlertBox("Result", "Image saved", getContext());
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        Utilities.getInstance().showAlertBox("Failed", "Saving failed", getContext());
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
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

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
            query.whereEqualTo("Parent", objectId);
            query.whereEqualTo("Deleted", false);
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
                                imagesEntity.setUrl(object.getParseFile("Files").getUrl());
                                imagesEntities.add(imagesEntity);
                            } catch (ParseException e1) {e1.printStackTrace();}
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

    private class DeleteSingleImage extends AsyncTask<Void, Void, Boolean>
    {
        boolean successful = false;
        boolean finished = false;
        AlertDialog dialog;
        String objectId;

        public DeleteSingleImage(String objectId) {
            this.objectId = objectId;
            dialog = new SpotsDialog.Builder()
                    .setMessage("Deleting record")
                    .setContext(getContext())
                    .setCancelable(false)
                    .build();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
            query.getInBackground(objectId, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(e == null && object != null)
                    {
                        object.put("Deleted", true);
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
            if(aBoolean == true)
            {
                imagesEntities.remove(pos);
                imagesAdapter.notifyItemRemoved(pos);
                Utilities.getInstance().showAlertBox("Response", "Record deleted successfully", getContext());
            }
            else
                Utilities.getInstance().showAlertBox("Error", "Record was not deleted successfully", getContext());
        }
    }
}
