package com.example.copia.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.copia.Entities.ClientEntity;
import com.example.copia.R;

import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {

    private Context context;
    private List<ClientEntity> clientList;

    public ClientAdapter(Context context, List<ClientEntity> clientList) {
        this.context = context;
        this.clientList = clientList;
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.row_layout, null);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder clientViewHolder, int i) {
        ClientEntity clientEntity = clientList.get(i);
        clientViewHolder.representative.setText(clientEntity.getRepresentative());
        clientViewHolder.position.setText(clientEntity.getPosition());
        clientViewHolder.company.setText(clientEntity.getCompany());
        clientViewHolder.industry.setText(clientEntity.getIndustry());
        clientViewHolder.type.setText(clientEntity.getType());
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    class ClientViewHolder extends RecyclerView.ViewHolder{
        TextView representative, position, company, industry, type;
        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            representative = itemView.findViewById(R.id.search_client_textview_representative);
            position = itemView.findViewById(R.id.search_client_textview_position);
            company = itemView.findViewById(R.id.search_client_textview_company);
            industry = itemView.findViewById(R.id.search_client_textview_industry);
            type = itemView.findViewById(R.id.search_client_textview_type);
        }
    }
}
