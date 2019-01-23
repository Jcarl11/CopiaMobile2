package com.example.copia;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText email, username, password,repassword;
    private Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressBar = (ProgressBar)findViewById(R.id.progressbar_register);
        email = (EditText)findViewById(R.id.editext_email);
        username = (EditText)findViewById(R.id.editext_username);
        password = (EditText)findViewById(R.id.edittext_password);
        repassword = (EditText)findViewById(R.id.edittext_repassword);
        submit = (Button)findViewById(R.id.btn_register_submit);
    }

    public void submitOnclick(View view)
    {
        if(isFieldsValid())
        {
            if(isPasswordMatched())
            {
                new TaskExecute().execute((Void)null);
            }
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
        if(!TextUtils.isEmpty(emailField) && !TextUtils.isEmpty(usernameField) && !TextUtils.isEmpty(passwordField) && !TextUtils.isEmpty(repasswordField))
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
        boolean finished = false;
        boolean successful = false;
        @Override
        protected Boolean doInBackground(Void... voids) {
            ParseUser user = new ParseUser();
            user.setUsername(username.getText().toString().trim());
            user.setPassword(password.getText().toString().trim());
            user.setEmail(email.getText().toString().trim());

            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        ParseUser.logOut();
                        successful = true;
                        finished = true;
                    } else {
                        finished = true;
                        successful = false;
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
        protected void onPreExecute() {
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setTitle("Important");
            builder.setMessage("A confirmation Email was sent to your email, Please confirm to be able to login");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog alert11 = builder.create();
            alert11.show();
        }
    }

}
