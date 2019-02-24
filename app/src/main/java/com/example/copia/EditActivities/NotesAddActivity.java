package com.example.copia.EditActivities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.copia.Entities.NotesEntity;
import com.example.copia.Fragments.FragmentNotes;
import com.example.copia.R;
import com.example.copia.Utilities;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;

public class NotesAddActivity extends AppCompatActivity {

    public static String NOTE_ENTITY_NEWRECORD = "NOTE_ENTITY_NEWRECORD";
    String PARENT_OBJECTID;
    TextInputLayout notes_add_remark;
    Button notes_add_addbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_add);
        getSupportActionBar().setTitle("Add new remark");
        notes_add_remark = (TextInputLayout)findViewById(R.id.notes_add_remark);
        notes_add_addbtn = (Button)findViewById(R.id.notes_add_addbtn);
        Intent intent = getIntent();
        PARENT_OBJECTID = intent.getStringExtra(FragmentNotes.NOTES_ENTITY_PARENT);
    }

    public void addClicked(View view)
    {
        String remark = notes_add_remark.getEditText().getText().toString().trim();
        if(TextUtils.isEmpty(remark))
            notes_add_remark.setError("Please don't leave empty fields");
        else
        {
            notes_add_remark.setError(null);
            new AddNewNotesTask().execute((Void)null);
        }
    }

    private class AddNewNotesTask extends AsyncTask<Void, Void, NotesEntity>
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        boolean successful = false;
        NotesEntity note = null;
        AlertDialog dialog = Utilities.getInstance().showLoading(NotesAddActivity.this, "Adding note", false);
        @Override
        protected NotesEntity doInBackground(Void... voids) {
            ParseObject query = new ParseObject("Notes");
            query.put("Remark", notes_add_remark.getEditText().getText().toString().trim());
            query.put("Parent", PARENT_OBJECTID);
            query.put("Deleted", false);
            try {
                query.save();
                note = new NotesEntity();
                note.setRemark(notes_add_remark.getEditText().getText().toString().trim());
                note.setCreatedAt(simpleDateFormat.format(query.getCreatedAt()));
                note.setObjectId(query.getObjectId());
            } catch (ParseException e) {e.printStackTrace();}
            return note;
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(NotesEntity entity) {
            dialog.dismiss();
            if(entity != null)
            {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(NOTE_ENTITY_NEWRECORD, entity);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
            else
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

}
