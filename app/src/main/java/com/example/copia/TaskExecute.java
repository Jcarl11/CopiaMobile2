package com.example.copia;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class TaskExecute extends AsyncTask<Void, Void, Boolean> {
    LoginActivity context;
    ProgressBar progressBar;
    String userName;
    String passWord;
    boolean finished = false;
    boolean successful;
    public TaskExecute(LoginActivity context, ProgressBar progressBar, String userName, String passWord)
    {
        this.context = context;
        this.progressBar = progressBar;
        this.userName = userName;
        this.passWord = passWord;
        setSuccessful(false);
        finished = false;
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
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
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
