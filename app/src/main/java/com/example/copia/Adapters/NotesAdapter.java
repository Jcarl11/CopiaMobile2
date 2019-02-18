package com.example.copia.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.copia.Entities.NotesEntity;
import com.example.copia.R;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder>
{
    private Context context;
    private List<NotesEntity> notesEntities;

    public NotesAdapter(Context context, List<NotesEntity> notesEntities) {
        this.context = context;
        this.notesEntities = notesEntities;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.notes_row_layout,viewGroup, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder notesViewHolder, int i) {
        NotesEntity notesEntity = notesEntities.get(i);
        notesViewHolder.notes_textview_date.setText(notesEntity.getCreatedAt());
        notesViewHolder.notes_textview_remark.setText(notesEntity.getRemark());
    }

    @Override
    public int getItemCount() {
        return notesEntities.size();
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder{
        TextView notes_textview_date, notes_textview_remark;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            notes_textview_date = itemView.findViewById(R.id.notes_textview_date);
            notes_textview_remark = itemView.findViewById(R.id.notes_textview_remark);
        }
    }
}
