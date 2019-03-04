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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONArray;

import dmax.dialog.SpotsDialog;


public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout email, username, password,repassword, fullname;
    private Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register");
        email = (TextInputLayout)findViewById(R.id.editext_email);
        username = (TextInputLayout)findViewById(R.id.editext_username);
        password = (TextInputLayout)findViewById(R.id.edittext_password);
        repassword = (TextInputLayout)findViewById(R.id.edittext_repassword);
        submit = (Button)findViewById(R.id.btn_register_submit);
        fullname = (TextInputLayout)findViewById(R.id.editext_fullname);
    }
    public void submitOnclick(View view)
    {
        if( !checkEmailForNull() | !checkNameForNull() | !checkUsernameForNull() | !checkPasswordForNull() | !checkRePasswordForNull())
            return;
        if( !checkEmailIfValid() )
            return;
        if( !isPasswordMatched() )
            return;

        new TaskExecute(this).execute((Void)null);

    }
    private boolean checkEmailForNull() {
        if(email.getEditText().getText().toString().isEmpty()) {
            email.setError("Email cannot be empty");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }
    private boolean checkNameForNull() {
        if(fullname.getEditText().getText().toString().isEmpty()) {
            fullname.setError("Name cannot be empty");
            return false;
        } else {
            fullname.setError(null);
            return true;
        }
    }
    private boolean checkUsernameForNull() {
        if(username.getEditText().getText().toString().isEmpty()) {
            username.setError("Username cannot be empty");
            return false;
        } else {
            username.setError(null);
            return true;
        }
    }
    private boolean checkPasswordForNull() {
        if(password.getEditText().getText().toString().isEmpty()) {
            password.setError("Password cannot be empty");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }
    private boolean checkRePasswordForNull() {
        if(repassword.getEditText().getText().toString().isEmpty()) {
            repassword.setError("Password cannot be empty");
            return false;
        } else {
            repassword.setError(null);
            return true;
        }
    }
    private boolean checkEmailIfValid(){
        String input = email.getEditText().getText().toString().trim();
        if(!Utilities.getInstance().isEmailValid(input)){
            email.setError("Not a valid email format");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }
    private boolean isPasswordMatched() {
        String firstPassword = password.getEditText().getText().toString().trim();
        String secondPassword = repassword.getEditText().getText().toString().trim();
        if( firstPassword.equals(secondPassword) == false ){
            password.setError("Does not match");
            repassword.setError("Does not match");
            return false;
        } else {
            password.setError(null);
            repassword.setError(null);
            return true;
        }
    }

    private class TaskExecute extends AsyncTask<Void, Void, Boolean>
    {
        AlertDialog dialog;
        RegisterActivity registerActivity;
        boolean successful = false;

        public TaskExecute(RegisterActivity registerActivity) {
            this.registerActivity = registerActivity;
            dialog = Utilities.getInstance().showLoading(RegisterActivity.this, "Please wait", false);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            ParseUser user = new ParseUser();
            user.setUsername(username.getEditText().getText().toString().trim());
            user.setPassword(password.getEditText().getText().toString().trim());
            user.setEmail(email.getEditText().getText().toString().trim());
            user.put("emailAlt", email.getEditText().getText().toString().trim());
            user.put("Fullname", fullname.getEditText().getText().toString().trim());
            try {
                user.signUp();
                user.put("arrayData", new JSONArray().put(user.getObjectId()));
                user.save();
                ParseObject pending = new ParseObject("UserInfo");
                pending.put("userid", user.getObjectId());
                pending.put("Verified", false);
                pending.put("Position", "");
                pending.save();
                ParseUser.logOut();
                successful = true;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return successful;
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            dialog.dismiss();
            if(aBoolean == true) {
                setResult(RESULT_OK);
                finish();
            } else {
                Intent intent = new Intent();
                intent.putExtra("RESPONSE", aBoolean);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        }
    }

}
