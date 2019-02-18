package com.example.copia.Entities;

import android.text.TextUtils;

import com.orm.SugarRecord;

import org.json.JSONException;
import org.json.JSONObject;

public class ComboboxEntity extends SugarRecord
{
    private String objectId;
    private String title;
    private String category;
    private String field;
    public ComboboxEntity(String objectId, String title, String category, String field)
    {
        this.objectId = objectId;
        this.title = title;
        this.category = category;
        this.field = field;
    }
    public String getObjectId() {return objectId;}
    public void setObjectId(String objectId) {this.objectId = objectId;}
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}
    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}
    public String getField() {return field;}
    public void setField(String field) {this.field = field;}

    public JSONObject toJSON()
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("objectId", getObjectId());
            jsonObject.put("title", getTitle());
            jsonObject.put("category", getCategory());
            jsonObject.put("field", getField());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public boolean isEntityNull()
    {
        boolean isNull = true;
        if(!TextUtils.isEmpty(getObjectId()) && !TextUtils.isEmpty(getTitle()) && !TextUtils.isEmpty(getCategory()) && !TextUtils.isEmpty(getField()))
            isNull = false;

        return isNull;
    }
    public ComboboxEntity(){}
}
