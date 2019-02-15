package com.example.copia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.copia.Entities.ImagesEntity;

import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>
{
    private Context context;
    private List<ImagesEntity> imagesEntities;

    public ImagesAdapter(Context context, List<ImagesEntity> imagesEntities) {
        this.context = context;
        this.imagesEntities = imagesEntities;
    }

    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.images_row_layout, null);
        return new ImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesViewHolder imagesViewHolder, int i) {
        ImagesEntity imagesEntity = imagesEntities.get(i);
        Bitmap bmp = BitmapFactory.decodeByteArray(imagesEntity.getImage(), 0, imagesEntity.getImage().length);
        imagesViewHolder.imageHolder.setImageBitmap(Bitmap.createBitmap(bmp));
        imagesViewHolder.imageName.setText(imagesEntity.getImageName());
        imagesViewHolder.imageSize.setText(imagesEntity.getSize() + "KB");
    }

    @Override
    public int getItemCount() {
        return imagesEntities.size();
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageHolder;
        TextView imageName, imageSize;
        public ImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageHolder = itemView.findViewById(R.id.images_imageview_image);
            imageName = itemView.findViewById(R.id.images_textview_filename);
            imageSize = itemView.findViewById(R.id.images_textview_size);
        }
    }
}
