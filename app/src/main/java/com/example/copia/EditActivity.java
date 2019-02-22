package com.example.copia;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.copia.Entities.NotesEntity;
import com.example.copia.Fragments.FragmentNotes;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class EditActivity extends AppCompatActivity {
    NotesEntity notesEntity = null;
    TextInputLayout notes_edit_remark;
    Button notes_edit_save;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setTitle("Edit remark");
        Intent intent = getIntent();
        notesEntity = (NotesEntity) intent.getSerializableExtra(FragmentNotes.NOTES_ENTITY);
        notes_edit_remark = (TextInputLayout) findViewById(R.id.notes_edit_remark);
        notes_edit_save = (Button) findViewById(R.id.notes_edit_save);
        notes_edit_remark.getEditText().setText(notesEntity.getRemark());
    }

    public void saveClicked(View view)
    {
        String remark = notes_edit_remark.getEditText().getText().toString().trim();
        if(!TextUtils.isEmpty(remark))
        {
            notes_edit_remark.setError(null);
            new EditNotesTask().execute((Void) null);
        }
        else
            notes_edit_remark.setError("Please don't leave this area blank");
    }
    private class EditNotesTask extends AsyncTask<Void, Void, Boolean>
    {
        AlertDialog dialog = Utilities.getInstance().showLoading(EditActivity.this, "Updating record", false);
        boolean finished = false;
        boolean successful = false;
        @Override
        protected Boolean doInBackground(Void... voids) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Notes");
            query.getInBackground(notesEntity.getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(e == null && object != null)
                    {
                        object.put("Remark", notes_edit_remark.getEditText().getText().toString().trim());
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null)
                                    successful = true;
                                finished = true;
                            }
                        });
                    }
                }
            });
            while(finished == false)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return successful;
        }

        @Override
        protected void onPreExecute() {dialog.show();}

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            if(result)
            {
                notesEntity.setRemark(notes_edit_remark.getEditText().getText().toString().trim());
                Intent intentResult = new Intent();
                intentResult.putExtra(FragmentNotes.NOTES_ENTITY, notesEntity);
                setResult(RESULT_OK, intentResult);
                finish();
            }
            else {
                Utilities.getInstance().showAlertBox("Response", "Failed", EditActivity.this);
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }
}
