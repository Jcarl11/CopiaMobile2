package com.example.copia.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.copia.Entities.PDFEntity;
import com.example.copia.R;

import java.util.List;

public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.PDFViewHolder>
{
    private Context context;
    private List<PDFEntity> pdfEntities;

    public PDFAdapter(Context context, List<PDFEntity> pdfEntities) {
        this.context = context;
        this.pdfEntities = pdfEntities;
    }

    @NonNull
    @Override
    public PDFViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.pdf_row_layout, viewGroup, false);
        return new PDFViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PDFViewHolder pdfViewHolder, int i) {
        PDFEntity pdfEntity = pdfEntities.get(i);
        pdfViewHolder.pdf_textview_filename.setText(pdfEntity.getFilename());
        pdfViewHolder.pdf_textview_size.setText(pdfEntity.getSize() + "KB");
        pdfViewHolder.pdf_textview_createdAt.setText(pdfEntity.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return pdfEntities.size();
    }

    public class PDFViewHolder extends RecyclerView.ViewHolder {
        TextView pdf_textview_filename, pdf_textview_size, pdf_textview_createdAt;
        public PDFViewHolder(@NonNull View itemView) {
            super(itemView);
            pdf_textview_filename = itemView.findViewById(R.id.pdf_textview_filename);
            pdf_textview_size = itemView.findViewById(R.id.pdf_textview_size);
            pdf_textview_createdAt = itemView.findViewById(R.id.pdf_textview_createdat);
        }
    }
}
