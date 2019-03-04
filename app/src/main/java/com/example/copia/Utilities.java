package com.example.copia;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class Utilities
{
    private Utilities(){}
    private static Utilities instance = null;
    public static Utilities getInstance()
    {
        if(instance == null)
            instance = new Utilities();
        return instance;
    }
    public static int API_LEVEL = android.os.Build.VERSION.SDK_INT;
    public void showAlertBox(String title, String message, Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
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
    public AlertDialog showLoading(Context context, String title, Boolean cancellable)
    {
        AlertDialog dialog = new SpotsDialog.Builder()
                .setMessage(title)
                .setContext(context)
                .setCancelable(cancellable)
                .build();
        return dialog;
    }
    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
