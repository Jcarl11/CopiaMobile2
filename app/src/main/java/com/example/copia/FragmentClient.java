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
import com.dpizarro.autolabel.library.AutoLabelUISettings;
import com.dpizarro.autolabel.library.Label;

import java.util.ArrayList;
import java.util.List;


public class FragmentClient extends Fragment
{
    ArrayList<String> industryList = new ArrayList<>();
    ArrayList<String> typeList = new ArrayList<>();
    AutoLabelUI mAutoLabel_tags;
    AutoLabelUI mAutoLabel_remark;
    ArrayList<String>tags = new ArrayList<>();
    Button btn_add,btn_upload,client_btn_addremark;
    EditText client_edittext_tags,client_edittext_remark;
    Spinner spinner_industry, spinner_type;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client,container,false);
        industryList.add("Hospitality");
        industryList.add("Design");
        industryList.add("Retail");
        industryList.add("Food and Beverage");
        industryList.add("Construction");
        typeList.add("Government");
        typeList.add("Private");
        typeList.add("Corporate");
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, industryList);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, typeList);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        btn_add = (Button)view.findViewById(R.id.client_button_add);
        btn_upload = (Button)view.findViewById(R.id.client_button_upload);
        client_btn_addremark = (Button)view.findViewById(R.id.client_button_addremark);
        client_edittext_tags = (EditText)view.findViewById(R.id.client_edittext_addtag);
        client_edittext_remark = (EditText)view.findViewById(R.id.client_edittext_remarks);
        spinner_industry = (Spinner)view.findViewById(R.id.combobox_industry);
        spinner_type = (Spinner)view.findViewById(R.id.combobox_type);
        spinner_industry.setAdapter(dataAdapter1);
        spinner_type.setAdapter(dataAdapter2);
        mAutoLabel_tags = (AutoLabelUI) view.findViewById(R.id.label_tag);
        mAutoLabel_remark = (AutoLabelUI)view.findViewById(R.id.label_remark);
        AutoLabelUISettings autoLabelUISettings = new AutoLabelUISettings.Builder().build();
        mAutoLabel_tags.setSettings(autoLabelUISettings);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String tag = client_edittext_tags.getText().toString().trim();
                if(!TextUtils.isEmpty(tag))
                    mAutoLabel_tags.addLabel(tag);
                else
                    Toast.makeText(getContext(), "No input detected",Toast.LENGTH_SHORT).show();
            }
        });
        client_btn_addremark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String remark = client_edittext_remark.getText().toString().trim();
                if(!TextUtils.isEmpty(remark))
                    mAutoLabel_remark.addLabel(remark);
                else
                    Toast.makeText(getContext(), "No input detected",Toast.LENGTH_SHORT).show();
            }
        });
        btn_upload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                List<Label> tags = mAutoLabel_tags.getLabels();
            }
        });

        return view;
    }
    public ArrayList<String> convertTags(ArrayList<Label> tags)
    {
        ArrayList<String> tagsList = new ArrayList<>();
        for(Label labels : tags)
        {
            tagsList.add(labels.getText().trim());
        }
        return tagsList;
    }

}
