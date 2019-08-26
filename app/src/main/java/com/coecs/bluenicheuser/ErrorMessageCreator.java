package com.coecs.bluenicheuser;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class ErrorMessageCreator {

    public void createSimpleErrorMessage(Context con, String title, String message){
        new AlertDialog.Builder(con)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Okay",null)
                .create().show();
    }

    public void createSimpleMessage(Context con, String title, String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(con)
                .setMessage(message)
                .setTitle(title)
                .setPositiveButton("Okay",listener)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setCancelable(false)
                .create().show();
    }

}
