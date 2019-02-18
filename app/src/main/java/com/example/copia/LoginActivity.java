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
    private CheckBox checkBox;
    private Button register, login;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
    private class TaskExecute extends AsyncTask<Void, Void, Boolean> {
        LoginActivity context;
        String userName;
        String passWord;
        boolean finished = false;
        boolean successful;
        AlertDialog dialog;
        public TaskExecute(LoginActivity context, String userName, String passWord)
        {
            this.context = context;
            this.userName = userName;
            this.passWord = passWord;
            setSuccessful(false);
            finished = false;
            dialog = new SpotsDialog.Builder()
                    .setMessage("Logging in")
                    .setContext(context)
                    .setCancelable(false)
                    .build();
        }
        @Override
        protected Boolean doInBackground(Void... strings) {

            ParseUser.logInInBackground(userName, passWord, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null && e == null){
                        setSuccessful(true);
                        finished = true;
                    } else {
                        setSuccessful(false);
                        finished = true;
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
            return isSuccessful();
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            dialog.dismiss();
            if(aBoolean == true)
            {
                context.startActivity(new Intent(context.getBaseContext(), MainActivity.class));
                context.finish();
            }
            else
                Toast.makeText(context.getBaseContext(), "Wrong credentials, Please try again", Toast.LENGTH_SHORT).show();
        }
        private boolean isSuccessful() {return successful;}
        private void setSuccessful(boolean successful) {this.successful = successful;}
    }
}
