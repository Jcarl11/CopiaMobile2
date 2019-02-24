package com.example.copia.EditActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.example.copia.Entities.ImagesEntity;
import com.example.copia.Fragments.FragmentImages;
import com.example.copia.R;
import com.example.copia.Utilities;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ImagesAddActivity extends AppCompatActivity {
    public static String IMAGE_ENTITY_NEWRECORD = "NOTE_ENTITY_NEWRECORD";
    String objectId;
    TextInputLayout images_add_filename;
    ImageView images_add_imageview;
    Button images_add_addbtn, images_add_choosefilebtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_add);
        getSupportActionBar().setTitle("Add new image");
        Intent intent = getIntent();
        objectId = intent.getStringExtra(FragmentImages.IMAGES_ENTITY_PARENT);
        images_add_filename = (TextInputLayout)findViewById(R.id.images_add_filename);
        images_add_imageview = (ImageView)findViewById(R.id.images_add_imageview);
        images_add_addbtn = (Button)findViewById(R.id.images_add_addbtn);
        images_add_choosefilebtn = (Button)findViewById(R.id.images_add_choosefilebtn);
    }

    public void images_chooseClicked(View view) {
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
                    for (ImageFile file : list)
                        Picasso.get().load(new File(file.getPath())).into(images_add_imageview);
                }
                break;
        }
    }

    public void images_addClicked(View view) {
        String textfield_filename = images_add_filename.getEditText().getText().toString().trim();
        if(TextUtils.isEmpty(textfield_filename))
            images_add_filename.setError("Please don't leave empty fields");
        else if(images_add_imageview.getDrawable() == null)
            Utilities.getInstance().showAlertBox("", "No file were chosen", this);
        else
            new AddNewImageTask(convertDrawable(images_add_imageview.getDrawable())).execute((Void)null);
    }
    private byte[] convertDrawable(Drawable drawable)
    {
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
        byte[] bitmapdata = stream.toByteArray();
        return bitmapdata;
    }
    private class AddNewImageTask extends AsyncTask<Void, Void, ImagesEntity>
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        ImagesEntity image = null;
        AlertDialog dialog = Utilities.getInstance().showLoading(ImagesAddActivity.this, "Adding image", false);
        String filename = images_add_filename.getEditText().getText().toString().trim();
        byte[] fileByte;

        public AddNewImageTask(byte[] fileByte) {
            this.fileByte = fileByte;
        }

        @Override
        protected ImagesEntity doInBackground(Void... voids) {
            ParseObject query = new ParseObject("Images");
            query.put("Files", new ParseFile(filename + ".jpg", fileByte));
            query.put("Name", filename);
            query.put("Parent", objectId);
            query.put("Deleted",false);
            query.put("Size", fileByte.length / 1024);
            try {
                query.save();
                image = new ImagesEntity();
                image.setImageName(filename);
                image.setSize(String.valueOf(fileByte.length / 1024));
                image.setObjectId(query.getObjectId());
                image.setUrl(query.getParseFile("Files").getUrl());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return image;
        }

        @Override
        protected void onPreExecute() {dialog.show();}

        @Override
        protected void onPostExecute(ImagesEntity imagesEntity) {
            dialog.dismiss();
            if(imagesEntity != null)
            {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(IMAGE_ENTITY_NEWRECORD, imagesEntity);
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
