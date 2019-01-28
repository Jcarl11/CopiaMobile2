package com.example.copia;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

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

    public void showAlertBox(String titile, String message, Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titile);
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
}
