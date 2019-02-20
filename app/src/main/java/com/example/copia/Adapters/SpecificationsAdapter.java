package com.example.copia.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.copia.Entities.SpecificationsEntity;
import com.example.copia.R;

import java.util.List;

public class SpecificationsAdapter extends RecyclerView.Adapter<SpecificationsAdapter.SpecificationsViewHolder> {
    private Context context;
    private List<SpecificationsEntity> specificationsEntities;

    public SpecificationsAdapter(Context context, List<SpecificationsEntity> specificationsEntities) {
        this.context = context;
        this.specificationsEntities = specificationsEntities;
    }

    @NonNull
    @Override
    public SpecificationsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.specifications_row_layout, viewGroup, false);
        return new SpecificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecificationsViewHolder specificationsViewHolder, int i) {
        SpecificationsEntity specificationsEntity = specificationsEntities.get(i);
        specificationsViewHolder.title.setText(specificationsEntity.getTitle());
        specificationsViewHolder.document.setText(specificationsEntity.getDocument());
        specificationsViewHolder.division.setText(specificationsEntity.getDivision());
        specificationsViewHolder.section.setText(specificationsEntity.getSection());
        specificationsViewHolder.type.setText(specificationsEntity.getType());
    }

    @Override
    public int getItemCount() {
        return specificationsEntities.size();
    }

    public class SpecificationsViewHolder extends RecyclerView.ViewHolder{
        TextView title, document, division, section, type;
        public SpecificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.spec_textview_title);
            document = itemView.findViewById(R.id.spec_textview_document);
            division = itemView.findViewById(R.id.spec_textview_division);
            section = itemView.findViewById(R.id.spec_textview_section);
            type = itemView.findViewById(R.id.spec_textview_type);
        }
    }
}
