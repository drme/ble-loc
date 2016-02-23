package com.example.btmatuoklis.classes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogBuilder {
    AlertDialog.Builder builder;
    Context context;
    int theme = AlertDialog.THEME_HOLO_DARK;

    public AlertDialogBuilder(Context context, String title, String message, int icon) {
        this.context = context;
        builder = new AlertDialog.Builder(this.context, theme);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(icon);
    }

    public AlertDialog.Builder getBuilder(){
        return builder;
    }

    public void setNegatvie(String cancel){
        builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    public void showDialog(){
        builder.show();
    }
}

