package com.example.copia;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    private EditText username,password;
    private Button register, login;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login");
        username = (EditText) findViewById(R.id.edittext_username);
        password = (EditText) findViewById(R.id.edittext_password);
        register = (Button)findViewById(R.id.btn_register);
        login = (Button)findViewById(R.id.btn_login);
    }
    public void loginOnClick(View view)
    {
        String mUsername = username.getText().toString().trim();
        String mPassword = password.getText().toString().trim();
        if(!TextUtils.isEmpty(mUsername) && !TextUtils.isEmpty(mPassword))
            new TaskExecute(this, mUsername, mPassword).execute((Void)null);
        else
            Toast.makeText(this, "Don't leave blank fields", Toast.LENGTH_SHORT).show();
    }
    public void registerOnClick(View view)
    {
        startActivity(new Intent(this, RegisterActivity.class));
    }
    private class TaskExecute extends AsyncTask<Void, Void, String> {
        LoginActivity context;
        String userName;
        String passWord;
        String response = null;
        boolean finished = false;
        AlertDialog dialog;
        String pending = "PENDING";
        String success = "SUCCESSFUL";
        String exception = "EXCEPTION";
        String wrong_credentials = "ERROR";
        public TaskExecute(LoginActivity context, String userName, String passWord)
        {
            this.context = context;
            this.userName = userName;
            this.passWord = passWord;
            finished = false;
            dialog = new SpotsDialog.Builder()
                    .setMessage("Logging in")
                    .setContext(context)
                    .setCancelable(false)
                    .build();
        }
        @Override
        protected String doInBackground(Void... strings) {

            ParseUser.logInInBackground(userName, passWord, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null)
                    {
                        if(user.getBoolean("Verified") == true)
                            response = success;
                        else {
                            response = pending;
                        }
                    }
                    else if(user == null)
                        response = wrong_credentials;
                    else if(e != null)
                        response = e.getMessage();

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
            return response;
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(String response) {
            dialog.dismiss();
            if(response.equalsIgnoreCase(success))
            {
                context.startActivity(new Intent(context.getBaseContext(), MainActivity.class));
                context.finish();
            }
            else if(response.equalsIgnoreCase(pending)){
                Utilities.getInstance().showAlertBox("Pending", response, LoginActivity.this);
                ParseUser.logOut();
            }
            else if(response.equalsIgnoreCase(wrong_credentials))
                Utilities.getInstance().showAlertBox("Wrong credentials", "Account not found", LoginActivity.this);
            else
                Utilities.getInstance().showAlertBox("Something went wrong", response, LoginActivity.this);

        }
    }
}
