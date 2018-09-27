package br.edu.ifspsaocarlos.sosprecos.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Andrey R. Brugnera on 22/09/2018.
 */
public class PhoneNumberTextWatcher implements TextWatcher {

    private boolean backspacingFlag = false;
    private EditText editText;

    public PhoneNumberTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //...
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        backspacingFlag = (count > after) ? true : false;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!backspacingFlag) {
            String text = editText.getText().toString();
            int textLength = editText.getText().length();
            if (text.endsWith("-") || text.endsWith(" ")) {
                return;
            }
            if (textLength == 1) {
                if (!text.contains("(")) {
                    editText.setText(new StringBuilder(text).insert(text.length() - 1, "(").toString());
                    editText.setSelection(editText.getText().length());
                }
            } else if (textLength == 5) {
                if (!text.contains(") ")) {
                    editText.setText(new StringBuilder(text).insert(text.length() - 1, ") ").toString());
                    editText.setSelection(editText.getText().length());
                }
            } else if (textLength == 11 || textLength == 17 || textLength == 20) {
                editText.setText(new StringBuilder(text).insert(text.length() - 1, "-").toString());
                editText.setSelection(editText.getText().length());
            }
        }
    }
}
