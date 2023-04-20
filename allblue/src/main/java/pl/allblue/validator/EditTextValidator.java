package pl.allblue.validator;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import pl.allblue.R;

public class EditTextValidator
{

    private Activity activity = null;
    List<RegexpField> fields = new ArrayList<>();
    List<ErrorField> customErrors = new ArrayList<>();


    public EditTextValidator(Activity activity)
    {
        this.activity = activity;
    }

    public void addError(EditText editText, String errorMessage)
    {
        ErrorField eField = new ErrorField();
        eField.editText = editText;
        eField.errorMessage = errorMessage;

        this.customErrors.add(eField);
    }

    public void addRegexp(final EditText edit_text, boolean required, String regexp,
            String format)
    {
        RegexpField f = new RegexpField();
        f.editText = edit_text;
        f.required = required;
        f.regexp = regexp;
        f.format = format;

        edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edit_text.setError(null);
            }
        });

        this.fields.add(f);
    }

//    public void addRegexp(final EditText edit_text, boolean required)
//    {
//        this.addRegexp(edit_text, required, null, "");
//    }

    public boolean validate()
    {
        boolean valid = true;

        for (int i = 0; i < this.fields.size(); i++) {
            RegexpField f = this.fields.get(i);
            String text = f.editText.getText().toString();

            this.activity.runOnUiThread(() -> {
                f.editText.clearFocus();
                f.editText.setError(null);
            });

            String error = null;

            if (text.equals("")) {
                if (f.required)
                    error = this.activity.getString(R.string.notValid_Empty);
            } else {
                if (f.regexp != null) {
                    if (!Pattern.matches(f.regexp, text)) {
                        error = this.activity.getString(R.string.notValid_Regexp) +
                                f.format;
                    }
                }

                if (error == null) {
                    for (int j = 0; j < this.customErrors.size(); j++) {
                        ErrorField eField = this.customErrors.get(j);
                        if (eField.editText != f.editText)
                            continue;

                        if (eField.errorMessage != null) {
                            error = eField.errorMessage;
                            break;
                        }
                    }
                }
            }

            if (error == null)
                continue;

            final boolean valid_Final = valid;
            final String error_Final = error;

            this.activity.runOnUiThread(() -> {
                f.editText.setError(error_Final);
                if (valid_Final)
                    f.editText.requestFocus();
            });

            valid = false;
        }

        return valid;
    }

    public boolean isFieldValid(EditText eField)
    {
        for (int i = 0; i < this.fields.size(); i++) {
            RegexpField rField = this.fields.get(i);
            if (rField.editText != eField)
                continue;

            String text = eField.getText().toString();

            if (text.equals("")) {
                if (rField.required)
                    return false;
            }

            if (!Pattern.matches(rField.regexp, text))
                return false;
        }

        return true;
    }


    class RegexpField
    {
        EditText editText = null;
        boolean required = false;
        String regexp = null;
        String format = "";
    }

    class ErrorField
    {
        EditText editText = null;
        String errorMessage = "";
    }

}
