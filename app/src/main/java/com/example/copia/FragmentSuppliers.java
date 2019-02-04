package com.example.copia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.dpizarro.autolabel.library.Label;

import java.util.ArrayList;
import java.util.List;

public class FragmentSuppliers extends Fragment
{
    ArrayList<String> industryList = new ArrayList<>();
    ArrayList<String> typeList = new ArrayList<>();
    AutoLabelUI mAutoLabel_tags;
    AutoLabelUI mAutoLabel_remark;
    EditText suppliers_edittext_representative, suppliers_edittext_position, suppliers_edittext_company,suppliers_edittext_brand
            ,suppliers_edittext_addtag,suppliers_edittext_addremark;
    Spinner suppliers_spinner_industry,suppliers_spinner_type;
    Button suppliers_btn_addtag,suppliers_btn_addremark,suppliers_btn_upload;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suppliers, container, false);

        List<ComboboxEntity> industry = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Suppliers", "Industry");
        List<ComboboxEntity> type = ComboboxEntity.find(ComboboxEntity.class, "category = ? and field = ?","Suppliers", "Type");
        for(ComboboxEntity entity : industry)
            industryList.add(entity.getTitle());
        for(ComboboxEntity entity : type)
            typeList.add(entity.getTitle());

        suppliers_edittext_representative = (EditText)view.findViewById(R.id.suppliers_edittext_representative);
        suppliers_edittext_position = (EditText)view.findViewById(R.id.suppliers_edittext_position);
        suppliers_edittext_company = (EditText)view.findViewById(R.id.suppliers_edittext_company);
        suppliers_edittext_brand = (EditText)view.findViewById(R.id.suppliers_edittext_brand);
        suppliers_edittext_addtag = (EditText)view.findViewById(R.id.suppliers_edittext_addtag);
        suppliers_edittext_addremark = (EditText)view.findViewById(R.id.suppliers_edittext_remarks);
        suppliers_spinner_industry = (Spinner)view.findViewById(R.id.suppliers_combobox_industry);
        suppliers_spinner_type = (Spinner)view.findViewById(R.id.suppliers_combobox_type);
        suppliers_btn_addtag = (Button)view.findViewById(R.id.suppliers_button_add);
        suppliers_btn_addremark = (Button)view.findViewById(R.id.suppliers_button_addremark);
        suppliers_btn_upload = (Button)view.findViewById(R.id.suppliers_button_upload);
        mAutoLabel_tags = (AutoLabelUI)view.findViewById(R.id.suppliers_label_tag);
        mAutoLabel_remark = (AutoLabelUI)view.findViewById(R.id.suppliers_label_remark);
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, industryList);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, typeList);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suppliers_spinner_industry.setAdapter(dataAdapter1);
        suppliers_spinner_type.setAdapter(dataAdapter2);
        suppliers_spinner_industry.setPrompt("Industry");
        suppliers_spinner_type.setPrompt("Type");
        suppliers_btn_addtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String tag = suppliers_edittext_addtag.getText().toString().trim();
                if(!TextUtils.isEmpty(tag))
                    mAutoLabel_tags.addLabel(tag);
                else
                    Toast.makeText(getContext(), "No input detected",Toast.LENGTH_SHORT).show();
            }
        });
        suppliers_btn_addremark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String remark = suppliers_edittext_addremark.getText().toString().trim();
                if(!TextUtils.isEmpty(remark))
                    mAutoLabel_remark.addLabel(remark);
                else
                    Toast.makeText(getContext(), "No input detected",Toast.LENGTH_SHORT).show();
            }
        });
        suppliers_btn_upload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                List<Label> tags = mAutoLabel_tags.getLabels();
            }
        });
        return view;
    }
}
