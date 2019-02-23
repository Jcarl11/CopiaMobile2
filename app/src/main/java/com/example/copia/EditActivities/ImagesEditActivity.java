package com.example.copia.EditActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.copia.Entities.ImagesEntity;
import com.example.copia.Fragments.FragmentImages;
import com.example.copia.R;
import com.example.copia.Utilities;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class ImagesEditActivity extends AppCompatActivity {
    ArrayList<ImageFile> imageList = new ArrayList<>();
    ImagesEntity imagesEntity;
    TextInputLayout images_edit_filename;
    ImageView images_edit_image;
    Button images_edit_choose, images_edit_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_edit);
        getSupportActionBar().setTitle("Edit image record");
        Intent intent = getIntent();
        imagesEntity = (ImagesEntity) intent.getSerializableExtra(FragmentImages.IMAGES_ENTITY);
        images_edit_filename = (TextInputLayout)findViewById(R.id.images_edit_filename);
        images_edit_image = (ImageView)findViewById(R.id.images_edit_image);
        images_edit_choose = (Button)findViewById(R.id.images_edit_choose);
        images_edit_save = (Button)findViewById(R.id.images_edit_save);
        images_edit_filename.getEditText().setText(imagesEntity.getImageName());
        Bitmap bitmap = BitmapFactory.decodeByteArray(imagesEntity.getImage(), 0, imagesEntity.getImage().length);
        images_edit_image.setImageBitmap(bitmap);
    }
    public void chooseImage(View view)
    {
        String IS_NEED_CAMERA = new String();
        Intent intent1 = new Intent(this, ImagePickActivity.class);
        intent1.putExtra(IS_NEED_CAMERA, true);
        intent1.putExtra(Constant.MAX_NUMBER, 1);
        startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    imageList = list;
                    for (ImageFile file : list)
                        Picasso.get().load(new File(file.getPath())).into(images_edit_image);
                }
                break;
        }
    }

    public void saveClicked(View view)
    {
        String filename = images_edit_filename.getEditText().getText().toString().trim();
        if(TextUtils.isEmpty(filename))
            images_edit_filename.setError("Please don't leave this area blank");
        else if(images_edit_image.getDrawable() == null)
            Utilities.getInstance().showAlertBox("", "Image is empty", ImagesEditActivity.this);
        else
        {
            images_edit_filename.setError(null);
            new ImageEditTask().execute((Void)null);
        }
    }
    private byte[] convertDrawable(Drawable drawable)
    {
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
        byte[] bitmapdata = stream.toByteArray();
        return bitmapdata;
    }
    private class ImageEditTask extends AsyncTask<Void, Void, Boolean>
    {
        byte[] newimage;
        boolean finished = false;
        boolean successful = false;
        AlertDialog dialog = Utilities.getInstance().showLoading(ImagesEditActivity.this, "Updating record", false);
        @Override
        protected Boolean doInBackground(Void... voids) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
            query.getInBackground(imagesEntity.getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(e == null && object != null)
                    {
                        newimage = convertDrawable(images_edit_image.getDrawable());
                        String newFilename = images_edit_filename.getEditText().getText().toString().trim();
                        object.put("Name", newFilename);
                            object.put("Files", new ParseFile(newFilename + ".jpg",newimage));
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
            while(finished == false)
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
                imagesEntity.setImage(newimage);
                imagesEntity.setImageName(images_edit_filename.getEditText().getText().toString().trim());
                Intent resultIntent = new Intent();
                resultIntent.putExtra(FragmentImages.IMAGES_ENTITY, imagesEntity);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
            else
            {
                Utilities.getInstance().showAlertBox("Response", "Failed", ImagesEditActivity.this);
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }
}
