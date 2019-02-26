package com.example.copia;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import dmax.dialog.SpotsDialog;


public class RegisterActivity extends AppCompatActivity {

    private EditText email, username, password,repassword, fullname;
    private Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register");
        email = (EditText)findViewById(R.id.editext_email);
        username = (EditText)findViewById(R.id.editext_username);
        password = (EditText)findViewById(R.id.edittext_password);
        repassword = (EditText)findViewById(R.id.edittext_repassword);
        submit = (Button)findViewById(R.id.btn_register_submit);
        fullname = (EditText)findViewById(R.id.editext_fullname);
        repassword.setOnEditorActionListener(listener());
    }
    private TextView.OnEditorActionListener listener()
    {
        TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    submitOnclick(null);
                }
                return false;
            }
        };
        return listener;
    }
    public void submitOnclick(View view)
    {
        if(isFieldsValid())
        {
            if(isPasswordMatched())
                new TaskExecute(this).execute((Void)null);
            else
                Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Don't leave null fields", Toast.LENGTH_SHORT).show();
    }
    private boolean isFieldsValid()
    {
        boolean valid = false;
        String emailField = email.getText().toString().trim();
        String usernameField = username.getText().toString().trim();
        String passwordField = password.getText().toString().trim();
        String repasswordField = repassword.getText().toString().trim();
        String full_name = fullname.getText().toString().trim();
        if(!TextUtils.isEmpty(emailField) && !TextUtils.isEmpty(usernameField) && !TextUtils.isEmpty(passwordField) && !TextUtils.isEmpty(repasswordField) && !TextUtils.isEmpty(full_name))
            valid = true;
        return valid;
    }
    private boolean isPasswordMatched()
    {
        boolean valid = false;
        if(password.getText().toString().trim().equals(repassword.getText().toString().trim()))
            valid = true;
        return valid;
    }

    private class TaskExecute extends AsyncTask<Void, Void, Boolean>
    {
        AlertDialog dialog;
        RegisterActivity registerActivity;
        boolean finished = false;
        boolean successful = false;

        public TaskExecute(RegisterActivity registerActivity)
        {
            this.registerActivity = registerActivity;
            dialog = new SpotsDialog.Builder()
                    .setMessage("Please wait")
                    .setContext(registerActivity)
                    .setCancelable(false)
                    .build();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            ParseUser user = new ParseUser();
            user.setUsername(username.getText().toString().trim());
            user.setPassword(password.getText().toString().trim());
            user.setEmail(email.getText().toString().trim());
            user.put("emailAlt", email.getText().toString().trim());
            user.put("Verified", false);
            user.put("Fullname", fullname.getText().toString().trim());
            user.signUpInBackground(new SignUpCallback()
            {
                public void done(ParseException e)
                {
                    if (e == null) {
                        successful = true;
                        ParseUser.logOut();
                    }
                    finished = true;
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
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            dialog.dismiss();
            Utilities.getInstance().showAlertBox("Important",
                    "A confirmation link was sent to your email. Your account is still pending until verified by administrator",
                    RegisterActivity.this);
        }
    }

}
