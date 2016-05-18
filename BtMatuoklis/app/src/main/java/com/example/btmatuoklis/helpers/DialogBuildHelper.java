package com.example.btmatuoklis.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.btmatuoklis.R;

import java.util.ArrayList;

public class DialogBuildHelper {
    AlertDialog.Builder builder;
    AlertDialog dialog;
    Context context;
    EditText input;
    int theme = AlertDialog.THEME_HOLO_DARK;
    int inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
    int nameLength;
    int color = Color.WHITE;
    ArrayList<String> names;
    String existingError;

    public DialogBuildHelper(Context context, String title, String message, int icon) {
        this.context = context;
        this.nameLength = context.getResources().getInteger(R.integer.max_name_length);
        builder = new AlertDialog.Builder(this.context, theme);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(icon);
        existingError = this.context.getString(R.string.dialog_error_existing);
    }

    public void setNameCheck(ArrayList<String> list){
        this.names = list;
    }

    public void setInput(){
        InputFilter[] lengthFilter = new InputFilter[]{new InputFilter.LengthFilter(this.nameLength)};
        input = new EditText(this.context);
        input.setInputType(inputType);
        input.setFilters(lengthFilter);
        input.setTextColor(color);
        builder.setView(input);
    }

    public AlertDialog.Builder getBuilder(){
        return this.builder;
    }

    public void setNegative(String cancel){
        builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    public void showDialog(){
        dialog = builder.create();
        dialog.show();
    }

    public void setInputListener(){
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString().trim();
                if (!string.isEmpty() && string.length() > 3) {
                    if (names != null && !names.isEmpty() && names.contains(string)){
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        input.setError(existingError);
                    }
                    else {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        input.setError(null);
                    }
                } else { dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false); }
            }
        });
    }

    public String getInputText(){
        return input.getText().toString();
    }

    public void cancelInput(){
        dialog.cancel();
    }
}

