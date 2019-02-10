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
}
