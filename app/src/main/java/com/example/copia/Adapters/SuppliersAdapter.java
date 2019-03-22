package com.example.copia.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.copia.Entities.SuppliersEntity;
import com.example.copia.R;

import java.util.List;

public class SuppliersAdapter extends RecyclerView.Adapter<SuppliersAdapter.SuppliersViewHolder> {
    private Context context;
    private List<SuppliersEntity> suppliersEntities;

    public SuppliersAdapter(Context context, List<SuppliersEntity> suppliersEntities) {
        this.context = context;
        this.suppliersEntities = suppliersEntities;
    }

    @NonNull
    @Override
    public SuppliersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.suppliers_row_layout, viewGroup, false);
        return new SuppliersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuppliersViewHolder suppliersViewHolder, int i) {
        SuppliersEntity suppliersEntity = suppliersEntities.get(i);
        suppliersViewHolder.representative.setText(suppliersEntity.getRepresentative());
        suppliersViewHolder.position.setText(suppliersEntity.getPosition());
        suppliersViewHolder.company.setText(suppliersEntity.getCompany());
        suppliersViewHolder.brand.setText(suppliersEntity.getBrand());
        suppliersViewHolder.industry.setText(suppliersEntity.getIndustry());
        suppliersViewHolder.type.setText(suppliersEntity.getType());
        suppliersViewHolder.remarkCount.setText(suppliersEntity.getRemarkCount());
    }

    @Override
    public int getItemCount() {
        return suppliersEntities.size();
    }

    public class SuppliersViewHolder extends RecyclerView.ViewHolder{
        TextView representative, position, company,brand, industry, type, remarkCount;
        public SuppliersViewHolder(@NonNull View itemView) {
            super(itemView);
            representative = itemView.findViewById(R.id.search_suppliers_textview_representative);
            position = itemView.findViewById(R.id.search_suppliers_textview_position);
            company = itemView.findViewById(R.id.search_suppliers_textview_company);
            brand = itemView.findViewById(R.id.search_suppliers_textview_brand);
            industry = itemView.findViewById(R.id.search_suppliers_textview_industry);
            type = itemView.findViewById(R.id.search_suppliers_textview_type);
            remarkCount = itemView.findViewById(R.id.search_suppliers_textview_remarks);
        }
    }
}
