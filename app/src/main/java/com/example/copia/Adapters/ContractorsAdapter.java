package com.example.copia.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.copia.Entities.ContractorsEntity;
import com.example.copia.R;

import java.util.List;

public class ContractorsAdapter extends RecyclerView.Adapter<ContractorsAdapter.ContractorsViewHolder> {
    
    private Context context;
    private List<ContractorsEntity> contractorsEntities;

    public ContractorsAdapter(Context context, List<ContractorsEntity> contractorsEntities) {
        this.context = context;
        this.contractorsEntities = contractorsEntities;
    }

    @NonNull
    @Override
    public ContractorsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.contractors_row_layout, viewGroup, false);
        return new ContractorsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContractorsViewHolder contractorsViewHolder, int i) {
        ContractorsEntity contractorsEntity = contractorsEntities.get(i);
        contractorsViewHolder.representative.setText(contractorsEntity.getRepresentative());
        contractorsViewHolder.position.setText(contractorsEntity.getPosition());
        contractorsViewHolder.company.setText(contractorsEntity.getCompany());
        contractorsViewHolder.specialization.setText(contractorsEntity.getSpecialization());
        contractorsViewHolder.classification.setText(contractorsEntity.getClassification());
        contractorsViewHolder.industry.setText(contractorsEntity.getIndustry());
    }

    @Override
    public int getItemCount() {
        return contractorsEntities.size();
    }

    public class ContractorsViewHolder extends RecyclerView.ViewHolder{
        TextView representative, position, company,specialization, classification, industry;
        public ContractorsViewHolder(@NonNull View itemView) {
            super(itemView);
            representative = itemView.findViewById(R.id.search_contractors_textview_representative);
            position = itemView.findViewById(R.id.search_contractors_textview_position);
            company = itemView.findViewById(R.id.search_contractors_textview_company);
            specialization = itemView.findViewById(R.id.search_contractors_textview_specialization);
            classification = itemView.findViewById(R.id.search_contractors_textview_classification);
            industry = itemView.findViewById(R.id.search_contractors_textview_industry);
        }
    }
}
