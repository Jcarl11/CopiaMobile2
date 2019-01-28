package com.example.copia;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import dmax.dialog.SpotsDialog;

public class LogoutTask extends AsyncTask<Void,Void,Boolean>
{
    private MainActivity mainActivity;
    boolean finished = false;
    boolean successful = false;
    AlertDialog dialog;
    public LogoutTask(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        dialog = new SpotsDialog.Builder()
                .setMessage("Logging out")
                .setContext(mainActivity)
                .setCancelable(false)
                .build();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    finished = true;
                    successful = true;
                }
                else
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
        if(!aBoolean)
            Utilities.getInstance().showAlertBox("Error","Unexpected error occured",mainActivity.getBaseContext());
        else
        {
            mainActivity.startActivity(new Intent(mainActivity.getBaseContext(), LoginActivity.class));
            mainActivity.finish();
        }
    }
}
