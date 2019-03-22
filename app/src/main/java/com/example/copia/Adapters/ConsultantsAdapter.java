package com.example.copia.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.copia.Entities.ConsultantsEntity;
import com.example.copia.R;

import java.util.List;

public class ConsultantsAdapter extends RecyclerView.Adapter<ConsultantsAdapter.ConsultantsViewHolder> {
    private Context context;
    private List<ConsultantsEntity> consultantsEntities;

    public ConsultantsAdapter(Context context, List<ConsultantsEntity> consultantsEntities) {
        this.context = context;
        this.consultantsEntities = consultantsEntities;
    }
    @NonNull
    @Override
    public ConsultantsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.consultants_row_layout, viewGroup, false);
        return new ConsultantsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultantsViewHolder consultantsViewHolder, int i) {
        ConsultantsEntity consultantsEntity = consultantsEntities.get(i);
        consultantsViewHolder.representative.setText(consultantsEntity.getRepresentative());
        consultantsViewHolder.position.setText(consultantsEntity.getPosition());
        consultantsViewHolder.company.setText(consultantsEntity.getCompany());
        consultantsViewHolder.specialization.setText(consultantsEntity.getSpecialization());
        consultantsViewHolder.classification.setText(consultantsEntity.getClassification());
        consultantsViewHolder.industry.setText(consultantsEntity.getIndustry());
    }

    @Override
    public int getItemCount() {
        return consultantsEntities.size();
    }

    public class ConsultantsViewHolder extends RecyclerView.ViewHolder{
        TextView representative, position, company,specialization, classification, industry, remarkCount;
        public ConsultantsViewHolder(@NonNull View itemView) {
            super(itemView);
            representative = itemView.findViewById(R.id.search_consultants_textview_representative);
            position = itemView.findViewById(R.id.search_consultants_textview_position);
            company = itemView.findViewById(R.id.search_consultants_textview_company);
            specialization = itemView.findViewById(R.id.search_consultants_textview_specialization);
            classification = itemView.findViewById(R.id.search_consultants_textview_classification);
            industry = itemView.findViewById(R.id.search_consultants_textview_industry);
            remarkCount = itemView.findViewById(R.id.search_consultants_textview_remarks);
        }

    }
}
